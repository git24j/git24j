#include "j_reference.h"
#include "j_common.h"
#include "j_exception.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>
#include <string.h>

const size_t MAX_REF_NAME_SIZE = 32768;

/**
 * int git_reference_foreach_cb(git_reference *reference, void *payload);
 * 
 * Consumer accept long as git_reference ptr and return integer
 *  */
int j_git_reference_foreach_cb(git_reference *reference, void *payload)
{
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = j_payload->env;
    jobject consumer = j_payload->consumer;
    jclass jclz = (*env)->GetObjectClass(env, consumer);
    assert(jclz && "jni error: could not resolve consumer class");
    jmethodID accept = (*env)->GetMethodID(env, jclz, "accept", "(J)I");
    // __debug_inspect(env, consumer);
    assert(accept && "jni error: could not resolve method consumer method");
    int r = (*env)->CallIntMethod(env, consumer, accept, (long)reference);
    (*env)->DeleteLocalRef(env, jclz);
    return r;
}
/**int git_reference_foreach_name_cb(const char *name, void *payload); */
int j_git_reference_foreach_name_cb(const char *name, void *payload)
{
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = j_payload->env;
    jobject consumer = j_payload->consumer;
    jclass jclz = (*env)->GetObjectClass(env, consumer);
    assert(jclz && "jni error: could not resolve consumer class");
    jstring j_name = (*env)->NewStringUTF(env, name);
    jmethodID accept = (*env)->GetMethodID(env, jclz, "accept", "(Ljava/lang/String;)I");
    assert(accept && "jni error: could not resolve method consumer method");
    int r = (*env)->CallIntMethod(env, consumer, accept, j_name);
    (*env)->DeleteLocalRef(env, j_name);
    (*env)->DeleteLocalRef(env, jclz);
    return r;
}

