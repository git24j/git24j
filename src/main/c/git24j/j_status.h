#include "j_common.h"
#include <jni.h>

#ifndef __GIT24J_STATUS_H__
#define __GIT24J_STATUS_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /**int git_status_file(unsigned int *status_flags, git_repository *repo, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniFile)(JNIEnv *env, jclass obj, jobject atomInt, jobject repo, jstring path);
#ifdef __cplusplus
}
#endif
#endif