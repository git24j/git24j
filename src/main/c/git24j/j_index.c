#include "j_index.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_mappers.h"
#include "j_repository.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <jni.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/**Pack jni objects to pass to update callback. */
typedef struct
{
    JNIEnv *env;
    jobject biConsumer;
} j_mached_cb_paylocads;

int standard_matched_cb(const char *path, const char *matched_pathspec, void *payload)
{
    if (payload == NULL)
    {
        return 0;
    }
    j_mached_cb_paylocads *j_payload = (j_mached_cb_paylocads *)payload;
    JNIEnv *env = j_payload->env;
    jobject biConsumer = j_payload->biConsumer;
    assert(biConsumer && "consumer object must not be null");
    jclass jclz = (*env)->GetObjectClass(env, biConsumer);
    if (jclz == NULL)
    {
        return 0;
    }
    jmethodID accept = (*env)->GetMethodID(env, jclz, "accept", "(Ljava/lang/String;Ljava/lang/String;)V");
    if (accept == NULL)
    {
        return 0;
    }
    jstring j_path = (*env)->NewStringUTF(env, path);
    jstring j_pathspec = (*env)->NewStringUTF(env, matched_pathspec);
    (*env)->CallVoidMethod(env, biConsumer, accept, j_path, j_pathspec);
    (*env)->DeleteLocalRef(env, j_path);
    (*env)->DeleteLocalRef(env, j_pathspec);
    (*env)->DeleteLocalRef(env, jclz);
    return 0;
}

