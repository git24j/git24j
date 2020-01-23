from .utils import *
from _collections import defaultdict
import re
import stringcase


class Git2Function(object):
    PAT = re.compile(
        r"^(?P<return_type>[\w *]+)\s+git_(?P<obj_name>[a-z0-9]+)_(?P<func_name>\w+)\((?P<param_list>[^()]*)\)\;$")
    PAT_0 = r"^(?P<return_type>\w+)\s+git_"
    PAT_1 = "_(?P<func_name>\w+)(?P<param_lsit>\([^()]*\))\;$"
    TPL_HEADER = "JNIEXPORT {jni_return_type} JNICALL J_MAKE_METHOD({jni_obj_name}_jni{jni_func_name})(JNIEnv *env, jclass obj, {c_wrapper_param_list});"
    TPL_WRAPPER_0 = "JNIEXPORT {jni_return_type} JNICALL J_MAKE_METHOD({jni_obj_name}_jni{jni_func_name})(JNIEnv *env, jclass obj, {c_wrapper_param_list}){{\n"
    TPL_WRAPPER_1 = "{c_wrapper_before_list}"
    TPL_WRAPPER_2 = "\t {c_wrapper_return_assign}git_{obj_name}_{func_name}({c_param_list});\n"
    TPL_WRAPPER_3 = "{c_wrapper_after_list}"
    TPL_WRAPPER_4 = "\t {c_wrapper_return_var}\n}}"
    TPL_JNIFUNC = "static native {return_type} jni{jni_func_name}({jni_param_list});"

    def parse(self, fsig: str):
        """
        int git_index_write_tree_to(git_oid *out, git_index *index, git_repository *repo); 

        - (header): 
            JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWriteTreeTo)(JNIEnv *env, jclass obj, jobject outOid, jlong indexPtr, jlong repoPtr);
        - (wrapper_before): 
            JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWriteTreeTo)(JNIEnv *env, jclass obj, jobject outOid, jlong indexPtr, jlong repoPtr)
            {
                git_oid c_oid = {0};
        - (call): 
                int e = git_index_write_tree_to(&c_oid, (git_index *)indexPtr, (git_repository *)repoPtr);
        - (wrapper_after): "git_strarray_free(&c_pathspec);"
                j_git_oid_to_java(env, &c_oid, outOid);
                return e;
            }
        - (jni param): 
            static native int jniWriteTreeTo(Oid outOid, long indexPtr, long repoPtr);
        """
        m = self.PAT.match(fsig)
        if not m:
            return
        self.match = m
        d = m.groupdict()
        for k, v in d.items():
            if v is None:
                d[k] = ''
        d['c_wrapper_return_assign'] = get_return_assign(d['return_type'])
        d['c_wrapper_return_var'] = get_return_var(d['return_type'])
        d['jni_return_type'] = get_jtype(d['return_type'])
        d['jni_obj_name'] = stringcase.pascalcase(d['obj_name'])
        d['jni_func_name'] = stringcase.pascalcase(d['func_name'])
        param_list = [
            get_git2_param(s.strip()) for s in d['param_list'].split(',')
        ]
        d['c_wrapper_param_list'] = get_c_wrapper_param_list(param_list)
        d['c_param_list'] = get_c_param_list(param_list)
        d['c_wrapper_before_list'] = get_c_wrapper_before_list(param_list)
        d['c_wrapper_after_list'] = get_c_wrapper_after_list(param_list)
        d['jni_param_list'] = get_jni_param_list(param_list)
        self.group_dict = d

    @property
    def header_sig(self) -> str:
        return self.TPL_HEADER.format(**self.group_dict)

    @property
    def wrapper(self) -> str:
        return ''.join((
            self.TPL_WRAPPER_0.format(**self.group_dict),
            self.TPL_WRAPPER_1.format(**self.group_dict),
            self.TPL_WRAPPER_2.format(**self.group_dict),
            self.TPL_WRAPPER_3.format(**self.group_dict),
            self.TPL_WRAPPER_4.format(**self.group_dict),
        ))

    @property
    def jni_sig(self) -> str:
        return self.TPL_JNIFUNC.format(**self.group_dict)
