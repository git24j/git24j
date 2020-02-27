#include "j_checkout.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;
// no matching type found for 'git_checkout_notify_t why'
/** int git_checkout_notify_cb(git_checkout_notify_t why, const char *path, const git_diff_file *baseline, const git_diff_file *target, const git_diff_file *workdir, void *payload); */
/** -------- Wrapper Body ---------- */
/** int git_checkout_init_options(git_checkout_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_checkout_init_options((git_checkout_options *)optsPtr, version);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version)
{
    git_checkout_options *opts = (git_checkout_options *)malloc(sizeof(git_checkout_options));
    int r = git_checkout_init_options((git_checkout_options *)outPtr, version);
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (long)opts);
    return r;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    if (optsPtr == 0)
    {
        return;
    }

    git_checkout_options *opts = (git_checkout_options *)optsPtr;
    free(opts->progress_payload);
    free(opts->perfdata_payload);
    free(opts->notify_payload);
    git_strarray_free(&(opts->paths));
    free((void *)opts->target_directory);
    free((void *)opts->ancestor_label);
    free((void *)opts->our_label);
    free((void *)opts->their_label);
}

/** int git_checkout_head(git_repository *repo, const git_checkout_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniHead)(JNIEnv *env, jclass obj, jlong repoPtr, jlong optsPtr)
{
    int r = git_checkout_head((git_repository *)repoPtr, (git_checkout_options *)optsPtr);
    return r;
}

/** int git_checkout_index(git_repository *repo, git_index *index, const git_checkout_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniIndex)(JNIEnv *env, jclass obj, jlong repoPtr, jlong indexPtr, jlong optsPtr)
{
    int r = git_checkout_index((git_repository *)repoPtr, (git_index *)indexPtr, (git_checkout_options *)optsPtr);
    return r;
}

/** int git_checkout_tree(git_repository *repo, const git_object *treeish, const git_checkout_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniTree)(JNIEnv *env, jclass obj, jlong repoPtr, jlong treeishPtr, jlong optsPtr)
{
    int r = git_checkout_tree((git_repository *)repoPtr, (git_object *)treeishPtr, (git_checkout_options *)optsPtr);
    return r;
}

// no matching type found for 'jobject notify_cb'
/** void git_checkout_options_set_notify_cb(const git_checkout_options *opts, jobject notify_cb); */
// no matching type found for 'jobject progress_cb'
/** void git_checkout_options_set_progress_cb(const git_checkout_options *opts, jobject progress_cb); */
// no matching type found for 'jobject perfdata_cb'
/** void git_checkout_options_set_perfdata_cb(const git_checkout_options *opts, jobject perfdata_cb); */
int j_git_checkout_notify_cb(git_checkout_notify_t why, const char *path, const git_diff_file *baseline, const git_diff_file *target, const git_diff_file *workdir, void *payload)
{
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = j_payload->env;
    jobject consumer = j_payload->consumer;
    if (consumer == NULL)
    {
        return 0;
    }
    jstring jPath = (*env)->NewStringUTF(env, path);
    jclass clz = (*env)->GetObjectClass(env, consumer);
    jmethodID mid = (*env)->GetMethodID(env, clz, "accept", "(ILjava/lang/String;JJJ)I");
    int r = (*env)->CallIntMethod(env, consumer, mid, (int)why, jPath, (jlong)baseline, (jlong)target, (jlong)workdir);
    (*env)->DeleteLocalRef(env, jPath);
    return r;
}

void j_git_checkout_progress_cb(const char *path, size_t completed_steps, size_t total_steps, void *payload)
{
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = j_payload->env;
    jobject consumer = j_payload->consumer;
    if (consumer == NULL)
    {
        return;
    }
    jstring jPath = (*env)->NewStringUTF(env, path);
    jclass clz = (*env)->GetObjectClass(env, consumer);
    jmethodID mid = (*env)->GetMethodID(env, clz, "accept", "(Ljava/lang/String;II)V");
    (*env)->CallVoidMethod(env, consumer, mid, jPath, completed_steps, total_steps);
    (*env)->DeleteLocalRef(env, jPath);
}

