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
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputFree)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** int git_merge_file_init_options(git_merge_file_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** int git_merge_init_options(git_merge_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsFree)(JNIEnv *env, jclass obj, jlong opts);

    /** int git_merge_base(git_oid *out, git_repository *repo, const git_oid *one, const git_oid *two); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBase)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jobject one, jobject two);

    /** int git_merge_bases(git_oidarray *out, git_repository *repo, const git_oid *one, const git_oid *two); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBases)(JNIEnv *env, jclass obj, jobject outOids, jlong repoPtr, jobject one, jobject two);

    /** int git_merge_trees(git_index **out, git_repository *repo, const git_tree *ancestor_tree, const git_tree *our_tree, const git_tree *their_tree, const git_merge_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniTrees)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ancestorTreePtr, jlong ourTreePtr, jlong theirTreePtr, jlong optsPtr);

    /** int git_merge_commits(git_index **out, git_repository *repo, const git_commit *our_commit, const git_commit *their_commit, const git_merge_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniCommits)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ourCommitPtr, jlong theirCommitPtr, jlong optsPtr);

    /** int git_merge_analysis(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniAnalysis)(JNIEnv *env, jclass obj, jobject analysisOut, jobject preferenceOut, jlong repoPtr, jlongArray theirHeads);

    /** int git_merge_analysis_for_ref(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, git_reference *our_ref, const git_annotated_commit **their_heads, size_t their_heads_len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniAnalysisForRef)(JNIEnv *env, jclass obj, jobject analysisOut, jobject preferenceOut, jlong repoPtr, jlong ourRefPtr, jlongArray theirHeads);

    /** int git_merge_base_many(git_oid *out, git_repository *repo, size_t length, const git_oid [] input_array); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBaseMany)(JNIEnv *env, jclass obj, jobject outOid, jlong repoPtr, jobjectArray inputArray);

    /** int git_merge_bases_many(git_oidarray *out, git_repository *repo, size_t length, const git_oid [] input_array); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBasesMany)(JNIEnv *env, jclass obj, jobject outOids, jlong repoPtr, jobjectArray inputArray);

    /** int git_merge_base_octopus(git_oid *out, git_repository *repo, size_t length, const git_oid [] input_array); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBaseOctopus)(JNIEnv *env, jclass obj, jobject outOid, jlong repoPtr, jobjectArray inputArray);
    // no matching type found for 'git_merge_file_result *out'
    /** int git_merge_file(git_merge_file_result *out, const git_merge_file_input *ancestor, const git_merge_file_input *ours, const git_merge_file_input *theirs, const git_merge_file_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFile)(JNIEnv *env, jclass obj, jobject out, jlong ancestorPtr, jlong oursPtr, jlong theirsPtr, jlong optsPtr);
    // no matching type found for 'git_merge_file_result *out'
    /** int git_merge_file_from_index(
     *      git_merge_file_result *out, 
     *      git_repository *repo,
     *      const git_index_entry *ancestor, 
     *      const git_index_entry *ours,
     *      const git_index_entry *theirs,
     *      const git_merge_file_options *opts); 
     * */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileFromIndex)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ancestorPtr, jlong oursPtr, jlong theirsPtr, jlong optsPtr);

    /** void git_merge_file_result_free(git_merge_file_result *result); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileResultFree)(JNIEnv *env, jclass obj, jlong resultPtr);

    /** int git_merge_create(git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len, const git_merge_options *merge_opts, const git_checkout_options *checkout_opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniMerge)(JNIEnv *env, jclass obj, jlong repoPtr, jlongArray theirHeads, jlong mergeOptsPtr, jlong checkoutOpts);

#ifdef __cplusplus
}
#endif
#endif