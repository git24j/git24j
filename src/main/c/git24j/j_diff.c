#include "j_diff.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/**Pack jni objects to pass to update callback. */
typedef struct
{
    JNIEnv *env;
    jobject fileCb;
    jobject binaryCb;
    jobject hunkCb;
    jobject lineCb;
} j_diff_callback_payload;

/**
 * Callback to operate on each file when iterating over a diff
 *
 * @see git_diff_file_cb
 *
 * @param delta A pointer to the delta data for the file
 * @param progress Goes from 0 to 1 over the diff
 * @param payload {@link j_diff_callback_payload}
 * @return non-zero to terminate the iteration
 */
int j_git_diff_file_cb(const git_diff_delta *delta, float progress, void *payload)
{
    j_diff_callback_payload *j_payload = (j_diff_callback_payload *)payload;
    JNIEnv *env = j_payload->env;
    jobject consumer = j_payload->fileCb;
    assert(consumer && "consumer must not be null");
    jclass jclz = (*env)->GetObjectClass(env, consumer);
    assert(jclz && "jni error: could not resolve consumer class");
    /** int accept(long diffDeltaPtr, float progress) */
    jmethodID accept = (*env)->GetMethodID(env, jclz, "accept", "(JF)I");
    assert(accept && "jni error: could not resolve method consumer method");
    int r = (*env)->CallIntMethod(env, consumer, accept, (long)delta, progress);
    (*env)->DeleteLocalRef(env, jclz);
    return r;
}

/**
 * Callback to operate on binary content when iterating over a diff
 *
 * @see git_diff_binary_cb
 *
 * @param delta A pointer to the delta data for the file
 * @param binary
 * @param payload {@link j_cb_payload}
 * @return non-zero to terminate the iteration
 */

int j_git_diff_binary_cb(const git_diff_delta *delta, const git_diff_binary *binary, void *payload)
{
    j_diff_callback_payload *j_payload = (j_diff_callback_payload *)payload;
    JNIEnv *env = j_payload->env;
    jobject consumer = j_payload->binaryCb;
    assert(consumer && "consumer must not be null");
    jclass jclz = (*env)->GetObjectClass(env, consumer);
    assert(jclz && "jni error: could not resolve consumer class");
    /** int accept(long diffDeltaPtr, long binaryPtr) */
    jmethodID accept = (*env)->GetMethodID(env, jclz, "accept", "(JJ)I");
    assert(accept && "jni error: could not resolve method consumer method");
    int r = (*env)->CallIntMethod(env, consumer, accept, (long)delta, (long)binary);
    (*env)->DeleteLocalRef(env, jclz);
    return r;
}

/**
 * Callback to operate on each hunk when iterating over a diff
 *
 * @see git_diff_hunk_cb
 *
 * @param delta A pointer to the delta data for the file
 * @param progress Goes from 0 to 1 over the diff
 * @param payload {@link j_cb_payload}
 * @return non-zero to terminate the iteration
 */
int j_git_diff_hunk_cb(const git_diff_delta *delta, const git_diff_hunk *hunk, void *payload)
{
    j_diff_callback_payload *j_payload = (j_diff_callback_payload *)payload;
    JNIEnv *env = j_payload->env;
    jobject consumer = j_payload->hunkCb;
    assert(consumer && "consumer must not be null");
    jclass jclz = (*env)->GetObjectClass(env, consumer);
    assert(jclz && "jni error: could not resolve consumer class");
    /** int accept(long diffDeltaPtr, long binaryPtr) */
    jmethodID accept = (*env)->GetMethodID(env, jclz, "accept", "(JJ)I");
    assert(accept && "jni error: could not resolve method consumer method");
    int r = (*env)->CallIntMethod(env, consumer, accept, (long)delta, (long)hunk);
    (*env)->DeleteLocalRef(env, jclz);
    return r;
}

