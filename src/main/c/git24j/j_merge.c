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
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniTrees)(JNIEnv *env, jclass obj, jlong outPtr, jlong repoPtr, jlong ancestorTreePtr, jlong ourTreePtr, jlong theirTreePtr, jlong optsPtr)
{
    int r = git_merge_trees((git_index *)outPtr, (git_repository *)repoPtr, (git_tree *)ancestorTreePtr, (git_tree *)ourTreePtr, (git_tree *)theirTreePtr, (git_merge_options *)optsPtr);
    return r;
}

/** int git_merge_commits(git_index **out, git_repository *repo, const git_commit *our_commit, const git_commit *their_commit, const git_merge_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniCommits)(JNIEnv *env, jclass obj, jlong outPtr, jlong repoPtr, jlong ourCommitPtr, jlong theirCommitPtr, jlong optsPtr)
{
    int r = git_merge_commits((git_index *)outPtr, (git_repository *)repoPtr, (git_commit *)ourCommitPtr, (git_commit *)theirCommitPtr, (git_merge_options *)optsPtr);
    return r;
}

/** int git_merge_analysis(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniAnalysis)(JNIEnv *env, jclass obj, jobject analysisOut, jobject preferenceOut, jlong repoPtr, jlongArray theirHeads)
{

    jsize theirHeadsLen = (*env)->GetArrayLength(env, theirHeads);
    const git_annotated_commit **their_heads = (const git_annotated_commit **)malloc(sizeof(const git_annotated_commit *) * theirHeadsLen);
    jlong *elements = (*env)->GetLongArrayElements(*env, theirHeads, 0);
    for (jsize i = 0; i < theirHeadsLen; i++)
    {
        their_heads[i] = (const git_annotated_commit *)elements[i];
    }

    int analysis_out = 0;
    int preference_out = 0;
    int r = git_merge_analysis(&analysis_out, &preference_out, (git_repository *)repoPtr, their_heads, theirHeadsLen);
    (*env)->CallVoidMethod(env, analysisOut, jniConstants->midAtomicIntSet, analysis_out);
    (*env)->CallVoidMethod(env, preferenceOut, jniConstants->midAtomicIntSet, preference_out);
    (*env)->ReleaseLongArrayElements(env, theirHeads, elements, 0);
    return r;
}
