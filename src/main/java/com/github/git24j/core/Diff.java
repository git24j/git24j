package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;

public class Diff extends CAutoReleasable {
    AtomicLong _rawPtr = new AtomicLong();

    protected Diff(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

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

    public enum OptionFlag implements IBitEnum {
        /** Normal diff, the default */
        NORMAL0(0),

        /*
         * Options controlling which files will be in the diff
         */

        /** Reverse the sides of the diff */
        REVERSE(1 << 0),

        /** Include ignored files in the diff */
        INCLUDE_IGNORED(1 << 1),

        /**
         * Even with GIT_DIFF_INCLUDE_IGNORED, an entire ignored directory will be marked with only
         * a single entry in the diff; this flag adds all files under the directory as IGNORED
         * entries, too.
         */
        RECURSE_IGNORED_DIRS(1 << 2),

        /** Include untracked files in the diff */
        INCLUDE_UNTRACKED(1 << 3),

        /**
         * Even with GIT_DIFF_INCLUDE_UNTRACKED, an entire untracked directory will be marked with
         * only a single entry in the diff (a la what core Git does in `git status`); this flag adds
         * *all* files under untracked directories as UNTRACKED entries, too.
         */
        RECURSE_UNTRACKED_DIRS(1 << 4),

        /** Include unmodified files in the diff */
        INCLUDE_UNMODIFIED(1 << 5),

        /**
         * Normally, a type change between files will be converted into a DELETED record for the old
         * and an ADDED record for the new; this options enabled the generation of TYPECHANGE delta
         * records.
         */
        INCLUDE_TYPECHANGE(1 << 6),

        /**
         * Even with GIT_DIFF_INCLUDE_TYPECHANGE, blob->tree changes still generally show as a
         * DELETED blob. This flag tries to correctly label blob->tree transitions as TYPECHANGE
         * records with new_file's mode set to tree. Note: the tree SHA will not be available.
         */
        INCLUDE_TYPECHANGE_TREES(1 << 7),

        /** Ignore file mode changes */
        IGNORE_FILEMODE(1 << 8),

        /** Treat all submodules as unmodified */
        IGNORE_SUBMODULES(1 << 9),

        /** Use case insensitive filename comparisons */
        IGNORE_CASE(1 << 10),

        /**
         * May be combined with `GIT_DIFF_IGNORE_CASE` to specify that a file that has changed case
         * will be returned as an add/delete pair.
         */
        INCLUDE_CASECHANGE(1 << 11),

        /**
         * If the pathspec is set in the diff options, this flags indicates that the paths will be
         * treated as literal paths instead of fnmatch patterns. Each path in the list must either
         * be a full path to a file or a directory. (A trailing slash indicates that the path will
         * _only_ match a directory). If a directory is specified, all children will be included.
         */
        DISABLE_PATHSPEC_MATCH(1 << 12),

        /**
         * Disable updating of the `binary` flag in delta records. This is useful when iterating
         * over a diff if you don't need hunk and data callbacks and want to avoid having to load
         * file completely.
         */
        SKIP_BINARY_CHECK(1 << 13),

        /**
         * When diff finds an untracked directory, to match the behavior of core Git, it scans the
         * contents for IGNORED and UNTRACKED files. If *all* contents are IGNORED, then the
         * directory is IGNORED; if any contents are not IGNORED, then the directory is UNTRACKED.
         * This is extra work that may not matter in many cases. This flag turns off that scan and
         * immediately labels an untracked directory as UNTRACKED (changing the behavior to not
         * match core Git).
         */
        ENABLE_FAST_UNTRACKED_DIRS(1 << 14),

        /**
         * When diff finds a file in the working directory with stat information different from the
         * index, but the OID ends up being the same, write the correct stat information into the
         * index. Note: without this flag, diff will always leave the index untouched.
         */
        UPDATE_INDEX(1 << 15),

        /** Include unreadable files in the diff */
        INCLUDE_UNREADABLE(1 << 16),

        /** Include unreadable files in the diff */
        INCLUDE_UNREADABLE_AS_UNTRACKED(1 << 17),

        /*
         * Options controlling how output will be generated
         */

        /**
         * Use a heuristic that takes indentation and whitespace into account which generally can
         * produce better diffs when dealing with ambiguous diff hunks.
         */
        INDENT_HEURISTIC(1 << 18),

        /** Treat all files as text, disabling binary attributes & detection */
        FORCE_TEXT(1 << 20),
        /** Treat all files as binary, disabling text diffs */
        FORCE_BINARY(1 << 21),

        /** Ignore all whitespace */
        IGNORE_WHITESPACE(1 << 22),
        /** Ignore changes in amount of whitespace */
        IGNORE_WHITESPACE_CHANGE(1 << 23),
        /** Ignore whitespace at end of line */
        IGNORE_WHITESPACE_EOL(1 << 24),

        /**
         * When generating patch text, include the content of untracked files. This automatically
         * turns on GIT_DIFF_INCLUDE_UNTRACKED but it does not turn on
         * GIT_DIFF_RECURSE_UNTRACKED_DIRS. Add that flag if you want the content of every single
         * UNTRACKED file.
         */
        SHOW_UNTRACKED_CONTENT(1 << 25),

        /**
         * When generating output, include the names of unmodified files if they are included in the
         * git_diff. Normally these are skipped in the formats that list files (e.g. name-only,
         * name-status, raw). Even with this, these will not be included in patch format.
         */
        SHOW_UNMODIFIED(1 << 26),

        /** Use the "patience diff" algorithm */
        PATIENCE(1 << 28),
        /** Take extra time to find minimal diff */
        MINIMAL(1 << 29),

        /**
         * Include the necessary deflate / delta information so that `git-apply` can apply given
         * diff information to binary files.
         */
        SHOW_BINARY(1 << 30),
        ;

        final int bit;

        OptionFlag(int bit) {
            this.bit = bit;
        }

        @Override
        public int getBit() {
            return bit;
        }
    }

    /**
     * Flags for diff options. A combination of these flags can be passed in via the `flags` value
     * in the `git_diff_options`.
     */
    public static class Options extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;

        Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        /**
         * Create and initialize a Diff Options object
         *
         * <p>Initializes a `git_diff_options` with default values. Equivalent to creating an
         * instance with GIT_DIFF_OPTIONS_INIT.
         *
         * @param version The struct version; pass `GIT_DIFF_OPTIONS_VERSION`.
         * @throws GitException git errors
         */
        public static Options create(int version) {
            Options opts = new Options(false, 0);
            Error.throwIfNeeded(jniInitOptions(opts._rawPtr, version));
            return opts;
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFreeOptions(cPtr);
        }
    }