int j_git_diff_line_cb(const git_diff_delta *delta, const git_diff_hunk *hunk, const git_diff_line *line, void *payload)
{
    j_diff_callback_payload *j_payload = (j_diff_callback_payload *)payload;
    JNIEnv *env = j_payload->env;
    jobject consumer = j_payload->lineCb;
    assert(consumer && "consumer must not be null");
    jclass jclz = (*env)->GetObjectClass(env, consumer);
    assert(jclz && "jni error: could not resolve consumer class");
    /** int accept(long diffDeltaPtr, long binaryPtr) */
    jmethodID accept = (*env)->GetMethodID(env, jclz, "accept", "(JJJ)I");
    assert(accept && "jni error: could not resolve method consumer method");
    int r = (*env)->CallIntMethod(env, consumer, accept, (long)delta, (long)hunk, (long)line);
    (*env)->DeleteLocalRef(env, jclz);
    return r;
}

/** -------- Wrapper Body ---------- */
/** int git_diff_init_options(git_diff_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_diff_init_options((git_diff_options *)optsPtr, version);
    return r;
}

/** int git_diff_find_init_options(git_diff_find_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFindInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_diff_find_init_options((git_diff_find_options *)optsPtr, version);
    return r;
}

/** void git_diff_free(git_diff *diff); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniFree)(JNIEnv *env, jclass obj, jlong diffPtr)
{
    git_diff_free((git_diff *)diffPtr);
}

/** int git_diff_tree_to_tree(git_diff **diff, git_repository *repo, git_tree *old_tree, git_tree *new_tree, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToTree)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong newTreePtr, jlong optsPtr)
{
    git_diff *c_diff;
    int r = git_diff_tree_to_tree(&c_diff, (git_repository *)repoPtr, (git_tree *)oldTreePtr, (git_tree *)newTreePtr, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, diff, jniConstants->midAtomicLongSet, (long)c_diff);
    git_diff_free(c_diff);
    return r;
}

/** int git_diff_tree_to_index(git_diff **diff, git_repository *repo, git_tree *old_tree, git_index *index, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToIndex)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong indexPtr, jlong optsPtr)
{
    git_diff *c_diff;
    int r = git_diff_tree_to_index(&c_diff, (git_repository *)repoPtr, (git_tree *)oldTreePtr, (git_index *)indexPtr, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, diff, jniConstants->midAtomicLongSet, (long)c_diff);
    git_diff_free(c_diff);
    return r;
}

/** int git_diff_index_to_workdir(git_diff **diff, git_repository *repo, git_index *index, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniIndexToWorkdir)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong indexPtr, jlong optsPtr)
{
    git_diff *c_diff;
    int r = git_diff_index_to_workdir(&c_diff, (git_repository *)repoPtr, (git_index *)indexPtr, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, diff, jniConstants->midAtomicLongSet, (long)c_diff);
    git_diff_free(c_diff);
    return r;
}

/** int git_diff_tree_to_workdir(git_diff **diff, git_repository *repo, git_tree *old_tree, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToWorkdir)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong optsPtr)
{
    git_diff *c_diff;
    int r = git_diff_tree_to_workdir(&c_diff, (git_repository *)repoPtr, (git_tree *)oldTreePtr, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, diff, jniConstants->midAtomicLongSet, (long)c_diff);
    git_diff_free(c_diff);
    return r;
}

/** int git_diff_tree_to_workdir_with_index(git_diff **diff, git_repository *repo, git_tree *old_tree, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToWorkdirWithIndex)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong optsPtr)
{
    git_diff *c_diff;
    int r = git_diff_tree_to_workdir_with_index(&c_diff, (git_repository *)repoPtr, (git_tree *)oldTreePtr, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, diff, jniConstants->midAtomicLongSet, (long)c_diff);
    git_diff_free(c_diff);
    return r;
}

/** int git_diff_index_to_index(git_diff **diff, git_repository *repo, git_index *old_index, git_index *new_index, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniIndexToIndex)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldIndexPtr, jlong newIndexPtr, jlong optsPtr)
{
    git_diff *c_diff;
    int r = git_diff_index_to_index(&c_diff, (git_repository *)repoPtr, (git_index *)oldIndexPtr, (git_index *)newIndexPtr, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, diff, jniConstants->midAtomicLongSet, (long)c_diff);
    git_diff_free(c_diff);
    return r;
}

/** int git_diff_merge(git_diff *onto, const git_diff *from); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniMerge)(JNIEnv *env, jclass obj, jlong ontoPtr, jlong fromPtr)
{
    int r = git_diff_merge((git_diff *)ontoPtr, (git_diff *)fromPtr);
    return r;
}

/** int git_diff_find_similar(git_diff *diff, const git_diff_find_options *options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFindSimilar)(JNIEnv *env, jclass obj, jlong diffPtr, jlong optionsPtr)
{
    int r = git_diff_find_similar((git_diff *)diffPtr, (git_diff_find_options *)optionsPtr);
    return r;
}

/** size_t git_diff_num_deltas(const git_diff *diff); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniNumDeltas)(JNIEnv *env, jclass obj, jlong diffPtr)
{
    size_t r = git_diff_num_deltas((git_diff *)diffPtr);
    return r;
}

/** size_t git_diff_num_deltas_of_type(const git_diff *diff, git_delta_t type); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniNumDeltasOfType)(JNIEnv *env, jclass obj, jlong diffPtr, jint type)
{
    size_t r = git_diff_num_deltas_of_type((git_diff *)diffPtr, type);
    return r;
}

/** const git_diff_delta * git_diff_get_delta(const git_diff *diff, size_t idx); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Diff_jniGetDelta)(JNIEnv *env, jclass obj, jlong diffPtr, jint idx)
{
    const git_diff_delta *r = git_diff_get_delta((git_diff *)diffPtr, idx);
    return (long)r;
}

/** int git_diff_is_sorted_icase(const git_diff *diff); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniIsSortedIcase)(JNIEnv *env, jclass obj, jlong diffPtr)
{
    int r = git_diff_is_sorted_icase((git_diff *)diffPtr);
    return r;
}

/** int git_diff_foreach(git_diff *diff, git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniForeach)(JNIEnv *env, jclass obj, jlong diffPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb)
{
    j_diff_callback_payload payload = {env};
    payload.fileCb = fileCb;
    payload.binaryCb = binaryCb;
    payload.hunkCb = hunkCb;
    payload.lineCb = lineCb;
    int r = git_diff_foreach((git_diff *)diffPtr, j_git_diff_file_cb, j_git_diff_binary_cb, j_git_diff_hunk_cb, j_git_diff_line_cb, &payload);
    return r;
}

/** char git_diff_status_char(git_delta_t status); */
JNIEXPORT jchar JNICALL J_MAKE_METHOD(Diff_jniStatusChar)(JNIEnv *env, jclass obj, jint status)
{
    char r = git_diff_status_char(status);
    return r;
}

