import re
from .git2_types import Git2Type


class Git2TypeConstRepository(Git2Type):
    """
        const git_repository *repo:
        - (header param): "jlong repoPtr"
        - (wrapper_before): ""
        - (c function param): "(git_repository *)repoPtr"
        - (wrapper_after): ""
        - (jni param): Oid oid
        """
    PAT = re.compile(r"\b(?P<const>const)?\s+git_repository\s+(?P<is_pointer>\*?)(?P<var_name>\S+)\b")
    C_HEADER_PARAM_STR = "jlong {var_name}Ptr"
    C_WRAPPER_BEFORE_STR = ""
    C_PARAM_STR = "(git_repository *){var_name}Ptr"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "long repoPtr"


class Git2TypeOutRepository(Git2Type):
    """
        git_repository **out:
        - (header param): "jobject out"
        - (wrapper_before): "git_repository *c_out;"
        - (c function param): "&c_out"
        - (wrapper_after): "(*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);"
        - (jni param): "AtomicLong out"
        """
    PAT = re.compile(r"\b(?P<const>const)?\s+git_repository\s+(?P<is_pointer>\*\*)(?P<var_name>\S+)\b")
    C_HEADER_PARAM_STR = "jobject {var_name}"
    C_WRAPPER_BEFORE_STR = "git_repository *c_{var_name};"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = "(*env)->CallVoidMethod(env, {var_name}, jniConstants->midAtomicLongSet, (long)c_{var_name});"
    JNI_PARAM_STR = "AtomicLong {var_name}"
