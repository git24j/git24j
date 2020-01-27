import re

from .git2_types import Git2Type
from .git2_type_common import (
    Git2TypeConstObject,
    Git2TypeOutObject,
    PAT1_STR,
    PAT2_STR,
    PAT3_STR,
)


class Git2TypeTree(Git2TypeConstObject):
    """
    git_tree *old_tree
    """
    PAT = re.compile(PAT1_STR + "(?P<obj_name>tree)" + PAT2_STR)
