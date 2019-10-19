
#include "j_libgit2.h"
#include <git2.h>
#include <jni.h>

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_init)(JNIEnv *env, jclass obj)
{
    git_libgit2_init();
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_shutdown)(JNIEnv *env, jclass obj)
{
    git_libgit2_shutdown();
}

JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_version)(JNIEnv *env, jclass obj)
{
    int major, minor, patch;
    git_libgit2_version(&major, &minor, &patch);

    jclass cls = (*env)->FindClass(env, J_CLZ_PREFIX "Version");
    if (cls == NULL)
    {
        return NULL;
    }

    jmethodID ctor = (*env)->GetMethodID(env, cls, "<init>", "(III)V");
    if (ctor == NULL)
    {
        return NULL;
    }

    jobject version = (*env)->NewObject(env, cls, ctor, major, minor, patch);
    return version;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Libgit2_features)(JNIEnv *env, jclass obj)
{
    return git_libgit2_features();
}
