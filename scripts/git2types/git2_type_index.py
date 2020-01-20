import re
from .git2_types import Git2Type


class Git2TypeConstIndex(Git2Type):
    """
    const git_object *obj
    - (header param): "jlong objPtr"
    - (wrapper_before): ""
    - (c function param): "(git_object *)objPtr"
    - (wrapper_after): ""
    - (jni param): Object obj
    """
    PAT = re.compile(
        r"^(?P<const>const\s+)?git_index\s+(?P<is_pointer>\*)\s*(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jlong {var_name}Ptr"
    C_WRAPPER_BEFORE_STR = ""
    C_PARAM_STR = "(git_repository *){var_name}Ptr"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "long {jni_var_name}"
