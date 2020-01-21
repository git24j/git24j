import re
from .git2_types import Git2Type


class Git2TypeStringArray(Git2Type):
    """
        const git_strarray *pathspec
        - (header param): "jobjectArray pathspec"
        - (wrapper_before): " \t git_strarray c_pathspec = {0}; \n\t git_strarray_of_jobject_array(env, pathspec, &c_pathspec);"
        - (c function param): "&c_pathspec"
        - (wrapper_after): "git_strarray_free(&c_pathspec);"
        - (jni param): "String[] pathspec"
        """
    PAT = re.compile(
        r"^(?P<const>const)\s*git_strarray\s+(?P<is_pointer>\*?)(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobjectArray {jni_var_name}"
    C_WRAPPER_BEFORE_STR = " \t git_strarray c_{var_name}; \n\t git_strarray_of_jobject_array(env, {jni_var_name}, &c_{var_name});\n"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = " \t git_strarray_free(&c_{var_name});\n"
    JNI_PARAM_STR = "String[] {jni_var_name}"


class Git2TypeOutStringArray(Git2Type):
    """
        git_strarray *pathspec
        git_strarray *tag_names
            git_strarray *c_array = (git_strarray *)malloc(sizeof(git_strarray));
            int e = git_reference_list(c_array, (git_repository *)repoPtr);
            j_strarray_to_java_list(env, c_array, strList);
            git_strarray_free(c_array);
            return e;
        """
    PAT = re.compile(r"^git_strarray\s+(?P<is_pointer>\*?)(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = " \tgit_strarray *c_{var_name} = (git_strarray *)malloc(sizeof(git_strarray));\n"
    C_PARAM_STR = "c_{var_name}"
    C_WRAPPER_AFTER_STR = (
        " \t j_strarray_to_java_list(env, c_{var_name}, {jni_var_name});\n"
        " \t git_strarray_free(c_{var_name});\n"
        " \t free(c_{var_name});\n"
    )
    JNI_PARAM_STR = "List<String> {jni_var_name}"