/**int git_reference_lookup(git_reference **out, git_repository *repo, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniLookup)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name)
{
    git_reference *out_ref;
    char *c_name = j_copy_of_jstring(env, name, false);
    int e = git_reference_lookup(&out_ref, (git_repository *)repoPtr, c_name);
    j_save_c_pointer(env, (void *)out_ref, outRef, "set");
    free(c_name);
    return e;
}
/**int git_reference_name_to_id(git_oid *out, git_repository *repo, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNameToId)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring name)
{
    git_oid c_oid = {0};
    char *c_name = j_copy_of_jstring(env, name, false);
    int e = git_reference_name_to_id(&c_oid, (git_repository *)repoPtr, c_name);
    j_git_oid_to_java(env, &c_oid, oid);
    free(c_name);
    return e;
}
/**int git_reference_dwim(git_reference **out, git_repository *repo, const char *shorthand); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniDwim)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring shorthand)
{
    git_reference *out_ref;
    char *short_hand = j_copy_of_jstring(env, shorthand, false);
    int e = git_reference_dwim(&out_ref, (git_repository *)repoPtr, short_hand);
    free(short_hand);
    j_save_c_pointer(env, (void *)out_ref, outRef, "set");
    return e;
}
/**int git_reference_symbolic_create_matching(git_reference **out, git_repository *repo, const char *name, const char *target, int force, const char *current_value, const char *log_message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSymbolicCreateMatching)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jstring target, jint force, jstring currentValue, jstring logMessage)
{
    git_reference *c_out;
    char *c_name = j_copy_of_jstring(env, name, false);
    char *c_target = j_copy_of_jstring(env, target, false);
    char *current_value = j_copy_of_jstring(env, currentValue, true);
    char *log_message = j_copy_of_jstring(env, logMessage, true);
    int e = git_reference_symbolic_create_matching(&c_out, (git_repository *)repoPtr, c_name, c_target, force, current_value, log_message);
    j_save_c_pointer(env, (void *)c_out, outRef, "set");
    free(log_message);
    free(current_value);
    free(c_target);
    free(c_name);
    return e;
}
/**int git_reference_symbolic_create(git_reference **out, git_repository *repo, const char *name, const char *target, int force, const char *log_message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSymbolicCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jstring target, jint force, jstring logMessage)
{
    git_reference *c_out;
    char *c_name = j_copy_of_jstring(env, name, false);
    char *c_target = j_copy_of_jstring(env, target, false);
    char *log_message = j_copy_of_jstring(env, logMessage, true);
    int e = git_reference_symbolic_create(&c_out, (git_repository *)repoPtr, c_name, c_target, force, log_message);
    j_save_c_pointer(env, (void *)c_out, outRef, "set");
    free(log_message);
    free(c_target);
    free(c_name);
    return e;
}
/**int git_reference_create(git_reference **out, git_repository *repo, const char *name, const git_oid *id, int force, const char *log_message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jobject oid, jint force, jstring logMessage)
{
    git_reference *c_out;
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    char *c_name = j_copy_of_jstring(env, name, false);
    char *log_message = j_copy_of_jstring(env, logMessage, true);
    int e = git_reference_create(&c_out, (git_repository *)repoPtr, c_name, &c_oid, force, log_message);
    j_save_c_pointer(env, (void *)c_out, outRef, "set");
    free(log_message);
    free(c_name);
    return e;
}
/**int git_reference_create_matching(git_reference **out, git_repository *repo, const char *name, const git_oid *id, int force, const git_oid *current_id, const char *log_message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniCreateMatching)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring name, jobject oid, jint force, jobject currentId, jstring logMessage)
{
    git_reference *c_out;
    char *c_name = j_copy_of_jstring(env, name, false);
    char *log_message = j_copy_of_jstring(env, logMessage, true);
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    git_oid current_id;
    j_git_oid_from_java(env, currentId, &current_id);
    int e = git_reference_create_matching(&c_out, (git_repository *)repoPtr, c_name, &c_oid, force, &current_id, log_message);
    free(c_name);
    free(log_message);
    return e;
}
/**const git_oid * git_reference_target(const git_reference *ref); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Reference_jniTarget)(JNIEnv *env, jclass obj, jobject oid, jlong refPtr)
{
    const git_oid *c_oid = git_reference_target((git_reference *)refPtr);
    j_git_oid_to_java(env, c_oid, oid);
}
/**const git_oid * git_reference_target_peel(const git_reference *ref); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Reference_jniTargetPeel)(JNIEnv *env, jclass obj, jobject oid, jlong refPtr)
{
    const git_oid *c_oid = git_reference_target_peel((git_reference *)refPtr);
    j_git_oid_to_java(env, c_oid, oid);
}
/**const char * git_reference_symbolic_target(const git_reference *ref); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reference_jniSymbolicTarget)(JNIEnv *env, jclass obj, jlong refPtr)
{
    const char *target = git_reference_symbolic_target((git_reference *)refPtr);
    return (*env)->NewStringUTF(env, target);
}
/**git_reference_t git_reference_type(const git_reference *ref); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniType)(JNIEnv *env, jclass obj, jlong refPtr)
{
    return git_reference_type((git_reference *)refPtr);
}
/**const char * git_reference_name(const git_reference *ref); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reference_jniName)(JNIEnv *env, jclass obj, jlong refPtr)
{
    const char *name = git_reference_name((git_reference *)refPtr);
    return (*env)->NewStringUTF(env, name);
}
/**int git_reference_resolve(git_reference **out, const git_reference *ref); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniResolve)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr)
{
    git_reference *c_out;
    int e = git_reference_resolve(&c_out, (git_reference *)refPtr);
    j_save_c_pointer(env, (void *)c_out, outRef, "set");
    return e;
}
/**git_repository * git_reference_owner(const git_reference *ref); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Reference_jniOwner)(JNIEnv *env, jclass obj, jlong refPtr)
{
    return (jlong)git_reference_owner((git_reference *)refPtr);
}
/**int git_reference_symbolic_set_target(git_reference **out, git_reference *ref, const char *target, const char *log_message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSymbolicSetTarget)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr, jstring target, jstring logMessage)
{
    git_reference *c_out;
    char *c_target = j_copy_of_jstring(env, target, false);
    char *log_message = j_copy_of_jstring(env, logMessage, false);
    int e = git_reference_symbolic_set_target(&c_out, (git_reference *)refPtr, c_target, log_message);
    j_save_c_pointer(env, (void *)c_out, outRef, "set");
    free(c_target);
    free(log_message);
    return e;
}
/**int git_reference_set_target(git_reference **out, git_reference *ref, const git_oid *id, const char *log_message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSetTarget)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr, jobject oid, jstring logMessage)
{
    git_reference *c_out;
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    char *log_message = j_copy_of_jstring(env, logMessage, true);
    int e = git_reference_set_target(&c_out, (git_reference *)refPtr, &c_oid, log_message);
    j_save_c_pointer(env, (void *)c_out, outRef, "set");
    free(log_message);
    return e;
}
/**int git_reference_rename(git_reference **new_ref, git_reference *ref, const char *new_name, int force, const char *log_message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniRename)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr, jstring newName, int force, jstring logMessage)
{
    git_reference *new_ref;
    char *new_name = j_copy_of_jstring(env, newName, false);
    char *log_message = j_copy_of_jstring(env, logMessage, true);
    int e = git_reference_rename(&new_ref, (git_reference *)refPtr, new_name, force, log_message);
    j_save_c_pointer(env, (void *)new_ref, outRef, "set");
    free(log_message);
    free(new_name);
    return e;
}
/**int git_reference_delete(git_reference *ref); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniDelete)(JNIEnv *env, jclass obj, jlong refPtr)
{
    return git_reference_delete((git_reference *)refPtr);
}
/**int git_reference_remove(git_repository *repo, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniRemove)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name)
{
    char *c_name = j_copy_of_jstring(env, name, false);
    int e = git_reference_remove((git_repository *)repoPtr, c_name);
    free(c_name);
    return e;
}
/**int git_reference_list(git_strarray *array, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniList)(JNIEnv *env, jclass obj, jobject strList, jlong repoPtr)
{
    git_strarray *c_array = (git_strarray *)malloc(sizeof(git_strarray));
    int e = git_reference_list(c_array, (git_repository *)repoPtr);
    j_strarray_to_java_list(env, c_array, strList);
    git_strarray_free(c_array);
    return e;
}
/**int git_reference_foreach(git_repository *repo, git_reference_foreach_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject callback)
{
    j_cb_payload payload = {env, callback};
    return git_reference_foreach((git_repository *)repoPtr, j_git_reference_foreach_cb, &payload);
}
/**int git_reference_foreach_name(git_repository *repo, git_reference_foreach_name_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniForeachName)(JNIEnv *env, jclass obj, jlong repoPtr, jobject consumer)
{
    j_cb_payload payload = {env, consumer};
    return git_reference_foreach_name((git_repository *)repoPtr, j_git_reference_foreach_name_cb, &payload);
}
/**int git_reference_dup(git_reference **dest, git_reference *source); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniDup)(JNIEnv *env, jclass obj, jobject outDest, jlong sourcePtr)
{
    git_reference *dest;
    int e = git_reference_dup(&dest, (git_reference *)sourcePtr);
    j_save_c_pointer(env, (void *)dest, outDest, "set");
    return e;
}
/**void git_reference_free(git_reference *ref); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Reference_jniFree)(JNIEnv *env, jclass obj, jlong refPtr)
{
    git_reference_free((git_reference *)refPtr);
}
/**int git_reference_cmp(const git_reference *ref1, const git_reference *ref2); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniCmp)(JNIEnv *env, jclass obj, jlong ref1Ptr, jlong ref2Ptr)
{
    return git_reference_cmp((git_reference *)ref1Ptr, (git_reference *)ref2Ptr);
}
/**int git_reference_iterator_new(git_reference_iterator **out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outIter, jlong repoPtr)
{
    git_reference_iterator *c_out;
    int e = git_reference_iterator_new(&c_out, (git_repository *)repoPtr);
    j_save_c_pointer(env, (void *)c_out, outIter, "set");
    return e;
}
/**int git_reference_iterator_glob_new(git_reference_iterator **out, git_repository *repo, const char *glob); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIteratorGlobNew)(JNIEnv *env, jclass obj, jobject outIter, jlong repoPtr, jstring glob)
{
    git_reference_iterator *c_out;
    char *c_glob = j_copy_of_jstring(env, glob, false);
    int e = git_reference_iterator_glob_new(&c_out, (git_repository *)repoPtr, c_glob);
    j_save_c_pointer(env, (void *)c_out, outIter, "set");
    free(c_glob);
    return e;
}
/**int git_reference_next(git_reference **out, git_reference_iterator *iter); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNext)(JNIEnv *env, jclass obj, jobject outRef, jlong iterPtr)
{
    git_reference *c_out;
    int e = git_reference_next(&c_out, (git_reference_iterator *)iterPtr);
    j_save_c_pointer(env, (void *)c_out, outRef, "set");
    return e;
}
/**int git_reference_next_name(const char **out, git_reference_iterator *iter); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNextName)(JNIEnv *env, jclass obj, jobject outName, jlong iterPtr)
{
    const char *out_str;
    int e = git_reference_next_name(&out_str, (git_reference_iterator *)iterPtr);
    jclass clz = (*env)->GetObjectClass(env, outName);
    assert(clz && "Failed to identify object class in Reference::jniNextName");
    j_call_setter_string_c(env, clz, outName, "set", out_str);
    return e;
}
/**void git_reference_iterator_free(git_reference_iterator *iter); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Reference_jniIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr)
{
    git_reference_iterator_free((git_reference_iterator *)iterPtr);
}
/**int git_reference_foreach_glob(git_repository *repo, const char *glob, git_reference_foreach_name_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniForeachGlob)(JNIEnv *env, jclass obj, jlong repoPtr, jstring glob, jobject callback)
{
    j_cb_payload payload = {env, callback};
    char *c_glob = j_copy_of_jstring(env, glob, false);
    int e = git_reference_foreach_glob((git_repository *)repoPtr, c_glob, j_git_reference_foreach_name_cb, &payload);
    free(c_glob);
    return e;
}
/**int git_reference_has_log(git_repository *repo, const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniHasLog)(JNIEnv *env, jclass obj, jlong repoPtr, jstring refname)
{
    char *c_refname = j_copy_of_jstring(env, refname, false);
    int e = git_reference_has_log((git_repository *)repoPtr, c_refname);
    free(c_refname);
    return e;
}
/**int git_reference_ensure_log(git_repository *repo, const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniEnsureLog)(JNIEnv *env, jclass obj, jlong repoPtr, jstring refname)
{
    char *c_refname = j_copy_of_jstring(env, refname, false);
    int e = git_reference_ensure_log((git_repository *)repoPtr, c_refname);
    free(c_refname);
    return e;
}
/**int git_reference_is_branch(const git_reference *ref); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsBranch)(JNIEnv *env, jclass obj, jlong refPtr)
{
    return git_reference_is_branch((git_reference *)refPtr);
}
/**int git_reference_is_remote(const git_reference *ref); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsRemote)(JNIEnv *env, jclass obj, jlong refPtr)
{
    return git_reference_is_remote((git_reference *)refPtr);
}
/**int git_reference_is_tag(const git_reference *ref); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsTag)(JNIEnv *env, jclass obj, jlong refPtr)
{
    return git_reference_is_tag((git_reference *)refPtr);
}
/**int git_reference_is_note(const git_reference *ref); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsNote)(JNIEnv *env, jclass obj, jlong refPtr)
{
    return git_reference_is_note((git_reference *)refPtr);
}

/**
 * Call git_reference_normalize_name with auto increasing buffer. 
 * Note: out_name must be free-ed after the call, *out_size must start with 0 and *out_name should start with NULL
 * */
