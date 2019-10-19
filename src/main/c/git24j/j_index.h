#include "j_common.h"

#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_INDEX_H__
#define __GIT24J_INDEX_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** standard callback that forwards c-callback to java. 
     * c: standard_matched_cb(path, pathspec, ...)
     * java: class IndexMachedCallback extends BiConsumer<String, String> {
     *     @override
     *     public void accept(String path, String pathspec) {
     *     }
     * }
     * 
     * jniAddAll(indexPtr.get(), new String[]{"foo/", "bar/"}, 0, (path, pathSpec) -> { ... })
     */
    int standard_matched_cb(const char *path, const char *matched_pathspec, void *payload);

    /**int git_index_update_all(git_index *index, const git_strarray *pathspec, git_index_matched_path_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniUpdateAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jobject biConsumer);

    /**int git_index_add(git_index *index, const git_index_entry *source_entry); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAdd)(JNIEnv *env, jclass obj, jlong index, jobject srcEntry);

    /** int git_index_add_all(git_index *index, const git_strarray *pathspec, unsigned int flags, git_index_matched_path_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jint flags, jobject biConsumer);

    /** int git_index_write(git_index *index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWrite)(JNIEnv *env, jclass obj, jlong index);

    /** void git_index_free(git_index *index); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniFree)(JNIEnv *env, jclass obj, jlong index);

    /** int git_index_add_bypath(git_index *index, const char *path);*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddByPath)(JNIEnv *env, jclass obj, jlong index, jstring path);

#ifdef __cplusplus
}
#endif
#endif