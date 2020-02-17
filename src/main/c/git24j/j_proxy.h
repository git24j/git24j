#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_PROXY_H__
#define __GIT24J_PROXY_H__
#ifdef __cplusplus
extern "C"
{
#endif
    /** create new proxy option object. */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Proxy_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
#ifdef __cplusplus
}
#endif
#endif