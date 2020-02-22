#include "j_status.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_exception.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <stdio.h>

// no matching type found for 'git_status_cb callback'
/** int git_status_foreach(git_repository *repo, git_status_cb callback, void *payload); */
// no matching type found for 'git_status_cb callback'
/** int git_status_foreach_ext(git_repository *repo, const git_status_options *opts, git_status_cb callback, void *payload); */
/** -------- Wrapper Body ---------- */
/** int git_status_init_options(git_status_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_status_init_options((git_status_options *)optsPtr, version);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_status_options *out = (git_status_options *)malloc(sizeof(git_status_options));
    int r = git_status_init_options(out, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (long)out);
    return r;
}

/** int git_status_file(unsigned int *status_flags, git_repository *repo, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniFile)(JNIEnv *env, jclass obj, jobject statusFlags, jlong repoPtr, jstring path)
{
    unsigned int c_status_flags;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_status_file(&c_status_flags, (git_repository *)repoPtr, c_path);
    (*env)->CallVoidMethod(env, statusFlags, jniConstants->midAtomicIntSet, c_status_flags);
    free(c_path);
    return r;
}

/** int git_status_list_new(git_status_list **out, git_repository *repo, const git_status_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniListNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr)
{
    git_status_list *c_out;
    int r = git_status_list_new(&c_out, (git_repository *)repoPtr, (git_status_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    /* git_status_list_free(c_out); */
    return r;
}

/** size_t git_status_list_entrycount(git_status_list *statuslist); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniListEntrycount)(JNIEnv *env, jclass obj, jlong statuslistPtr)
{
    size_t r = git_status_list_entrycount((git_status_list *)statuslistPtr);
    return r;
}

/** const git_status_entry * git_status_byindex(git_status_list *statuslist, size_t idx); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Status_jniByindex)(JNIEnv *env, jclass obj, jlong statuslistPtr, jint idx)
{
    const git_status_entry *r = git_status_byindex((git_status_list *)statuslistPtr, idx);
    return (jlong)r;
}

/** void git_status_list_free(git_status_list *statuslist); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Status_jniListFree)(JNIEnv *env, jclass obj, jlong statuslistPtr)
{
    git_status_list_free((git_status_list *)statuslistPtr);
}

/** int git_status_should_ignore(int *ignored, git_repository *repo, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniShouldIgnore)(JNIEnv *env, jclass obj, jobject ignored, jlong repoPtr, jstring path)
{
    int c_ignored;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_status_should_ignore(&c_ignored, (git_repository *)repoPtr, c_path);
    (*env)->CallVoidMethod(env, ignored, jniConstants->midAtomicIntSet, c_ignored);
    free(c_path);
    return r;
}
