#include "j_status.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_exception.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <stdio.h>

JNIEXPORT jint JNICALL J_MAKE_METHOD(Status_jniFile)(JNIEnv *env, jclass obj, jobject atomInt, jobject repo, jstring path)
{
    assert(atomInt && "receiving object must not be null");
    jclass jclz = (*env)->GetObjectClass(env, atomInt);
    jmethodID setter = (*env)->GetMethodID(env, jclz, "set", "(I)V");
    assert(setter && "setter 'set(int x)' not found");
    git_repository *c_repo = (git_repository *)repo;
    char *c_path = j_copy_of_jstring(env, path, false);
    unsigned int status;
    int error = git_status_file(&status, c_repo, c_path);
    free(c_path);
    (*env)->CallVoidMethod(env, atomInt, setter, status);
    (*env)->DeleteLocalRef(env, jclz);
    return error;
}