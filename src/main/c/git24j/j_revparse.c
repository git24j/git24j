#include "j_revparse.h"
#include "j_ensure.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>

void j_save_revspec_c_value(JNIEnv *env, git_revspec *rev_spec, jobject revSpec)
{
    jclass clz = (*env)->GetObjectClass(env, revSpec);
    assert(clz && "Could not find Revspec class from given revspec object");
    j_call_setter_long(env, clz, revSpec, "setFrom", (long)(rev_spec->from));
    j_call_setter_long(env, clz, revSpec, "setTo", (long)(rev_spec->to));
    j_call_setter_int(env, clz, revSpec, "setFlags", (long)(rev_spec->flags));
    (*env)->DeleteLocalRef(env, clz);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniCall)(JNIEnv *env, jclass obj, jobject revspec, jlong repoPtr, jstring spec)
{
    git_revspec c_revspec;
    char *c_spec = j_copy_of_jstring(env, spec, false);
    int error = git_revparse(&c_revspec, (git_repository *)repoPtr, c_spec);
    j_save_revspec_c_value(env, &c_revspec, revspec);
    free(c_spec);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniSingle)(JNIEnv *env, jclass obj, jobject outObj, jlong repoPtr, jstring spec)
{
    git_object *c_out;
    char *c_spec = j_copy_of_jstring(env, spec, false);
    int error = git_revparse_single(&c_out, (git_repository *)repoPtr, c_spec);
    j_save_c_pointer(env, (void *)c_out, outObj, "set");
    free(c_spec);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniExt)(JNIEnv *env, jclass obj, jobject outObj, jobject outRef, jlong repoPtr, jstring spec)
{
    git_object *c_out_obj;
    git_reference *c_out_ref;
    char *c_spec = j_copy_of_jstring(env, spec, false);
    int error = git_revparse_ext(&c_out_obj, &c_out_ref, (git_repository *)repoPtr, c_spec);
    j_save_c_pointer(env, (void *)c_out_obj, outObj, "set");
    j_save_c_pointer(env, (void *)c_out_ref, outRef, "set");
    free(c_spec);
    return error;
}
