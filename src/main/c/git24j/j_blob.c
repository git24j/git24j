#include "j_blob.h"
#include "j_mappers.h"
#include "j_util.h"

/** int git_blob_lookup(git_blob **blob, git_repository *repo, const git_oid *id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniLookup)(JNIEnv *env, jclass obj, jobject outBlob, long repoPtr, jobject oid)
{
    git_blob *c_blob;
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    int e = git_blob_lookup(&c_blob, (git_repository *)repoPtr, &c_oid);
    j_save_c_pointer(env, (void *)c_blob, outBlob, "set");
    return e;
}

/** int git_blob_lookup_prefix(git_blob **blob, git_repository *repo, const git_oid *id, size_t len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniLookupPrefix)(JNIEnv *env, jclass obj, jobject outBlob, long repoPtr, jobject oid, jint len)
{
    git_blob *c_blob;
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    int e = git_blob_lookup_prefix(&c_blob, (git_repository *)repoPtr, &c_oid, len);
    j_save_c_pointer(env, (void *)c_blob, outBlob, "set");
    return e;
}

/** void git_blob_free(git_blob *blob); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniFree)(JNIEnv *env, jclass obj, long blobPtr)
{
    git_blob_free((git_blob *)blobPtr);
}

/** const git_oid * git_blob_id(const git_blob *blob); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Blob_jniId)(JNIEnv *env, jclass obj, long blobPtr, jobject outId)
{
    const git_oid *c_oid = git_blob_id((git_blob *)blobPtr);
    j_git_oid_to_java(env, c_oid, outId);
}

/** git_repository * git_blob_owner(const git_blob *blob); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniOwner)(JNIEnv *env, jclass obj, long blobPtr)
{
    return (jlong)git_blob_owner((git_blob *)blobPtr);
}

/** const void * git_blob_rawcontent(const git_blob *blob); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniRawContent)(JNIEnv *env, jclass obj, long blobPtr)
{
    return (jlong)git_blob_rawcontent((git_blob *)blobPtr);
}

/** git_off_t git_blob_rawsize(const git_blob *blob); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniRawSize)(JNIEnv *env, jclass obj, long blobPtr)
{
    return (jlong)git_blob_rawsize((git_blob *)blobPtr);
}

/** int git_blob_create_from_workdir(git_oid *id, git_repository *repo, const char *relative_path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromWorkdir)(JNIEnv *env, jclass obj, jobject outId, long repoPtr, jstring relativePath)
{
    git_oid c_oid;
    char *relative_path = j_copy_of_jstring(env, relativePath, true);
    int e = git_blob_create_fromworkdir(&c_oid, (git_repository *)repoPtr, relative_path);
    free(relative_path);
    j_git_oid_to_java(env, &c_oid, outId);
    return e;
}

/** int git_blob_create_from_disk(git_oid *id, git_repository *repo, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromDisk)(JNIEnv *env, jclass obj, jobject outId, long repoPtr, jstring path)
{
    git_oid c_oid;
    char *c_path = j_copy_of_jstring(env, path, true);
    int e = git_blob_create_fromdisk(&c_oid, (git_repository *)repoPtr, c_path);
    free(c_path);
    j_git_oid_to_java(env, &c_oid, outId);
    return e;
}

/** int git_blob_create_from_stream(git_writestream **out, git_repository *repo, const char *hintpath); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromStream)(JNIEnv *env, jclass obj, jobject outStream, long repoPtr, jstring hintPath)
{
    char *hint_path = j_copy_of_jstring(env, hintPath, true);
    git_writestream *out_stream;
    int e = git_blob_create_fromstream(&out_stream, (git_repository *)repoPtr, hint_path);
    j_save_c_pointer(env, (void *)out_stream, outStream, "set");
    free(hint_path);
    return e;
}

/** int git_blob_create_from_stream_commit(git_oid *out, git_writestream *stream); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromStreamCommit)(JNIEnv *env, jclass obj, jobject outId, long streamPtr)
{
    git_oid c_oid;
    int e = git_blob_create_fromstream_commit(&c_oid, (git_writestream *)streamPtr);
    j_git_oid_to_java(env, &c_oid, outId);
    return e;
}

/** int git_blob_create_from_buffer(git_oid *id, git_repository *repo, const void *buffer, size_t len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromBuffer)(JNIEnv *env, jclass obj, jobject outId, long repoPtr, jbyteArray buf)
{
    git_oid c_oid;
    int c_buf_len;
    unsigned char *c_buf = j_unsigned_chars_from_java(env, buf, &c_buf_len);
    int e = git_blob_create_frombuffer(&c_oid, (git_repository *)repoPtr, (const void *)c_buf, c_buf_len);
    free(c_buf);
    j_git_oid_to_java(env, &c_oid, outId);
    return e;
}

/** int git_blob_is_binary(const git_blob *blob); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniIsBinary)(JNIEnv *env, jclass obj, long blobPtr)
{
    return git_blob_is_binary((git_blob *)blobPtr);
}

/** int git_blob_dup(git_blob **out, git_blob *source); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniDup)(JNIEnv *env, jclass obj, jobject outDest, jlong srcPtr)
{
    git_blob *out;
    int e = git_blob_dup(&out, (git_blob *)srcPtr);
    j_save_c_pointer(env, (void *)out, outDest, "set");
    return e;
}