void j_git_checkout_perfdata_cb(const git_checkout_perfdata *perfdata, void *payload)
{
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = j_payload->env;
    jobject consumer = j_payload->consumer;
    if (consumer == NULL)
    {
        return;
    }
    jclass clz = (*env)->GetObjectClass(env, consumer);
    jmethodID mid = (*env)->GetMethodID(env, clz, "accept", "(III)V");
    (*env)->CallVoidMethod(env, consumer, mid, perfdata->mkdir_calls, perfdata->stat_calls, perfdata->chmod_calls);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetNotifyCb)(JNIEnv *env, jclass obj, jlong optsPtr, jobject notifyCb)
{
    git_checkout_options *opts = (git_checkout_options *)optsPtr;
    /* released in `jniOptionsFree()` */
    j_cb_payload *payload = (j_cb_payload *)malloc(sizeof(j_cb_payload));
    payload->env = env;
    payload->consumer = notifyCb;
    opts->notify_payload = payload;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetProcessCb)(JNIEnv *env, jclass obj, jlong optsPtr, jobject processCb)
{
    git_checkout_options *opts = (git_checkout_options *)optsPtr;
    /* released in `jniOptionsFree()` */
    j_cb_payload *payload = (j_cb_payload *)malloc(sizeof(j_cb_payload));
    payload->env = env;
    payload->consumer = processCb;
    opts->progress_payload = payload;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetPerfdataCb)(JNIEnv *env, jclass obj, jlong optsPtr, jobject perfdataCb)
{
    git_checkout_options *opts = (git_checkout_options *)optsPtr;
    /* released in `jniOptionsFree()` */
    j_cb_payload *payload = (j_cb_payload *)malloc(sizeof(j_cb_payload));
    payload->env = env;
    payload->consumer = perfdataCb;
    opts->perfdata_payload = payload;
}

