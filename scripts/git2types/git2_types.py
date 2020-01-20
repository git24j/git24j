import re
import stringcase


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

    def __init__(self, m: re.Match):
        self.match = m
        self.group_dict = self.match.groupdict()
        names = [k for k in self.group_dict.keys() if k.endswith('_name')]
        for k in names:
            self.group_dict[f'jni_{k}'] = \
                stringcase.camelcase(self.group_dict[k])
            self.group_dict[f'cap_{k}'] = \
                stringcase.capitalcase(self.group_dict[k])

    @classmethod
    def parse(cls, input: str) -> 'Git2Type':
        m = cls.PAT.match(input)
        # print(f"qqqq pat={cls.PAT}, input={input}")
        if m:
            return cls(m)
        return None

    @property
    def c_header_param(self) -> str:
        """
        git_oid *oid => jobject oid
        """
        if not self.match:
            return ""
        return self.__class__.C_HEADER_PARAM_STR.format(**self.group_dict)

    @property
    def c_wrapper_before(self) -> str:
        """
        git_oid *oid => ' git_oid c_oid; \n j_git_oid_from_java(env, oid, &c_oid);'
        """
        if not self.match:
            return ""
        return self.__class__.C_WRAPPER_BEFORE_STR.format(**self.group_dict)

    @property
    def c_wrapper_param(self) -> str:
        """
        git_oid *oid => &c_oid
        """
        if not self.match:
            return ""
        return self.__class__.C_PARAM_STR.format(**self.group_dict)

    @property
    def c_wrapper_after(self) -> str:
        """
        git_oid *oid => j_git_oid_to_java(env, &c_oid, oid);
        """
        if not self.match:
            return ""
        return self.__class__.C_WRAPPER_AFTER_STR.format(**self.group_dict)

    @property
    def jni_param(self) -> str:
        """
        git_oid *oid => Oid oid
        """
        if not self.match:
            return ""
        return self.__class__.JNI_PARAM_STR.format(**self.group_dict)
