
## Common practice in development.

1. How to return Oid? 
Oid in git24j is stored in java, so every time a git function returns an Oid, we need to pass jobject to the corresponding jni function and call j_git_oid_to_jave to copy c value to java.

```
    const git_oid *c_oid = git_reference_target((git_reference *)refPtr);
    j_git_oid_to_java(env, c_oid, oid);
```

2. How to pass read-only pointer? 
C pointers are passed from java to c as a long value, and can be casted directly to corresponding pointer.
```
// jni C wrapper
long repoPtr;
(git_repository **)repoPtr
// jni java wrapper
repo.getRawPointer()

```

3. How to return a pointer from libgit2 to java?
 and often passed from c to java using `AtomicLong` object. 
For example, following java code pass an `AtomicLong` object to c: 
```
public static Reference create(Repository repo, String branchName, Commit target, int force) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniCreate(outRef, repo.getRawPointer(), branchName, target.getRawPointer(), force));
        return new Reference(outRef.get());
    }
```

And in c, `j_save_c_pointer` is called to save pass the long value to the `AtomicLong` object:
   
```c
JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jlong targetPtr, jint force)
{
    git_reference *c_ref;
    char *c_branch_name = j_copy_of_jstring(env, branchName, false);
    int error = git_branch_create(&c_ref, (git_repository *)repoPtr, c_branch_name, (const git_commit *)targetPtr, force);
    free(c_branch_name);
    j_save_c_pointer(env, (void *)c_ref, outRef, "set");
    return error;
}
```

4. Pass string array around
in libgit2, string arrays are passed around using `git_sarray`. In c wrappers, string arrays are passed through `jobjectArray`

To get `git_sarray` from, `jobjectArray`, use `git_strarray_of_jobject_array`.

Example:
```
// Given: jobjectArray pathspec 
git_strarray c_pathspec = {0};
git_strarray_of_jobject_array(env, pathspec, &c_pathspec);
git_strarray_free(&c_pathspec);

```

5. How to deal with callback
- first we need to define a payload struct, which packs jni environment and java callback object(`consumer`).
```
/**Pack jni objects to pass to update callback. */
typedef struct
{
    JNIEnv *env;
    jobject consumer;
} j_cb_payload;
```

- second, we need to implement a callback function, within the function:
    * cast `(void *)playload` to `j_cb_payload`
    * unpack `env` and `consumer` objects
    * call java consumer's accept method
    * delete local refs
```
/**int git_repository_fetchhead_foreach_cb(const char *ref_name, const char *remote_url, const git_oid *oid, unsigned int is_merge, void *payload);*/
int j_fetchhead_foreach_cb(const char *ref_name, const char *remote_url, const git_oid *oid, unsigned int is_merge, void *payload)
{
    /* assert(payload && "jni callback cannot be null"); */
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = j_payload->env;
    jobject consumer = j_payload->consumer;
    jclass jclz = (*env)->GetObjectClass(env, consumer);
    assert(jclz && "jni error: could not resolve consumer class");
    /** int accetp(String remoteUrl, byte[] oid, int isMerge)*/
    jmethodID accept = (*env)->GetMethodID(env, jclz, "accept", "(Ljava/lang/String;[BI)I");
    assert(accept && "jni error: could not resolve method consumer method");

    jstring j_remoteUrl = (*env)->NewStringUTF(env, remote_url);
    jbyteArray j_oidBytes = j_byte_array_from_c(env, oid->id, GIT_OID_RAWSZ);
    int r = (*env)->CallIntMethod(env, consumer, accept, j_remoteUrl, j_oidBytes, (jint)is_merge);

    (*env)->DeleteLocalRef(env, j_remoteUrl);
    (*env)->DeleteLocalRef(env, j_oidBytes);
    (*env)->DeleteLocalRef(env, jclz);
    return r;
}

```

