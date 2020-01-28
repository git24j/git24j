import re

from .git2_types import Git2Type
from .git2_type_common import (
    Git2TypeConstObject,
    Git2TypeOutObject,
    PAT1_STR,
    PAT2_STR,
    PAT3_STR,
)


class Git2TypeMergeAnalysis(Git2Type):
    """
    git_merge_analysis_t *analysis_out
    - (header param): 'jobject analysisOut'
    - (wrapper_before): 'int c_analysis_out'
    - (c function param): "&c_analysis_out"
    - (wrapper_after): (*env)->CallVoidMethod(env, out, jniConstants->midAtomicIntSet, c_analysis_out);
    - (jni param): AtomicInteger analysisOut
    """
    PAT = re.compile(r"^(?P<const>const)\s+(?P<obj_name>git_merge_analysis_t)\s+\*(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = "\t int c_{var_name};\n"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = "\t (*env)->CallVoidMethod(env, {jni_var_name}, jniConstants->midAtomicIntSet, c_{var_name});\n"
    JNI_PARAM_STR = "JJCallback {jni_var_name}"


class Git2TypeConstMergeFileInput(Git2TypeConstObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>merge_file_input)" + PAT2_STR)


class Git2TypeOutMergeFileInput(Git2TypeOutObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>merge_file_input)" + PAT3_STR)


class Git2TypeConstOidArray(Git2TypeConstObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>oidarray)" + PAT2_STR)


class Git2TypeOutOidArray(Git2TypeOutObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>oidarray)" + PAT3_STR)


class Git2TypeConstMergeFileOptions(Git2TypeConstObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>merge_file_options)" + PAT2_STR)


class Git2TypeOutMergeFileOptions(Git2TypeOutObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>merge_file_options)" + PAT3_STR)


class Git2TypeConstMergeOptions(Git2TypeConstObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>merge_options)" + PAT2_STR)


class Git2TypeOutMergeOptions(Git2TypeOutObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>merge_options)" + PAT3_STR)


class Git2TypeConstMergeFileResult(Git2TypeConstObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>merge_options)" + PAT2_STR)


class Git2TypeOutMergeFileResult(Git2TypeOutObject):
    """
    git_merge_file_result *out
    - (header param): 'jobject analysisOut'
    - (wrapper_before): 'int c_analysis_out'
    - (c function param): "&c_analysis_out"
    - (wrapper_after): (*env)->CallVoidMethod(env, out, jniConstants->midAtomicIntSet, c_analysis_out);
    - (jni param): AtomicInteger analysisOut
    """
    PAT = re.compile(r"^(?P<const>const)\s+(?P<obj_name>git_merge_analysis_t)\s+\*(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = "\t int c_{var_name};\n"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = "\t (*env)->CallVoidMethod(env, {jni_var_name}, jniConstants->midAtomicIntSet, c_{var_name});\n"
    JNI_PARAM_STR = "JJCallback {jni_var_name}"
