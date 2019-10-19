#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_CONFIG_H__
#define __GIT24J_CONFIG_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** void git_config_free(git_config *cfg); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Config_jniFree)(JNIEnv *env, jclass obj, jlong cfgPtr);

    /** int git_config_find_global(git_buf *out); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindGlobal)(JNIEnv *env, jclass obj, jobject buf);

    /** int git_config_get_string_buf(git_buf *out, const git_config *cfg, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetStringBuf)(JNIEnv *env, jclass obj, jobject buf, jlong cfgPtr, jstring name);

#ifdef __cplusplus
}
#endif
#endif