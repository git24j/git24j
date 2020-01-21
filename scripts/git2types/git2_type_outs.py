import re

from .git2_types import Git2Type


class Git2TypeOutInt32(Git2Type):
    """
    int *out
    int32_t *out
    - (header param): "jobject out"
    - (wrapper_before): "int c_out"
    - (c function param): "&c_out"
    - (wrapper_after): (*env)->CallVoidMethod(env, out, jniConstants->midAtomicIntSet, c_out);
    - (jni param): AtomicInteger out
    """
    PAT = re.compile(r"^(int|int32_t)\s+\*\w+$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = "int c_{var_name}"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = (
        "(*env)->CallVoidMethod(env, {jni_var_name}, jniConstants->midAtomicIntSet, c_{var_name});"
    )
    JNI_PARAM_STR = "AtomicInteger {jni_var_name}"


class Git2TypeOutInt64(Git2Type):
    """
    long *out
    int64_t *out
    - (header param): "jobject out"
    - (wrapper_before): "int c_out"
    - (c function param): "&c_out"
    - (wrapper_after): (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    - (jni param): AtomicLong out
    """
    PAT = re.compile(r"^(long|int64_t)\s+\*\w+$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = "int c_{var_name}"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = (
        "(*env)->CallVoidMethod(env, {jni_var_name}, jniConstants->midAtomicLongSet, c_{var_name});"
    )
    JNI_PARAM_STR = "AtomicLong {jni_var_name}"

