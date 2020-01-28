from typing import List
from .git2_type_native import (
    Git2TypeOutString,
    Git2TypeConstString,
    Git2TypePrimitive,
)
from .git2_type_oid import Git2TypeConstOid, Git2TypeOid
from .git2_type_repository import Git2TypeConstRepository, Git2TypeOutRepository
from .git2_type_strarray import Git2TypeStringArray, Git2TypeOutStringArray
from .git2_type_buf import Git2TypeOutBuf
from .git2_type_common import (
    Git2TypeConstIndex,
    Git2TypeOutIndex,
    Git2TypeConstConfigEntry,
    Git2TypeOutConfigEntry,
    Git2TypeOutInt32,
    Git2TypeOutInt64,
    Git2TypeInt32,
    Git2TypeInt64,
    Git2TypeConstVoid,
)
from .git2_type_config import (
    Git2TypeConstConfig,
    Git2TypeOutConfig,
    Git2TypeGitConfigLevelT,
    Git2TypeConstConfigIterator,
    Git2TypeOutConfigIterator,
    Git2TypeConfigForeachCb,
    Git2TypeVoidPtrPayload,
    Git2TypeConstConfigMap,
    Git2TypeConstConfigBackend,
    Git2TypeOutTransaction,
)

from .git2_type_diff import (
    Git2TypeDiffOptions,
    Git2TypeDiffFindOptions,
    Git2TypeConstDiff,
    Git2TypeOutDiff,
    Git2TypeDiffFileCb,
    Git2TypeDiffBinaryCb,
    Git2TypeDiffHunkCb,
    Git2TypeDiffLineCb,
    Git2TypeConstBlob,
    Git2TypeOutBlob,
    Git2TypeConstDiffStats,
    Git2TypeOutDiffStats,
    Git2TypeConstDiffFormatEmail,
    Git2TypeOutDiffFormatEmail,
    Git2TypeConstCommit,
    Git2TypeOutCommit,
    Git2TypeConstDiffPatchIdOptions,
    Git2TypeOutDiffPatchIdOptions,
    Git2TypeConstPatch,
    Git2TypeOutPatch,
    Git2TypeConstDiffHunk,
    Git2TypeOutDiffHunk,
    Git2TypeConstDiffLine,
    Git2TypeOutDiffLine,
)

from .git2_type_merge import (
    Git2TypeConstMergeFileInput,
    Git2TypeOutMergeFileInput,
    Git2TypeConstOidArray,
    Git2TypeOutOidArray,
    Git2TypeConstMergeFileOptions,
    Git2TypeOutMergeFileOptions,
    Git2TypeConstMergeOptions,
    Git2TypeOutMergeOptions,
)

from .git2_type_tree import Git2TypeTree

import re


RAW_PAT = re.compile(
    r"\b(?P<type_name>byte|char|short|int|long|float|double)\b")
STRING_PAT = re.compile(r"\b(?P<const>const)?\s*char\s*\*")
INT_ISH_TYPE = re.compile(r"\b(?P<type_name>size_t)\b")
LONG_ISH_TYPE = re.compile(r"^const\s+(?P<type_name>\w+)\s+\*")
JNI_JAVA_TYPE_MAP = {
    'void': 'void',
    'jboolean': 'boolean',
    'jbyte': 'byte',
    'jchar': 'char',
    'jshort': 'int',
    'jint': 'int',
    'jlong': 'long',
    'jstring': 'String',
    'jobject': 'Object',
    'jclass': 'Class',
    'jobjectArray': 'Object[]',
    'jbooleanArray': 'boolean[]',
    'jcharArray': 'char[]',
    'jshortArray': 'int[]',
    'jintArray': 'int[]',
    'jlongArray': 'long[]',
    'jfloatArray': 'float[]',
    'jdoubleArray': 'double[]',
}

