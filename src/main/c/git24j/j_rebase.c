#include "j_rebase.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

/** -------- Wrapper Body ---------- */
/** int git_rebase_init_options(git_rebase_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_rebase_init_options((git_rebase_options *)optsPtr, version);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_rebase_options *opts = (git_rebase_options *)malloc(sizeof(git_rebase_options));
    int r = git_rebase_init_options(opts, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (long)opts);
    return r;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    free((git_rebase_options *)optsPtr);
}

/** int git_rebase_init(git_rebase **out, git_repository *repo, const git_annotated_commit *branch, const git_annotated_commit *upstream, const git_annotated_commit *onto, const git_rebase_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInit)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong branchPtr, jlong upstreamPtr, jlong ontoPtr, jlong optsPtr)
{
    git_rebase *c_out;
    int r = git_rebase_init(&c_out, (git_repository *)repoPtr, (git_annotated_commit *)branchPtr, (git_annotated_commit *)upstreamPtr, (git_annotated_commit *)ontoPtr, (git_rebase_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    return r;
}

/** int git_rebase_open(git_rebase **out, git_repository *repo, const git_rebase_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOpen)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr)
{
    git_rebase *c_out;
    int r = git_rebase_open(&c_out, (git_repository *)repoPtr, (git_rebase_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    return r;
}

/** size_t git_rebase_operation_entrycount(git_rebase *rebase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationEntrycount)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    size_t r = git_rebase_operation_entrycount((git_rebase *)rebasePtr);
    return r;
}

/** size_t git_rebase_operation_current(git_rebase *rebase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationCurrent)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    size_t r = git_rebase_operation_current((git_rebase *)rebasePtr);
    return r;
}

/** git_rebase_operation * git_rebase_operation_byindex(git_rebase *rebase, size_t idx); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Rebase_jniOperationByindex)(JNIEnv *env, jclass obj, jlong rebasePtr, jint idx)
{
    git_rebase_operation *r = git_rebase_operation_byindex((git_rebase *)rebasePtr, idx);
    return (jlong)r;
}

/** int git_rebase_next(git_rebase_operation **operation, git_rebase *rebase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniNext)(JNIEnv *env, jclass obj, jobject operation, jlong rebasePtr)
{
    git_rebase_operation *c_operation;
    int r = git_rebase_next(&c_operation, (git_rebase *)rebasePtr);
    (*env)->CallVoidMethod(env, operation, jniConstants->midAtomicLongSet, (long)c_operation);
    return r;
}

/** int git_rebase_inmemory_index(git_index **index, git_rebase *rebase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInmemoryIndex)(JNIEnv *env, jclass obj, jobject index, jlong rebasePtr)
{
    git_index *c_index;
    int r = git_rebase_inmemory_index(&c_index, (git_rebase *)rebasePtr);
    (*env)->CallVoidMethod(env, index, jniConstants->midAtomicLongSet, (long)c_index);
    return r;
}

/** int git_rebase_commit(git_oid *id, git_rebase *rebase, const git_signature *author, const git_signature *committer, const char *message_encoding, const char *message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniCommit)(JNIEnv *env, jclass obj, jobject id, jlong rebasePtr, jlong authorPtr, jlong committerPtr, jstring message_encoding, jstring message)
{
    git_oid c_id;
    char *c_message_encoding = j_copy_of_jstring(env, message_encoding, true);
    char *c_message = j_copy_of_jstring(env, message, true);
    int r = git_rebase_commit(&c_id, (git_rebase *)rebasePtr, (git_signature *)authorPtr, (git_signature *)committerPtr, c_message_encoding, c_message);
    j_git_oid_to_java(env, &c_id, id);
    free(c_message_encoding);
    free(c_message);
    return r;
}

/** int git_rebase_abort(git_rebase *rebase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniAbort)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    int r = git_rebase_abort((git_rebase *)rebasePtr);
    return r;
}

/** int git_rebase_finish(git_rebase *rebase, const git_signature *signature); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniFinish)(JNIEnv *env, jclass obj, jlong rebasePtr, jlong signaturePtr)
{
    int r = git_rebase_finish((git_rebase *)rebasePtr, (git_signature *)signaturePtr);
    return r;
}

/** void git_rebase_free(git_rebase *rebase); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniFree)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    git_rebase_free((git_rebase *)rebasePtr);
}
