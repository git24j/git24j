import re


class Git2Type(object):
    """
    git_oid *oid:
    - (header param): 'jobject oid'
    - (wrapper_before): ' git_oid c_oid; \n j_git_oid_from_java(env, oid, &c_oid);'
    - (c function param): '&c_oid'
    - (wrapper_after): ' j_git_oid_to_java(env, &c_oid, oid);'
    - (jni param): Oid oid
    """
    PAT = re.compile(r"\bgit_oid\s+(?P<is_pointer>\*?)(?P<var_name>\S+)\b")
    C_HEADER_PARAM_STR = "jobject oid"
    C_WRAPPER_BEFORE_STR = "\t git_oid c_oid; \n\t j_git_oid_from_java(env, oid, &c_oid);\n"
    C_PARAM_STR = "&c_{var_name}"
    C_WRAPPER_AFTER_STR = "\t j_git_oid_to_java(env, &c_oid, oid);\n"
    JNI_PARAM_STR = "Oid oid"

    def __init__(self, input: str):
        self.match = self.__class__.PAT.match(input)
        self.group_dict = self.match.groupdict()

    @property
    def c_header_param(self) -> str:
        if not self.match:
            return ""
        return self.__class__.C_HEADER_PARAM_STR.format(**self.group_dict)

    @property
    def c_wrapper_before(self) -> str:
        if not self.match:
            return ""
        return self.__class__.C_WRAPPER_BEFORE_STR.format(**self.group_dict)

    @property
    def c_wrapper_param(self) -> str:
        if not self.match:
            return ""
        return self.__class__.C_PARAM_STR.format(**self.group_dict)

    @property
    def c_wrapper_after(self) -> str:
        if not self.match:
            return ""
        return self.__class__.C_WRAPPER_AFTER_STR.format(**self.group_dict)

    @property
    def jni_param(self) -> str:
        if not self.match:
            return ""
        return self.__class__.JNI_PARAM_STR.format(**self.group_dict)
