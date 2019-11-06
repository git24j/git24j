#include <git2.h>
#include <jni.h>
#include <stdbool.h>
#ifndef __GIT24J_MAPPERS_H__
#define __GIT24J_MAPPERS_H__
#ifdef __cplusplus
extern "C"
{
#endif
    /**
     * Duplicate c string, returned string needs to be free-ed separately.
     * */
    char *j_strdup(const char *src);

    /** 
     * Cast jstring to const char*, throw java error if failed.
     * throw java NullPointer error if not nullable, but jstr is null.
     * returned pointer needs to be free-ed.
     */
    char *j_copy_of_jstring(JNIEnv *env, jstring jstr, bool nullable);

    /** Copy values of git_buf to java::Buf */
    void j_git_buf_to_java(JNIEnv *env, git_buf *c_buf, jobject buf);

    /** copy values of java Index.Entry object to git_index_entry struct. */
    void index_entry_from_java(JNIEnv *env, git_index_entry *c_entry, jobject entry);

    /** 
     * Create jni jbyteArray from c unsigned char array. 
     * Returned jbyteArray should be copied and then released.
     * Don't try to retain jni objects unless you know what you are doing.
     * */
    jbyteArray j_byte_array_from_c(JNIEnv *env, const unsigned char *buf, int len);

    /** 
     * Copy jni jbyteArray to unsigned char array. 
     * @param array jni array from which content is copied.
     * @param out_len length of the output array.
     * @return pointer to the output array.
     * Returned array should be free-ed by the caller.
     * */
    unsigned char *j_unsigned_chars_from_java(JNIEnv *env, jbyteArray array, int *out_len);

    /** Copy git_oid value to java Oid vis setter. */
    void j_git_oid_to_java(JNIEnv *env, const git_oid *c_oid, jobject oid);

    /** Copy value of java Oid to git_oid struct in c. */
    void j_git_oid_from_java(JNIEnv *env, jobject oid, git_oid *c_oid);

    /**
     * call `String obj.getXXX()` and get the result string. 
     * Note: User must free the returned string.
     */
    char *j_call_getter_string(JNIEnv *env, jclass clz, jobject obj, const char *methodName);

    /** Call `obj.method(val)` to set a java object value. */
    void j_call_setter_long(JNIEnv *env, jclass clz, jobject obj, const char *method, jlong val);

    /** Call `obj.method(val)` to set a java object value. */
    void j_call_setter_int(JNIEnv *env, jclass clz, jobject obj, const char *method, jint val);

    /** Call `obj.method(val)` to set a java object value. */
    void j_call_setter_string(JNIEnv *env, jclass clz, jobject obj, const char *method, jstring val);

    /** Call `obj.method(val)` to set a java object value. */
    void j_call_setter_string_c(JNIEnv *env, jclass clz, jobject obj, const char *method, const char *val);

    /** Call `obj.method(val)` to set byte array values to object. */
    void j_call_setter_byte_array(JNIEnv *env, jclass clz, jobject obj, const char *method, jbyteArray val);

    /**Call `obj.method()` to get */
    jbyteArray j_call_getter_byte_array(JNIEnv *env, jclass clz, jobject obj, const char *method);

    /** Call obj.field = val to set value. */
    void j_set_object_field(JNIEnv *env, jclass clz, jobject obj, jobject val, const char *field, const char *sig);

    /** Call obj.field = val to set string value. */
    void j_set_string_field_c(JNIEnv *env, jclass clz, jobject obj, const char *val, const char *field);

#ifdef __cplusplus
}
#endif
#endif
