package com.github.git24j.core;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Blame extends CAutoReleasable {
    protected Blame(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }


    /** int git_blame_init_options(git_blame_options *opts, unsigned int version); */
    static native int jniInitOptions(long opts, int version);

    static native void jniOptionsNew(AtomicLong outPtr, int version);

    /** size_t git_blame_get_hunk_count(git_blame *blame); */
    static native int jniGetHunkCount(long blame);

    /** Gets the number of hunks that exist in the blame structure. */
    public int getHunkCount() {
        return jniGetHunkCount(getRawPointer());
    }

    /** const git_blame_hunk * git_blame_get_hunk_byindex(git_blame *blame, size_t index); */
    static native long jniGetHunkByindex(long blame, int index);

    /**
     * Gets the blame hunk at the given index.
     *
     * @param index index of the hunk to retrieve
     * @return the hunk at the given index, or empty if non is available
     * @throws GitException git errors
     */
    public Optional<BlameHunk> getHunkByIndex(int index) {
        long ptr = jniGetHunkByindex(getRawPointer(), index);
        if (ptr == 0) {
            return Optional.empty();
        }
        return Optional.of(new BlameHunk(false, ptr));
    }

    /** const git_blame_hunk * git_blame_get_hunk_byline(git_blame *blame, size_t lineno); */
    static native long jniGetHunkByline(long blame, int lineno);

    /**
     * Gets the hunk that relates to the given line number in the newest commit.
     *
     * @param lineno the (1-based) line number to find a hunk for
     * @return the hunk that contains the given line, or NULL on error
     */
    public Optional<BlameHunk> getHunkByLine(int lineno) {
        long ptr = jniGetHunkByline(getRawPointer(), lineno);
        return ptr == 0 ? Optional.empty() : Optional.of(new BlameHunk(false, ptr));
    }

    /**
     * int git_blame_file(git_blame **out, git_repository *repo, const char *path, git_blame_options
     * *options);
     */
    static native int jniFile(AtomicLong out, long repoPtr, String path, long options);

    /**
     * Get the blame for a single file.
     *
     * @param repo repository whose history is to be walked
     * @param path path to file to consider
     * @param options options for the blame operation. If NULL, default Options will be used.
     * @return Blame object
     * @throws GitException git errors
     */
    @Nonnull
    public static Blame file(
            @Nonnull Repository repo, @Nonnull String path, @Nullable BlameOptions options) {
        Blame blame = new Blame(false, 0);
        Error.throwIfNeeded(
                jniFile(
                        blame._rawPtr,
                        repo.getRawPointer(),
                        path,
                        options == null ? 0 : options.getRawPointer()));
        return blame;
    }

    /**
     * int git_blame_buffer(git_blame **out, git_blame *reference, const char *buffer, size_t
     * buffer_len);
     */
    static native int jniBuffer(AtomicLong out, long reference, String buffer, int bufferLen);

    /**
     * Get blame data for a file that has been modified in memory. The `reference` parameter is a
     * pre-calculated blame for the in-odb history of the file. This means that once a file blame is
     * completed (which can be expensive), updating the buffer blame is very fast.
     *
     * <p>Lines that differ between the buffer and the committed version are marked as having a zero
     * OID for their final_commit_id.
     *
     * @param reference cached blame from the history of the file (usually the output from
     *     git_blame_file)
     * @param buffer the (possibly) modified contents of the file
     * @return blame data.
     * @throws GitException git errors
     */
    @Nonnull
    public static Blame buffer(@Nonnull Blame reference, @Nonnull String buffer) {
        Blame out = new Blame(false, 0);
        Error.throwIfNeeded(
                jniBuffer(out._rawPtr, reference.getRawPointer(), buffer, buffer.length()));
        return out;
    }

    /** void git_blame_free(git_blame *blame); */
    static native void jniFree(long blame);
}
