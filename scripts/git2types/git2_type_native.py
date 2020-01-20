import re
from .git2_types import Git2Type


class Git2TypeConstString(Git2Type):
    """
        const char *name:
        - (header param): "jstring name"
        - (wrapper_before): "char * c_name = j_copy_of_jstring(env, name, true);"
        - (c function param): c_name
        - (wrapper_after): "free(c_name)"
        - (jni param): String name
        """
    PAT = re.compile(
        r"\b(?P<const>const)?\s+char\s+(?P<is_pointer>\*?)(?P<var_name>\S+)\b")
    C_HEADER_PARAM_STR = "jstring {var_name}"
    C_WRAPPER_BEFORE_STR = " \t char * c_{var_name} = j_copy_of_jstring(env, {var_name}, true);"
    C_PARAM_STR = "c_{var_name}"
    C_WRAPPER_AFTER_STR = " \t free(c_{var_name});\n"
    JNI_PARAM_STR = "String {jni_var_name}"


class Git2TypePrimitive(Git2Type):
    """
        int flag, long val, char var, float var
        - (header param): "jint name"
        - (wrapper_before): ""
        - (c function param): val
        - (wrapper_after): ""
        - (jni param): int flag
    """
    PAT = re.compile(
        r"^\s*\b(?P<type_name>byte|char|short|int|long|float|double)\s+(?P<var_name>\w+)\s*$")
    C_HEADER_PARAM_STR = "j{type_name} {var_name}"
    C_WRAPPER_BEFORE_STR = ""
    C_PARAM_STR = "{var_name}"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "{type_name} {jni_var_name}"
