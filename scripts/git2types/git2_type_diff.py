import re

from .git2_types import Git2Type
from .git2_type_common import (
    Git2TypeConstObject,
    Git2TypeOutObject,
    PAT1_STR,
    PAT2_STR,
    PAT3_STR,
)


class Git2TypeDiffOptions(Git2TypeConstObject):
    """
    git_diff_options *opts
    - (header param): 'jlong optsPtr'
    - (c function param): "(git_diff_options *)optsPtr"
    - (jni param): long optsPtr
    """
    PAT = re.compile(PAT1_STR + "(?P<obj_name>diff_options)" + PAT2_STR)


class Git2TypeDiffFindOptions(Git2TypeConstObject):
    """
    git_diff_options *opts
    - (header param): 'jlong optsPtr'
    - (c function param): "(git_find_diff_options *)optsPtr"
    - (jni param): long optsPtr
    """
    PAT = re.compile(PAT1_STR + "(?P<obj_name>diff_find_options)" + PAT2_STR)


class Git2TypeOutDiff(Git2TypeOutObject):
    """
    git_diff **diff
    """
    PAT = re.compile(PAT1_STR + "(?P<obj_name>diff)" + PAT3_STR)


class Git2TypeConstDiff(Git2TypeConstObject):
    """
    git_diff *diff
    """
    PAT = re.compile(PAT1_STR + "(?P<obj_name>diff)" + PAT2_STR)


class Git2TypeDiffFileCb(Git2Type):
    """
    git_diff_file_cb	file_cb	
    - (header param): 'jobject callback'
    - (wrapper_before): 'j_diff_callback_payload payload = {env, callback};'
    - (c function param): "j_git_diff_file_cb, &payload"
    - (wrapper_after):  ''
    - (jni param): JFCallback callback
    """
    PAT = re.compile(r"^git_diff_file_cb\s+(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = (
        "\t j_diff_callback_payload payload = {{env}};\n"
        "\t payload.fileCb = {jni_var_name};\n"
    )
    C_PARAM_STR = "j_git_diff_file_cb"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "JFCallback {jni_var_name}"


class Git2TypeDiffBinaryCb(Git2TypeDiffFileCb):
    """
    git_diff_binary_cb	binary_cb
    - (header param): 'jobject callback'
    - (wrapper_before): 'j_cb_payload payload = {env, callback};'
    - (c function param): "j_git_diff_binary_cb, &payload"
    - (wrapper_after):  ''
    - (jni param): JJCallback callback
    """
    PAT = re.compile(r"^git_diff_binary_cb\s+(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = "\t payload.binaryCb = {jni_var_name};\n"
    C_PARAM_STR = "j_git_diff_binary_cb"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "JJCallback {jni_var_name}"


class Git2TypeDiffHunkCb(Git2TypeDiffFileCb):
    """
    git_diff_hunk_cb	hunk_cb
    - (header param): 'jobject callback'
    - (wrapper_before): 'j_cb_payload payload = {env, callback};'
    - (c function param): "j_git_diff_hunk_cb, &payload"
    - (wrapper_after):  ''
    - (jni param): JJCallback callback
    """
    PAT = re.compile(r"^git_diff_hunk_cb\s+(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = "\t payload.hunkCb = {jni_var_name};\n"
    C_PARAM_STR = "j_git_diff_hunk_cb"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "JJCallback {jni_var_name}"


class Git2TypeDiffLineCb(Git2TypeDiffFileCb):
    """
    git_diff_line_cb	line_cb
    - (header param): 'jobject callback'
    - (wrapper_before): 'j_cb_payload payload = {env, callback};'
    - (c function param): "j_git_diff_line_cb, &payload"
    - (wrapper_after):  ''
    - (jni param): JJCallback callback
    """
    PAT = re.compile(r"^git_diff_line_cb\s+(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject {jni_var_name}"
    C_WRAPPER_BEFORE_STR = "\t payload.lineCb = {jni_var_name};\n"
    C_PARAM_STR = "j_git_diff_line_cb"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "JJJCallback {jni_var_name}"


class Git2TypeConstBlob(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>blob)" + PAT2_STR)


class Git2TypeOutBlob(Git2TypeOutObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>blob)" + PAT3_STR)


class Git2TypeConstDiffStats(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>diff_stats)" + PAT2_STR)


class Git2TypeOutDiffStats(Git2TypeOutObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>diff_stats)" + PAT3_STR)


class Git2TypeConstDiffFormatEmail(Git2TypeConstObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>diff_format_email_options)" + PAT2_STR)


class Git2TypeOutDiffFormatEmail(Git2TypeOutObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>diff_format_email_options)" + PAT3_STR)


class Git2TypeConstCommit(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>commit)" + PAT2_STR)


class Git2TypeOutCommit(Git2TypeOutObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>commit)" + PAT3_STR)


class Git2TypeConstDiffPatchIdOptions(Git2TypeConstObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>diff_patchid_options)" + PAT2_STR)


class Git2TypeOutDiffPatchIdOptions(Git2TypeOutObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>diff_patchid_options)" + PAT3_STR)


class Git2TypeConstPatch(Git2TypeConstObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>diff_patchid_options)" + PAT2_STR)


class Git2TypeOutPatch(Git2TypeOutObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>diff_patchid_options)" + PAT3_STR)


class Git2TypeConstDiffHunk(Git2TypeConstObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>diff_hunk)" + PAT2_STR)


class Git2TypeOutDiffHunk(Git2TypeOutObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>diff_hunk)" + PAT3_STR)


class Git2TypeConstDiffLine(Git2TypeConstObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>diff_line)" + PAT2_STR)


class Git2TypeOutDiffLine(Git2TypeOutObject):
    PAT = re.compile(
        PAT1_STR + "(?P<obj_name>diff_line)" + PAT3_STR)
