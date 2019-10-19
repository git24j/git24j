#include "j_branch.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_exception.h"
#include "j_mappers.h"
#include "j_util.h"
#include <git2.h>
#include <stdio.h>

JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jlong targetPtr, jint force)
{
    git_reference *c_ref;
    char *c_branch_name = j_copy_of_jstring(env, branchName, false);
    int error = git_branch_create(&c_ref, (git_repository *)repoPtr, c_branch_name, (const git_commit *)targetPtr, force);
    free(c_branch_name);
    j_save_c_pointer(env, (void *)c_ref, outRef, "set");
    return error;
}