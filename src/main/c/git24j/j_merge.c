#include "j_merge.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;
/** int git_merge_file_init_input(git_merge_file_input *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitInput)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_merge_file_init_input((git_merge_file_input *)optsPtr, version);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_merge_file_input *opts = (git_merge_file_input *)malloc(sizeof(git_merge_file_input));
    int r = git_merge_file_init_input(opts, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (long)opts);
    return r;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    free((git_merge_file_input *)optsPtr);
}

/** int git_merge_file_init_options(git_merge_file_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_merge_file_init_options((git_merge_file_options *)optsPtr, version);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_merge_file_options *opts = (git_merge_file_options *)malloc(sizeof(git_merge_file_options));
    int r = git_merge_file_init_options((git_merge_file_options *)opts, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (long)opts);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    free((git_merge_file_options *)optsPtr);
}

/** int git_merge_init_options(git_merge_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_merge_init_options((git_merge_options *)optsPtr, version);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_merge_options *opts = (git_merge_options *)malloc(sizeof(git_merge_options));
    int r = git_merge_init_options((git_merge_options *)opts, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (long)opts);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsFree)(JNIEnv *env, jclass obj, jlong opts)
{
    free((git_merge_options *)opts);
}

/** int git_merge_base(git_oid *out, git_repository *repo, const git_oid *one, const git_oid *two); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBase)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jobject one, jobject two)
{
    git_oid c_out;
    git_oid c_one;
    j_git_oid_from_java(env, one, &c_one);
    git_oid c_two;
    j_git_oid_from_java(env, two, &c_two);
    int r = git_merge_base(&c_out, (git_repository *)repoPtr, &c_one, &c_two);
    j_git_oid_to_java(env, &c_out, out);
    return r;
}

/** int git_merge_bases(git_oidarray *out, git_repository *repo, const git_oid *one, const git_oid *two); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBases)(JNIEnv *env, jclass obj, jobject outOids, jlong repoPtr, jobject one, jobject two)
{
    git_oid c_one;
    j_git_oid_from_java(env, one, &c_one);
    git_oid c_two;
    j_git_oid_from_java(env, two, &c_two);
    git_oidarray c_arr = {NULL, 0};
    int r = git_merge_bases(&c_arr, (git_repository *)repoPtr, &c_one, &c_two);
    j_git_oidarray_to_java(env, outOids, &c_arr);
    git_oidarray_free(&c_arr);
    return r;
}

/** int git_merge_trees(git_index **out, git_repository *repo, const git_tree *ancestor_tree, const git_tree *our_tree, const git_tree *their_tree, const git_merge_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniTrees)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ancestorTreePtr, jlong ourTreePtr, jlong theirTreePtr, jlong optsPtr)
{
    git_index *c_out;
    int r = git_merge_trees(&c_out, (git_repository *)repoPtr, (git_tree *)ancestorTreePtr, (git_tree *)ourTreePtr, (git_tree *)theirTreePtr, (git_merge_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_index_free(c_out);
    return r;
}

/** int git_merge_commits(git_index **out, git_repository *repo, const git_commit *our_commit, const git_commit *their_commit, const git_merge_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniCommits)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ourCommitPtr, jlong theirCommitPtr, jlong optsPtr)
{
    git_index *c_out;
    int r = git_merge_commits(&c_out, (git_repository *)repoPtr, (git_commit *)ourCommitPtr, (git_commit *)theirCommitPtr, (git_merge_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_index_free(c_out);
    return r;
}

/** int git_merge_analysis(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniAnalysis)(JNIEnv *env, jclass obj, jobject analysisOut, jobject preferenceOut, jlong repoPtr, jlongArray theirHeads)
{
    jsize theirHeadsLen = (*env)->GetArrayLength(env, theirHeads);
    jlong *elements = (*env)->GetLongArrayElements(env, theirHeads, 0);
    const git_annotated_commit **their_heads = (const git_annotated_commit **)malloc(sizeof(const git_annotated_commit *) * theirHeadsLen);
    for (jsize i = 0; i < theirHeadsLen; i++)
    {
        their_heads[i] = (const git_annotated_commit *)elements[i];
    }

    git_merge_analysis_t analysis_out;
    git_merge_preference_t preference_out;
    int r = git_merge_analysis(&analysis_out, &preference_out, (git_repository *)repoPtr, their_heads, theirHeadsLen);
    (*env)->CallVoidMethod(env, analysisOut, jniConstants->midAtomicIntSet, analysis_out);
    (*env)->CallVoidMethod(env, preferenceOut, jniConstants->midAtomicIntSet, preference_out);
    free(their_heads);
    (*env)->ReleaseLongArrayElements(env, theirHeads, elements, 0);
    return r;
}

/** int git_merge_analysis_for_ref(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, git_reference *our_ref, const git_annotated_commit **their_heads, size_t their_heads_len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniAnalysisForRef)(JNIEnv *env, jclass obj, jobject analysisOut, jobject preferenceOut, jlong repoPtr, jlong ourRefPtr, jlongArray theirHeads)
{
    jsize theirHeadsLen = (*env)->GetArrayLength(env, theirHeads);
    jlong *elemens = (*env)->GetLongArrayElements(env, theirHeads, 0);
    const git_annotated_commit **their_heads = (const git_annotated_commit **)malloc(sizeof(const git_annotated_commit *) * theirHeadsLen);
    for (jsize i = 0; i < theirHeadsLen; i++)
    {
        their_heads[i] = (const git_annotated_commit *)elemens[i];
    }
    git_merge_analysis_t analysis_out;
    git_merge_preference_t preference_out;
    int r = git_merge_analysis_for_ref(&analysis_out, &preference_out, (git_repository *)repoPtr, (git_reference *)ourRefPtr, their_heads, theirHeadsLen);
    (*env)->CallVoidMethod(env, analysisOut, jniConstants->midAtomicIntSet, analysis_out);
    (*env)->CallVoidMethod(env, preferenceOut, jniConstants->midAtomicIntSet, preference_out);
    free(their_heads);
    (*env)->ReleaseLongArrayElements(env, theirHeads, elemens, 0);
    return r;
}

/** int git_merge_base_many(git_oid *out, git_repository *repo, size_t length, const git_oid [] input_array); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBaseMany)(JNIEnv *env, jclass obj, jobject outOid, jlong repoPtr, jobjectArray inputArray)
{
    jsize arrayLen = (*env)->GetArrayLength(env, inputArray);
    git_oid *input_array = (git_oid *)malloc(sizeof(git_oid) * arrayLen);
    for (jsize i = 0; i < arrayLen; i++)
    {
        jobject xi = (*env)->GetObjectArrayElement(env, inputArray, i);
        j_git_oid_from_java(env, xi, &input_array[i]);
        (*env)->DeleteLocalRef(env, xi);
    }
    git_oid out_oid;
    int r = git_merge_base_many(&out_oid, (git_repository *)repoPtr, arrayLen, input_array);
    j_git_oid_to_java(env, &out_oid, outOid);
    free(input_array);
    return r;
}

/** int git_merge_bases_many(git_oidarray *out, git_repository *repo, size_t length, const git_oid [] input_array); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBasesMany)(JNIEnv *env, jclass obj, jobject outOids, jlong repoPtr, jobjectArray inputArray)
{
    jsize arrayLen = (*env)->GetArrayLength(env, inputArray);
    git_oid *input_array = (git_oid *)malloc(sizeof(git_oid) * arrayLen);
    for (jsize i = 0; i < arrayLen; i++)
    {
        jobject xi = (*env)->GetObjectArrayElement(env, inputArray, i);
        j_git_oid_from_java(env, xi, &input_array[i]);
        (*env)->DeleteLocalRef(env, xi);
    }
    git_oidarray c_out;
    int r = git_merge_bases_many(&c_out, (git_repository *)repoPtr, arrayLen, input_array);
    j_git_oidarray_to_java(env, outOids, &c_out);
    git_oidarray_free(&c_out);
    free(input_array);
    return r;
}

/** int git_merge_base_octopus(git_oid *out, git_repository *repo, size_t length, const git_oid [] input_array); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBaseOctopus)(JNIEnv *env, jclass obj, jobject outOid, jlong repoPtr, jobjectArray inputArray)
{
    jsize arrayLen = (*env)->GetArrayLength(env, inputArray);
    git_oid *input_array = (git_oid *)malloc(sizeof(git_oid) * arrayLen);
    for (jsize i = 0; i < arrayLen; i++)
    {
        jobject xi = (*env)->GetObjectArrayElement(env, inputArray, i);
        j_git_oid_from_java(env, xi, &input_array[i]);
        (*env)->DeleteLocalRef(env, xi);
    }
    git_oid out_oid;
    int r = git_merge_base_octopus(&out_oid, (git_repository *)repoPtr, arrayLen, input_array);
    j_git_oid_to_java(env, &out_oid, outOid);
    free(input_array);
    return r;
}

/** int git_merge_file(git_merge_file_result *out, const git_merge_file_input *ancestor, const git_merge_file_input *ours, const git_merge_file_input *theirs, const git_merge_file_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFile)(JNIEnv *env, jclass obj, jobject out, jlong ancestorPtr, jlong oursPtr, jlong theirsPtr, jlong optsPtr)
{
    git_merge_file_result *c_out = (git_merge_file_result *)malloc(sizeof(git_merge_file_result));
    int r = git_merge_file(
        c_out,
        (const git_merge_file_input *)ancestorPtr,
        (const git_merge_file_input *)oursPtr,
        (const git_merge_file_input *)theirsPtr,
        (const git_merge_file_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    return r;
}

/** int git_merge_file_from_index(
 *      git_merge_file_result *out, 
 *      git_repository *repo,
 *      const git_index_entry *ancestor, 
 *      const git_index_entry *ours,
 *      const git_index_entry *theirs,
 *      const git_merge_file_options *opts); 
 * */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileFromIndex)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ancestorPtr, jlong oursPtr, jlong theirsPtr, jlong optsPtr)
{
    git_merge_file_result *c_out = (git_merge_file_result *)malloc(sizeof(git_merge_file_result));
    int r = git_merge_file_from_index(
        c_out,
        (git_repository *)repoPtr,
        (const git_index_entry *)ancestorPtr,
        (const git_index_entry *)oursPtr,
        (const git_index_entry *)theirsPtr,
        (const git_merge_file_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    return r;
}

/** void git_merge_file_result_free(git_merge_file_result *result); 
 * Note: this also frees the resultPtr itself.
 */
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileResultFree)(JNIEnv *env, jclass obj, jlong resultPtr)
{
    git_merge_file_result_free((git_merge_file_result *)resultPtr);
    free((git_merge_file_result *)resultPtr);
}

/** int git_merge_create(git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len, const git_merge_options *merge_opts, const git_checkout_options *checkout_opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniMerge)(JNIEnv *env, jclass obj, jlong repoPtr, jlongArray theirHeads, jlong mergeOptsPtr, jlong checkoutOpts)
{
    jsize arrayLen = (*env)->GetArrayLength(env, theirHeads);
    jlong *elements = (*env)->GetLongArrayElements(env, theirHeads, 0);
    int r = git_merge((git_repository *)repoPtr, (const git_annotated_commit **)elements, arrayLen, (const git_merge_options *)mergeOptsPtr, (const git_checkout_options *)checkoutOpts);
    (*env)->ReleaseLongArrayElements(env, theirHeads, elements, 0);
    return r;
}