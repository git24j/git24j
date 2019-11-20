package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

public class Branch {
    static native int jniCreate(
            AtomicLong outRef, long repoPtr, String branchName, long targetPtr, int force);

    /**
     * Create a new branch pointing at a target commit.
     *
     * @param repo Repository
     * @param branchName Name for the branch; this name is validated for consistency. It should also
     *     not conflict with an already existing branch name.
     * @param target Commit to which this branch should point. This object must belong to the given
     *     `repo`.
     * @param force Overwrite existing branch.
     * @return git reference that must be closed by the caller.
     * @throws GitException git error.
     * @throws IllegalStateException if repository has already been closed.
     */
    public static Reference create(Repository repo, String branchName, Commit target, int force) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniCreate(outRef, repo.getRawPointer(), branchName, target.getRawPointer(), force));
        return new Reference(outRef.get());
    }

//    /**int git_branch_create_from_annotated(git_reference **ref_out, git_repository *repository, const char *branch_name, const git_annotated_commit *commit, int force); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniCreateFromAnnotated)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jlong annoCommitPtr, jint force);
    static native int jniCreateFromAnnotated(AtomicLong outRef, long repoPtr, String branchName, long annoCommitPtr, int force);
    /**
     * Create a new branch pointing at a target commit
     *
     * This behaves like `git_branch_create()` but takes an annotated
     * commit, which lets you specify which extended sha syntax string was
     * specified by a user, allowing for more exact reflog messages.
     *
     * See the documentation for `git_branch_create()`.
     *
     * @param repo repository from which the branch is created
     * @param branchName name of the branch to be created
     * @param annotatedCommit annotated commit the branch is going to point to
     * @param force override existing branch
     * @return reference of the created branch
     * @throws GitException
     */
    public static Reference createFromAnnotated(Repository repo, String branchName, AnnotatedCommit annotatedCommit, boolean force) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniCreateFromAnnotated(outRef, repo.getRawPointer(), branchName, annotatedCommit.getRawPointer(), force ? 1 : 0));
        return new Reference(outRef.get());
    }
//    /**int git_branch_delete(git_reference *branch); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniDelete)(JNIEnv *env, jclass obj, jlong refPtr);
//    /**int git_branch_iterator_new(git_branch_iterator **out, git_repository *repo, git_branch_t list_flags); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outBranchIter, jlong repoPtr, jint listFlags);
//    /**int git_branch_next(git_reference **out, git_branch_t *out_type, git_branch_iterator *iter); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniNext)(JNIEnv *env, jclass obj, jobject outRef, jobject outType, jlong branchIterPtr);
//    /**void git_branch_iterator_free(git_branch_iterator *iter); */
//    JNIEXPORT void JNICALL J_MAKE_METHOD(Branch_jniIteratorFree)(JNIEnv *env, jclass obj, jlong branchIterPtr);
//    /**int git_branch_move(git_reference **out, git_reference *branch, const char *new_branch_name, int force); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniMove)(JNIEnv *env, jclass obj, jobject outRef, jlong branchPtr, jstring branchName, jint force);
//    /**int git_branch_lookup(git_reference **out, git_repository *repo, const char *branch_name, git_branch_t branch_type); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniLookup)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jint branchType);
//    /**int git_branch_name(const char **out, const git_reference *ref); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniName)(JNIEnv *env, jclass obj, jobject outStr, jlong refPtr);
//    /**int git_branch_upstream(git_reference **out, const git_reference *branch); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstream)(JNIEnv *env, jclass obj, jobject outRef, jlong branchPtr);
//    /**int git_branch_set_upstream(git_reference *branch, const char *upstream_name); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniSetUpstream)(JNIEnv *env, jclass obj, jlong refPtr, jstring upstreamName);
//    /**int git_branch_upstream_name(git_buf *out, git_repository *repo, const char *refname); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstreamName)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring refName);
//    /**int git_branch_is_head(const git_reference *branch); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIsHead)(JNIEnv *env, jclass obj, jlong refPtr);
//    /**int git_branch_is_checked_out(const git_reference *branch); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIsCheckedOut)(JNIEnv *env, jclass obj, jlong rePftr);
//    /**int git_branch_remote_name(git_buf *out, git_repository *repo, const char *canonical_branch_name); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniRemoteName)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring canonicalBranchName);
//    /**int git_branch_upstream_remote(git_buf *buf, git_repository *repo, const char *refname); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstreamRemote)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring refName);

}
