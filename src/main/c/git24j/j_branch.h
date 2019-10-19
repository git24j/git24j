#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_BRANCH_H__
#define __GIT24J_BRANCH_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_branch_create(git_reference **out, git_repository *repo, const char *branch_name, const git_commit *target, int force); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jlong targetPtr, jint force);

#ifdef __cplusplus
}
#endif
#endif