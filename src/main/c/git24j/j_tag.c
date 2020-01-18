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

/** int git_tag_create(git_oid *oid, git_repository *repo, const char *tag_name, const git_object *target, const git_signature *tagger, const char *message, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreate)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tagName, jlong targetPtr, jobject tagger, jstring message, jint force)
{
    int e = 0;
    git_signature *c_tagger = NULL;
    e = j_signature_from_java(env, tagger, &c_tagger);
    if (e != 0)
    {
        goto free_and_return;
    }

    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    char *tag_name = j_copy_of_jstring(env, message, true);
    char *c_message = j_copy_of_jstring(env, message, true);
    e = git_tag_create(&c_oid, (git_repository *)repoPtr, tag_name, (const git_object *)targetPtr, c_tagger, c_message, force);
    free(c_message);
    free(tag_name);
free_and_return:
    git_signature_free(c_tagger);
    return e;
}
/** int git_tag_annotation_create(git_oid *oid, git_repository *repo, const char *tag_name, const git_object *target, const git_signature *tagger, const char *message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniAnnotationCreate)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tagName, jlong targetPtr, jobject tagger, jstring message)
{
    int e;
    git_signature *c_tagger = NULL;
    e = j_signature_from_java(env, tagger, &c_tagger);
    if (e != 0)
    {
        goto free_and_return;
    }
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    char *tag_name = j_copy_of_jstring(env, tagName, c_tagger);
    char *c_message = j_copy_of_jstring(env, message, true);
    e = git_tag_annotation_create(&c_oid, (git_repository *)repoPtr, tag_name, (git_object *)targetPtr, c_tagger, c_message);
    free(tag_name);
    free(c_message);
free_and_return:
    git_signature_free(c_tagger);
    return e;
}

/** int git_tag_create_from_buffer(git_oid *oid, git_repository *repo, const char *buffer, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreateFromBuffer)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring buffer, jint force)
{
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    char *c_buffer = j_copy_of_jstring(env, buffer, true);
    int e = git_tag_create_frombuffer(&c_oid, (git_repository *)repoPtr, c_buffer, force);
    j_git_oid_to_java(env, &c_oid, oid);
    free(c_buffer);
    return e;
}

/** int git_tag_create_lightweight(git_oid *oid, git_repository *repo, const char *tag_name, const git_object *target, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreateLightWeight)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tagName, jlong targetPtr, jint force)
{
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    char *tag_name = j_copy_of_jstring(env, tagName, true);
    int e = git_tag_create_lightweight(&c_oid, (git_repository *)repoPtr, tag_name, (const git_object *)targetPtr, force);
    free(tag_name);
    return e;
}

/** int git_tag_delete(git_repository *repo, const char *tag_name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniDelete)(JNIEnv *env, jclass obj, jlong repoPtr, jstring tagName)
{
    char *tag_name = j_copy_of_jstring(env, tagName, true);
    int e = git_tag_delete((git_repository *)repoPtr, tag_name);

    free(tag_name);
    return e;
}
