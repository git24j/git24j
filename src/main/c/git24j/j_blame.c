#include "j_blame.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_blame_init_options(git_blame_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_blame_init_options((git_blame_options *)optsPtr, version);
    return r;
}

/** size_t git_blame_get_hunk_count(git_blame *blame); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniGetHunkCount)(JNIEnv *env, jclass obj, jlong blamePtr)
{
    size_t r = git_blame_get_hunk_count((git_blame *)blamePtr);
    return r;
}

/** const git_blame_hunk * git_blame_get_hunk_byindex(git_blame *blame, size_t index); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniGetHunkByindex)(JNIEnv *env, jclass obj, jlong blamePtr, jint index)
{
    const git_blame_hunk *r = git_blame_get_hunk_byindex((git_blame *)blamePtr, index);
    return r;
}

/** const git_blame_hunk * git_blame_get_hunk_byline(git_blame *blame, size_t lineno); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniGetHunkByline)(JNIEnv *env, jclass obj, jlong blamePtr, jint lineno)
{
    const git_blame_hunk *r = git_blame_get_hunk_byline((git_blame *)blamePtr, lineno);
    return r;
}

/** int git_blame_file(git_blame **out, git_repository *repo, const char *path, git_blame_options *options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniFile)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring path, jlong optionsPtr)
{
    git_blame *c_out;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_blame_file(&c_out, (git_repository *)repoPtr, c_path, (git_blame_options *)optionsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    /* git_blame_free(c_out); */
    free(c_path);
    return r;
}

/** int git_blame_buffer(git_blame **out, git_blame *reference, const char *buffer, size_t buffer_len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniBuffer)(JNIEnv *env, jclass obj, jobject out, jlong referencePtr, jstring buffer, jint bufferLen)
{
    git_blame *c_out;
    char *c_buffer = j_copy_of_jstring(env, buffer, true);
    int r = git_blame_buffer(&c_out, (git_blame *)referencePtr, c_buffer, bufferLen);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    /* git_blame_free(c_out); */
    free(c_buffer);
    return r;
}

/** void git_blame_free(git_blame *blame); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Blame_jniFree)(JNIEnv *env, jclass obj, jlong blamePtr)
{
    git_blame_free((git_blame *)blamePtr);
}
