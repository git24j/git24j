import re

from .git2_types import Git2Type
from .git2_type_common import (
    Git2TypeConstObject,
    Git2TypeOutObject,
    PAT1_STR,
    PAT2_STR,
    PAT3_STR,
)

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
