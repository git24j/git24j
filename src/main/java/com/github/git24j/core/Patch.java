package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.git24j.core.Internals.*;

public class Patch {
    /**
     * int git_patch_from_blobs(git_patch **out, const git_blob *old_blob, const char *old_as_path,
     * const git_blob *new_blob, const char *new_as_path, const git_diff_options *opts);
     */
    // TODO
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
    // TODO
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
    // TODO
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
    // TODO
    static native int jniGetHunk(
            AtomicLong out, AtomicInteger linesInHunk, long patch, int hunkIdx);

    /**
     * int git_patch_get_line_in_hunk(const git_diff_line **out, git_patch *patch, size_t hunk_idx,
     * size_t line_of_hunk);
     */
    // TODO
    static native int jniGetLineInHunk(AtomicLong out, long patch, int hunkIdx, int lineOfHunk);

    /** int git_patch_print(git_patch *patch, git_diff_line_cb print_cb, void *payload); */
    // TODO
    static native int jniPrint(long patch, JJJCallback printCb);
}
