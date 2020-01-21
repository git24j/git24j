import re

from .git2_types import Git2Type
from .git2_type_common import (
    Git2TypeConstObject,
    Git2TypeOutObject,
    PAT1_STR,
    PAT2_STR,
    PAT3_STR,
)


class Git2TypeConstConfig(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>config)" + PAT2_STR)


class Git2TypeOutConfig(Git2TypeOutObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>config)" + PAT3_STR)


class Git2TypeGitConfigLevelT(Git2Type):
    """
    git_config_level_t level
    - (header param): 'jint level'
    - (wrapper_before): ''
    - (c function param): "(git_config_level_t)level"
    - (wrapper_after):  ''
    - (jni param): int level
    """
    PAT = re.compile(r"^git_config_level_t\s+(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jint {jni_var_name}"
    C_WRAPPER_BEFORE_STR = ""
    C_PARAM_STR = "(git_config_level_t){jni_var_name}"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "int {jni_var_name}"


class Git2TypeConstConfigIterator(Git2TypeConstObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>config_iterator)" + PAT2_STR)


class Git2TypeOutConfigIterator(Git2TypeOutObject):
    PAT = re.compile(PAT1_STR + "(?P<obj_name>config_iterator)" + PAT3_STR)
