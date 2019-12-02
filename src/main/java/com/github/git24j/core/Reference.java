package com.github.git24j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**TODO: reference is not a GitObject*/
public class Reference extends GitObject {
    public Reference(long rawPointer) {
        super(rawPointer);
    }

    /** const git_oid * git_reference_target(const git_reference *ref); */
    static native void jniTarget(Oid oid, long refPtr);

    static native int jniLookup(AtomicLong outRef, long repoPtr, String name);

    /**
     * Lookup a reference by name in a repository.
     *
     * The name will be checked for validity.
     * See `git_reference_symbolic_create()` for rules about valid names.
     *
     * @param repo the repository to look up the reference
     * @param name the long name for the reference (e.g. HEAD, refs/heads/master, refs/tags/v0.1.0, ...)
     * @return found reference, if not found, a GitException will be thrown.
     * @throws GitException
     */
    public static Reference lookup(Repository repo, String name) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniLookup(outRef, repo.getRawPointer(), name));
        return new Reference(outRef.get());
    }
    static native int jniNameToId(Oid oid, long repoPtr, String name);

    /**
     * Lookup a reference by name and resolve immediately to OID.
     *
     * This function provides a quick way to resolve a reference name straight
     * through to the object id that it refers to.  This avoids having to
     * allocate or free any `git_reference` objects for simple situations.
     *
     * The name will be checked for validity.
     * See `git_reference_symbolic_create()` for rules about valid names.
     *
     * @param repo The repository in which to look up the reference
     * @param name The long name for the reference (e.g. HEAD, refs/heads/master, refs/tags/v0.1.0, ...)
     * @return found Oid
     * @throws GitException GIT_ENOTFOUND, GIT_EINVALIDSPEC or an error code.
     */
    public static Oid nameToId(Repository repo, String name) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniNameToId(oid, repo.getRawPointer(), name));
        return oid;
    }
    static native int jniDwim(AtomicLong outRef, long repoPtr, String shorthand);
    /**
     * Lookup a reference by DWIMing its short name
     *
     * Apply the git precendence rules to the given shorthand to determine
     * which reference the user is referring to.
     *
     * @param repo the repository in which to look
     * @param shorthand the short name for the reference
     * @return found reference
     * @throws GitException
     */
    public static Reference dwim(Repository repo, String shorthand) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniDwim(outRef, repo.getRawPointer(), shorthand));
        return new Reference(outRef.get());
    }
    static native int jniSymbolicMatching(AtomicLong outRef, long repoPtr, String name, String target, int force, String currentValue, String logMessage);
    /**
     * Create a symbolic reference that points to another reference (target reference) and matches the {@code currentValue} when updating.
     *
     * @param repo Repository where the target reference and the symbolic reference live
     * @param name name of the symbolic reference
     * @param target name of the target reference
     * @param force Overwrite if reference already exist
     * @param currentValue The expected name of the reference when updating
     * @param logMessage The one line long message to be appended to the reflog
     * @return 0 on success
     * @throws GitException GIT_EEXISTS, GIT_EINVALIDSPEC, GIT_EMODIFIED or an error code
     */
    public static Reference symbolicCreateMatching(Repository repo, String name, String target, boolean force, String currentValue, String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniSymbolicMatching(outRef, repo.getRawPointer(), name, target, force ? 1 : 0, currentValue, logMessage));
        return new Reference(outRef.get());
    }

    static native int jniSymbolicCreate(AtomicLong outRef, long repoPtr, String name, String target, int force, String logMessage);

    /**
     * Create a new symbolic reference.
     *
     * @param repo Repository where that reference will live
     * @param name The name of the reference
     * @param target The target of the reference
     * @param force Overwrite existing references
     * @param logMessage The one line long message to be appended to the reflog
     * @return created reference
     * @throws GitException GIT_EEXISTS, GIT_EINVALIDSPEC or an error code
     */
    public static Reference symbolicCreate(Repository repo, String name, String target, boolean force, String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniSymbolicCreate(outRef, repo.getRawPointer(), name, target, force ? 1 : 0, logMessage));
        return new Reference(outRef.get());
    }
    static native int jniCreate(AtomicLong outRef, long repoPtr, String name, Oid oid, int force, String logMessage);
    /**
     * Create a new direct reference that refers directly to an Oid.
     *
     *
     * @param repo Repository where that reference will live
     * @param name The name of the reference
     * @param oid The object id pointed to by the reference.
     * @param force Overwrite existing references
     * @param logMessage The one line long message to be appended to the reflog
     * @return created reference.
     * @throws GitException GIT_EEXISTS, GIT_EINVALIDSPEC or an error code
     */
    public static Reference create(Repository repo, String name, Oid oid, boolean force, String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniCreate(outRef, repo.getRawPointer(), name, oid, force ? 1 : 0, logMessage));
        return new Reference(outRef.get());
    }
    static native int jniCreateMatching(AtomicLong outRef, long repoPtr, String name, Oid oid, int force, Oid currentId, String logMessage);
    /**
     * Conditionally create new direct reference
     *
     * @param repo Repository where that reference will live
     * @param name The name of the reference
     * @param oid The object id pointed to by the reference.
     * @param force Overwrite existing references
     * @param currentId The expected value of the reference at the time of update
     * @param logMessage The one line long message to be appended to the reflog
     * @return created reference.
     * @throws GitException GIT_EMODIFIED if the value of the reference has changed, GIT_EEXISTS, GIT_EINVALIDSPEC or an error code
     */
    public static Reference createMatching(Repository repo, String name, Oid oid, boolean force, Oid currentId, String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniCreateMatching(outRef, repo.getRawPointer(), name, oid, force ? 1 :0, currentId, logMessage));
        return new Reference(outRef.get());
    }
    static native void jniTargetPeel(Oid oid, long refPtr);

    /**
     * Return the peeled OID target of this reference.
     *
     * This peeled OID only applies to direct references that point to
     * a hard Tag object: it is the result of peeling such Tag.
     *
     * @return peeled Oid
     */
    public Oid targetPeel() {
        Oid oid = new Oid();
        jniTargetPeel(oid, this.getRawPointer());
        return oid;
    }
    static native String jniSymbolicTarget(long refPtr);
     /**
     * Get full name to the reference pointed to by a symbolic reference.
     *
     * Only available if the reference is symbolic.
     *
     * @return name of the reference
     */
     public String symbolicTarget() {
         return jniSymbolicTarget(this.getRawPointer());
     }

     public enum ReferenceType implements IBitEnum {
         INVALID(0),
         DIRECT(1),
         SYMBOLIC(2),
         ALL(3);

         private final int _value;

         ReferenceType(int value) {
             _value = value;
         }

         @Override
         public int getBit() {
             return 0;
         }

         static ReferenceType valueOf(int iVal) {
             for (ReferenceType x : ReferenceType.values()) {
                 if (x._value == iVal) {
                     return x;
                 }
             }
             return INVALID;
         }
     }
    static native int jniType(long refPtr);
    /**
     * Get the type of a reference.
     *
     * Either direct (GIT_REFERENCE_DIRECT) or symbolic (GIT_REFERENCE_SYMBOLIC)
     *
     * @return the type
     */
    public ReferenceType referenceType() {
        return ReferenceType.valueOf(jniType(this.getRawPointer()));
    }
    static native String jniName(long refPtr);
    /**
     * Get the full name of a reference.
     *
     * See `git_reference_symbolic_create()` for rules about valid names.
     *
     * @return the full name for the ref
     */
    public String name() {
        return jniName(this.getRawPointer());
    }
    static native int jniResolve(AtomicLong outRef, long refPtr);
    /**
     * Resolve a symbolic reference to a direct reference.
     *
     * This method iteratively peels a symbolic reference until it resolves to
     * a direct reference to an OID.
     *
     * The peeled reference is returned in the `resolved_ref` argument, and
     * must be freed manually once it's no longer needed.
     *
     * If a direct reference is passed as an argument, a copy of that
     * reference is returned. This copy must be manually freed too.
     *
     * @return resolved reference
     * @throws GitException resolve attempt failed
     */
    public Reference resolve() {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniResolve(outRef, getRawPointer()));
        return new Reference(outRef.get());
    }
    static native long jniOwner(long refPtr);
    /**
     * Get the repository where a reference resides.
     *
     * @return owner repository
     */
    @Override
    public Repository owner(){
        return new Repository(jniOwner(getRawPointer()));
    }
    static native int jniSymbolicSetTarget(AtomicLong outRef, long refPtr, String target, String logMessage);
    /**
     * Create a new reference with the same name as the given reference but a
     * different symbolic target. The reference must be a symbolic reference,
     * otherwise this will fail.
     *
     * The new reference will be written to disk, overwriting the given reference.
     *
     * The target name will be checked for validity.
     * See `git_reference_symbolic_create()` for rules about valid names.
     *
     * The message for the reflog will be ignored if the reference does
     * not belong in the standard set (HEAD, branches and remote-tracking
     * branches) and and it does not have a reflog.
     *
     * @param target The new target for the reference
     * @param logMessage The one line long message to be appended to the reflog
     * @return newly created Reference
     * @throws GitException GIT_EINVALIDSPEC or an error code
     */
    public Reference symbolicSetTarget(String target, String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniSymbolicSetTarget(outRef, getRawPointer(), target, logMessage));
        return new Reference(outRef.get());
    }
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniSetTarget)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr, jobject oid, jstring logMessage);
    static native int jniSetTarget(AtomicLong outRef, long refPtr, Oid oid, String logMessage);

    /**
     * Conditionally create a new reference with the same name as the given reference but a
     * different OID target. The reference must be a direct reference, otherwise
     * this will fail.
     *
     * The new reference will be written to disk, overwriting the given reference.
     *
     * @param oid The new target OID for the reference
     * @param logMessage The one line long message to be appended to the reflog
     * @return newly created Reference
     * @throws GitException GIT_EMODIFIED if the value of the reference
     * has changed since it was read, or an error code
     */
    public Reference setTarget(Oid oid, String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniSetTarget(outRef, getRawPointer(), oid, logMessage));
        return new Reference(outRef.get());
    }
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniRename)(JNIEnv *env, jclass obj, jobject outRef, jlong refPtr, jstring newName, int force, jstring logMessage);
    static native int jniRename(AtomicLong outRef, long refPtr, String newName, int force, String logMessage);


    /**
     * Rename an existing reference.
     *
     * This method works for both direct and symbolic references.
     *
     * The new name will be checked for validity.
     * See `git_reference_symbolic_create()` for rules about valid names.
     *
     * If the `force` flag is not enabled, and there's already
     * a reference with the given name, the renaming will fail.
     *
     * IMPORTANT:
     * The user needs to write a proper reflog entry if the
     * reflog is enabled for the repository. We only rename
     * the reflog if it exists.
     *
     * @param newName The new name for the reference
     * @param force Overwrite an existing reference
     * @param logMessage The one line long message to be appended to the reflog
     * @return new reference
     * @throws GitException GIT_EINVALIDSPEC, GIT_EEXISTS or an error code
     *
     */

    public Reference rename(String newName, boolean force, String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniRename(outRef, getRawPointer(), newName, force ? 1 : 0, logMessage));
        return new Reference(outRef.get());
    }
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniDelete)(JNIEnv *env, jclass obj, jlong refPtr);
    static native int jniDelete(long refPtr);
    /**
     * Delete an existing reference.
     *
     * This method works for both direct and symbolic references.  The reference
     * will be immediately removed on disk but the memory will not be freed.
     * Callers must call `git_reference_free`.
     *
     * This function will return an error if the reference has changed
     * from the time it was looked up.
     *
     * @throws GitException GIT_EMODIFIED or an error code
     */
    public static void delete(Reference ref) {
        if (ref != null && ref.getRawPointer() != 0) {
            Error.throwIfNeeded(jniDelete(ref.getRawPointer()));
        }
    }
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniRemove)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name);
    static native int jniRemove(long repoPtr, String name);
    /**
     * Delete an existing reference by name
     *
     * This method removes the named reference from the repository without
     * looking at its old value.
     *
     * @param repo repository where the reference lives
     * @param name The reference to remove
     * @throws GitException git error
     */
    public static void remote(Repository repo, String name) {
        Error.throwIfNeeded(jniRemove(repo.getRawPointer(), name));
    }
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniList)(JNIEnv *env, jclass obj, jobjectArray strList, jlong repoPtr);
    static native int jniList(List<String> strList, long repoPtr);
    /**
     * Fill a list with all the references that can be found in a repository.
     *
     * The string array will be filled with the names of all references; these
     * values are owned by the user and should be free'd manually when no
     * longer needed, using `git_strarray_free()`.
     *
     * @param repo Repository where to find the refs
     * @return list of reference names
     * @throws GitException git error
     */
    public static List<String> list(Repository repo) {
        List<String> strList = new ArrayList<>();
        Error.throwIfNeeded(jniList(strList, repo.getRawPointer()));
        return strList;
    }
    static native int jniForeach(long repoPtr, Consumer<Integer> consumer);

    /**
     * Perform a callback on each reference in the repository.
     *
     * The `callback` function will be called for each reference in the
     * repository, receiving the reference object and the `payload` value
     * passed to this method.  Returning a non-zero value from the callback
     * will terminate the iteration.
     *
     * Note that the callback function is responsible to call `git_reference_free`
     * on each reference passed to it.
     *
     * @param repo Repository where to find the refs
     * @param callback Function which will be called for every listed ref
     * @throws GitException git errors
     */
    public static void foreach(Repository repo, Consumer<Reference> callback) {
        int e = jniForeach(repo.getRawPointer(), (ptr) -> {
            try(Reference ref = new Reference(ptr)){
                callback.accept(ref);
            }
        });
        Error.throwIfNeeded(e);
    }
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniForeachName)(JNIEnv *env, jclass obj, jlong repoPtr, jobject consumer);
    static native int jniForeachName(long repoPtr, Consumer<String> consumer);

    /**
     * Perform a callback on the fully-qualified name of each reference.
     *
     * The `callback` function will be called for each reference in the
     * repository, receiving the name of the reference and the `payload` value
     * passed to this method.  Returning a non-zero value from the callback
     * will terminate the iteration.
     *
     * @param repo Repository where to find the refs
     * @param callback Function which will be called for every listed ref name
     * @throws GitException git errors
     */
    public static void foreachName(Repository repo, Consumer<String> callback) {
        Error.throwIfNeeded(jniForeachName(repo.getRawPointer(), callback));
    }
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniDup)(JNIEnv *env, jclass obj, jobject outDest, jlong sourcePtr);
    static native int jniDup(AtomicLong outDest, long sourcePtr);
    /**
     * Create a copy of an existing reference.
     *
     * Call `git_reference_free` to free the data.
     *
     * @return 0 or an error code
     */
    @Override
    public Reference dup() {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniDup(outRef, this.getRawPointer()));
        return new Reference(outRef.get());
    }
