#include "j_mappers.h"
#include "j_common.h"
#include "j_exception.h"
#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>

char *j_strdup(const char *src)
{
    char *copy = NULL;
    size_t len = strlen(src) + 1;
    if (src)
    {
        copy = malloc(len);
        if (copy)
        {
            memcpy(copy, src, len);
        }
    }
    return copy;
}

void j_git_buf_to_java(JNIEnv *env, git_buf *c_buf, jobject buf)
{

    jclass jclz = (*env)->GetObjectClass(env, buf);
    assert(jclz && "Could not find Buf class from given buf object");
    j_call_setter_string_c(env, jclz, buf, "setPtr", c_buf->ptr);
    j_call_setter_int(env, jclz, buf, "setSize", c_buf->size);
    j_call_setter_int(env, jclz, buf, "setAsize", c_buf->asize);
    (*env)->DeleteLocalRef(env, jclz);
}

char *j_call_getter_string(JNIEnv *env, jclass clz, jobject obj, const char *methodName)
{
    jmethodID method = (*env)->GetMethodID(env, clz, methodName, "()Ljava/lang/String;");
    if (method == NULL)
    {
        return NULL;
    }
    jstring jstr = (*env)->CallObjectMethod(env, obj, method);
    return j_copy_of_jstring(env, jstr, true);
}

char *j_copy_of_jstring(JNIEnv *env, jstring jstr, bool nullable)
{
    if (!jstr)
    {
        if (nullable)
        {
            return NULL;
        }
        else
        {
            j_throw_java_error(env, "java/lang/NullPointerException", "cannot cast null to c string");
        }
    }

    const char *c_str = (*env)->GetStringUTFChars(env, jstr, NULL);
    char *copy = j_strdup(c_str);
    (*env)->ReleaseStringUTFChars(env, jstr, c_str);
    return copy;
}

int j_get_int_field(JNIEnv *env, jclass jclz, jobject obj, const char *fieldName)
{
    jfieldID fid = (*env)->GetFieldID(env, jclz, fieldName, "I");
    assert(fid && "getter I() not found");
    jint value = (*env)->GetIntField(env, obj, fid);
    return value;
}

/* NOTE: Returned string needs to be free-ed. */
char *j_get_string_field(JNIEnv *env, jclass jclz, jobject obj, const char *fieldName)
{
    jfieldID fid = (*env)->GetFieldID(env, jclz, fieldName, "Ljava/lang/String;");
    assert(fid && "getter Ljava/lang/String() not found");
    jstring value = (*env)->GetObjectField(env, obj, fid);
    char *cstr = j_copy_of_jstring(env, value, true);
    (*env)->DeleteLocalRef(env, value);
    return cstr;
}

/** copy values of java Index.Entry object to git_index_entry struct. */
void index_entry_from_java(JNIEnv *env, git_index_entry *c_entry, jobject entry)
{
    assert(c_entry);
    jclass jclz = (*env)->GetObjectClass(env, entry);
    assert(jclz && "Could not find Entry class from given entry object");
    c_entry->ctime.seconds = j_get_int_field(env, jclz, entry, "ctimeSec");
    c_entry->ctime.nanoseconds = j_get_int_field(env, jclz, entry, "ctimeNanoSec");
    c_entry->mtime.seconds = j_get_int_field(env, jclz, entry, "mtimeSec");
    c_entry->mtime.nanoseconds = j_get_int_field(env, jclz, entry, "mtimeNanoSec");
    c_entry->dev = j_get_int_field(env, jclz, entry, "dev");
    c_entry->mode = j_get_int_field(env, jclz, entry, "mode");
    c_entry->uid = j_get_int_field(env, jclz, entry, "uid");
    c_entry->gid = j_get_int_field(env, jclz, entry, "gid");
    char *oid = j_get_string_field(env, jclz, entry, "oid");
    git_oid_fromstr(&(c_entry->id), oid);
    free(oid);
    c_entry->file_size = j_get_int_field(env, jclz, entry, "fileSize");
    c_entry->flags = j_get_int_field(env, jclz, entry, "flags");
    c_entry->flags_extended = j_get_int_field(env, jclz, entry, "flagsExtended");
    free((void *)c_entry->path);
    c_entry->path = j_get_string_field(env, jclz, entry, "path");
}

