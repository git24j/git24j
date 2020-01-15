#include "j_commit.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_exception.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
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

/**const char * git_commit_raw_header(const git_commit *commit); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniRawHeader)(JNIEnv *env, jclass obj, jlong commitPtr)
{
    const char *raw_header = git_commit_raw_header((git_commit *)commitPtr);
    return (*env)->NewStringUTF(env, raw_header);
}

/**int git_commit_tree(git_tree **tree_out, const git_commit *commit); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniTree)(JNIEnv *env, jclass obj, jobject outTreePtr, jlong commitPtr)
{
    git_tree *tree;
    int e = git_commit_tree(&tree, (git_commit *)commitPtr);
    j_save_c_pointer(env, (void *)tree, outTreePtr, "set");
    return e;
}

/**const git_oid * git_commit_tree_id(const git_commit *commit); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Commit_jniTreeId)(JNIEnv *env, jclass obj, jobject outOid, jlong commitPtr)
{
    const git_oid *c_oid = git_commit_tree_id((git_commit *)commitPtr);
    j_git_oid_to_java(env, c_oid, outOid);
}

/**unsigned int git_commit_parentcount(const git_commit *commit); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniParentCount)(JNIEnv *env, jclass obj, long commitPtr)
{
    return (jint)git_commit_parentcount((git_commit *)commitPtr);
}

/**int git_commit_parent(git_commit **out, const git_commit *commit, unsigned int n); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniParent)(JNIEnv *env, jclass obj, jobject outPtr, long commitPtr, jint n)
{
    git_commit *c_out;
    int e = git_commit_parent(&c_out, (git_commit *)commitPtr, (unsigned int)n);
    j_save_c_pointer(env, (void *)c_out, outPtr, "set");
    return e;
}

/**const git_oid * git_commit_parent_id(const git_commit *commit, unsigned int n); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Commit_jniParentId)(JNIEnv *env, jclass obj, jobject outOid, jlong commitPtr, jint n)
{
    const git_oid *c_oid = git_commit_parent_id((git_commit *)commitPtr, (unsigned int)n);
    j_git_oid_to_java(env, c_oid, outOid);
}

/**int git_commit_nth_gen_ancestor(git_commit **ancestor, const git_commit *commit, unsigned int n); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniNthGenAncestor)(JNIEnv *env, jclass obj, jobject outPtr, jlong commitPtr, jint n)
{
    git_commit *ancestor;
    int e = git_commit_nth_gen_ancestor(&ancestor, (git_commit *)commitPtr, (unsigned int)n);
    j_save_c_pointer(env, (void *)ancestor, outPtr, "set");
    return e;
}

/**int git_commit_header_field(git_buf *out, const git_commit *commit, const char *field); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniHeaderField)(JNIEnv *env, jclass obj, jobject outBuf, jlong commitPtr, jstring field)
{
    git_buf c_buf = {0};
    char *c_field = j_copy_of_jstring(env, field, true);
    int e = git_commit_header_field(&c_buf, (git_commit *)commitPtr, c_field);
    j_git_buf_to_java(env, &c_buf, outBuf);
    git_buf_dispose(&c_buf);
    free(c_field);
    return e;
}

/**
 * int git_commit_create(git_oid *id, 
 *                       git_repository *repo, 
 *                       const char *update_ref, 
 *                       const git_signature *author, 
 *                       const git_signature *committer,
 *                       const char *message_encoding,
 *                       const char *message,
 *                       const git_tree *tree,
 *                       size_t parent_count,
 *                       const git_commit *[] parents);
 *  */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreate)(JNIEnv *env,
                                                       jclass obj,
                                                       jobject outOid,
                                                       jlong repoPtr,
                                                       jstring updateRef,
                                                       jobject author,
                                                       jobject committer,
                                                       jstring msgEncoding,
                                                       jstring message,
                                                       jlong treePtr,
                                                       jlongArray parents)
{
    assert(parents && "parents must not be null");
    int e = 0;
    git_signature *c_author, *c_committer;
    e = j_signature_from_java(env, author, &c_author);
    if (e != 0)
    {
        git_signature_free(c_author);
        return e;
    }

    j_signature_from_java(env, committer, &c_committer);
    if (e != 0)
    {
        git_signature_free(c_committer);
        return e;
    }

    jsize np = (*env)->GetArrayLength(env, parents);
    const git_commit **c_parents = (const git_commit **)malloc(sizeof(git_commit *) * np);
    for (jsize i = 0; i < np; i++)
    {
        jlong *x = (*env)->GetLongArrayElements(env, parents, 0);
        c_parents[i] = (git_commit *)(*x);
    }
    char *update_ref = j_copy_of_jstring(env, updateRef, true);
    char *message_encoding = j_copy_of_jstring(env, msgEncoding, true);
    char *c_message = j_copy_of_jstring(env, message, true);
    git_oid c_oid;
    e = git_commit_create(&c_oid,
                          (git_repository *)repoPtr,
                          update_ref,
                          c_author,
                          c_committer,
                          message_encoding,
                          c_message,
                          (git_tree *)treePtr,
                          np,
                          c_parents);
    j_git_oid_to_java(env, &c_oid, outOid);
    free(c_parents);
    free(c_message);
    free(message_encoding);
    free(update_ref);
    git_signature_free(c_committer);
    return e;
}

