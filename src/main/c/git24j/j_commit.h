#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_COMMIT_H__
#define __GIT24J_COMMIT_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /**int git_commit_lookup(git_commit **commit, git_repository *repo, const git_oid *id); */
    /**int git_commit_lookup_prefix(git_commit **commit, git_repository *repo, const git_oid *id, size_t len); */
    /**void git_commit_free(git_commit *commit); */
    /**const git_oid * git_commit_id(const git_commit *commit); */
    /**git_repository * git_commit_owner(const git_commit *commit); */
    /*<-- Above methods already implemented in j_gitobject.h -->*/

    /**const char * git_commit_message_encoding(const git_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessageEncoding)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**const char * git_commit_message(const git_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessage)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**const char * git_commit_message_raw(const git_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessageRaw)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**const char * git_commit_summary(git_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniSummary)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**const char * git_commit_body(git_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniBody)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**git_time_t git_commit_time(const git_commit *commit); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Commit_jniTime)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**int git_commit_time_offset(const git_commit *commit); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniTimeOffset)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**const git_signature * git_commit_committer(const git_commit *commit); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Commit_jniCommitter)(JNIEnv *env, jclass obj, jlong commitPtr, jobject outSig);
    /**const git_signature * git_commit_author(const git_commit *commit); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Commit_jniAuthor)(JNIEnv *env, jclass obj, jlong commitPtr, jobject outSig);

    /**int git_commit_committer_with_mailmap(git_signature **out, const git_commit *commit, const git_mailmap *mailmap); */
    /**int git_commit_author_with_mailmap(git_signature **out, const git_commit *commit, const git_mailmap *mailmap); */
    /**const char * git_commit_raw_header(const git_commit *commit); */
    /**int git_commit_tree(git_tree **tree_out, const git_commit *commit); */
    /**const git_oid * git_commit_tree_id(const git_commit *commit); */
    /**unsigned int git_commit_parentcount(const git_commit *commit); */
    /**int git_commit_parent(git_commit **out, const git_commit *commit, unsigned int n); */
    /**const git_oid * git_commit_parent_id(const git_commit *commit, unsigned int n); */
    /**int git_commit_nth_gen_ancestor(git_commit **ancestor, const git_commit *commit, unsigned int n); */
    /**int git_commit_header_field(git_buf *out, const git_commit *commit, const char *field); */
    /**int git_commit_extract_signature(git_buf *signature, git_buf *signed_data, git_repository *repo, git_oid *commit_id, const char *field); */
    /**int git_commit_create(git_oid *id, git_repository *repo, const char *update_ref, const git_signature *author, const git_signature *committer, const char *message_encoding, const char *message, const git_tree *tree, size_t parent_count, const git_commit *[] parents); */
    /**int git_commit_create_v(git_oid *id, git_repository *repo, const char *update_ref, const git_signature *author, const git_signature *committer, const char *message_encoding, const char *message, const git_tree *tree, size_t parent_count); */
    /**int git_commit_amend(git_oid *id, const git_commit *commit_to_amend, const char *update_ref, const git_signature *author, const git_signature *committer, const char *message_encoding, const char *message, const git_tree *tree); */
    /**int git_commit_create_buffer(git_buf *out, git_repository *repo, const git_signature *author, const git_signature *committer, const char *message_encoding, const char *message, const git_tree *tree, size_t parent_count, const git_commit *[] parents); */
    /**int git_commit_create_with_signature(git_oid *out, git_repository *repo, const char *commit_content, const char *signature, const char *signature_field); */
    /**int git_commit_dup(git_commit **out, git_commit *source); */
    /**int git_commit_signing_cb(git_buf *signature, git_buf *signature_field, const char *commit_content, void *payload); */

#ifdef __cplusplus
}
#endif
#endif