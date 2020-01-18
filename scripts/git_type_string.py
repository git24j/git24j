import re
from .git2_types import Git2Type


class Git2TypeConstSring(Git2Type):
    """
        const char *name:
        - (header param): "jstring name"
        - (wrapper_before): "char * c_name = j_copy_of_jstring(env, name, true);"
        - (c function param): c_name
        - (wrapper_after): "free(c_name)"
        - (jni param): String name
        """
    PAT = re.compile(r"\b(?P<const>const)?\s+char\s+(?P<is_pointer>\*?)(?P<var_name>\S+)\b")
    C_HEADER_PARAM_STR = "jstring {var_name}"
    C_WRAPPER_BEFORE_STR = " \t char * c_{var_name} = j_copy_of_jstring(env, {var_name}, true);"
    C_PARAM_STR = "c_{var_name}"
    C_WRAPPER_AFTER_STR = "free(c_{var_name});"
    JNI_PARAM_STR = "String {var_name}"