    /**
     * Control behavior of rename and copy detection
     *
     * <p>These options mostly mimic parameters that can be passed to git-diff.
     */
    public static class FindOptions extends CAutoReleasable {
        FindOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        /**
         * Create and initialize git_diff_find_options structure
         *
         * <p>Initializes a `git_diff_find_options` with default values. Equivalent to creating an
         * instance with GIT_DIFF_FIND_OPTIONS_INIT.
         *
         * @param version The struct version; pass `GIT_DIFF_FIND_OPTIONS_VERSION`.
         * @throws GitException git errors
         */
        public static FindOptions create(int version) {
            FindOptions opts = new FindOptions(false, 0);
            jniFindInitOptions(opts._rawPtr, version);
            return opts;
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFreeFindOptions(cPtr);
        }
    }

    /** int git_diff_init_options(git_diff_options *opts, unsigned int version); */
    static native int jniInitOptions(AtomicLong outOpts, int version);

    /** free diff options */
    static native void jniFreeOptions(long opts);

    /** int git_diff_find_init_options(git_diff_find_options *opts, unsigned int version); */
    static native int jniFindInitOptions(AtomicLong outOpts, int version);

    static native void jniFreeFindOptions(long findOptsPtr);

    /** void git_diff_free(git_diff *diff); */
    static native void jniFree(long diff);

