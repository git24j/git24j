package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

public class Note {
    // no matching type found for 'git_note_foreach_cb note_cb'
    /**
     * int git_note_foreach(git_repository *repo, const char *notes_ref, git_note_foreach_cb
     * note_cb, void *payload);
     */
    @FunctionalInterface
    interface ForeachCb {
        int accept(Oid blobId, Oid annotatedObjectId);
    }
    /** -------- Jni Signature ---------- */
    /**
     * int git_note_iterator_new(git_note_iterator **out, git_repository *repo, const char
     * *notes_ref);
     */
    static native int jniIteratorNew(AtomicLong out, long repoPtr, String notesRef);

    /** int git_note_commit_iterator_new(git_note_iterator **out, git_commit *notes_commit); */
    static native int jniCommitIteratorNew(AtomicLong out, long notesCommit);

    /** void git_note_iterator_free(git_note_iterator *it); */
    static native void jniIteratorFree(long it);

    /** int git_note_next(git_oid *note_id, git_oid *annotated_id, git_note_iterator *it); */
    static native int jniNext(Oid note_id, Oid annotated_id, long it);

    /**
     * int git_note_read(git_note **out, git_repository *repo, const char *notes_ref, const git_oid
     * *oid);
     */
    static native int jniRead(AtomicLong out, long repoPtr, String notesRef, Oid oid);

    /**
     * int git_note_commit_read(git_note **out, git_repository *repo, git_commit *notes_commit,
     * const git_oid *oid);
     */
    static native int jniCommitRead(AtomicLong out, long repoPtr, long notesCommit, Oid oid);

    /** const git_signature * git_note_author(const git_note *note); */
    static native long jniAuthor(long note);

    /** const git_signature * git_note_committer(const git_note *note); */
    static native long jniCommitter(long note);

    /** const char * git_note_message(const git_note *note); */
    static native String jniMessage(long note);

    /** const git_oid * git_note_id(const git_note *note); */
    static native long jniId(long note);

    /**
     * int git_note_create(git_oid *out, git_repository *repo, const char *notes_ref, const
     * git_signature *author, const git_signature *committer, const git_oid *oid, const char *note,
     * int force);
     */
    static native int jniCreate(
            Oid out,
            long repoPtr,
            String notesRef,
            long author,
            long committer,
            Oid oid,
            String note,
            int force);

    /**
     * int git_note_commit_create(git_oid *notes_commit_out, git_oid *notes_blob_out, git_repository
     * *repo, git_commit *parent, const git_signature *author, const git_signature *committer, const
     * git_oid *oid, const char *note, int allow_note_overwrite);
     */
    static native int jniCommitCreate(
            Oid notes_commit_out,
            Oid notes_blob_out,
            long repoPtr,
            long parent,
            long author,
            long committer,
            Oid oid,
            String note,
            int allowNoteOverwrite);

    /**
     * int git_note_remove(git_repository *repo, const char *notes_ref, const git_signature *author,
     * const git_signature *committer, const git_oid *oid);
     */
    static native int jniRemove(
            long repoPtr, String notesRef, long author, long committer, Oid oid);

    /**
     * int git_note_commit_remove(git_oid *notes_commit_out, git_repository *repo, git_commit
     * *notes_commit, const git_signature *author, const git_signature *committer, const git_oid
     * *oid);
     */
    static native int jniCommitRemove(
            Oid notes_commit_out,
            long repoPtr,
            long notesCommit,
            long author,
            long committer,
            Oid oid);

    /** void git_note_free(git_note *note); */
    static native void jniFree(long note);

    /** int git_note_default_ref(git_buf *out, git_repository *repo); */
    static native int jniDefaultRef(Buf out, long repoPtr);

    /**
     * int git_note_foreach_cb(const git_oid *blob_id, const git_oid *annotated_object_id, void
     * *payload);
     */
    static native int jniForeachCb(Oid blobId, Oid annotatedObjectId);
}
