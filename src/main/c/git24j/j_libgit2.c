
#include "j_libgit2.h"
#include <assert.h>
#include <git2.h>
#include <jni.h>
#include <stdio.h>

jclass __find_and_hold_clz(JNIEnv *env, const char *descriptor)
{
    jclass clz = (*env)->FindClass(env, descriptor);
    assert(clz && "class not found");
    jclass gClz = (jclass)(*env)->NewGlobalRef(env, clz);
    (*env)->DeleteLocalRef(env, clz);
    return gClz;
}

void git24j_init(JNIEnv *env)
{
    assert(env && "cannot initiate git24j without jvm");
    jniConstants = (j_constants_t *)malloc(sizeof(j_constants_t));
    jniConstants->clzAtomicInt = __find_and_hold_clz(env, "Ljava/util/concurrent/atomic/AtomicInteger;");
    jniConstants->clzAtomicLong = __find_and_hold_clz(env, "Ljava/util/concurrent/atomic/AtomicLong;");
    jniConstants->clzAtomicReference = __find_and_hold_clz(env, "Ljava/util/concurrent/atomic/AtomicReference;");
    jniConstants->clzList = __find_and_hold_clz(env, "Ljava/util/List;");
    assert(jniConstants->clzAtomicInt && "AtomicInteger::set not found");
    assert(jniConstants->clzAtomicLong && "AtomicLong::set not found");
    assert(jniConstants->clzAtomicReference && "AtomicReference::set not found");
    assert(jniConstants->clzList && "List::get not found");
    jniConstants->midAtomicIntSet = (*env)->GetMethodID(env, jniConstants->clzAtomicInt, "set", "(I)V");
    jniConstants->midAtomicLongSet = (*env)->GetMethodID(env, jniConstants->clzAtomicLong, "set", "(J)V");
    jniConstants->midAtomicReferenceSet = (*env)->GetMethodID(env, jniConstants->clzAtomicReference, "set", "(Ljava/lang/Object;)V");
    jniConstants->midListGetI = (*env)->GetMethodID(env, jniConstants->clzAtomicReference, "get", "(I)Ljava/lang/Object;");
}

void git24j_shutdown(JNIEnv *env)
{
    assert(jniConstants && env && "jvm was shutdown unexpected");
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicInt);
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicLong);
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicReference);
    (*env)->DeleteGlobalRef(env, jniConstants->clzList);
    free(jniConstants);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_init)(JNIEnv *env, jclass obj)
{
    git_libgit2_init();
    git24j_init(env);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_shutdown)(JNIEnv *env, jclass obj)
{
    git24j_shutdown(env);
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