/** int git_diff_print(git_diff *diff, git_diff_format_t format, git_diff_line_cb print_cb, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniPrint)(JNIEnv *env, jclass obj, jlong diffPtr, jint format, jobject printCb)
{
    j_diff_callback_payload payload = {env};
    payload.lineCb = printCb;
    int r = git_diff_print((git_diff *)diffPtr, format, j_git_diff_line_cb, &payload);
    return r;
}

/** int git_diff_to_buf(git_buf *out, git_diff *diff, git_diff_format_t format); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniToBuf)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jint format)
{
    git_buf c_out = {0};
    int r = git_diff_to_buf(&c_out, (git_diff *)diffPtr, format);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}

/** int git_diff_blobs(const git_blob *old_blob, const char *old_as_path, const git_blob *new_blob, const char *new_as_path, const git_diff_options *options, git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBlobs)(JNIEnv *env, jclass obj, jlong oldBlobPtr, jstring old_as_path, jlong newBlobPtr, jstring new_as_path, jlong optionsPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb)
{
    char *c_old_as_path = j_copy_of_jstring(env, old_as_path, true);
    char *c_new_as_path = j_copy_of_jstring(env, new_as_path, true);
    j_diff_callback_payload payload = {env};
    payload.fileCb = fileCb;
    payload.binaryCb = binaryCb;
    payload.hunkCb = hunkCb;
    payload.lineCb = lineCb;
    int r = git_diff_blobs((git_blob *)oldBlobPtr, c_old_as_path, (git_blob *)newBlobPtr, c_new_as_path, (git_diff_options *)optionsPtr, j_git_diff_file_cb, j_git_diff_binary_cb, j_git_diff_hunk_cb, j_git_diff_line_cb, &payload);
    free(c_old_as_path);
    free(c_new_as_path);
    return r;
}

/** int git_diff_blob_to_buffer(const git_blob *old_blob, const char *old_as_path, const char *buffer, size_t buffer_len, const char *buffer_as_path, const git_diff_options *options, git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBlobToBuffer)(JNIEnv *env, jclass obj, jlong oldBlobPtr, jstring old_as_path, jstring buffer, jint bufferLen, jstring buffer_as_path, jlong optionsPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb)
{
    char *c_old_as_path = j_copy_of_jstring(env, old_as_path, true);
    char *c_buffer = j_copy_of_jstring(env, buffer, true);
    char *c_buffer_as_path = j_copy_of_jstring(env, buffer_as_path, true);
    j_diff_callback_payload payload = {env};
    payload.fileCb = fileCb;
    payload.binaryCb = binaryCb;
    payload.hunkCb = hunkCb;
    payload.lineCb = lineCb;
    int r = git_diff_blob_to_buffer((git_blob *)oldBlobPtr, c_old_as_path, c_buffer, bufferLen, c_buffer_as_path, (git_diff_options *)optionsPtr, j_git_diff_file_cb, j_git_diff_binary_cb, j_git_diff_hunk_cb, j_git_diff_line_cb, &payload);
    free(c_old_as_path);
    free(c_buffer);
    free(c_buffer_as_path);
    return r;
}

/** int git_diff_buffers(const void *old_buffer, size_t old_len, const char *old_as_path, const void *new_buffer, size_t new_len, const char *new_as_path, const git_diff_options *options, git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBuffers)(JNIEnv *env, jclass obj, jbyteArray oldBuffer, jint oldLen, jstring old_as_path, jbyteArray newBuffer, jint newLen, jstring new_as_path, jlong optionsPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb)
{
    int old_buffer_len;
    unsigned char *c_old_buffer = j_unsigned_chars_from_java(env, oldBuffer, &old_buffer_len);
    char *c_old_as_path = j_copy_of_jstring(env, old_as_path, true);
    int new_buffer_len;
    unsigned char *c_new_buffer = j_unsigned_chars_from_java(env, newBuffer, &new_buffer_len);
    char *c_new_as_path = j_copy_of_jstring(env, new_as_path, true);
    j_diff_callback_payload payload = {env};
    payload.fileCb = fileCb;
    payload.binaryCb = binaryCb;
    payload.hunkCb = hunkCb;
    payload.lineCb = lineCb;
    int r = git_diff_buffers((void *)c_old_buffer, oldLen, c_old_as_path, (void *)c_new_buffer, newLen, c_new_as_path, (git_diff_options *)optionsPtr, j_git_diff_file_cb, j_git_diff_binary_cb, j_git_diff_hunk_cb, j_git_diff_line_cb, &payload);
    (*env)->ReleaseByteArrayElements(env, oldBuffer, (jbyte *)c_old_buffer, 0);
    free(c_old_as_path);
    (*env)->ReleaseByteArrayElements(env, newBuffer, (jbyte *)c_new_buffer, 0);
    free(c_new_as_path);
    return r;
}

/** int git_diff_from_buffer(git_diff **out, const char *content, size_t content_len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFromBuffer)(JNIEnv *env, jclass obj, jobject out, jstring content, jint contentLen)
{
    git_diff *c_out;
    char *c_content = j_copy_of_jstring(env, content, true);
    int r = git_diff_from_buffer(&c_out, c_content, contentLen);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_diff_free(c_out);
    free(c_content);
    return r;
}

/** int git_diff_get_stats(git_diff_stats **out, git_diff *diff); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniGetStats)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr)
{
    git_diff_stats *c_out;
    int r = git_diff_get_stats(&c_out, (git_diff *)diffPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_diff_stats_free(c_out);
    return r;
}

/** size_t git_diff_stats_files_changed(const git_diff_stats *stats); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsFilesChanged)(JNIEnv *env, jclass obj, jlong statsPtr)
{
    size_t r = git_diff_stats_files_changed((git_diff_stats *)statsPtr);
    return r;
}

/** size_t git_diff_stats_insertions(const git_diff_stats *stats); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsInsertions)(JNIEnv *env, jclass obj, jlong statsPtr)
{
    size_t r = git_diff_stats_insertions((git_diff_stats *)statsPtr);
    return r;
}

/** size_t git_diff_stats_deletions(const git_diff_stats *stats); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsDeletions)(JNIEnv *env, jclass obj, jlong statsPtr)
{
    size_t r = git_diff_stats_deletions((git_diff_stats *)statsPtr);
    return r;
}

/** int git_diff_stats_to_buf(git_buf *out, const git_diff_stats *stats, git_diff_stats_format_t format, size_t width); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsToBuf)(JNIEnv *env, jclass obj, jobject out, jlong statsPtr, jint format, jint width)
{
    git_buf c_out = {0};
    int r = git_diff_stats_to_buf(&c_out, (git_diff_stats *)statsPtr, format, width);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}

/** void git_diff_stats_free(git_diff_stats *stats); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniStatsFree)(JNIEnv *env, jclass obj, jlong statsPtr)
{
    git_diff_stats_free((git_diff_stats *)statsPtr);
}

/** int git_diff_format_email(git_buf *out, git_diff *diff, const git_diff_format_email_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFormatEmail)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jlong optsPtr)
{
    git_buf c_out = {0};
    int r = git_diff_format_email(&c_out, (git_diff *)diffPtr, (git_diff_format_email_options *)optsPtr);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}

/** int git_diff_commit_as_email(git_buf *out, git_repository *repo, git_commit *commit, size_t patch_no, size_t total_patches, git_diff_format_email_flags_t flags, const git_diff_options *diff_opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniCommitAsEmail)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong commitPtr, jint patchNo, jint totalPatches, jint flags, jlong diffOptsPtr)
{
    git_buf c_out = {0};
    int r = git_diff_commit_as_email(&c_out, (git_repository *)repoPtr, (git_commit *)commitPtr, patchNo, totalPatches, flags, (git_diff_options *)diffOptsPtr);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}

/** int git_diff_format_email_init_options(git_diff_format_email_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFormatEmailInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_diff_format_email_init_options((git_diff_format_email_options *)optsPtr, version);
    return r;
}

/** int git_diff_patchid_init_options(git_diff_patchid_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniPatchidInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_diff_patchid_init_options((git_diff_patchid_options *)optsPtr, version);
    return r;
}

/** int git_diff_patchid(git_oid *out, git_diff *diff, git_diff_patchid_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniPatchid)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jlong optsPtr)
{
    git_oid c_out;
    int r = git_diff_patchid(&c_out, (git_diff *)diffPtr, (git_diff_patchid_options *)optsPtr);
    j_git_oid_to_java(env, &c_out, out);
    return r;
}

/** int git_patch_from_blobs(git_patch **out, const git_blob *old_blob, const char *old_as_path, const git_blob *new_blob, const char *new_as_path, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBlobs)(JNIEnv *env, jclass obj, jobject out, jlong oldBlobPtr, jstring old_as_path, jlong newBlobPtr, jstring new_as_path, jlong optsPtr)
{
    git_patch *c_out;
    char *c_old_as_path = j_copy_of_jstring(env, old_as_path, true);
    char *c_new_as_path = j_copy_of_jstring(env, new_as_path, true);
    int r = git_patch_from_blobs(&c_out, (git_blob *)oldBlobPtr, c_old_as_path, (git_blob *)newBlobPtr, c_new_as_path, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_patch_free(c_out);
    free(c_old_as_path);
    free(c_new_as_path);
    return r;
}

/** int git_patch_from_blob_and_buffer(git_patch **out, const git_blob *old_blob, const char *old_as_path, const void *buffer, size_t buffer_len, const char *buffer_as_path, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBlobAndBuffer)(JNIEnv *env, jclass obj, jobject out, jlong oldBlobPtr, jstring old_as_path, jbyteArray buffer, jint bufferLen, jstring buffer_as_path, jlong optsPtr)
{
    git_patch *c_out;
    char *c_old_as_path = j_copy_of_jstring(env, old_as_path, true);
    int buffer_len;
    unsigned char *c_buffer = j_unsigned_chars_from_java(env, buffer, &buffer_len);
    char *c_buffer_as_path = j_copy_of_jstring(env, buffer_as_path, true);
    int r = git_patch_from_blob_and_buffer(&c_out, (git_blob *)oldBlobPtr, c_old_as_path, (void *)c_buffer, bufferLen, c_buffer_as_path, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_patch_free(c_out);
    free(c_old_as_path);
    (*env)->ReleaseByteArrayElements(env, buffer, (jbyte *)c_buffer, 0);
    free(c_buffer_as_path);
    return r;
}

/** int git_patch_from_buffers(git_patch **out, const void *old_buffer, size_t old_len, const char *old_as_path, const void *new_buffer, size_t new_len, const char *new_as_path, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBuffers)(JNIEnv *env, jclass obj, jobject out, jbyteArray oldBuffer, jint oldLen, jstring old_as_path, jbyteArray newBuffer, jint newLen, jstring new_as_path, jlong optsPtr)
{
    git_patch *c_out;
    int old_buffer_len;
    unsigned char *c_old_buffer = j_unsigned_chars_from_java(env, oldBuffer, &old_buffer_len);
    char *c_old_as_path = j_copy_of_jstring(env, old_as_path, true);
    int new_buffer_len;
    unsigned char *c_new_buffer = j_unsigned_chars_from_java(env, newBuffer, &new_buffer_len);
    char *c_new_as_path = j_copy_of_jstring(env, new_as_path, true);
    int r = git_patch_from_buffers(&c_out, (void *)c_old_buffer, oldLen, c_old_as_path, (void *)c_new_buffer, newLen, c_new_as_path, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_patch_free(c_out);
    (*env)->ReleaseByteArrayElements(env, oldBuffer, (jbyte *)c_old_buffer, 0);
    free(c_old_as_path);
    (*env)->ReleaseByteArrayElements(env, newBuffer, (jbyte *)c_new_buffer, 0);
    free(c_new_as_path);
    return r;
}

/** int git_patch_get_hunk(const git_diff_hunk **out, size_t *lines_in_hunk, git_patch *patch, size_t hunk_idx); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniGetHunk)(JNIEnv *env, jclass obj, jobject out, jobject linesInHunk, jlong patchPtr, jint hunkIdx)
{
    const git_diff_hunk *c_out;
    size_t c_lines_in_hunk;
    int r = git_patch_get_hunk(&c_out, &c_lines_in_hunk, (git_patch *)patchPtr, hunkIdx);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    (*env)->CallVoidMethod(env, linesInHunk, jniConstants->midAtomicIntSet, c_lines_in_hunk);
    return r;
}

/** int git_patch_get_line_in_hunk(const git_diff_line **out, git_patch *patch, size_t hunk_idx, size_t line_of_hunk); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniGetLineInHunk)(JNIEnv *env, jclass obj, jobject out, jlong patchPtr, jint hunkIdx, jint lineOfHunk)
{
    const git_diff_line *c_out;
    int r = git_patch_get_line_in_hunk(&c_out, (git_patch *)patchPtr, hunkIdx, lineOfHunk);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    return r;
}

/** int git_patch_print(git_patch *patch, git_diff_line_cb print_cb, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniPrint)(JNIEnv *env, jclass obj, jlong patchPtr, jobject printCb)
{
    j_diff_callback_payload payload = {env};
    payload.lineCb = printCb;
    int r = git_patch_print((git_patch *)patchPtr, j_git_diff_line_cb, &payload);
    return r;
}
