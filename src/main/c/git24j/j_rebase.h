#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_REBASE_H__
#define __GIT24J_REBASE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- Signature of the header ---------- */
    /** int git_rebase_init_options(git_rebase_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** int git_rebase_init(git_rebase **out, git_repository *repo, const git_annotated_commit *branch, const git_annotated_commit *upstream, const git_annotated_commit *onto, const git_rebase_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInit)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong branchPtr, jlong upstreamPtr, jlong ontoPtr, jlong optsPtr);

    /** int git_rebase_open(git_rebase **out, git_repository *repo, const git_rebase_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOpen)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr);

    /** size_t git_rebase_operation_entrycount(git_rebase *rebase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationEntrycount)(JNIEnv *env, jclass obj, jlong rebasePtr);

    /** size_t git_rebase_operation_current(git_rebase *rebase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationCurrent)(JNIEnv *env, jclass obj, jlong rebasePtr);

    /** git_rebase_operation * git_rebase_operation_byindex(git_rebase *rebase, size_t idx); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Rebase_jniOperationByindex)(JNIEnv *env, jclass obj, jlong rebasePtr, jint idx);

    /** int git_rebase_next(git_rebase_operation **operation, git_rebase *rebase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniNext)(JNIEnv *env, jclass obj, jobject operation, jlong rebasePtr);

    /** int git_rebase_inmemory_index(git_index **index, git_rebase *rebase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInmemoryIndex)(JNIEnv *env, jclass obj, jobject index, jlong rebasePtr);

    /** int git_rebase_commit(git_oid *id, git_rebase *rebase, const git_signature *author, const git_signature *committer, const char *message_encoding, const char *message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniCommit)(JNIEnv *env, jclass obj, jobject id, jlong rebasePtr, jlong authorPtr, jlong committerPtr, jstring message_encoding, jstring message);

    /** int git_rebase_abort(git_rebase *rebase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniAbort)(JNIEnv *env, jclass obj, jlong rebasePtr);

    /** int git_rebase_finish(git_rebase *rebase, const git_signature *signature); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniFinish)(JNIEnv *env, jclass obj, jlong rebasePtr, jlong signaturePtr);

    /** void git_rebase_free(git_rebase *rebase); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniFree)(JNIEnv *env, jclass obj, jlong rebasePtr);

#ifdef __cplusplus
}
#endif
#endif