/** -------- Wrapper Body ---------- */
/** unsigned int git_checkout_options_get_version(const git_checkout_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    assert(optsPtr && "options must not be NULL");
    unsigned int r = ((git_checkout_options *)optsPtr)->version;
    return r;
}

/** void git_checkout_options_set_version(const git_checkout_options *opts, unsigned int version); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    assert(optsPtr && "options must not be NULL");
    ((git_checkout_options *)optsPtr)->version = version;
}

/** unsigned int git_checkout_options_get_strategy(const git_checkout_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetStrategy)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    assert(optsPtr && "options must not be NULL");
    return ((git_checkout_options *)optsPtr)->checkout_strategy;
}

/** void git_checkout_options_set_strategy(const git_checkout_options *opts, unsigned int strategy); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetStrategy)(JNIEnv *env, jclass obj, jlong optsPtr, jint strategy)
{
    assert(optsPtr && "options must not be NULL");
    ((git_checkout_options *)optsPtr)->checkout_strategy = strategy;
}

/** int git_checkout_options_get_disable_filters(const git_checkout_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetDisableFilters)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    assert(optsPtr && "options must not be NULL");
    return ((git_checkout_options *)optsPtr)->checkout_strategy;
}

/** void git_checkout_options_set_disable_filters(const git_checkout_options *opts, int disalbe_filters); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetDisableFilters)(JNIEnv *env, jclass obj, jlong optsPtr, jint disalbe_filters)
{
    assert(optsPtr && "options must not be NULL");
    ((git_checkout_options *)optsPtr)->disable_filters = disalbe_filters;
}

/** unsigned int git_checkout_options_get_dir_mode(const git_checkout_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetDirMode)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    assert(optsPtr && "options must not be NULL");
    return ((git_checkout_options *)optsPtr)->dir_mode;
}

/** void git_checkout_options_set_dir_mode(const git_checkout_options *opts, unsigned int mode); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetDirMode)(JNIEnv *env, jclass obj, jlong optsPtr, jint mode)
{
    assert(optsPtr && "options must not be NULL");
    ((git_checkout_options *)optsPtr)->dir_mode = mode;
}

/** unsigned int git_checkout_options_get_file_mode(const git_checkout_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetFileMode)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    assert(optsPtr && "options must not be NULL");
    return ((git_checkout_options *)optsPtr)->file_mode;
}

/** void git_checkout_options_set_file_mode(const git_checkout_options *opts, unsigned int mode); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetFileMode)(JNIEnv *env, jclass obj, jlong optsPtr, jint mode)
{
    ((git_checkout_options *)optsPtr)->file_mode = mode;
}

/** int git_checkout_options_get_file_open_flags(const git_checkout_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetFileOpenFlags)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    return ((git_checkout_options *)optsPtr)->file_open_flags;
}

/** void git_checkout_options_set_file_open_flags(const git_checkout_options *opts, int flags); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetFileOpenFlags)(JNIEnv *env, jclass obj, jlong optsPtr, jint flags)
{
    ((git_checkout_options *)optsPtr)->file_open_flags = flags;
}

/** unsigned int git_checkout_options_get_notify_flags(const git_checkout_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetNotifyFlags)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    return ((git_checkout_options *)optsPtr)->notify_flags;
}

/** void git_checkout_options_set_notify_flags(const git_checkout_options *opts, unsigned int flags); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetNotifyFlags)(JNIEnv *env, jclass obj, jlong optsPtr, jint flags)
{
    ((git_checkout_options *)optsPtr)->notify_flags = flags;
}

/** void git_checkout_options_set_paths(const git_checkout_options *opts, git_strarray paths); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetPaths)(JNIEnv *env, jclass obj, jlong optsPtr, jobjectArray paths)
{
    if (optsPtr == 0)
    {
        return;
    }

    git_checkout_options *opts = (git_checkout_options *)optsPtr;
    j_strarray_from_java(env, &opts->paths, paths);
}

/** void git_checkout_options_set_baseline(const git_checkout_options *opts, git_tree *baseline); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetBaseline)(JNIEnv *env, jclass obj, jlong optsPtr, jlong baselinePtr)
{
    ((git_checkout_options *)optsPtr)->baseline = (git_tree *)baselinePtr;
}

/** void git_checkout_options_set_baseline_index(const git_checkout_options *opts, git_index *baseline_index); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetBaselineIndex)(JNIEnv *env, jclass obj, jlong optsPtr, jlong baselineIndexPtr)
{
    ((git_checkout_options *)optsPtr)->baseline_index = (git_index *)baselineIndexPtr;
}

/** void git_checkout_options_set_target_directory(const git_checkout_options *opts, const char *target_directory); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetTargetDirectory)(JNIEnv *env, jclass obj, jlong optsPtr, jstring target_directory)
{
    ((git_checkout_options *)optsPtr)->target_directory = j_copy_of_jstring(env, target_directory, true);
}

/** void git_checkout_options_set_ancestor_label(const git_checkout_options *opts, const char *ancestor_label); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetAncestorLabel)(JNIEnv *env, jclass obj, jlong optsPtr, jstring ancestor_label)
{
    ((git_checkout_options *)optsPtr)->ancestor_label = j_copy_of_jstring(env, ancestor_label, true);
}

/** void git_checkout_options_set_our_label(const git_checkout_options *opts, const char *our_label); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetOurLabel)(JNIEnv *env, jclass obj, jlong optsPtr, jstring our_label)
{
    ((git_checkout_options *)optsPtr)->our_label = j_copy_of_jstring(env, our_label, true);
}

/** void git_checkout_options_set_their_lable(const git_checkout_options *opts, const char *their_label); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetTheirLable)(JNIEnv *env, jclass obj, jlong optsPtr, jstring their_label)
{
    ((git_checkout_options *)optsPtr)->their_label = j_copy_of_jstring(env, their_label, true);
}
