#include "j_common.h"
#include <jni.h>

#ifndef __GIT24J_GLOBAL_H__
#define __GIT24J_GLOBAL_H__
#ifdef __cplusplus
extern "C"
{
#endif

    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_init)(JNIEnv *, jclass);

    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_shutdown)(JNIEnv *, jclass);

    JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_version)(JNIEnv *, jclass);

    JNIEXPORT jint JNICALL J_MAKE_METHOD(Libgit2_features)(JNIEnv *, jclass);

    /**TODO: git_libgit2_opts. */

#ifdef __cplusplus
}
#endif
#endif
