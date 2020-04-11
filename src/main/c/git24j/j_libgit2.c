
#include "j_libgit2.h"
#include <assert.h>
#include <git2.h>
#include <jni.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

jclass j_find_and_hold_clz(JNIEnv *env, const char *descriptor)
{
    jclass clz = (*env)->FindClass(env, descriptor);
    assert(clz && "class not found");
    jclass gClz = (jclass)(*env)->NewGlobalRef(env, clz);
    (*env)->DeleteLocalRef(env, clz);
    return gClz;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    globalJvm = vm;
    return JNI_VERSION_1_6;
}

void git24j_init(JNIEnv *env)
{
    assert(env && "cannot initiate git24j without jvm");
    jniConstants = (j_constants_t *)malloc(sizeof(j_constants_t));
    jniConstants->clzAtomicInt = j_find_and_hold_clz(env, "Ljava/util/concurrent/atomic/AtomicInteger;");
    jniConstants->clzAtomicLong = j_find_and_hold_clz(env, "Ljava/util/concurrent/atomic/AtomicLong;");
    jniConstants->clzAtomicReference = j_find_and_hold_clz(env, "Ljava/util/concurrent/atomic/AtomicReference;");
    jniConstants->clzList = j_find_and_hold_clz(env, "Ljava/util/List;");
    assert(jniConstants->clzAtomicInt && "AtomicInteger class not found");
    assert(jniConstants->clzAtomicLong && "AtomicLong class not found");
    assert(jniConstants->clzAtomicReference && "AtomicReference class not found");
    assert(jniConstants->clzList && "List class not found");
    jniConstants->midAtomicIntSet = (*env)->GetMethodID(env, jniConstants->clzAtomicInt, "set", "(I)V");
    jniConstants->midAtomicLongSet = (*env)->GetMethodID(env, jniConstants->clzAtomicLong, "set", "(J)V");
    jniConstants->midAtomicLongGet = (*env)->GetMethodID(env, jniConstants->clzAtomicLong, "get", "()J");
    jniConstants->midAtomicLongInit = (*env)->GetMethodID(env, jniConstants->clzAtomicLong, "<init>", "()V");
    jniConstants->midAtomicReferenceSet = (*env)->GetMethodID(env, jniConstants->clzAtomicReference, "set", "(Ljava/lang/Object;)V");
    jniConstants->midListGetI = (*env)->GetMethodID(env, jniConstants->clzList, "get", "(I)Ljava/lang/Object;");
    /* Remote constants */
    jclass clz = j_find_and_hold_clz(env, J_CLZ_PREFIX "Remote$Callbacks");
    assert(clz && "Remote.Callbacks class not found");
    jniConstants->remote.clzCallbacks = clz;
    jniConstants->remote.midAcquireCred = (*env)->GetMethodID(env, clz, "acquireCred", "(Ljava/lang/String;Ljava/lang/String;I)J");
    jniConstants->remote.midTransportMessage = (*env)->GetMethodID(env, clz, "transportMessage", "(Ljava/lang/String;)I");
    jniConstants->remote.midTransportCertificateCheck = (*env)->GetMethodID(env, clz, "transportMessageCheck", "(JILjava/lang/String;)I");
    jniConstants->remote.midTransferProgress = (*env)->GetMethodID(env, clz, "transferProgress", "(J)I");
    jniConstants->remote.midUpdateTips = (*env)->GetMethodID(env, clz, "updateTips", "(Ljava/lang/String;[B[B)I");
    jniConstants->remote.midPackProgress = (*env)->GetMethodID(env, clz, "packProgress", "(IJJ)I");
    jniConstants->remote.midPushTransferProgress = (*env)->GetMethodID(env, clz, "pushTransferProgress", "(JJI)I");
    jniConstants->remote.midPushUpdateReference = (*env)->GetMethodID(env, clz, "pushUpdateReference", "(Ljava/lang/String;Ljava/lang/String;)I");
    jniConstants->remote.midPushNegotiation = (*env)->GetMethodID(env, clz, "pushNegotiation", "([J)I");
    jniConstants->remote.midTransport = (*env)->GetMethodID(env, clz, "transport", "(J)J");
    /* java/util/List<?> */

    /* clz = j_find_and_hold_clz(env, "java/util/List");
    assert(clz && "List class not found");
    jniConstants->list.midAdd = (*env)->GetMethodID(env, clz, "add", "(Ljava/lang/Object;)z");
    jniConstants->list.midGet = (*env)->GetMethodID(env, clz, "get", "(I)Ljava/lang/Object;");
    jniConstants->list.midSize = (*env)->GetMethodID(env, clz, "size", "()I"); */

    /* oid class and methods */
    clz = j_find_and_hold_clz(env, J_CLZ_PREFIX "Oid");
    assert(clz && "Oid class not found");
    jniConstants->oid.clzOid = clz;
    jniConstants->oid.midSetId = (*env)->GetMethodID(env, clz, "setId", "([B)V");
    jniConstants->oid.midGetId = (*env)->GetMethodID(env, clz, "getId", "()[B");
    jniConstants->oid.midGetESize = (*env)->GetMethodID(env, clz, "getEffectiveSize", "()I");
    jniConstants->oid.midSetESize = (*env)->GetMethodID(env, clz, "setEffectiveSize", "(I)V");
}

void git24j_shutdown(JNIEnv *env)
{
    assert(jniConstants && env && "jvm was shutdown unexpected");
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicInt);
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicLong);
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicReference);
    (*env)->DeleteGlobalRef(env, jniConstants->clzList);
    (*env)->DeleteGlobalRef(env, jniConstants->remote.clzCallbacks);
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

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_jniShadowFree)(JNIEnv *env, jclass obj, long ptr)
{
    free((void *)ptr);
}
