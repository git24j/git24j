import re
from .git2_types import Git2Type

# 'const git_' or 'git_'
PAT1_STR = r"^(?P<const>const\s+)?git_"
# *object
PAT2_STR = r"\s+(?P<is_pointer>\*)\s*(?P<var_name>\w+)$"
# **out
PAT3_STR = r"\s+(?P<is_pointer>\*\*)\s*(?P<var_name>\w+)$"


class Git2TypeInt32(Git2Type):
    """
    size_t map_n
    int value
    int32_t value
    - (header param): "jint value"
    - (wrapper_before): ""
    - (c function param): "value"
    - (wrapper_after): ""
    - (jni param): "int value"
    """
    PAT = re.compile(r"^(size_t|int|int32_t)\s+(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jint {jni_var_name}"
    C_WRAPPER_BEFORE_STR = ""
    C_PARAM_STR = "{jni_var_name}"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "int {jni_var_name}"


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
    PAT = re.compile(r"^(?P<type_name>size_t|int|int32_t)\s+\*(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = "\t {type_name} c_{var_name};\n"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = (
        "\t (*env)->CallVoidMethod(env, {jni_var_name}, jniConstants->midAtomicIntSet, c_{var_name});\n"
    )
    JNI_PARAM_STR = "AtomicInteger {jni_var_name}"


class Git2TypeInt64(Git2Type):
    """
        long value
        int64_t value
        - (header param): "jlong value"
        - (wrapper_before): ""
        - (c function param): "value"
        - (wrapper_after): ""
        - (jni param): "long value"
        """
    PAT = re.compile(r"^(long|int64_t)\s+(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jlong {jni_var_name}"
    C_WRAPPER_BEFORE_STR = ""
    C_PARAM_STR = "{jni_var_name}"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "long {jni_var_name}"


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
    PAT = re.compile(r"^(?P<type_name>long|int64_t)\s+\*(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = "\t {type_name} c_{var_name};\n"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = (
        "\t (*env)->CallVoidMethod(env, {jni_var_name}, jniConstants->midAtomicLongSet, c_{var_name});\n"
    )
    JNI_PARAM_STR = "AtomicLong {jni_var_name}"


class Git2TypeConstObject(Git2Type):
    """
    const git_object *obj
    - (header param): "jlong objPtr"
    - (wrapper_before): ""
    - (c function param): "(git_object *)objPtr"
    - (wrapper_after): ""
    - (jni param): long objPtr
    """
    PAT = re.compile(PAT1_STR + "(?P<obj_name>object)" + PAT2_STR)
    C_HEADER_PARAM_STR = "jlong {jni_var_name}Ptr"
    C_WRAPPER_BEFORE_STR = ""
    C_PARAM_STR = "(git_{obj_name} *){jni_var_name}Ptr"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "long {jni_var_name}"


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
    C_WRAPPER_BEFORE_STR = "git_{obj_name} *c_{var_name};\n"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = (
        "\t (*env)->CallVoidMethod(env, {jni_var_name}, jniConstants->midAtomicLongSet, (long)c_{var_name});\n"
        "\t git_{obj_name}_free(c_{var_name});\n"
    )
    JNI_PARAM_STR = "AtomicLong {jni_var_name}"


class Git2TypeConstIndex(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>index)" + PAT2_STR)


class Git2TypeConstConfigEntry(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>config_entry)" + PAT2_STR)


class Git2TypeOutConfigEntry(Git2TypeOutObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>config_entry)" + PAT3_STR)
