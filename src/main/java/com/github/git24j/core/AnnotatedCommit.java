package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

/**
 * An annotated commit contains information about how it was looked up, which may be useful for
 * functions like merge or rebase to provide context to the operation. For example, conflict files
 * will include the name of the source or target branches being merged. It is therefore preferable
 * to use the most specific function (eg `git_annotated_commit_from_ref`) instead of this one when
 * that data is known.
 */
public class AnnotatedCommit extends CAutoCloseable {
    AnnotatedCommit(long rawPointer) {
        super(rawPointer);
    }

    static native int jniFromRef(AtomicLong outAc, long repoPtr, long refPtr);

    /**
     * Initialize {@link AnnotatedCommit} from a {@link Reference} of a repository.
     *
     * @param repo git repository
     * @param ref git reference
     * @return AnnotatedCommit
     * @throws GitException git error
     */
    public static AnnotatedCommit fromRef(Repository repo, Reference ref) {
        AtomicLong outAc = new AtomicLong();
        Error.throwIfNeeded(jniFromRef(outAc, repo.getRawPointer(), ref.getRawPointer()));
        return new AnnotatedCommit(outAc.get());
    }

    static native int jniFromFetchHead(
            AtomicLong outAc, long repoPtr, String branchName, String remoteUrl, Oid oid);

    /**
     * Initialize {@link AnnotatedCommit} from FETCH_HEAD data
     *
     * @param repo git repository
     * @param branchName branch name, nonnull
     * @param remoteUrl remote url, nonnull
     * @param oid oid, must not null
     * @return {@link AnnotatedCommit}
     * @throws GitException git error
     */
    public static AnnotatedCommit fromFetchHead(
            Repository repo, String branchName, String remoteUrl, Oid oid) {
        AtomicLong outAc = new AtomicLong();
        Error.throwIfNeeded(
                jniFromFetchHead(outAc, repo.getRawPointer(), branchName, remoteUrl, oid));
        return new AnnotatedCommit(outAc.get());
    }

    static native int jniLookup(AtomicLong outAc, long repoPtr, Oid oid);

    /**
     * Initialize {@link AnnotatedCommit} from Oid
     *
     * @param repo
     * @param oid
     * @return
     */
    public static AnnotatedCommit lookup(Repository repo, Oid oid) {
        AtomicLong outAc = new AtomicLong();
        Error.throwIfNeeded(jniLookup(outAc, repo.getRawPointer(), oid));
        return new AnnotatedCommit(outAc.get());
    }

    static native int jniFromRevspec(AtomicLong outAc, long repoPtr, String revspec);

    public static AnnotatedCommit fromRevspec(Repository repo, String revspec) {
        AtomicLong outAc = new AtomicLong();
        Error.throwIfNeeded(jniFromRevspec(outAc, repo.getRawPointer(), revspec));
        return new AnnotatedCommit(outAc.get());
    }

    static native void jniId(Oid outId, long acPtr);

    static native String jniRef(long acPtr);

    static native String jniFree(long acPtr);

    public Oid id() {
        Oid oid = new Oid();
        jniId(oid, getRawPointer());
        return oid;
    }

    public String ref() {
        return jniRef(getRawPointer());
    }

    @Override
    public void close() {
        jniFree(_rawPtr.getAndSet(0));
    }
}
