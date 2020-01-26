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


class Git2TypeConstDiff(Git2TypeOutObject):
    """
    git_diff *diff
    """
    PAT = re.compile(PAT1_STR + "(?P<obj_name>diff)" + PAT2_STR)
