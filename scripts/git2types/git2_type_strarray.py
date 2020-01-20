import re
from .git2_types import Git2Type


class Git2TypeStringArray(Git2Type):
    """
        const git_strarray *pathspec
        git_strarray *tag_names
        - (header param): "jobjectArray pathspec"
        - (wrapper_before): " \t git_strarray c_pathspec = {0}; \n\t git_strarray_of_jobject_array(env, pathspec, &c_pathspec);"
        - (c function param): "&c_pathspec"
        - (wrapper_after): "git_strarray_free(&c_pathspec);"
        - (jni param): "String[] pathspec"
        """
    PAT = re.compile(
        r"^(?P<const>const)?\s*git_strarray\s+(?P<is_pointer>\*?)(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobjectArray {var_name}"
    C_WRAPPER_BEFORE_STR = " \t git_strarray c_{var_name}; \n\t git_strarray_of_jobject_array(env, {var_name}, &c_{var_name});\n"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = " \t git_strarray_free(&c_{var_name});\n"
    JNI_PARAM_STR = "String[] {jni_var_name}"
