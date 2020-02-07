package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Diff {
    AtomicLong _rawPtr = new AtomicLong();

    @FunctionalInterface
    interface JFCallback {
        int accept(long ptr, float f);
    }

    @FunctionalInterface
    interface JJCallback {
        int accept(long ptr1, long ptr2);
    }

    @FunctionalInterface
    interface JJJCallback {
        int accept(long ptr1, long ptr2, long ptr3);
    }

    /** int git_diff_init_options(git_diff_options *opts, unsigned int version); */
    static native int jniInitOptions(long opts, int version);

    /** int git_diff_find_init_options(git_diff_find_options *opts, unsigned int version); */
    static native int jniFindInitOptions(long opts, int version);

    /** void git_diff_free(git_diff *diff); */
    static native void jniFree(long diff);

    /**
     * int git_diff_tree_to_tree(git_diff **diff, git_repository *repo, git_tree *old_tree, git_tree
     * *new_tree, const git_diff_options *opts);
     */
    static native int jniTreeToTree(
            AtomicLong diff, long repoPtr, long oldTree, long newTree, long opts);

    /**
     * int git_diff_tree_to_index(git_diff **diff, git_repository *repo, git_tree *old_tree,
     * git_index *index, const git_diff_options *opts);
     */
    static native int jniTreeToIndex(
            AtomicLong diff, long repoPtr, long oldTree, long index, long opts);

    /**
     * int git_diff_index_to_workdir(git_diff **diff, git_repository *repo, git_index *index, const
     * git_diff_options *opts);
     */
    static native int jniIndexToWorkdir(AtomicLong diff, long repoPtr, long index, long opts);

    /**
     * int git_diff_tree_to_workdir(git_diff **diff, git_repository *repo, git_tree *old_tree, const
     * git_diff_options *opts);
     */
    static native int jniTreeToWorkdir(AtomicLong diff, long repoPtr, long oldTree, long opts);

    /**
     * int git_diff_tree_to_workdir_with_index(git_diff **diff, git_repository *repo, git_tree
     * *old_tree, const git_diff_options *opts);
     */
    static native int jniTreeToWorkdirWithIndex(
            AtomicLong diff, long repoPtr, long oldTree, long opts);

    /**
     * int git_diff_index_to_index(git_diff **diff, git_repository *repo, git_index *old_index,
     * git_index *new_index, const git_diff_options *opts);
     */
    static native int jniIndexToIndex(
            AtomicLong diff, long repoPtr, long oldIndex, long newIndex, long opts);

    /** int git_diff_merge(git_diff *onto, const git_diff *from); */
    static native int jniMerge(long onto, long from);

    /** int git_diff_find_similar(git_diff *diff, const git_diff_find_options *options); */
    static native int jniFindSimilar(long diff, long options);

    /** size_t git_diff_num_deltas(const git_diff *diff); */
    static native int jniNumDeltas(long diff);

    /** size_t git_diff_num_deltas_of_type(const git_diff *diff, git_delta_t type); */
    static native int jniNumDeltasOfType(long diff, int type);

    /** const git_diff_delta * git_diff_get_delta(const git_diff *diff, size_t idx); */
    static native long jniGetDelta(long diff, int idx);

    /** int git_diff_is_sorted_icase(const git_diff *diff); */
    static native int jniIsSortedIcase(long diff);

    /**
     * int git_diff_foreach(git_diff *diff, git_diff_file_cb file_cb, git_diff_binary_cb binary_cb,
     * git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb, void *payload);
     */
    static native int jniForeach(
            long diff,
            JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);

    /** char git_diff_status_char(git_delta_t status); */
    static native char jniStatusChar(int status);

    /**
     * int git_diff_print(git_diff *diff, git_diff_format_t format, git_diff_line_cb print_cb, void
     * *payload);
     */
    static native int jniPrint(long diff, int format, JJJCallback printCb);

    /** int git_diff_to_buf(git_buf *out, git_diff *diff, git_diff_format_t format); */
    static native int jniToBuf(Buf out, long diff, int format);

    /**
     * int git_diff_blobs(const git_blob *old_blob, const char *old_as_path, const git_blob
     * *new_blob, const char *new_as_path, const git_diff_options *options, git_diff_file_cb
     * file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb,
     * void *payload);
     */
    static native int jniBlobs(
            long oldBlob,
            String oldAsPath,
            long newBlob,
            String newAsPath,
            long options,
            JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);

    /**
     * int git_diff_blob_to_buffer(const git_blob *old_blob, const char *old_as_path, const char
     * *buffer, size_t buffer_len, const char *buffer_as_path, const git_diff_options *options,
     * git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb,
     * git_diff_line_cb line_cb, void *payload);
     */
    static native int jniBlobToBuffer(
            long oldBlob,
            String oldAsPath,
            String buffer,
            int bufferLen,
            String bufferAsPath,
            long options,
            JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);

    /**
     * int git_diff_buffers(const void *old_buffer, size_t old_len, const char *old_as_path, const
     * void *new_buffer, size_t new_len, const char *new_as_path, const git_diff_options *options,
     * git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb,
     * git_diff_line_cb line_cb, void *payload);
     */
    static native int jniBuffers(
            byte[] oldBuffer,
            int oldLen,
            String oldAsPath,
            byte[] newBuffer,
            int newLen,
            String newAsPath,
            long options,
            JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);

    /** int git_diff_from_buffer(git_diff **out, const char *content, size_t content_len); */
    static native int jniFromBuffer(AtomicLong out, String content, int contentLen);

    /** int git_diff_get_stats(git_diff_stats **out, git_diff *diff); */
    static native int jniGetStats(AtomicLong out, long diff);

    /** size_t git_diff_stats_files_changed(const git_diff_stats *stats); */
    static native int jniStatsFilesChanged(long stats);

    /** size_t git_diff_stats_insertions(const git_diff_stats *stats); */
    static native int jniStatsInsertions(long stats);

    /** size_t git_diff_stats_deletions(const git_diff_stats *stats); */
    static native int jniStatsDeletions(long stats);

    /**
     * int git_diff_stats_to_buf(git_buf *out, const git_diff_stats *stats, git_diff_stats_format_t
     * format, size_t width);
     */
    static native int jniStatsToBuf(Buf out, long stats, int format, int width);

    /** void git_diff_stats_free(git_diff_stats *stats); */
    static native void jniStatsFree(long stats);

    /**
     * int git_diff_format_email(git_buf *out, git_diff *diff, const git_diff_format_email_options
     * *opts);
     */
    static native int jniFormatEmail(Buf out, long diff, long opts);

    /**
     * int git_diff_commit_as_email(git_buf *out, git_repository *repo, git_commit *commit, size_t
     * patch_no, size_t total_patches, git_diff_format_email_flags_t flags, const git_diff_options
     * *diff_opts);
     */
    static native int jniCommitAsEmail(
            Buf out,
            long repoPtr,
            long commit,
            int patchNo,
            int totalPatches,
            int flags,
            long diffOpts);

    /**
     * int git_diff_format_email_init_options(git_diff_format_email_options *opts, unsigned int
     * version);
     */
    static native int jniFormatEmailInitOptions(long opts, int version);

    /** int git_diff_patchid_init_options(git_diff_patchid_options *opts, unsigned int version); */
    static native int jniPatchidInitOptions(long opts, int version);

    /** int git_diff_patchid(git_oid *out, git_diff *diff, git_diff_patchid_options *opts); */
    static native int jniPatchid(Oid out, long diff, long opts);

    /**
     * int git_patch_from_blobs(git_patch **out, const git_blob *old_blob, const char *old_as_path,
     * const git_blob *new_blob, const char *new_as_path, const git_diff_options *opts);
     */
    static native int jniFromBlobs(
            AtomicLong out,
            long oldBlob,
            String oldAsPath,
            long newBlob,
            String newAsPath,
            long opts);

    /**
     * int git_patch_from_blob_and_buffer(git_patch **out, const git_blob *old_blob, const char
     * *old_as_path, const void *buffer, size_t buffer_len, const char *buffer_as_path, const
     * git_diff_options *opts);
     */
    static native int jniFromBlobAndBuffer(
            AtomicLong out,
            long oldBlob,
            String oldAsPath,
            byte[] buffer,
            int bufferLen,
            String bufferAsPath,
            long opts);

    /**
     * int git_patch_from_buffers(git_patch **out, const void *old_buffer, size_t old_len, const
     * char *old_as_path, const void *new_buffer, size_t new_len, const char *new_as_path, const
     * git_diff_options *opts);
     */
    static native int jniFromBuffers(
            AtomicLong out,
            byte[] oldBuffer,
            int oldLen,
            String oldAsPath,
            byte[] newBuffer,
            int newLen,
            String newAsPath,
            long opts);

    /**
     * int git_patch_get_hunk(const git_diff_hunk **out, size_t *lines_in_hunk, git_patch *patch,
     * size_t hunk_idx);
     */
    static native int jniGetHunk(
            AtomicLong out, AtomicInteger linesInHunk, long patch, int hunkIdx);

    /**
     * int git_patch_get_line_in_hunk(const git_diff_line **out, git_patch *patch, size_t hunk_idx,
     * size_t line_of_hunk);
     */
    static native int jniGetLineInHunk(AtomicLong out, long patch, int hunkIdx, int lineOfHunk);

    /** int git_patch_print(git_patch *patch, git_diff_line_cb print_cb, void *payload); */
    static native int jniPrint(long patch, JJJCallback printCb);

    @Override
    protected void finalize() throws Throwable {
        if (_rawPtr.get() > 0) {
            jniFree(_rawPtr.getAndSet(0));
        }
        super.finalize();
    }
}