    /**
     * int git_diff_tree_to_tree(git_diff **diff, git_repository *repo, git_tree *old_tree, git_tree
     * *new_tree, const git_diff_options *opts);
     */
    static native int jniTreeToTree(
            AtomicLong diff, long repoPtr, long oldTree, long newTree, long opts);

    /**
     * Create a diff with the difference between two tree objects.
     *
     * <p>This is equivalent to `git diff <old-tree> <new-tree>`
     *
     * <p>The first tree will be used for the "old_file" side of the delta and the second tree will
     * be used for the "new_file" side of the delta. You can pass NULL to indicate an empty tree,
     * although it is an error to pass NULL for both the `old_tree` and `new_tree`.
     *
     * @param repo The repository containing the trees.
     * @param oldTree A git_tree object to diff from, or NULL for empty tree.
     * @param newTree A git_tree object to diff to, or NULL for empty tree.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @return Diff between {@code oldTree} and {@code newTree}
     * @throws GitException git errors
     */
    public static Diff treeToTree(Repository repo, Tree oldTree, Tree newTree, Options opts) {
        Diff diff = new Diff(false, 0);
        int e =
                jniTreeToTree(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree.getRawPointer(),
                        newTree.getRawPointer(),
                        opts.getRawPointer());
        Error.throwIfNeeded(e);
        return diff;
    }

    /**
     * int git_diff_tree_to_index(git_diff **diff, git_repository *repo, git_tree *old_tree,
     * git_index *index, const git_diff_options *opts);
     */
    static native int jniTreeToIndex(
            AtomicLong diff, long repoPtr, long oldTree, long index, long opts);

    /**
     * Create a diff between a tree and repository index.
     *
     * <p>This is equivalent to `git diff --cached <treeish>` or if you pass the HEAD tree, then
     * like `git diff --cached`.
     *
     * <p>The tree you pass will be used for the "old_file" side of the delta, and the index will be
     * used for the "new_file" side of the delta.
     *
     * <p>If you pass NULL for the index, then the existing index of the `repo` will be used. In
     * this case, the index will be refreshed from disk (if it has changed) before the diff is
     * generated.
     *
     * @param repo The repository containing the tree and index.
     * @param oldTree A git_tree object to diff from, or NULL for empty tree.
     * @param index The index to diff with; repo index used if NULL.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @throws GitException git errors
     */
    public static Diff treeToIndex(Repository repo, Tree oldTree, Index index, Options opts) {
        Diff diff = new Diff(false, 0);
        int e =
                jniTreeToIndex(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree.getRawPointer(),
                        index.getRawPointer(),
                        opts.getRawPointer());
        Error.throwIfNeeded(e);
        return diff;
    }

    /**
     * int git_diff_index_to_workdir(git_diff **diff, git_repository *repo, git_index *index, const
     * git_diff_options *opts);
     */
    static native int jniIndexToWorkdir(AtomicLong diff, long repoPtr, long index, long opts);