/** int git_index_open(git_index **out, const char *index_path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniOpen)(JNIEnv *env, jclass obj, jobject outIndexPtr, jstring indexPath)
{
    git_index *c_out;
    char *index_path = j_copy_of_jstring(env, indexPath, false);
    int e = git_index_open(&c_out, index_path);
    (*env)->CallVoidMethod(env, outIndexPtr, jniConstants->midAtomicLongSet, (long)c_out);
    free(index_path);
    return e;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniFree)(JNIEnv *env, jclass obj, jlong index)
{
    git_index_free((git_index *)index);
}

/** git_repository * git_index_owner(const git_index *index); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniOwner)(JNIEnv *env, jclass obj, jlong indexPtr)
{
    return (jlong)git_index_owner((git_index *)indexPtr);
}

/** int git_index_caps(const git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniCaps)(JNIEnv *env, jclass obj, jlong idxPtr)
{
    return (jint)git_index_caps((git_index *)idxPtr);
}
/** int git_index_set_caps(git_index *index, int caps); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniSetCaps)(JNIEnv *env, jclass obj, jlong idxPtr, jint caps)
{
    return git_index_set_caps((git_index *)idxPtr, caps);
}

/** unsigned int git_index_version(git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniVersion)(JNIEnv *env, jclass obj, jlong idxPtr)
{
    return (jint)git_index_version((git_index *)idxPtr);
    ;
}

/** int git_index_set_version(git_index *index, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniSetVersion)(JNIEnv *env, jclass obj, jlong idxPtr, jint version)
{
    return git_index_set_version((git_index *)idxPtr, (unsigned int)version);
}

/** int git_index_read(git_index *index, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRead)(JNIEnv *env, jclass obj, jlong indexPtr, int force)
{
    return git_index_read((git_index *)indexPtr, force);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWrite)(JNIEnv *env, jclass obj, jlong index)
{
    return git_index_write((git_index *)index);
}

/** const char * git_index_path(const git_index *index); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Index_jniPath)(JNIEnv *env, jclass obj, jlong idxPtr)
{
    const char *path = git_index_path((git_index *)idxPtr);
    return (*env)->NewStringUTF(env, path);
}

/** const git_oid * git_index_checksum(git_index *index); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniChecksum)(JNIEnv *env, jclass obj, jobject outOid, jlong indexPtr)
{
    const git_oid *c_oid = git_index_checksum((git_index *)indexPtr);
    j_git_oid_to_java(env, c_oid, outOid);
}

/** int git_index_read_tree(git_index *index, const git_tree *tree); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniReadTree)(JNIEnv *env, jclass obj, jlong indexPtr, jlong treePtr)
{
    return git_index_read_tree((git_index *)indexPtr, (git_tree *)treePtr);
}

/** int git_index_write_tree(git_oid *out, git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWriteTree)(JNIEnv *env, jclass obj, jobject outOid, jlong indexPtr)
{
    git_oid c_oid = {0};
    int e = git_index_write_tree(&c_oid, (git_index *)indexPtr);
    j_git_oid_to_java(env, &c_oid, outOid);
    return e;
}
/** int git_index_write_tree_to(git_oid *out, git_index *index, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWriteTreeTo)(JNIEnv *env, jclass obj, jobject outOid, jlong indexPtr, jlong repoPtr)
{
    git_oid c_oid = {0};
    int e = git_index_write_tree_to(&c_oid, (git_index *)indexPtr, (git_repository *)repoPtr);
    j_git_oid_to_java(env, &c_oid, outOid);
    return e;
}

/** size_t git_index_entrycount(const git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryCount)(JNIEnv *env, jclass obj, jlong indexPtr)
{
    return (jint)git_index_entrycount((git_index *)indexPtr);
}

/** int git_index_clear(git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniClear)(JNIEnv *env, jclass obj, jlong indexPtr)
{
    return git_index_clear((git_index *)indexPtr);
}

/** const git_index_entry * git_index_get_byindex(git_index *index, size_t n); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniGetByIndex)(JNIEnv *env, jclass obj, jlong indexPtr, jint n)
{
    return (jlong)git_index_get_byindex((git_index *)indexPtr, (size_t)n);
}
/** const git_index_entry * git_index_get_bypath(git_index *index, const char *path, int stage); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniGetByPath)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path, jint stage)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    const git_index_entry *entry = git_index_get_bypath((git_index *)indexPtr, c_path, stage);
    free(c_path);
    return (jlong)entry;
}

/** int git_index_remove(git_index *index, const char *path, int stage); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemove)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path, jint stage)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    int e = git_index_remove((git_index *)indexPtr, c_path, stage);
    free(c_path);
    return e;
}

/** int git_index_remove_directory(git_index *index, const char *dir, int stage); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemoveDirectory)(JNIEnv *env, jclass obj, jlong indexPtr, jstring dir, jint stage)
{
    char *c_dir = j_copy_of_jstring(env, dir, true);
    int e = git_index_remove_directory((git_index *)indexPtr, c_dir, stage);
    free(c_dir);
    return e;
}

/** int git_index_add(git_index *index, const git_index_entry *source_entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAdd)(JNIEnv *env, jclass obj, jlong index, jlong entryPtr)
{
    return git_index_add((git_index *)index, (git_index_entry *)entryPtr);
}

/** int git_index_entry_stage(const git_index_entry *entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryStage)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return git_index_entry_stage((git_index_entry *)entryPtr);
}

/** int git_index_entry_is_conflict(const git_index_entry *entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryIsConflict)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return git_index_entry_is_conflict((git_index_entry *)entryPtr);
}

/** int git_index_iterator_new(git_index_iterator **iterator_out, git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outIterPtr, jlong indexPtr)
{
    git_index_iterator *iterator_out;
    int e = git_index_iterator_new(&iterator_out, (git_index *)indexPtr);
    (*env)->CallVoidMethod(env, outIterPtr, jniConstants->midAtomicLongSet, (long)iterator_out);
    return e;
}

/** int git_index_iterator_next(const git_index_entry **out, git_index_iterator *iterator); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniIteratorNext)(JNIEnv *env, jclass obj, jobject outEntryPtr, jlong iterPtr)
{
    const git_index_entry *c_out;
    int e = git_index_iterator_next(&c_out, (git_index_iterator *)iterPtr);
    (*env)->CallVoidMethod(env, outEntryPtr, jniConstants->midAtomicLongSet, (long)c_out);
    return e;
}

/** void git_index_iterator_free(git_index_iterator *iterator); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr)
{
    git_index_iterator_free((git_index_iterator *)iterPtr);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddByPath)(JNIEnv *env, jclass obj, jlong index, jstring path)
{
    git_index *c_index = (git_index *)index;
    char *c_path = j_copy_of_jstring(env, path, false);
    int error = git_index_add_bypath(c_index, c_path);
    free(c_path);
    return error;
}

/** int git_index_add_from_buffer(git_index *index, const git_index_entry *entry, const void *buffer, size_t len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddFromBuffer)(JNIEnv *env, jclass obj, jlong indexPtr, jlong entryPtr, jbyteArray buffer)
{
    int out_len;
    unsigned char *c_buffer = j_unsigned_chars_from_java(env, buffer, &out_len);
    int e = git_index_add_frombuffer((git_index *)indexPtr, (git_index_entry *)entryPtr, (void *)c_buffer, out_len);
    (*env)->ReleaseByteArrayElements(env, buffer, (jbyte *)c_buffer, 0);
    return e;
}

/** int git_index_remove_bypath(git_index *index, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemoveByPath)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    int e = git_index_remove_bypath((git_index *)indexPtr, c_path);
    free(c_path);
    return e;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jint flags, jobject biConsumer)
{
    j_mached_cb_paylocads j_payloads = {env, biConsumer};
    git_strarray c_pathspec = {0};
    git_index *c_index = (git_index *)index;
    git_strarray_of_jobject_array(env, pathspec, &c_pathspec);
    int error = git_index_add_all(c_index, &c_pathspec, (unsigned int)flags, standard_matched_cb, (void *)(&j_payloads));
    git_strarray_free(&c_pathspec);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniUpdateAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jobject biConsumer)
{
    j_mached_cb_paylocads j_payloads = {env, biConsumer};
    git_strarray c_pathspec = {0};
    git_index *c_index = (git_index *)index;

    git_strarray_of_jobject_array(env, pathspec, &c_pathspec);
    int error = git_index_update_all(c_index, &c_pathspec, standard_matched_cb, (void *)(&j_payloads));
    git_strarray_free(&c_pathspec);
    return error;
}

/** int git_index_find(size_t *at_pos, git_index *index, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniFind)(JNIEnv *env, jclass obj, jobject outPos, jlong indexPtr, jstring path)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    size_t at_pos;
    int e = git_index_find(&at_pos, (git_index *)indexPtr, c_path);
    (*env)->CallVoidMethod(env, outPos, jniConstants->midAtomicIntSet, (jint)at_pos);
    free(c_path);
    return e;
}

/** int git_index_find_prefix(size_t *at_pos, git_index *index, const char *prefix); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniFindPrefix)(JNIEnv *env, jclass obj, jobject outPos, jlong indexPtr, jstring prefix)
{
    char *c_prefix = j_copy_of_jstring(env, prefix, true);
    size_t at_pos;
    int e = git_index_find_prefix(&at_pos, (git_index *)indexPtr, c_prefix);
    (*env)->CallVoidMethod(env, outPos, jniConstants->midAtomicIntSet, (jint)at_pos);
    free(c_prefix);
    return e;
}

/** int git_index_conflict_add(git_index *index, const git_index_entry *ancestor_entry, const git_index_entry *our_entry, const git_index_entry *their_entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictAdd)(JNIEnv *env, jclass obj, jlong indexPtr, jlong ancestorEntryPtr, jlong outEntryPtr, jlong theirEntryPtr)
{
    return git_index_conflict_add((git_index *)indexPtr,
                                  (git_index_entry *)ancestorEntryPtr,
                                  (git_index_entry *)outEntryPtr,
                                  (git_index_entry *)theirEntryPtr);
}

/** int git_index_conflict_get(const git_index_entry **ancestor_out, const git_index_entry **our_out, const git_index_entry **their_out, git_index *index, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictGet)(JNIEnv *env, jclass obj, jobject ancestorOut, jobject ourOut, jobject theirOut, jlong indexPtr, jstring path)
{
    const git_index_entry *ancestor_out, *our_out, *their_out;
    char *c_path = j_copy_of_jstring(env, path, true);
    int e = git_index_conflict_get(&ancestor_out, &our_out, &their_out, (git_index *)indexPtr, c_path);
    free(c_path);
    j_atomic_long_set(env, (long)ancestor_out, ancestorOut);
    j_atomic_long_set(env, (long)our_out, ourOut);
    j_atomic_long_set(env, (long)their_out, theirOut);
    return e;
}

/** int git_index_conflict_remove(git_index *index, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictRemove)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    int e = git_index_conflict_remove((git_index *)indexPtr, c_path);
    free(c_path);
    return e;
}

/** int git_index_conflict_cleanup(git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictCleanup)(JNIEnv *env, jclass obj, jlong indexPtr)
{
    return git_index_conflict_cleanup((git_index *)indexPtr);
}

/** int git_index_has_conflicts(const git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniHasConflicts)(JNIEnv *env, jclass obj, jlong indexPtr)
{
    return git_index_has_conflicts((git_index *)indexPtr);
}

/** int git_index_conflict_iterator_new(git_index_conflict_iterator **iterator_out, git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictIteratorNew)(JNIEnv *env, jclass obj, jobject outIterPtr, jlong indexPtr)
{
    git_index_conflict_iterator *iterator_out;
    int e = git_index_conflict_iterator_new(&iterator_out, (git_index *)indexPtr);
    (*env)->CallVoidMethod(env, outIterPtr, jniConstants->midAtomicLongSet, (long)iterator_out);
    return e;
}

/** int git_index_conflict_next(const git_index_entry **ancestor_out, const git_index_entry **our_out, const git_index_entry **their_out, git_index_conflict_iterator *iterator); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictNext)(JNIEnv *env, jclass obj, jobject ancestorOut, jobject ourOut, jobject theirOut, jlong iterPtr)
{
    const git_index_entry *ancestor_out, *our_out, *their_out;
    int e = git_index_conflict_next(&ancestor_out, &our_out, &their_out, (git_index_conflict_iterator *)iterPtr);
    (*env)->CallVoidMethod(env, ancestorOut, jniConstants->midAtomicLongSet, (long)ancestor_out);
    (*env)->CallVoidMethod(env, ourOut, jniConstants->midAtomicLongSet, (long)our_out);
    (*env)->CallVoidMethod(env, theirOut, jniConstants->midAtomicLongSet, (long)their_out);
    return e;
}
/** void git_index_conflict_iterator_free(git_index_conflict_iterator *iterator); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniConflictIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr)
{
    git_index_conflict_iterator_free((git_index_conflict_iterator *)iterPtr);
}
