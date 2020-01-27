import re

from .git2_types import Git2Type


class Git2TypeOutBuf(Git2Type):
    """
    git_buf *out:
    - (header param): 'jobject out'
    - (wrapper_before): ' git_buf c_buf; '
    - (c function param): "&c_oid"
    - (wrapper_after):  ' j_git_buf_to_java(env, &c_buf, outBuf); git_buf_dispose(&c_buf);'
    - (jni param): Oid oid
    """
    PAT = re.compile(r"^git_buf\s+(?P<is_pointer>\*)(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {var_name}"
    C_WRAPPER_BEFORE_STR = "\t git_buf c_{var_name} = {{0}};\n"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = "\t j_git_buf_to_java(env, &c_{var_name}, {var_name}); \n\t git_buf_dispose(&c_{var_name});\n"
    JNI_PARAM_STR = "Buf {var_name}"