/** create jni jbyteArray from c unsigned char array. */
jbyteArray j_byte_array_from_c(JNIEnv *env, const unsigned char *buf, int len)
{
    if (buf == NULL || len == 0)
    {
        return NULL;
    }
    jbyteArray array = (*env)->NewByteArray(env, len);
    (*env)->SetByteArrayRegion(env, array, 0, len, (jbyte *)buf);
    return array;
}

/** create c unsigned char array from jni jbyteArray. */
unsigned char *j_unsigned_chars_from_java(JNIEnv *env, jbyteArray array, int *out_len)
{
    if (array == NULL)
    {
        *out_len = 0;
        return NULL;
    }

    int len = (*env)->GetArrayLength(env, array);
    unsigned char *buf = (unsigned char *)malloc(sizeof(unsigned char) * len);
    (*env)->GetByteArrayRegion(env, array, 0, len, (jbyte *)buf);
    *out_len = len;
    return buf;
}

/** Copy git_oid value to java Oid vis setter. */
void j_git_oid_to_java(JNIEnv *env, const git_oid *c_oid, jobject oid)
{
    jclass clz = (*env)->GetObjectClass(env, oid);
    assert(clz && "Oid class not found");
    jbyteArray raw = j_byte_array_from_c(env, c_oid->id, GIT_OID_RAWSZ);
    j_call_setter_byte_array(env, clz, oid, "setId", raw);
    (*env)->DeleteLocalRef(env, raw);
}

//TODO: test oid copying functions
/** Copy value of java Oid to git_oid struct in c. */
void j_git_oid_from_java(JNIEnv *env, jobject oid, git_oid *c_oid)
{
    jclass clz = (*env)->GetObjectClass(env, oid);
    assert(clz && "Oid class not found");
    jbyteArray jBytes = j_call_getter_byte_array(env, clz, oid, "getId");
    int out_len;
    unsigned char *c_bytes = j_unsigned_chars_from_java(env, jBytes, &out_len);
    git_oid_fromraw(c_oid, c_bytes);
    free(c_bytes);
    (*env)->DeleteGlobalRef(env, jBytes);
}

/** Call `obj.method(val)` to set a java object value. */
void j_call_setter_long(JNIEnv *env, jclass clz, jobject obj, const char *method, jlong val)
{
    jmethodID setter = (*env)->GetMethodID(env, clz, method, "(J)V");
    (*env)->CallVoidMethod(env, obj, setter, val);
}

/** Call `obj.method(val)` to set a java object value. */
void j_call_setter_int(JNIEnv *env, jclass clz, jobject obj, const char *method, jint val)
{
    jmethodID setter = (*env)->GetMethodID(env, clz, method, "(I)V");
    assert(setter && "setter not found");
    (*env)->CallVoidMethod(env, obj, setter, val);
}

void j_call_setter_string(JNIEnv *env, jclass clz, jobject obj, const char *method, jstring val)
{
    jmethodID setter = (*env)->GetMethodID(env, clz, method, "(Ljava/lang/String;)V");
    (*env)->CallVoidMethod(env, obj, setter, val);
}

void j_call_setter_string_c(JNIEnv *env, jclass clz, jobject obj, const char *method, const char *val)
{
    jstring jVal = (*env)->NewStringUTF(env, val);
    j_call_setter_string(env, clz, obj, method, jVal);
    (*env)->DeleteLocalRef(env, jVal);
}

void j_call_setter_byte_array(JNIEnv *env, jclass clz, jobject obj, const char *method, jbyteArray val)
{
    jmethodID setter = (*env)->GetMethodID(env, clz, method, "([B)V");
    (*env)->CallVoidMethod(env, obj, setter, val);
}

jbyteArray j_call_getter_byte_array(JNIEnv *env, jclass clz, jobject obj, const char *method)
{
    jmethodID getter = (*env)->GetMethodID(env, clz, method, "()[B");
    if (getter == NULL)
    {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, obj, getter);
}

void j_set_object_field(JNIEnv *env, jclass clz, jobject obj, jobject val, const char *field, const char *sig)
{
    jfieldID fid = (*env)->GetFieldID(env, clz, field, sig);
    (*env)->SetObjectField(env, obj, fid, val);
}

void j_set_string_field_c(JNIEnv *env, jclass clz, jobject obj, const char *val, const char *field)
{
    jstring jVal = (*env)->NewStringUTF(env, val);
    j_set_object_field(env, clz, obj, jVal, field, "Ljava/lang/String;");
    (*env)->DeleteLocalRef(env, jVal);
}