    /**
     * Create a diff between the repository index and the workdir directory.
     *
     * <p>This matches the `git diff` command. See the note below on `git_diff_tree_to_workdir` for
     * a discussion of the difference between `git diff` and `git diff HEAD` and how to emulate a
     * `git diff <treeish>` using libgit2.
     *
     * <p>The index will be used for the "old_file" side of the delta, and the working directory
     * will be used for the "new_file" side of the delta.
     *
     * <p>If you pass NULL for the index, then the existing index of the `repo` will be used. In
     * this case, the index will be refreshed from disk (if it has changed) before the diff is
     * generated.
     *
     * @param repo The repository.
     * @param index The index to diff from; repo index used if NULL.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @throws GitException git errors
     */
    public static Diff indexToWorkdir(Repository repo, Index index, Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniIndexToWorkdir(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        index.getRawPointer(),
                        opts.getRawPointer()));
        return diff;
    }

    /**
     * int git_diff_tree_to_workdir(git_diff **diff, git_repository *repo, git_tree *old_tree, const
     * git_diff_options *opts);
     */
    static native int jniTreeToWorkdir(AtomicLong diff, long repoPtr, long oldTree, long opts);

    /**
     * Create a diff between a tree and the working directory.
     *
     * <p>The tree you provide will be used for the "old_file" side of the delta, and the working
     * directory will be used for the "new_file" side.
     *
     * <p>This is not the same as `git diff <treeish>` or `git diff-index <treeish>`. Those commands
     * use information from the index, whereas this function strictly returns the differences
     * between the tree and the files in the working directory, regardless of the state of the
     * index. Use `git_diff_tree_to_workdir_with_index` to emulate those commands.
     *
     * <p>To see difference between this and `git_diff_tree_to_workdir_with_index`, consider the
     * example of a staged file deletion where the file has then been put back into the working dir
     * and further modified. The tree-to-workdir diff for that file is 'modified', but `git diff`
     * would show status 'deleted' since there is a staged delete.
     *
     * @param repo The repository containing the tree.
     * @param oldTree A git_tree object to diff from, or NULL for empty tree.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @return diff between {@code tree} and the working directory
     * @throws GitException git errors
     */
    public static Diff treeToWorkdir(Repository repo, Tree oldTree, Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniTreeToWorkdir(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree.getRawPointer(),
                        opts.getRawPointer()));
        return diff;
    }

    /**
     * int git_diff_tree_to_workdir_with_index(git_diff **diff, git_repository *repo, git_tree
     * *old_tree, const git_diff_options *opts);
     */
    static native int jniTreeToWorkdirWithIndex(
            AtomicLong diff, long repoPtr, long oldTree, long opts);

    /**
     * Create a diff between a tree and the working directory using index data to account for staged
     * deletes, tracked files, etc.
     *
     * <p>This emulates `git diff <tree>` by diffing the tree to the index and the index to the
     * working directory and blending the results into a single diff that includes staged deleted,
     * etc.
     *
     * @param repo The repository containing the tree.
     * @param oldTree A git_tree object to diff from, or NULL for empty tree.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @throws GitException git errors
     */
    public static Diff treeToWorkdirWithIndex(Repository repo, Tree oldTree, Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniTreeToWorkdirWithIndex(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldTree.getRawPointer(),
                        opts.getRawPointer()));
        return diff;
    }

    /**
     * int git_diff_index_to_index(git_diff **diff, git_repository *repo, git_index *old_index,
     * git_index *new_index, const git_diff_options *opts);
     */
    static native int jniIndexToIndex(
            AtomicLong diff, long repoPtr, long oldIndex, long newIndex, long opts);

    /**
     * Create a diff with the difference between two index objects.
     *
     * <p>The first index will be used for the "old_file" side of the delta and the second index
     * will be used for the "new_file" side of the delta.
     *
     * @param repo The repository containing the indexes.
     * @param oldIndex A git_index object to diff from.
     * @param newIndex A git_index object to diff to.
     * @param opts Structure with options to influence diff or NULL for defaults.
     * @throws GitException git errors
     */
    @Nonnull
    public static Diff indexToIndex(
            @Nonnull Repository repo,
            @Nonnull Index oldIndex,
            @Nonnull Index newIndex,
            @Nonnull Options opts) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(
                jniIndexToIndex(
                        diff._rawPtr,
                        repo.getRawPointer(),
                        oldIndex.getRawPointer(),
                        newIndex.getRawPointer(),
                        opts.getRawPointer()));
        return diff;
    }
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
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
}
