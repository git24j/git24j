#include "j_commit.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_exception.h"
#include "j_mappers.h"
#include "j_util.h"
#include <git2.h>
#include <stdio.h>

/**int git_commit_lookup(git_commit **commit, git_repository *repo, const git_oid *id); */
/**int git_commit_lookup_prefix(git_commit **commit, git_repository *repo, const git_oid *id, size_t len); */
/**void git_commit_free(git_commit *commit); */
/**const git_oid * git_commit_id(const git_commit *commit); */
/**git_repository * git_commit_owner(const git_commit *commit); */

/**const char * git_commit_message_encoding(const git_commit *commit); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessageEncoding)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *encoding = git_commit_message_encoding((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, encoding);
}

/**const char * git_commit_message(const git_commit *commit); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessage)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *message = git_commit_message((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, message);
}

/**const char * git_commit_message_raw(const git_commit *commit); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessageRaw)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *msg_raw = git_commit_message_raw((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, msg_raw);
}

/**const char * git_commit_summary(git_commit *commit); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniSummary)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *summary = git_commit_summary((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, summary);
}

/**const char * git_commit_body(git_commit *commit); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniBody)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *body = git_commit_body((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, body);
}

/**git_time_t git_commit_time(const git_commit *commit); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Commit_jniTime)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    return (jlong)git_commit_time((git_commit *)commitPtr);
}

/**int git_commit_time_offset(const git_commit *commit); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniTimeOffset)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    return (jint)git_commit_time_offset((git_commit *)commitPtr);
}

/**const git_signature * git_commit_committer(const git_commit *commit); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Commit_jniCommitter)(JNIEnv *env, jclass obj, jlong commitPtr, jobject outSig)
{
    const git_signature *c_sig = git_commit_committer((git_commit *)commitPtr);
    j_signature_to_java(env, c_sig, outSig);
}
/**const git_signature * git_commit_author(const git_commit *commit); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Commit_jniAuthor)(JNIEnv *env, jclass obj, jlong commitPtr, jobject outSig)
{
    const git_signature *c_sig = git_commit_author((git_commit *)commitPtr);
    j_signature_to_java(env, c_sig, outSig);
}

/**int git_commit_committer_with_mailmap(git_signature **out, const git_commit *commit, const git_mailmap *mailmap); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCommitterWithMailmap)(JNIEnv *env, jclass obj, jobject outSig, jlong commitPtr, jlong mailmapPtr)
{
    git_signature *c_sig;
    int e = git_commit_committer_with_mailmap(&c_sig, (git_commit *)commitPtr, (git_mailmap *)mailmapPtr);
    j_signature_to_java(env, c_sig, outSig);
    git_signature_free(c_sig);
    return e;
}

/**int git_commit_author_with_mailmap(git_signature **out, const git_commit *commit, const git_mailmap *mailmap); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniAuthorWithMailmap)(JNIEnv *env, jclass obj, jobject outSig, jlong commitPtr, jlong mailmapPtr)
{
    git_signature *c_sig;
    int e = git_commit_author_with_mailmap(&c_sig, (git_commit *)commitPtr, (git_mailmap *)mailmapPtr);
    j_signature_to_java(env, c_sig, outSig);
    git_signature_free(c_sig);
    return e;
}
