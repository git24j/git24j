import re
from .git2_types import Git2Type

PAT1_STR = r"^(?P<const>const\s+)?git_"
PAT2_STR = r"\s+(?P<is_pointer>\*)\s*(?P<var_name>\w+)$"


class Git2TypeConstObject(Git2Type):
    """
    const git_object *obj
    - (header param): "jlong objPtr"
    - (wrapper_before): ""
    - (c function param): "(git_object *)objPtr"
    - (wrapper_after): ""
    - (jni param): Object obj
    """
    PAT = re.compile(PAT1_STR + "(?P<obj_name>object)" + PAT2_STR)
    C_HEADER_PARAM_STR = "jlong {var_name}Ptr"
    C_WRAPPER_BEFORE_STR = ""
    C_PARAM_STR = "(git_{obj_name} *){var_name}Ptr"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "{cap_obj_name} {jni_var_name}"


class Git2TypeConstIndex(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>index)" + PAT2_STR)
