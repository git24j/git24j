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
    """
    git_config **out
    """
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


class Git2TypeConfigForeachCb(Git2Type):
    """
    git_config_foreach_cb callback
    - (header param): 'jobject callback'
    - (wrapper_before): 'j_cb_payload payload = {env, callback};'
    - (c function param): "j_git_config_foreach_cb, &payload"
    - (wrapper_after):  ''
    - (jni param): ForeachCb callback
    """
    PAT = re.compile(r"^git_config_foreach_cb\s+(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jobject callback"
    C_WRAPPER_BEFORE_STR = "j_cb_payload payload = {{env, callback}};"
    C_PARAM_STR = "j_git_config_foreach_cb"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "ForeachCb callback"


class Git2TypeVoidPtrPayload(Git2Type):
    """
    void *payload
    - (header param): ''
    - (wrapper_before): ''
    - (c function param): "&payload"
    - (wrapper_after):  ''
    - (jni param): ''
    """
    PAT = re.compile(r"^void\s+\*\s*(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = ""
    C_WRAPPER_BEFORE_STR = ""
    C_PARAM_STR = "&payload"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = ""


class Git2TypeConstConfigMap(Git2Type):
    """
    const git_configmap *maps
    - (header param): 'jlong mapsPtr'
    - (wrapper_before): ''
    - (c function param): "(const git_configmap *)mapsPtr"
    - (wrapper_after):  ''
    - (jni param): 'long mapsPtr'
    """
    PAT = re.compile(r"^const\s+git_configmap\s+\*\s*(?P<var_name>\w+)$")
    C_HEADER_PARAM_STR = "jlong {jni_var_name}Ptr"
    C_WRAPPER_BEFORE_STR = ""
    C_PARAM_STR = "(const git_configmap *){jni_var_name}Ptr"
    C_WRAPPER_AFTER_STR = ""
    JNI_PARAM_STR = "long {jni_var_name}Ptr"


class Git2TypeConstConfigBackend(Git2TypeConstObject):
    """
    git_config_backend *backend
    """
    PAT = re.compile(PAT1_STR + "(?P<obj_name>config_backend)" + PAT2_STR)


class Git2TypeOutTransaction(Git2TypeOutObject):
    """
    git_transaction **tx
    """
    PAT = re.compile(PAT1_STR + "(?P<obj_name>transaction)" + PAT3_STR)
