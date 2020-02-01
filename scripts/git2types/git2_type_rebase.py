import re

from .git2_types import Git2Type
from .git2_type_common import (
    Git2TypeConstObject,
    Git2TypeOutObject,
    PAT1_STR,
    PAT2_STR,
    PAT3_STR,
)

class Git2TypeDiffFileCbxx(Git2Type):
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


class Git2TypeConstRebaseOptions(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>rebase_options)" + PAT2_STR)


class Git2TypeOutRebaseOptions(Git2TypeOutObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>rebase_options)" + PAT3_STR)


class Git2TypeConstRebase(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>rebase)" + PAT2_STR)


class Git2TypeOutRebase(Git2TypeOutObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>rebase)" + PAT3_STR)

class Git2TypeConstRebaseOperation(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>rebase_operation)" + PAT2_STR)


class Git2TypeOutRebaseOperation(Git2TypeOutObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>rebase_operation)" + PAT3_STR)
