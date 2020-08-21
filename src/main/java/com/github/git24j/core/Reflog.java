package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

public class Reflog extends CAutoReleasable {
    /**
     * int git_reflog_append(git_reflog *reflog, const git_oid *id, const git_signature *committer,
     * const char *msg);
     */
    static native int jniAppend(long reflog, Oid id, long committer, String msg);

    /** int git_reflog_delete(git_repository *repo, const char *name); */
    static native int jniDelete(long repoPtr, String name);
    /** -------- Jni Signature ---------- */

    /** int git_reflog_drop(git_reflog *reflog, size_t idx, int rewrite_previous_entry); */
    static native int jniDrop(long reflog, int idx, int rewritePreviousEntry);

    /** const git_reflog_entry * git_reflog_entry_byindex(const git_reflog *reflog, size_t idx); */
    static native long jniEntryByindex(long reflog, int idx);

    /** const git_signature * git_reflog_entry_committer(const git_reflog_entry *entry); */
    static native long jniEntryCommitter(long entry);

    /** const git_oid * git_reflog_entry_id_new(const git_reflog_entry *entry); */
    static native byte[] jniEntryIdNew(long entry);

    /** const git_oid * git_reflog_entry_id_old(const git_reflog_entry *entry); */
    static native byte[] jniEntryIdOld(long entry);

    /** const char * git_reflog_entry_message(const git_reflog_entry *entry); */
    static native String jniEntryMessage(long entry);

    /** size_t git_reflog_entrycount(git_reflog *reflog); */
    static native int jniEntrycount(long reflog);

    /** void git_reflog_free(git_reflog *reflog); */
    static native void jniFree(long reflog);

    /** int git_reflog_read(git_reflog **out, git_repository *repo, const char *name); */
    static native int jniRead(AtomicLong out, long repoPtr, String name);

    /** int git_reflog_rename(git_repository *repo, const char *old_name, const char *name); */
    static native int jniRename(long repoPtr, String oldName, String name);

    /**
     * int git_transaction_set_reflog(git_transaction *tx, const char *refname, const git_reflog
     * *reflog);
     */
    static native int jniSetReflog(long tx, String refname, long reflog);

    /** int git_reflog_write(git_reflog *reflog); */
    static native int jniWrite(long reflog);

    protected Reflog(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
}
