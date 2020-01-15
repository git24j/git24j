#include "j_tag.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** int git_tag_lookup_prefix(git_tag **out, git_repository *repo, const git_oid *id, size_t len); */
/** void git_tag_free(git_tag *tag); */
/** const git_oid * git_tag_id(const git_tag *tag); */
/** git_repository * git_tag_owner(const git_tag *tag); */

/** int git_tag_target(git_object **target_out, const git_tag *tag); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniTarget)(JNIEnv *env, jclass obj, jobject outTargetPtr, jlong tagPtr)
{
    git_object *target_out;
    int e = git_tag_target(&target_out, (git_tag *)tagPtr);
    (*env)->CallVoidMethod(env, outTargetPtr, jniConstants->midAtomicLongSet, (long)target_out);
    return e;
}
/** const git_oid * git_tag_target_id(const git_tag *tag); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Tag_jniTargetId)(JNIEnv *env, jclass obj, jlong tagPtr, jobject outOid)
{
    const git_oid *c_oid = git_tag_target_id((const git_tag *)tagPtr);
    j_git_oid_to_java(env, c_oid, outOid);
}

/** git_object_t git_tag_target_type(const git_tag *tag); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniTargetType)(JNIEnv *env, jclass obj, jlong tagPtr)
{
    return (jint)git_tag_target_type((const git_tag *)tagPtr);
}
/** const char * git_tag_name(const git_tag *tag); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Tag_jniName)(JNIEnv *env, jclass obj, jlong tagPtr)
{
    const char *c_name = git_tag_name((const git_tag *)tagPtr);
    return (*env)->NewStringUTF(env, c_name);
}

/** const git_signature * git_tag_tagger(const git_tag *tag); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Tag_jniTagger)(JNIEnv *env, jclass obj, jobject outSig, jlong tagPtr)
{
    const git_signature *tagger = git_tag_tagger((const git_tag *)tagPtr);
    j_signature_to_java(env, tagger, outSig);
}

/** const char * git_tag_message(const git_tag *tag); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Tag_jniMessage)(JNIEnv *env, jclass obj, jlong tagPtr)
{
    const char *message = git_tag_message((const git_tag *)tagPtr);
    return (*env)->NewStringUTF(env, message);
}