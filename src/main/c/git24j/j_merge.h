#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_MERGE_H__
#define __GIT24J_MERGE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_merge_file_init_input(git_merge_file_input *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitInput)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);

    /** int git_merge_file_init_options(git_merge_file_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);

    /** int git_merge_init_options(git_merge_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);

    /** int git_merge_base(git_oid *out, git_repository *repo, const git_oid *one, const git_oid *two); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBase)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jobject one, jobject two);

    /** int git_merge_bases(git_oidarray *out, git_repository *repo, const git_oid *one, const git_oid *two); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBases)(JNIEnv *env, jclass obj, jlong outPtr, jlong repoPtr, jobject one, jobject two);

    /** int git_merge_trees(git_index **out, git_repository *repo, const git_tree *ancestor_tree, const git_tree *our_tree, const git_tree *their_tree, const git_merge_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniTrees)(JNIEnv *env, jclass obj, jlong outPtr, jlong repoPtr, jlong ancestorTreePtr, jlong ourTreePtr, jlong theirTreePtr, jlong optsPtr);

    /** int git_merge_commits(git_index **out, git_repository *repo, const git_commit *our_commit, const git_commit *their_commit, const git_merge_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniCommits)(JNIEnv *env, jclass obj, jlong outPtr, jlong repoPtr, jlong ourCommitPtr, jlong theirCommitPtr, jlong optsPtr);

    // no matching type found for 'git_merge_analysis_t *analysis_out'
    /** int git_merge_analysis(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len); */

    // no matching type found for 'git_merge_analysis_t *analysis_out'
    /** int git_merge_analysis_for_ref(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, git_reference *our_ref, const git_annotated_commit **their_heads, size_t their_heads_len); */
    // no matching type found for 'const git_oid [] input_array'
    /** int git_merge_base_many(git_oid *out, git_repository *repo, size_t length, const git_oid [] input_array); */
    // no matching type found for 'const git_oid [] input_array'
    /** int git_merge_bases_many(git_oidarray *out, git_repository *repo, size_t length, const git_oid [] input_array); */
    // no matching type found for 'const git_oid [] input_array'
    /** int git_merge_base_octopus(git_oid *out, git_repository *repo, size_t length, const git_oid [] input_array); */
    // no matching type found for 'git_merge_file_result *out'
    /** int git_merge_file(git_merge_file_result *out, const git_merge_file_input *ancestor, const git_merge_file_input *ours, const git_merge_file_input *theirs, const git_merge_file_options *opts); */
    // no matching type found for 'git_merge_file_result *out'
    /** int git_merge_file_from_index(git_merge_file_result *out, git_repository *repo, const git_index_entry *ancestor, const git_index_entry *ours, const git_index_entry *theirs, const git_merge_file_options *opts); */
    // no matching type found for 'git_merge_file_result *result'
    /** void git_merge_file_result_free(git_merge_file_result *result); */
    // no matching type found for 'const git_annotated_commit **their_heads'
    /** int git_merge_create(git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len, const git_merge_options *merge_opts, const git_checkout_options *checkout_opts); */

#ifdef __cplusplus
}
#endif
#endif