GIT2_PARAM_PARSERS = [
    Git2TypeOutString,
    Git2TypeConstString,
    Git2TypePrimitive,
    Git2TypeConstOid,
    Git2TypeOid,
    Git2TypeConstRepository,
    Git2TypeOutRepository,
    Git2TypeOutStringArray,
    Git2TypeStringArray,
    Git2TypeConstIndex,
    Git2TypeOutIndex,
    Git2TypeConstConfigEntry,
    Git2TypeOutConfigEntry,
    Git2TypeOutBuf,
    Git2TypeConstConfig,
    Git2TypeOutConfig,
    Git2TypeGitConfigLevelT,
    Git2TypeConstConfigIterator,
    Git2TypeOutConfigIterator,
    Git2TypeOutInt32,
    Git2TypeOutInt64,
    Git2TypeInt32,
    Git2TypeInt64,
    Git2TypeConstVoid,
    Git2TypeConfigForeachCb,
    Git2TypeVoidPtrPayload,
    Git2TypeConstConfigMap,
    Git2TypeConstConfigBackend,
    Git2TypeOutTransaction,
    Git2TypeDiffOptions,
    Git2TypeDiffFindOptions,
    Git2TypeTree,
    Git2TypeOutDiff,
    Git2TypeConstDiff,
    Git2TypeDiffFileCb,
    Git2TypeDiffBinaryCb,
    Git2TypeDiffHunkCb,
    Git2TypeDiffLineCb,
    Git2TypeConstBlob,
    Git2TypeOutBlob,
    Git2TypeConstDiffStats,
    Git2TypeOutDiffStats,
    Git2TypeConstDiffFormatEmail,
    Git2TypeOutDiffFormatEmail,
    Git2TypeConstCommit,
    Git2TypeOutCommit,
    Git2TypeConstDiffPatchIdOptions,
    Git2TypeOutDiffPatchIdOptions,
    Git2TypeConstPatch,
    Git2TypeOutPatch,
    Git2TypeConstDiffHunk,
    Git2TypeOutDiffHunk,
    Git2TypeConstDiffLine,
    Git2TypeOutDiffLine,
    Git2TypeConstMergeFileInput,
    Git2TypeOutMergeFileInput,
    Git2TypeConstOidArray,
    Git2TypeOutOidArray,
    Git2TypeConstMergeFileOptions,
    Git2TypeOutMergeFileOptions,
    Git2TypeConstMergeOptions,
    Git2TypeOutMergeOptions,
]


def get_jtype_raw(c_type: str) -> str:
    s = c_type.strip()
    if 'void' == s:
        return 'void'
    m = RAW_PAT.match(s)
    if not m:
        return ""
    return 'j{}'.format(m.group('type_name'))


def get_jtype_string(c_type: str) -> str:
    m = STRING_PAT.match(c_type)
    if not m:
        return ""
    return "jstring"


def get_jtype_int(c_type: str) -> str:
    m = INT_ISH_TYPE.match(c_type)
    if not m:
        return ""
    return 'jint'


def get_jtype_long(c_type: str) -> str:
    m = LONG_ISH_TYPE.match(c_type)
    if not m:
        return ""
    return 'jlong'


def get_jtype(c_type: str) -> str:
    """
    return jni type of c types, e.g:
    int -> jint
    'const char *' -> jstring
    'void' -> 'void'
    """
    s = c_type.strip()
    return get_jtype_raw(s) or get_jtype_string(s) or get_jtype_int(s) or get_jtype_long(s)


def get_java_type(j_type: str) -> str:
    """
    return native java type of a jni type:
    jint -> int
    jstring -> String
    """
    return JNI_JAVA_TYPE_MAP[j_type]


def get_return_assign(return_type: str) -> str:
    """
    int foo() => 'int r ='
    void foo() => ''
    """
    s = return_type.strip()
    if 'void' == s:
        return ''
    return '{} r ='.format(s)


def get_return_var(return_type: str) -> str:
    """
    int foo() => return r;
    void foo() => ''
    """
    s = return_type.strip()
    if 'void' == s:
        return ''
    return 'return r;'


def get_git2_param(param: str) -> 'Git2Type':
    for p in GIT2_PARAM_PARSERS:
        t = p.parse(param)
        if t:
            return t
    raise NotImplementedError(f"no matching type found for '{param}'")


def get_c_wrapper_param_list(param_list: List['Git2Type']) -> str:
    """
    from: git_oid *out, git_index *index, git_repository *repo
    to: jobject outOid, jlong indexPtr, jlong repoPtr
    """
    params = [p.c_header_param for p in param_list]
    return ', '.join([x for x in params if x])


def get_c_param_list(param_list: List['Git2Type']) -> str:
    """
    from: git_oid *out, git_index *index, git_repository *repo
    to: &c_oid, (git_index *)indexPtr, (git_repository *)repoPtr
    """
    params = [p.c_wrapper_param for p in param_list]
    return ', '.join([x for x in params if x])


def get_c_wrapper_before_list(param_list: List['Git2Type']) -> str:
    """
    from: git_oid *out, git_index *index, git_repository *repo
    to: 'git_oid c_oid;'
    """
    return ''.join([p.c_wrapper_before for p in param_list])


def get_c_wrapper_after_list(param_list: List['Git2Type']) -> str:
    """
    from: git_oid *out, git_index *index, git_repository *repo
    to: 'git_oid c_oid;'
    """
    return ''.join([p.c_wrapper_after for p in param_list])


def get_jni_param_list(param_list: List['Git2Type']) -> str:
    """
    from: git_oid *out, git_index *index, git_repository *repo
    to: Oid out, long indexPtr, long repoPtr
    """
    params = [p.jni_param for p in param_list]
    return ', '.join([x for x in params if x])
