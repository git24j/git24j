package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

public class Rebase {
    /** int git_rebase_init_options(git_rebase_options *opts, unsigned int version); */
    static native int jniInitOptions(long opts, int version);

    /**
     * int git_rebase_init(git_rebase **out, git_repository *repo, const git_annotated_commit
     * *branch, const git_annotated_commit *upstream, const git_annotated_commit *onto, const
     * git_rebase_options *opts);
     */
    static native int jniInit(
            AtomicLong out, long repoPtr, long branch, long upstream, long onto, long opts);

    /**
     * int git_rebase_open(git_rebase **out, git_repository *repo, const git_rebase_options *opts);
     */
    static native int jniOpen(AtomicLong out, long repoPtr, long opts);

    /** size_t git_rebase_operation_entrycount(git_rebase *rebase); */
    static native int jniOperationEntrycount(long rebase);

    /** size_t git_rebase_operation_current(git_rebase *rebase); */
    static native int jniOperationCurrent(long rebase);

    /** git_rebase_operation * git_rebase_operation_byindex(git_rebase *rebase, size_t idx); */
    static native long jniOperationByindex(long rebase, int idx);

    /** int git_rebase_next(git_rebase_operation **operation, git_rebase *rebase); */
    static native int jniNext(AtomicLong operation, long rebase);

    /** int git_rebase_inmemory_index(git_index **index, git_rebase *rebase); */
    static native int jniInmemoryIndex(AtomicLong index, long rebase);

    /**
     * int git_rebase_commit(git_oid *id, git_rebase *rebase, const git_signature *author, const
     * git_signature *committer, const char *message_encoding, const char *message);
     */
    static native int jniCommit(
            Oid id,
            long rebase,
            long author,
            long committer,
            String messageEncoding,
            String message);

    /** int git_rebase_abort(git_rebase *rebase); */
    static native int jniAbort(long rebase);

    /** int git_rebase_finish(git_rebase *rebase, const git_signature *signature); */
    static native int jniFinish(long rebase, long signature);

    /** void git_rebase_free(git_rebase *rebase); */
    static native void jniFree(long rebase);
}