int _git_reference_normalize_name_dynamic(char **out_name, size_t *out_size, const char *name, int flags)
{
    if (*out_size > MAX_REF_NAME_SIZE)
    {
        return GIT_EBUFS;
    }

    if (*out_size == 0)
    {
        *out_size = 256;
        free(*out_name);
    }

    *out_name = (char *)malloc(sizeof(char) * (*out_size));
    memset(*out_name, '\0', sizeof(char) * (*out_size));
    int e = git_reference_normalize_name(*out_name, *out_size, name, flags);
    if (e == GIT_EBUFS)
    {
        *out_size *= 2;
        return _git_reference_normalize_name_dynamic(out_name, out_size, name, flags);
    }
    return e;
}
/**int git_reference_normalize_name(char *buffer_out, size_t buffer_size, const char *name, unsigned int flags); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNormalizeName)(JNIEnv *env, jclass obj, jobject bufferOut, jstring name, jint flags)
{
    char *out_name = NULL;
    size_t out_size = 0;
    jclass clz = (*env)->GetObjectClass(env, bufferOut);
    assert(clz && "could not idenfity class of bufferOut in the call to Reference::jniNormalizeName");
    char *c_name = j_copy_of_jstring(env, name, false);
    int e = _git_reference_normalize_name_dynamic(&out_name, &out_size, c_name, flags);
    j_call_setter_string_c(env, clz, bufferOut, "set", out_name);
    (*env)->DeleteLocalRef(env, clz);
    free(c_name);
    free(out_name);
    out_name = NULL;
    return e;
}
/**int git_reference_peel(git_object **out, const git_reference *ref, git_object_t type); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniPeel)(JNIEnv *env, jclass obj, jobject outObj, jlong refPtr, jint objType)
{
    git_object *c_out;
    int e = git_reference_peel(&c_out, (git_reference *)refPtr, (git_object_t)objType);
    j_save_c_pointer(env, (void *)c_out, outObj, "set");
    return e;
}
/**int git_reference_is_valid_name(const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsValid)(JNIEnv *env, jclass obj, jstring refname)
{
    char *c_refname = j_copy_of_jstring(env, refname, false);
    int e = git_reference_is_valid_name(c_refname);
    free(c_refname);
    return e;
}
/**const char * git_reference_shorthand(const git_reference *ref); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reference_jniShorthand)(JNIEnv *env, jclass obj, jlong refPtr)
{
    const char *res = git_reference_shorthand((git_reference *)refPtr);
    return (*env)->NewStringUTF(env, res);
}