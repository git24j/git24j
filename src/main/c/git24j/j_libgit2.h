#include "j_common.h"
#include "j_mappers.h"
#include <jni.h>

#ifndef __GIT24J_GLOBAL_H__
#define __GIT24J_GLOBAL_H__
#ifdef __cplusplus
extern "C"
{
#endif
    jclass j_find_and_hold_clz(JNIEnv *env, const char *descriptor);

    extern JavaVM *globalJvm;

    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_init)(JNIEnv *, jclass);

    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_shutdown)(JNIEnv *, jclass);

    JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_version)(JNIEnv *, jclass);

    JNIEXPORT jint JNICALL J_MAKE_METHOD(Libgit2_features)(JNIEnv *, jclass);

    /**call free(void* ptr) without recursively freeing fields. */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_jniShadowFree)(JNIEnv *env, jclass obj, long ptr);

    // git_libgit2_opts() start
    //GIT_OPT_SET_MWINDOW_SIZE
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMwindowSize)(JNIEnv *env, jclass obj, jlong mWindowSize);
    //GIT_OPT_GET_MWINDOW_SIZE
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMwindowSize)(JNIEnv *env, jclass obj);

    //GIT_OPT_SET_MWINDOW_MAPPED_LIMIT
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMWindowMappedLimit)(JNIEnv *env, jclass obj, jlong mWindowMappedLimit);
    //GIT_OPT_GET_MWINDOW_MAPPED_LIMIT
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMWindowMappedLimit)(JNIEnv *env, jclass obj);

    //GIT_OPT_SET_MWINDOW_FILE_LIMIT
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMWindowFileLimit)(JNIEnv *env, jclass obj, jlong mWindowFileLimit);
    //GIT_OPT_GET_MWINDOW_FILE_LIMIT
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMWindowFileLimit)(JNIEnv *env, jclass obj);

    //GIT_OPT_GET_SEARCH_PATH
    //return is GitBuf Object: com.github.git24j.core.Buf
    JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetSearchPath)(JNIEnv *env, jclass obj, jlong sysdir);
    //GIT_OPT_SET_SEARCH_PATH
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSearchPath)(JNIEnv *env, jclass obj, jlong sysdir, jstring path);

    //GIT_OPT_SET_CACHE_OBJECT_LIMIT
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetCacheObjectLimit)(JNIEnv *env, jclass obj, jlong type, jlong size);

    //GIT_OPT_SET_CACHE_MAX_SIZE
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetCacheMaxSize)(JNIEnv *env, jclass obj, jlong size);

    //GIT_OPT_ENABLE_CACHING
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableCaching)(JNIEnv *env, jclass obj, jboolean enable);

    //GIT_OPT_GET_CACHED_MEMORY
    //return is :com.github.git24j.core.GitCachedMemorySaver
    JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetCachedMemory)(JNIEnv *env, jclass obj);

    //GIT_OPT_GET_TEMPLATE_PATH
    //return is GitBuf Object: com.github.git24j.core.Buf
    JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetTemplatePath)(JNIEnv *env, jclass obj);
    //GIT_OPT_SET_TEMPLATE_PATH
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetTemplatePath)(JNIEnv *env, jclass obj, jstring path);

    //GIT_OPT_SET_SSL_CERT_LOCATIONS
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSslCertLocations)(JNIEnv *env, jclass obj, jstring file, jstring path);

    //GIT_OPT_SET_USER_AGENT
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetUserAgent)(JNIEnv *env, jclass obj, jstring useragent);

    //GIT_OPT_ENABLE_STRICT_OBJECT_CREATION
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableStrictObjectCreation)(JNIEnv *env, jclass obj, jboolean enable);
    //GIT_OPT_ENABLE_STRICT_SYMBOLIC_REF_CREATION
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableStrictSymbolicRefCreation)(JNIEnv *env, jclass obj, jboolean enable);

    //GIT_OPT_SET_SSL_CIPHERS
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSslCiphers)(JNIEnv *env, jclass obj, jstring sslCiphers);

    //GIT_OPT_GET_USER_AGENT
    //return is GitBuf Object: com.github.git24j.core.Buf
    JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetUserAgent)(JNIEnv *env, jclass obj);

    //GIT_OPT_ENABLE_OFS_DELTA
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableOfsDelta)(JNIEnv *env, jclass obj, jboolean enable);

    //GIT_OPT_ENABLE_FSYNC_GITDIR
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableFsyncGitDir)(JNIEnv *env, jclass obj, jboolean enable);

    //TODO git_libgit2_opts() not done
    // git_libgit2_opts() end

#ifdef __cplusplus
}
#endif
#endif
