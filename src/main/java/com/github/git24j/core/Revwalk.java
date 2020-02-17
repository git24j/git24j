package com.github.git24j.core;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicLong;

public class Revwalk extends CAutoReleasable {
    protected Revwalk(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }


    // no matching type found for 'git_revwalk_hide_cb hide_cb'
    /**
     * int git_revwalk_add_hide_cb(git_revwalk *walk, git_revwalk_hide_cb hide_cb, void *payload);
     */
    /** -------- Jni Signature ---------- */
    /** void git_revwalk_free(git_revwalk *walk); */
    static native void jniFree(long walk);

    /** int git_revwalk_hide(git_revwalk *walk, const git_oid *commit_id); */
    static native int jniHide(long walk, Oid commitId);

    /** int git_revwalk_hide_glob(git_revwalk *walk, const char *glob); */
    static native int jniHideGlob(long walk, String glob);

    /** int git_revwalk_hide_head(git_revwalk *walk); */
    static native int jniHideHead(long walk);

    /** int git_revwalk_hide_ref(git_revwalk *walk, const char *refname); */
    static native int jniHideRef(long walk, String refname);

    /** int git_revwalk_new(git_revwalk **out, git_repository *repo); */
    static native int jniNew(AtomicLong out, long repoPtr);

    /**
     * Allocate a new revision walker to iterate through a repo.
     *
     * This revision walker uses a custom memory pool and an internal
     * commit cache, so it is relatively expensive to allocate.
     *
     * For maximum performance, this revision walker should be
     * reused for different walks.
     *
     * This revision walker is *not* thread safe: it may only be
     * used to walk a repository on a single thread; however,
     * it is possible to have several revision walkers in
     * several different threads walking the same repository.
     *
     * @param repo the repo to walk through
     * @return the new revision walker
     * @throws GitException git errors
     */
    @Nonnull
    public static Revwalk create(@Nonnull Repository repo) {
        Revwalk out = new Revwalk(false, 0);
        Error.throwIfNeeded(jniNew(out._rawPtr, repo.getRawPointer()));
        return out;
    }
    /** int git_revwalk_next(git_oid *out, git_revwalk *walk); */
    static native int jniNext(Oid out, long walk);

    /** int git_revwalk_push(git_revwalk *walk, const git_oid *id); */
    static native int jniPush(long walk, Oid id);

    /** int git_revwalk_push_glob(git_revwalk *walk, const char *glob); */
    static native int jniPushGlob(long walk, String glob);

    /** int git_revwalk_push_head(git_revwalk *walk); */
    static native int jniPushHead(long walk);

    /** int git_revwalk_push_range(git_revwalk *walk, const char *range); */
    static native int jniPushRange(long walk, String range);

    /** int git_revwalk_push_ref(git_revwalk *walk, const char *refname); */
    static native int jniPushRef(long walk, String refname);

    /** git_repository * git_revwalk_repository(git_revwalk *walk); */
    static native long jniRepository(long walk);

    /** void git_revwalk_reset(git_revwalk *walker); */
    static native void jniReset(long walker);

    /** void git_revwalk_simplify_first_parent(git_revwalk *walk); */
    static native void jniSimplifyFirstParent(long walk);

    /** void git_revwalk_sorting(git_revwalk *walk, unsigned int sort_mode); */
    static native void jniSorting(long walk, int sortMode);
}