/**int git_commit_amend(git_oid *id, 
 *                      const git_commit *commit_to_amend, 
 *                      const char *update_ref, 
 *                      const git_signature *author, 
 *                      const git_signature *committer, 
 *                      const char *message_encoding, 
 *                      const char *message, 
 *                      const git_tree *tree); 
 * */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniAmend)(JNIEnv *env, jclass obj,
                                                      jobject outOid,
                                                      jlong commitToAmend,
                                                      jstring updateRef,
                                                      jobject author,
                                                      jobject committer,
                                                      jstring messageEncoding,
                                                      jstring message,
                                                      jlong treePtr)
{
    int e = 0;
    git_signature *c_author = NULL, *c_committer = NULL;
    e = j_signature_from_java(env, author, &c_author);
    if (e != 0)
    {
        git_signature_free(c_author);
        return e;
    }
    e = j_signature_from_java(env, committer, &c_committer);
    if (e != 0)
    {
        git_signature_free(c_committer);
        return e;
    }

    git_oid c_oid;
    char *update_ref = j_copy_of_jstring(env, updateRef, true);
    char *message_encoding = j_copy_of_jstring(env, messageEncoding, true);
    char *c_message = j_copy_of_jstring(env, message, true);
    e = git_commit_amend(
        &c_oid,
        (git_commit *)commitToAmend,
        update_ref,
        c_author,
        c_committer,
        message_encoding,
        c_message,
        (git_tree *)treePtr);
    j_git_oid_to_java(env, &c_oid, outOid);
    free(c_message);
    free(message_encoding);
    free(update_ref);
    git_signature_free(c_author);
    git_signature_free(c_committer);
    return e;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreateBuffer)(JNIEnv *env, jclass obj,
                                                             jobject outBuf,
                                                             jlong repoPtr,
                                                             jobject author,
                                                             jobject committer,
                                                             jstring messageEncodeing,
                                                             jstring message,
                                                             jlong treePtr,
                                                             jlongArray parents)
{
    assert(parents && "parents must not be null");
    int e = 0;
    git_signature *c_author, *c_committer;
    e = j_signature_from_java(env, author, &c_author);
    if (e != 0)
    {
        git_signature_free(c_author);
        return e;
    }

    j_signature_from_java(env, committer, &c_committer);
    if (e != 0)
    {
        git_signature_free(c_committer);
        return e;
    }

    jsize np = (*env)->GetArrayLength(env, parents);
    const git_commit **c_parents = (const git_commit **)malloc(sizeof(git_commit *) * np);
    for (jsize i = 0; i < np; i++)
    {
        jlong *x = (*env)->GetLongArrayElements(env, parents, 0);
        c_parents[i] = (git_commit *)(*x);
    }
    char *message_encoding = j_copy_of_jstring(env, messageEncodeing, true);
    char *c_message = j_copy_of_jstring(env, message, true);
    git_buf c_buf = {0};
    e = git_commit_create_buffer(&c_buf,
                                 (git_repository *)repoPtr,
                                 c_author,
                                 c_committer,
                                 message_encoding,
                                 c_message,
                                 (git_tree *)treePtr,
                                 np,
                                 c_parents);
    j_git_buf_to_java(env, &c_buf, outBuf);
    git_buf_dispose(&c_buf);
    free(c_parents);
    free(c_message);
    free(message_encoding);
    git_signature_free(c_committer);
    return e;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreateWithSignature)(JNIEnv *env, jclass obj,
                                                                    jobject outOid,
                                                                    jlong repoPtr,
                                                                    jstring commitContent,
                                                                    jstring signature,
                                                                    jstring signatureField)
{
    git_oid c_oid;
    char *commit_content = j_copy_of_jstring(env, commitContent, true);
    char *c_signature = j_copy_of_jstring(env, signature, true);
    char *signature_field = j_copy_of_jstring(env, signatureField, true);
    int e = git_commit_create_with_signature(
        &c_oid,
        (git_repository *)repoPtr,
        commit_content,
        c_signature,
        signature_field);
    free(signature_field);
    free(c_signature);
    free(commit_content);
    j_git_oid_to_java(env, &c_oid, outOid);
    return e;
}