//    JNIEXPORT void JNICALL J_MAKE_METHOD(Reference_jniFree)(JNIEnv *env, jclass obj, jlong refPtr);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniCmp)(JNIEnv *env, jclass obj, jlong ref1Ptr, jlong ref2Ptr);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outIter, jlong repoPtr);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIteratorGlobNew)(JNIEnv *env, jclass obj, jobject outIter, jlong repoPtr, jstring glob);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNext)(JNIEnv *env, jclass obj, jobject outRef, jlong iterPtr);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNextName)(JNIEnv *env, jclass obj, jstring outRef, jlong iterPtr);
//    JNIEXPORT void JNICALL J_MAKE_METHOD(Reference_jniIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniForeachGlob)(JNIEnv *env, jclass obj, jlong repoPtr, jstring glob, jobject callback);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniHasLog)(JNIEnv *env, jclass obj, jlong repoPtr, jstring refname);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniEnsureLog)(JNIEnv *env, jclass obj, jlong repoPtr, jstring refname);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsBranch)(JNIEnv *env, jclass obj, jlong refPtr);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsRemote)(JNIEnv *env, jclass obj, jlong refPtr);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsTag)(JNIEnv *env, jclass obj, jlong refPtr);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsNote)(JNIEnv *env, jclass obj, jlong refPtr);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniNormalizeName)(JNIEnv *env, jclass obj, jobject bufferOut, jstring name, jint flags);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniPeel)(JNIEnv *env, jclass obj, jobject outObj, jlong refPtr, jint objType);
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reference_jniIsValid)(JNIEnv *env, jclass obj, jstring refname);
//    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reference_jniShorthand)(JNIEnv *env, jclass obj, jlong refPtr);

    /**
     * Get the OID pointed to by a direct reference.
     *
     * Only available if the reference is direct (i.e. an object id reference,
     * not a symbolic one).
     *
     * To find the OID of a symbolic ref, call `git_reference_resolve()` and
     * then this function (or maybe use `git_reference_name_to_id()` to
     * directly resolve a reference name all the way through to an OID).
     *
     * @return a pointer to the oid if available, NULL otherwise
     */
    public Oid target() {
        Oid oid = new Oid();
        jniTarget(oid, getRawPointer());
        return oid;
    }

    /**
     * @return target of this reference.
     */
    @Override
    public Oid id() {
        return target();
    }
}
