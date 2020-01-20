#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_TAG_H__
#define __GIT24J_TAG_H__
#ifdef __cplusplus
extern "C"
{
#endif
    /** int git_tag_lookup_prefix(git_tag **out, git_repository *repo, const git_oid *id, size_t len); */
    /* JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniLookupPrefix)(JNIEnv *env, jclass obj, jobject outTagPtr, jlong repoPtr, jobject oid); */

    /** void git_tag_free(git_tag *tag); */
    /* JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniFree)(JNIEnv *env, jclass obj, jlong tagPtr); */
    /** const git_oid * git_tag_id(const git_tag *tag); */
    /* JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniId)(JNIEnv *env, jclass obj, jlong tagPtr); */
    /** git_repository * git_tag_owner(const git_tag *tag); */
    /* JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniOwner)(JNIEnv *env, jclass obj, jlong tagPtr); */
    /** int git_tag_target(git_object **target_out, const git_tag *tag); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniTarget)(JNIEnv *env, jclass obj, jobject outTargetPtr, jlong tagPtr);
    /** const git_oid * git_tag_target_id(const git_tag *tag); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tag_jniTargetId)(JNIEnv *env, jclass obj, jlong tagPtr, jobject outOid);

    /** git_object_t git_tag_target_type(const git_tag *tag); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniTargetType)(JNIEnv *env, jclass obj, jlong tagPtr);
    /** const char * git_tag_name(const git_tag *tag); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Tag_jniName)(JNIEnv *env, jclass obj, jlong tagPtr);
    /** const git_signature * git_tag_tagger(const git_tag *tag); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tag_jniTagger)(JNIEnv *env, jclass obj, jobject outSig, jlong tagPtr);
    /** const char * git_tag_message(const git_tag *tag); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Tag_jniMessage)(JNIEnv *env, jclass obj, jlong tagPtr);

    /** int git_tag_create(git_oid *oid, git_repository *repo, const char *tag_name, const git_object *target, const git_signature *tagger, const char *message, int force); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreate)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tagName, jlong targetPtr, jobject tagger, jstring message, jint force);
    /** int git_tag_annotation_create(git_oid *oid, git_repository *repo, const char *tag_name, const git_object *target, const git_signature *tagger, const char *message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniAnnotationCreate)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tagName, jlong targetPtr, jobject tagger, jstring message);
    /** int git_tag_create_from_buffer(git_oid *oid, git_repository *repo, const char *buffer, int force); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreateFromBuffer)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring buffer, jint force);
    /** int git_tag_create_lightweight(git_oid *oid, git_repository *repo, const char *tag_name, const git_object *target, int force); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreateLightWeight)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tagName, jlong targetPtr, jint force);
    /** int git_tag_delete(git_repository *repo, const char *tag_name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniDelete)(JNIEnv *env, jclass obj, jlong repoPtr, jstring tagName);
    /** int git_tag_list(git_strarray *tag_names, git_repository *repo); */
    /** int git_tag_list_match(git_strarray *tag_names, const char *pattern, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniList)(JNIEnv *env, jclass obj, jobjectArray tag_names, jlong repoPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniListMatch)(JNIEnv *env, jclass obj, jobjectArray tag_names, jstring pattern, jlong repoPtr);
    /** int git_tag_foreach(git_repository *repo, git_tag_foreach_cb callback, void *payload); */
    /** int git_tag_peel(git_object **tag_target_out, const git_tag *tag); */
    /** int git_tag_dup(git_tag **out, git_tag *source); */
    /** int git_tag_foreach_cb(const char *name, git_oid *oid, void *payload); */

#ifdef __cplusplus
}
#endif
#endif