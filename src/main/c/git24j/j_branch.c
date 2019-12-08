#include "j_branch.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_exception.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jlong targetPtr, jint force)
{
    git_reference *c_ref;
    char *c_branch_name = j_copy_of_jstring(env, branchName, false);
    int error = git_branch_create(&c_ref, (git_repository *)repoPtr, c_branch_name, (const git_commit *)targetPtr, force);
    free(c_branch_name);
    j_save_c_pointer(env, (void *)c_ref, outRef, "set");
    return error;
}

/**int git_branch_create_from_annotated(git_reference **ref_out, git_repository *repository, const char *branch_name, const git_annotated_commit *commit, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniCreateFromAnnotated)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jlong annoCommitPtr, jint force)
{
    git_reference *c_ref;
    char *branch_name = j_copy_of_jstring(env, branchName, false);
    int e = git_branch_create_from_annotated(&c_ref, (git_repository *)repoPtr, branch_name, (git_annotated_commit *)annoCommitPtr, force);
    j_save_c_pointer(env, (void *)c_ref, outRef, "set");
    free(branch_name);
    return e;
}

/**int git_branch_delete(git_reference *branch); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniDelete)(JNIEnv *env, jclass obj, jlong refPtr)
{
    return git_branch_delete((git_reference *)refPtr);
}

/**int git_branch_iterator_new(git_branch_iterator **out, git_repository *repo, git_branch_t list_flags); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outBranchIter, jlong repoPtr, jint listFlags)
{
    git_branch_iterator *c_branch_iter;
    int e = git_branch_iterator_new(&c_branch_iter, (git_repository *)repoPtr, (git_branch_t)listFlags);
    j_save_c_pointer(env, (void *)c_branch_iter, outBranchIter, "set");
    return e;
}

/**int git_branch_next(git_reference **out, git_branch_t *out_type, git_branch_iterator *iter); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniNext)(JNIEnv *env, jclass obj, jobject outRef, jobject outType, jlong branchIterPtr)
{
    git_reference *c_out_ref;
    git_branch_t c_out_branch_type;
    int e = git_branch_next(&c_out_ref, &c_out_branch_type, (git_branch_iterator *)branchIterPtr);
    jclass jclz = (*env)->GetObjectClass(env, outType);
    assert(jclz && "Could not find outType class from given outType object");
    j_save_c_pointer(env, (void *)c_out_ref, outRef, "set");
    j_call_setter_int(env, NULL, outType, "set", (jint)c_out_branch_type);
    (*env)->DeleteLocalRef(env, jclz);
    return e;
}

/**void git_branch_iterator_free(git_branch_iterator *iter); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Branch_jniIteratorFree)(JNIEnv *env, jclass obj, jlong branchIterPtr)
{
    git_branch_iterator_free((git_branch_iterator *)branchIterPtr);
}

/**int git_branch_move(git_reference **out, git_reference *branch, const char *new_branch_name, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniMove)(JNIEnv *env, jclass obj, jobject outRef, jlong branchPtr, jstring branchName, jint force)
{
    git_reference *c_out_ref;
    char *branch_name = j_copy_of_jstring(env, branchName, false);
    int e = git_branch_move(&c_out_ref, (git_reference *)branchPtr, branch_name, force);
    free(branch_name);
    return e;
}
/**int git_branch_lookup(git_reference **out, git_repository *repo, const char *branch_name, git_branch_t branch_type); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniLookup)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jint branchType)
{
    git_reference *c_out_ref;
    char *branch_name = j_copy_of_jstring(env, branchName, false);
    int e = git_branch_lookup(&c_out_ref, (git_repository *)repoPtr, branch_name, (git_branch_t)branchType);
    free(branch_name);
    return e;
}

/**int git_branch_name(const char **out, const git_reference *ref); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniName)(JNIEnv *env, jclass obj, jobject outStr, jlong refPtr)
{
    const char *out_name;
    jclass jclz = (*env)->GetObjectClass(env, outStr);
    assert(jclz && "Could not find AtomicReference class from given outStr object");
    int e = git_branch_name(&out_name, (git_reference *)refPtr);
    j_call_setter_string_c(env, jclz, outStr, "set", out_name);
    (*env)->DeleteLocalRef(env, jclz);
    return e;
}
/**int git_branch_upstream(git_reference **out, const git_reference *branch); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstream)(JNIEnv *env, jclass obj, jobject outRef, jlong branchPtr)
{
    git_reference *out_ref;
    int e = git_branch_upstream(&out_ref, (git_reference *)branchPtr);
    j_save_c_pointer(env, (void *)out_ref, outRef, "set");
    return e;
}
/**int git_branch_set_upstream(git_reference *branch, const char *upstream_name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniSetUpstream)(JNIEnv *env, jclass obj, jlong refPtr, jstring upstreamName)
{
    char *upstream_name = j_copy_of_jstring(env, upstreamName, false);
    int e = git_branch_set_upstream((git_reference *)refPtr, upstream_name);
    free(upstream_name);
    return e;
}

/**int git_branch_upstream_name(git_buf *out, git_repository *repo, const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstreamName)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring refName)
{
    git_buf c_buf = {0};
    char *c_refname = j_copy_of_jstring(env, refName, false);
    int e = git_branch_upstream_name(&c_buf, (git_repository *)repoPtr, c_refname);
    free(c_refname);
    j_git_buf_to_java(env, &c_buf, outBuf);
    git_buf_dispose(&c_buf);
    return e;
}
/**int git_branch_is_head(const git_reference *branch); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIsHead)(JNIEnv *env, jclass obj, jlong refPtr)
{
    return git_branch_is_head((git_reference *)refPtr);
}
/**int git_branch_is_checked_out(const git_reference *branch); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIsCheckedOut)(JNIEnv *env, jclass obj, jlong refPtr)
{
    return git_branch_is_checked_out((git_reference *)refPtr);
}
/**int git_branch_remote_name(git_buf *out, git_repository *repo, const char *canonical_branch_name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniRemoteName)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring canonicalBranchName)
{
    git_buf c_buf = {0};
    char *branch_name = j_copy_of_jstring(env, canonicalBranchName, false);
    int e = git_branch_remote_name(&c_buf, (git_repository *)repoPtr, branch_name);
    free(branch_name);
    j_git_buf_to_java(env, &c_buf, outBuf);
    git_buf_dispose(&c_buf);
    return e;
}
/**int git_branch_upstream_remote(git_buf *buf, git_repository *repo, const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstreamRemote)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring refName)
{
    char *ref_name = j_copy_of_jstring(env, refName, false);
    git_buf c_buf = {0};
    int e = git_branch_upstream_remote(&c_buf, (git_repository *)repoPtr, ref_name);
    j_git_buf_to_java(env, &c_buf, outBuf);
    git_buf_dispose(&c_buf);
    free(ref_name);
    return e;
}