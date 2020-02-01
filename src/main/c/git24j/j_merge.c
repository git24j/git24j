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

/** int git_merge_file_init_options(git_merge_file_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_merge_file_init_options((git_merge_file_options *)optsPtr, version);
    return r;
}

/** int git_merge_init_options(git_merge_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_merge_init_options((git_merge_options *)optsPtr, version);
    return r;
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
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBases)(JNIEnv *env, jclass obj, jlong outPtr, jlong repoPtr, jobject one, jobject two)
{
    git_oid c_one;
    j_git_oid_from_java(env, one, &c_one);
    git_oid c_two;
    j_git_oid_from_java(env, two, &c_two);
    int r = git_merge_bases((git_oidarray *)outPtr, (git_repository *)repoPtr, &c_one, &c_two);
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
    git_oidarray_free(&c_out);
    jclass clz = (*env)->GetObjectClass(env, outOids);
    assert(clz && "class of outOids is not found");
    jmethodID midSetter = (*env)->GetMethodID(env, clz, "add", "([B])V");
    assert(midSetter && "outOids should have a method called `add`");
    for (size_t i = 0; i < c_out.count; i++)
    {
        jbyteArray raw = j_byte_array_from_c(env, c_out.ids[i].id, GIT_OID_RAWSZ);
        (*env)->CallVoidMethod(env, outOids, midSetter, raw);
        (*env)->DeleteLocalRef(env, raw);
    }
    (*env)->DeleteLocalRef(env, clz);
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

/** void git_merge_file_result_free(git_merge_file_result *result); 
 * Note: this also frees the resultPtr itself.
 */
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileResultFree)(JNIEnv *env, jclass obj, jlong resultPtr)
{
    git_merge_file_result_free((git_merge_file_result *)resultPtr);
    free((git_merge_file_result *)resultPtr);
}
