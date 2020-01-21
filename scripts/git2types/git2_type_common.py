import re
from .git2_types import Git2Type

PAT1_STR = r"^(?P<const>const\s+)?git_"
PAT2_STR = r"\s+(?P<is_pointer>\*)\s*(?P<var_name>\w+)$"
PAT3_STR = r"\s+(?P<is_pointer>\*\*)\s*(?P<var_name>\w+)$"


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


class Git2TypeOutObject(Git2Type):
    """
    git_object **out
    - (header param): "jobject out"
    - (wrapper_before): "git_object *c_out;"
    - (c function param): "&c_out"
    - (wrapper_after): "git_object_free(c_out);"
                       (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    - (jni param): AtomicLong out
    """
    PAT = re.compile(PAT1_STR + "(?P<obj_name>object)" + PAT3_STR)
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = "git_{obj_name} *c_{var_name}"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = (
        "\t git_{obj_name}_free(c_{var_name});\n"
        "(*env)->CallVoidMethod(env, {jni_var_name}, jniConstants->midAtomicLongSet, (long)c_{var_name});"
    )
    JNI_PARAM_STR = "AtomicLong {jni_var_name}"


class Git2TypeConstIndex(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>index)" + PAT2_STR)


class Git2TypeConstConfigEntry(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>config_entry)" + PAT2_STR)


class Git2TypeOutConfigEntry(Git2TypeOutObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>config_entry)" + PAT3_STR)
