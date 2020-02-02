#include "j_remote.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>

// no matching type found for 'const git_remote_head ***out'
/** int git_remote_ls(const git_remote_head ***out, size_t *size, git_remote *remote); */
/** -------- Wrapper Body ---------- */
/** int git_remote_add_fetch(git_repository *repo, const char *remote, const char *refspec); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAddFetch)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring refspec)
{
    char *c_remote = j_copy_of_jstring(env, remote, true);
    char *c_refspec = j_copy_of_jstring(env, refspec, true);
    int r = git_remote_add_fetch((git_repository *)repoPtr, c_remote, c_refspec);
    free(c_remote);
    free(c_refspec);
    return r;
}

/** int git_remote_add_push(git_repository *repo, const char *remote, const char *refspec); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAddPush)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring refspec)
{
    char *c_remote = j_copy_of_jstring(env, remote, true);
    char *c_refspec = j_copy_of_jstring(env, refspec, true);
    int r = git_remote_add_push((git_repository *)repoPtr, c_remote, c_refspec);
    free(c_remote);
    free(c_refspec);
    return r;
}

/** git_remote_autotag_option_t git_remote_autotag(const git_remote *remote); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAutotag)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    git_remote_autotag_option_t r = git_remote_autotag((git_remote *)remotePtr);
    return r;
}

/** int git_remote_connect(git_remote *remote, git_direction direction, const git_remote_callbacks *callbacks, const git_proxy_options *proxy_opts, const git_strarray *custom_headers); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniConnect)(JNIEnv *env, jclass obj, jlong remotePtr, jint direction, jlong callbacksPtr, jlong proxyOptsPtr, jobjectArray customHeaders)
{
    git_strarray c_custom_headers;
    git_strarray_of_jobject_array(env, customHeaders, &c_custom_headers);
    int r = git_remote_connect((git_remote *)remotePtr, direction, (git_remote_callbacks *)callbacksPtr, (git_proxy_options *)proxyOptsPtr, &c_custom_headers);
    git_strarray_free(&c_custom_headers);
    return r;
}

/** int git_remote_connected(const git_remote *remote); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniConnected)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    int r = git_remote_connected((git_remote *)remotePtr);
    return r;
}

/** int git_remote_create(git_remote **out, git_repository *repo, const char *name, const char *url); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreate)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name, jstring url)
{
    git_remote *c_out;
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_create(&c_out, (git_repository *)repoPtr, c_name, c_url);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_remote_free(c_out);
    free(c_name);
    free(c_url);
    return r;
}

/** int git_remote_create_anonymous(git_remote **out, git_repository *repo, const char *url); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateAnonymous)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring url)
{
    git_remote *c_out;
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_create_anonymous(&c_out, (git_repository *)repoPtr, c_url);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_remote_free(c_out);
    free(c_url);
    return r;
}

/** int git_remote_create_detached(git_remote **out, const char *url); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateDetached)(JNIEnv *env, jclass obj, jobject out, jstring url)
{
    git_remote *c_out;
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_create_detached(&c_out, c_url);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_remote_free(c_out);
    free(c_url);
    return r;
}

/** int git_remote_create_init_options(git_remote_create_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_remote_create_init_options((git_remote_create_options *)optsPtr, version);
    return r;
}

/** int git_remote_create_with_fetchspec(git_remote **out, git_repository *repo, const char *name, const char *url, const char *fetch); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateWithFetchspec)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name, jstring url, jstring fetch)
{
    git_remote *c_out;
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_url = j_copy_of_jstring(env, url, true);
    char *c_fetch = j_copy_of_jstring(env, fetch, true);
    int r = git_remote_create_with_fetchspec(&c_out, (git_repository *)repoPtr, c_name, c_url, c_fetch);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_remote_free(c_out);
    free(c_name);
    free(c_url);
    free(c_fetch);
    return r;
}

/** int git_remote_create_with_opts(git_remote **out, const char *url, const git_remote_create_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateWithOpts)(JNIEnv *env, jclass obj, jobject out, jstring url, jlong optsPtr)
{
    git_remote *c_out;
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_create_with_opts(&c_out, c_url, (git_remote_create_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_remote_free(c_out);
    free(c_url);
    return r;
}

/** int git_remote_default_branch(git_buf *out, git_remote *remote); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDefaultBranch)(JNIEnv *env, jclass obj, jobject out, jlong remotePtr)
{
    git_buf c_out = {0};
    int r = git_remote_default_branch(&c_out, (git_remote *)remotePtr);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}

/** int git_remote_delete(git_repository *repo, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDelete)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_remote_delete((git_repository *)repoPtr, c_name);
    free(c_name);
    return r;
}

/** void git_remote_disconnect(git_remote *remote); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniDisconnect)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    git_remote_disconnect((git_remote *)remotePtr);
}

/** int git_remote_download(git_remote *remote, const git_strarray *refspecs, const git_fetch_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDownload)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr)
{
    git_strarray c_refspecs;
    git_strarray_of_jobject_array(env, refspecs, &c_refspecs);
    int r = git_remote_download((git_remote *)remotePtr, &c_refspecs, (git_fetch_options *)optsPtr);
    git_strarray_free(&c_refspecs);
    return r;
}

/** int git_remote_dup(git_remote **dest, git_remote *source); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDup)(JNIEnv *env, jclass obj, jobject dest, jlong sourcePtr)
{
    git_remote *c_dest;
    int r = git_remote_dup(&c_dest, (git_remote *)sourcePtr);
    (*env)->CallVoidMethod(env, dest, jniConstants->midAtomicLongSet, (long)c_dest);
    git_remote_free(c_dest);
    return r;
}

/** int git_remote_fetch(git_remote *remote, const git_strarray *refspecs, const git_fetch_options *opts, const char *reflog_message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetch)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr, jstring reflog_message)
{
    git_strarray c_refspecs;
    git_strarray_of_jobject_array(env, refspecs, &c_refspecs);
    char *c_reflog_message = j_copy_of_jstring(env, reflog_message, true);
    int r = git_remote_fetch((git_remote *)remotePtr, &c_refspecs, (git_fetch_options *)optsPtr, c_reflog_message);
    git_strarray_free(&c_refspecs);
    free(c_reflog_message);
    return r;
}

/** void git_remote_free(git_remote *remote); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFree)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    git_remote_free((git_remote *)remotePtr);
}

/** int git_remote_get_fetch_refspecs(git_strarray *array, const git_remote *remote); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniGetFetchRefspecs)(JNIEnv *env, jclass obj, jobject array, jlong remotePtr)
{
    git_strarray *c_array = (git_strarray *)malloc(sizeof(git_strarray));
    int r = git_remote_get_fetch_refspecs(c_array, (git_remote *)remotePtr);
    j_strarray_to_java_list(env, c_array, array);
    git_strarray_free(c_array);
    free(c_array);
    return r;
}

/** int git_remote_get_push_refspecs(git_strarray *array, const git_remote *remote); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniGetPushRefspecs)(JNIEnv *env, jclass obj, jobject array, jlong remotePtr)
{
    git_strarray *c_array = (git_strarray *)malloc(sizeof(git_strarray));
    int r = git_remote_get_push_refspecs(c_array, (git_remote *)remotePtr);
    j_strarray_to_java_list(env, c_array, array);
    git_strarray_free(c_array);
    free(c_array);
    return r;
}

/** const git_refspec * git_remote_get_refspec(const git_remote *remote, size_t n); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniGetRefspec)(JNIEnv *env, jclass obj, jlong remotePtr, jint n)
{
    const git_refspec *r = git_remote_get_refspec((git_remote *)remotePtr, n);
    return (jlong)r;
}

/** int git_remote_init_callbacks(git_remote_callbacks *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniInitCallbacks)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_remote_init_callbacks((git_remote_callbacks *)optsPtr, version);
    return r;
}

/** int git_remote_is_valid_name(const char *remote_name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniIsValidName)(JNIEnv *env, jclass obj, jstring remote_name)
{
    char *c_remote_name = j_copy_of_jstring(env, remote_name, true);
    int r = git_remote_is_valid_name(c_remote_name);
    free(c_remote_name);
    return r;
}

/** int git_remote_list(git_strarray *out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniList)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr)
{
    git_strarray *c_out = (git_strarray *)malloc(sizeof(git_strarray));
    int r = git_remote_list(c_out, (git_repository *)repoPtr);
    j_strarray_to_java_list(env, c_out, out);
    git_strarray_free(c_out);
    free(c_out);
    return r;
}

/** int git_remote_lookup(git_remote **out, git_repository *repo, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name)
{
    git_remote *c_out;
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_remote_lookup(&c_out, (git_repository *)repoPtr, c_name);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_remote_free(c_out);
    free(c_name);
    return r;
}

/** const char * git_remote_name(const git_remote *remote); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniName)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    const char *r = git_remote_name((git_remote *)remotePtr);
    return (*env)->NewStringUTF(env, r);
}

/** git_repository * git_remote_owner(const git_remote *remote); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniOwner)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    git_repository *r = git_remote_owner((git_remote *)remotePtr);
    return (jlong)r;
}

/** int git_remote_prune(git_remote *remote, const git_remote_callbacks *callbacks); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPrune)(JNIEnv *env, jclass obj, jlong remotePtr, jlong callbacksPtr)
{
    int r = git_remote_prune((git_remote *)remotePtr, (git_remote_callbacks *)callbacksPtr);
    return r;
}

/** int git_remote_prune_refs(const git_remote *remote); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPruneRefs)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    int r = git_remote_prune_refs((git_remote *)remotePtr);
    return r;
}

/** int git_remote_push(git_remote *remote, const git_strarray *refspecs, const git_push_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPush)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr)
{
    git_strarray c_refspecs;
    git_strarray_of_jobject_array(env, refspecs, &c_refspecs);
    int r = git_remote_push((git_remote *)remotePtr, &c_refspecs, (git_push_options *)optsPtr);
    git_strarray_free(&c_refspecs);
    return r;
}

/** const char * git_remote_pushurl(const git_remote *remote); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniPushurl)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    const char *r = git_remote_pushurl((git_remote *)remotePtr);
    return (*env)->NewStringUTF(env, r);
}

/** size_t git_remote_refspec_count(const git_remote *remote); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniRefspecCount)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    size_t r = git_remote_refspec_count((git_remote *)remotePtr);
    return r;
}

/** int git_remote_rename(git_strarray *problems, git_repository *repo, const char *name, const char *new_name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniRename)(JNIEnv *env, jclass obj, jobject problems, jlong repoPtr, jstring name, jstring new_name)
{
    git_strarray *c_problems = (git_strarray *)malloc(sizeof(git_strarray));
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_new_name = j_copy_of_jstring(env, new_name, true);
    int r = git_remote_rename(c_problems, (git_repository *)repoPtr, c_name, c_new_name);
    j_strarray_to_java_list(env, c_problems, problems);
    git_strarray_free(c_problems);
    free(c_problems);
    free(c_name);
    free(c_new_name);
    return r;
}

/** int git_remote_set_autotag(git_repository *repo, const char *remote, git_remote_autotag_option_t value); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetAutotag)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jint value)
{
    char *c_remote = j_copy_of_jstring(env, remote, true);
    int r = git_remote_set_autotag((git_repository *)repoPtr, c_remote, value);
    free(c_remote);
    return r;
}

/** int git_remote_set_pushurl(git_repository *repo, const char *remote, const char *url); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetPushurl)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring url)
{
    char *c_remote = j_copy_of_jstring(env, remote, true);
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_set_pushurl((git_repository *)repoPtr, c_remote, c_url);
    free(c_remote);
    free(c_url);
    return r;
}

/** int git_remote_set_url(git_repository *repo, const char *remote, const char *url); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetUrl)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring url)
{
    char *c_remote = j_copy_of_jstring(env, remote, true);
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_remote_set_url((git_repository *)repoPtr, c_remote, c_url);
    free(c_remote);
    free(c_url);
    return r;
}

/** const git_transfer_progress * git_remote_stats(git_remote *remote); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniStats)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    const git_transfer_progress *r = git_remote_stats((git_remote *)remotePtr);
    return (jlong)r;
}

/** void git_remote_stop(git_remote *remote); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniStop)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    git_remote_stop((git_remote *)remotePtr);
}

/** int git_remote_update_tips(git_remote *remote, const git_remote_callbacks *callbacks, int update_fetchhead, git_remote_autotag_option_t download_tags, const char *reflog_message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniUpdateTips)(JNIEnv *env, jclass obj, jlong remotePtr, jlong callbacksPtr, jint update_fetchhead, jint downloadTags, jstring reflog_message)
{
    char *c_reflog_message = j_copy_of_jstring(env, reflog_message, true);
    int r = git_remote_update_tips((git_remote *)remotePtr, (git_remote_callbacks *)callbacksPtr, update_fetchhead, downloadTags, c_reflog_message);
    free(c_reflog_message);
    return r;
}

/** int git_remote_upload(git_remote *remote, const git_strarray *refspecs, const git_push_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniUpload)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr)
{
    git_strarray c_refspecs;
    git_strarray_of_jobject_array(env, refspecs, &c_refspecs);
    int r = git_remote_upload((git_remote *)remotePtr, &c_refspecs, (git_push_options *)optsPtr);
    git_strarray_free(&c_refspecs);
    return r;
}

/** const char * git_remote_url(const git_remote *remote); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniUrl)(JNIEnv *env, jclass obj, jlong remotePtr)
{
    const char *r = git_remote_url((git_remote *)remotePtr);
    return (*env)->NewStringUTF(env, r);
}
