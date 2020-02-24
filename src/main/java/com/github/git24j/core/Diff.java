package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.git24j.core.Internals.JFCallback;
import static com.github.git24j.core.Internals.JJCallback;
import static com.github.git24j.core.Internals.JJJCallback;

public class Diff extends CAutoReleasable {
    AtomicLong _rawPtr = new AtomicLong();

    protected Diff(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    @FunctionalInterface
    interface FileCb {
        int accept(Delta delta, float progress);
    }

    @FunctionalInterface
    interface BinaryCb {
        int accept(Delta delta, Binary binary);
    }

    @FunctionalInterface
    interface HunkCb {
        int accept(Delta delta, Hunk hunk);
    }

    @FunctionalInterface
    interface LineCb {
        int accept(Delta delta, Hunk hunk, Line line);
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

    /**
     * What type of change is described by a git_diff_delta?
     *
     * <p>`RENAMED` and `COPIED` will only show up if you run `git_diff_find_similar()` on the diff
     * object.
     *
     * <p>`TYPECHANGE` only shows up given `GIT_DIFF_INCLUDE_TYPECHANGE` in the option flags
     * (otherwise type changes will be split into ADDED / DELETED pairs).
     */
    public enum DeltaT {
        UNMODIFIED(0),
        /** < no changes */
        ADDED(1),
        /** < entry does not exist in old version */
        DELETED(2),
        /** < entry does not exist in new version */
        MODIFIED(3),
        /** < entry content changed between old and new */
        RENAMED(4),
        /** < entry was renamed between old and new */
        COPIED(5),
        /** < entry was copied from another old entry */
        IGNORED(6),
        /** < entry is ignored item in workdir */
        UNTRACKED(7),
        /** < entry is untracked item in workdir */
        TYPECHANGE(8),
        /** < type of entry changed between old and new */
        UNREADABLE(9),
        /** < entry is unreadable */
        CONFLICTED(10),
        ;
        /** < entry in the index is conflicted */
        private final int _code;

        DeltaT(int _code) {
            this._code = _code;
        }

        public int getCode() {
            return _code;
        }
    }

    /**
     * Description of changes to one entry.
     *
     * <p>A `delta` is a file pair with an old and new revision. The old version may be absent if
     * the file was just created and the new version may be absent if the file was deleted. A diff
     * is mostly just a list of deltas.
     *
     * <p>When iterating over a diff, this will be passed to most callbacks and you can use the
     * contents to understand exactly what has changed.
     *
     * <p>The `old_file` represents the "from" side of the diff and the `new_file` represents to
     * "to" side of the diff. What those means depend on the function that was used to generate the
     * diff and will be documented below. You can also use the `GIT_DIFF_REVERSE` flag to flip it
     * around.
     *
     * <p>Although the two sides of the delta are named "old_file" and "new_file", they actually may
     * correspond to entries that represent a file, a symbolic link, a submodule commit id, or even
     * a tree (if you are tracking type changes or ignored/untracked directories).
     *
     * <p>Under some circumstances, in the name of efficiency, not all fields will be filled in, but
     * we generally try to fill in as much as possible. One example is that the "flags" field may
     * not have either the `BINARY` or the `NOT_BINARY` flag set to avoid examining file contents if
     * you do not pass in hunk and/or line callbacks to the diff foreach iteration function. It will
     * just use the git attributes for those files.
     *
     * <p>The similarity score is zero unless you call `git_diff_find_similar()` which does a
     * similarity analysis of files in the diff. Use that function to do rename and copy detection,
     * and to split heavily modified files in add/delete pairs. After that call, deltas with a
     * status of GIT_DELTA_RENAMED or GIT_DELTA_COPIED will have a similarity score between 0 and
     * 100 indicating how similar the old and new sides are.
     *
     * <p>If you ask `git_diff_find_similar` to find heavily modified files to break, but to not
     * *actually* break the records, then GIT_DELTA_MODIFIED records may have a non-zero similarity
     * score if the self-similarity is below the split threshold. To display this value like core
     * Git, invert the score (a la `printf("M%03d", 100 - delta->similarity)`).
     */
    public static class Delta extends CAutoReleasable {

        /**
         * Construct a weakref to c struct
         *
         * @param rawPtr c pointer
         */
        protected Delta(long rawPtr) {
            super(true, rawPtr);
        }

        @CheckForNull
        static Delta of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Delta(rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Delta is owned by Diff and should not be released manually");
        }
    }

    /**
     * Structure describing the binary contents of a diff.
     *
     * <p>A `binary` file / delta is a file (or pair) for which no text diffs should be generated. A
     * diff can contain delta entries that are binary, but no diff content will be output for those
     * files. There is a base heuristic for binary detection and you can further tune the behavior
     * with git attributes or diff flags and option settings.
     */
    public static class Binary extends CAutoReleasable {
        protected Binary(long rawPtr) {
            super(true, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Binary is owned by Diff and should not be released manually");
        }

        @CheckForNull
        static Binary of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Binary(rawPtr);
        }
    }
    /**
     * Structure describing a hunk of a diff.
     *
     * <p>A `hunk` is a span of modified lines in a delta along with some stable surrounding
     * context. You can configure the amount of context and other properties of how hunks are
     * generated. Each hunk also comes with a header that described where it starts and ends in both
     * the old and new versions in the delta.
     */
    public static class Hunk extends CAutoReleasable {
        protected Hunk(long rawPtr) {
            super(true, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Hunk is owned by Diff and should not be released manually");
        }
        @CheckForNull
        static Hunk of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Hunk(rawPtr);
        }
    }

    /**
     * Structure describing a line (or data span) of a diff.
     *
     * <p>A `line` is a range of characters inside a hunk. It could be a context line (i.e. in both
     * old and new versions), an added line (i.e. only in the new version), or a removed line (i.e.
     * only in the old version). Unfortunately, we don't know anything about the encoding of data in
     * the file being diffed, so we cannot tell you much about the line content. Line data will not
     * be NUL-byte terminated, however, because it will be just a span of bytes inside the larger
     * file.
     */
    public static class Line extends CAutoReleasable {
        protected Line(long rawPtr) {
            super(true, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            throw new IllegalStateException(
                    "Diff.Line is owned by Diff and should not be released manually");
        }
        @CheckForNull
        static Line of(long rawPtr) {
            if (rawPtr == 0) {
                return null;
            }
            return new Line(rawPtr);
        }
    }

    /** Possible output formats for diff data */
    public enum FormatT {
        /** < full git diff */
        PATCH(1),
        /** < just the file headers of patch */
        PATCH_HEADER(2),
        /** < like git diff --raw */
        RAW(3),
        /** < like git diff --name-only */
        NAME_ONLY(4),
        /** < like git diff --name-status */
        NAME_STATUS(5),
        ;
        private final int _code;

        FormatT(int _code) {
            this._code = _code;
        }

        public int getCode() {
            return _code;
        }
    }

    public enum StatsFormatT {
        /** No stats */
        NONE(0),

        /** Full statistics, equivalent of `--stat` */
        FULL(1 << 0),

        /** Short statistics, equivalent of `--shortstat` */
        SHORT(1 << 1),

        /** Number statistics, equivalent of `--numstat` */
        NUMBER(1 << 2),

        /**
         * Extended header information such as creations, renames and mode changes, equivalent of
         * `--summary`
         */
        INCLUDE_SUMMARY(1 << 3),
        ;
        private final int _code;

        StatsFormatT(int _code) {
            this._code = _code;
        }

        public int getCode() {
            return _code;
        }
    }

    public static class Stats extends CAutoReleasable {
        protected Stats(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniStatsFree(cPtr);
        }

        /** @return total number of files changed in the diff */
        public int filesChanged() {
            return jniStatsFilesChanged(getRawPointer());
        }

        /** @return total number of insertions in the diff */
        public int insertions() {
            return jniStatsInsertions(getRawPointer());
        }

        /** @return total number of deletions in the diff */
        public int deletions() {
            return jniStatsDeletions(getRawPointer());
        }

        /**
         * Print diff statistics to a `git_buf`.
         *
         * @param format Formatting option.
         * @param width Target width for output (only affects GIT_DIFF_STATS_FULL)
         * @return the formatted diff statistics in.
         * @throws GitException git errors
         */
        public Buf toBuf(StatsFormatT format, int width) {
            Buf out = new Buf();
            Error.throwIfNeeded(jniStatsToBuf(out, getRawPointer(), format.getCode(), width));
            return out;
        }
    }

    /** Formatting options for diff e-mail generation */
    public enum FormatEmailFlagT {
        /** Normal patch, the default */
        NONE(0),

        /** Don't insert "[PATCH]" in the subject header */
        EXCLUDE_SUBJECT_PATCH_MARKER(1 << 0);
        private final int _code;

        FormatEmailFlagT(int _code) {
            this._code = _code;
        }

        public int getCode() {
            return _code;
        }
    }

    /** Options for controlling the formatting of the generated e-mail. */
    public static class FormatEmailOptions extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;

        private static class Holder {
            static final FormatEmailOptions __DEFAULT = FormatEmailOptions.create(CURRENT_VERSION);
        }

        protected FormatEmailOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFormatEmailOptionsFree(cPtr);
        }

        /** Create FormatEmailOptions by version. */
        @Nonnull
        public static FormatEmailOptions create(int version) {
            FormatEmailOptions opts = new FormatEmailOptions(false, 0);
            Error.throwIfNeeded(jniFormatEmailNewOptions(opts._rawPtr, version));
            return opts;
        }

        public static FormatEmailOptions defaultOptions() {
            return Holder.__DEFAULT;
        }
    }

    public static class PatchidOptions extends CAutoReleasable {
        protected PatchidOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniPatchidOptionsFree(cPtr);
        }

        public PatchidOptions create(int version) {
            PatchidOptions out = new PatchidOptions(false, 0);
            Error.throwIfNeeded(jniPatchidOptionsNew(out._rawPtr, version));
            return out;
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

    /**
     * Merge one diff into another.
     *
     * <p>This merges items from the "from" list into the "onto" list. The resulting diff will have
     * all items that appear in either list. If an item appears in both lists, then it will be
     * "merged" to appear as if the old version was from the "onto" list and the new version is from
     * the "from" list (with the exception that if the item has a pending DELETE in the middle, then
     * it will show as deleted).
     *
     * @param onto Diff to merge into.
     * @param from Diff to merge.
     */
    public static void merge(@Nonnull Diff onto, @Nonnull Diff from) {
        Error.throwIfNeeded(jniMerge(onto.getRawPointer(), from.getRawPointer()));
    }

    /** int git_diff_find_similar(git_diff *diff, const git_diff_find_options *options); */
    static native int jniFindSimilar(long diff, long options);

    /**
     * Transform a diff marking file renames, copies, etc.
     *
     * <p>This modifies a diff in place, replacing old entries that look like renames or copies with
     * new entries reflecting those changes. This also will, if requested, break modified files into
     * add/remove pairs if the amount of change is above a threshold.
     *
     * @param options Control how detection should be run, NULL for defaults
     * @throws GitException git errors
     */
    public void findSimilar(@Nonnull FindOptions options) {
        Error.throwIfNeeded(jniFindSimilar(getRawPointer(), options.getRawPointer()));
    }

    /** size_t git_diff_num_deltas(const git_diff *diff); */
    static native int jniNumDeltas(long diff);

    /**
     * Query how many diff records are there in a diff.
     *
     * @return Count of number of deltas in the list
     */
    public int numDeltas() {
        return jniNumDeltas(getRawPointer());
    }

    /** size_t git_diff_num_deltas_of_type(const git_diff *diff, git_delta_t type); */
    static native int jniNumDeltasOfType(long diff, int type);

    /**
     * Query how many diff deltas are there in a diff filtered by type.
     *
     * <p>This works just like `git_diff_entrycount()` with an extra parameter that is a
     * `git_delta_t` and returns just the count of how many deltas match that particular type.
     *
     * @param type A git_delta_t value to filter the count
     * @return Count of number of deltas matching delta_t type
     */
    public int numDeltasOfType(@Nonnull DeltaT type) {
        return jniNumDeltasOfType(getRawPointer(), type.getCode());
    }

    /** const git_diff_delta * git_diff_get_delta(const git_diff *diff, size_t idx); */
    static native long jniGetDelta(long diff, int idx);

    /**
     * Return the diff delta for an entry in the diff list.
     *
     * <p>The `git_diff_delta` pointer points to internal data and you do not have to release it
     * when you are done with it. It will go away when the * `git_diff` (or any associated
     * `git_patch`) goes away.
     *
     * <p>Note that the flags on the delta related to whether it has binary content or not may not
     * be set if there are no attributes set for the file and there has been no reason to load the
     * file data at this point. For now, if you need those flags to be up to date, your only option
     * is to either use `git_diff_foreach` or create a `git_patch`.
     *
     * @param idx Index into diff list
     * @return Pointer to git_diff_delta (or NULL if `idx` out of range)
     */
    @CheckForNull
    public Delta getDelta(int idx) {
        long ptr = jniGetDelta(getRawPointer(), idx);
        if (ptr == 0) {
            return null;
        }
        return new Delta(ptr);
    }

    /** int git_diff_is_sorted_icase(const git_diff *diff); */
    static native int jniIsSortedIcase(long diff);

    /**
     * Check if deltas are sorted case sensitively or insensitively.
     *
     * @return false if case sensitive, true if case is ignored
     */
    public boolean isSortedIcase() {
        return jniIsSortedIcase(getRawPointer()) == 1;
    }

    /**
     * int git_diff_foreach(git_diff *diff, git_diff_file_cb file_cb, git_diff_binary_cb binary_cb,
     * git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb, void *payload);
     */
    static native int jniForeach(
            long diff,
            Internals.JFCallback fileCb,
            JJCallback binaryCb,
            JJCallback hunkCb,
            JJJCallback lineCb);

    /**
     * Loop over all deltas in a diff issuing callbacks.
     *
     * <p>This will iterate through all of the files described in a diff. You should provide a file
     * callback to learn about each file.
     *
     * <p>The "hunk" and "line" callbacks are optional, and the text diff of the files will only be
     * calculated if they are not NULL. Of course, these callbacks will not be invoked for binary
     * files on the diff or for files whose only changed is a file mode change.
     *
     * <p>Returning a non-zero value from any of the callbacks will terminate the iteration and
     * return the value to the user.
     *
     * @param fileCb Callback function to make per file in the diff.
     * @param binaryCb Optional callback to make for binary files.
     * @param hunkCb Optional callback to make per hunk of text diff. This callback is called to
     *     describe a range of lines in the diff. It will not be issued for binary files.
     * @param lineCb Optional callback to make per line of diff text. This same callback will be
     *     made for context lines, added, and removed lines, and even for a deleted trailing
     *     newline.
     * @return 0 on success, non-zero callback return value
     * @throws GitException git errors
     */
    public int foreach(
            @Nullable FileCb fileCb,
            @Nullable BinaryCb binaryCb,
            @Nullable HunkCb hunkCb,
            @Nullable LineCb lineCb) {
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e = jniForeach(getRawPointer(), jniFileCb, jniBinaryCb, jniHunkCb, jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }

    /** char git_diff_status_char(git_delta_t status); */
    static native char jniStatusChar(int status);

    /**
     * Look up the single character abbreviation for a delta status code.
     *
     * <p>When you run `git diff --name-status` it uses single letter codes in the output such as
     * 'A' for added, 'D' for deleted, 'M' for modified, etc. This function converts a git_delta_t
     * value into these letters for your own purposes. GIT_DELTA_UNTRACKED will return a space (i.e.
     * ' ').
     *
     * @param status The git_delta_t value to look up
     * @return The single character label for that code
     */
    public static char statusChar(@Nonnull DeltaT status) {
        return jniStatusChar(status.getCode());
    }

    /**
     * int git_diff_print(git_diff *diff, git_diff_format_t format, git_diff_line_cb print_cb, void
     * *payload);
     */
    static native int jniPrint(long diff, int format, JJJCallback printCb);

    /**
     * Iterate over a diff generating formatted text output.
     *
     * <p>Returning a non-zero value from the callbacks will terminate the iteration and return the
     * non-zero value to the caller.
     *
     * @param format A git_diff_format_t value to pick the text format.
     * @param lineCb Callback to make per line of diff text.
     * @return 0 on success, non-zero callback return value
     * @throws GitException git errors
     */
    public int print(@Nonnull FormatT format, @Nullable LineCb lineCb) {
        JJJCallback cb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e = jniPrint(getRawPointer(), format.getCode(), cb);
        Error.throwIfNeeded(e);
        return e;
    }

    /** int git_diff_to_buf(git_buf *out, git_diff *diff, git_diff_format_t format); */
    static native int jniToBuf(Buf out, long diff, int format);

    /**
     * Produce the complete formatted text output from a diff into a buffer.
     *
     * @param format A git_diff_format_t value to pick the text format.
     * @return formated diff text
     * @throws GitException git errors
     */
    @Nonnull
    public Buf toBuf(@Nonnull FormatT format) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniToBuf(out, getRawPointer(), format.getCode()));
        return out;
    }

    /**
     * int git_diff_blobs( const git_blob *old_blob, const char *old_as_path, const git_blob
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
     * Directly run a diff on two blobs.
     *
     * <p>Compared to a file, a blob lacks some contextual information. As such, the `git_diff_file`
     * given to the callback will have some fake data; i.e. `mode` will be 0 and `path` will be
     * NULL.
     *
     * <p>NULL is allowed for either `old_blob` or `new_blob` and will be treated as an empty blob,
     * with the `oid` set to NULL in the `git_diff_file` data. Passing NULL for both blobs is a
     * noop; no callbacks will be made at all.
     *
     * <p>We do run a binary content check on the blob content and if either blob looks like binary
     * data, the `git_diff_delta` binary attribute will be set to 1 and no call to the hunk_cb nor
     * line_cb will be made (unless you pass `GIT_DIFF_FORCE_TEXT` of course).
     *
     * @param oldBlob Blob for old side of diff, or NULL for empty blob
     * @param oldAsPath Treat old blob as if it had this filename; can be NULL
     * @param newBlob Blob for new side of diff, or NULL for empty blob
     * @param newAsPath Treat new blob as if it had this filename; can be NULL
     * @param options Options for diff, or NULL for default options
     * @param fileCb Callback for "file"; made once if there is a diff; can be NULL
     * @param binaryCb Callback for binary files; can be NULL
     * @param hunkCb Callback for each hunk in diff; can be NULL
     * @param lineCb Callback for each line in diff; can be NULL
     * @return non-zero callback return value
     * @throws GitException git errors
     */
    public static int blobs(
            @Nullable Blob oldBlob,
            @Nullable Path oldAsPath,
            @Nullable Blob newBlob,
            @Nullable Path newAsPath,
            @Nullable Options options,
            @Nullable FileCb fileCb,
            @Nullable BinaryCb binaryCb,
            @Nullable HunkCb hunkCb,
            @Nullable LineCb lineCb) {
        long jniOldBlob = oldBlob == null ? 0 : oldBlob.getRawPointer();
        String jniOldAsPath = oldAsPath == null ? null : oldAsPath.toString();
        long jniNewBlob = newBlob == null ? 0 : newBlob.getRawPointer();
        String jniNewAsPath = newAsPath == null ? null : newAsPath.toString();
        long jniOptions =
                options == null
                        ? Options.create(Options.CURRENT_VERSION).getRawPointer()
                        : options.getRawPointer();
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e =
                jniBlobs(
                        jniOldBlob,
                        jniOldAsPath,
                        jniNewBlob,
                        jniNewAsPath,
                        jniOptions,
                        jniFileCb,
                        jniBinaryCb,
                        jniHunkCb,
                        jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }

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
     * Directly run a diff between a blob and a buffer.
     *
     * <p>As with `git_diff_blobs`, comparing a blob and buffer lacks some context, so the
     * `git_diff_file` parameters to the callbacks will be faked a la the rules for
     * `git_diff_blobs()`.
     *
     * <p>Passing NULL for `old_blob` will be treated as an empty blob (i.e. the `file_cb` will be
     * invoked with GIT_DELTA_ADDED and the diff will be the entire content of the buffer added).
     * Passing NULL to the buffer will do the reverse, with GIT_DELTA_REMOVED and blob content
     * removed.
     *
     * @param oldBlob Blob for old side of diff, or NULL for empty blob
     * @param oldAsPath Treat old blob as if it had this filename; can be NULL
     * @param buffer Raw data for new side of diff, or NULL for empty
     * @param bufferAsPath Treat buffer as if it had this filename; can be NULL
     * @param options Options for diff, or NULL for default options
     * @param fileCb Callback for "file"; made once if there is a diff; can be NULL
     * @param binaryCb Callback for binary files; can be NULL
     * @param hunkCb Callback for each hunk in diff; can be NULL
     * @param lineCb Callback for each line in diff; can be NULL
     * @return non-zero callback return value
     * @throws GitException git errors
     */
    public static int blobToBuff(
            @Nullable Blob oldBlob,
            @Nullable Path oldAsPath,
            @Nullable String buffer,
            @Nullable String bufferAsPath,
            @Nullable Options options,
            @Nullable FileCb fileCb,
            @Nullable BinaryCb binaryCb,
            @Nullable HunkCb hunkCb,
            @Nullable LineCb lineCb) {
        long oldBlobPtr = oldBlob == null ? 0 : oldBlob.getRawPointer();
        String oldAsPathStr = oldAsPath == null ? null : oldAsPath.toString();
        long optsPtr = options == null ? 0 : options.getRawPointer();
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e =
                jniBlobToBuffer(
                        oldBlobPtr,
                        oldAsPathStr,
                        buffer,
                        buffer == null ? 0 : buffer.length(),
                        bufferAsPath,
                        optsPtr,
                        jniFileCb,
                        jniBinaryCb,
                        jniHunkCb,
                        jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }

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

    /**
     * Directly run a diff between two buffers.
     *
     * <p>Even more than with `git_diff_blobs`, comparing two buffer lacks context, so the
     * `git_diff_file` parameters to the callbacks will be faked a la the rules for
     * `git_diff_blobs()`.
     *
     * @param oldBuffer Raw data for old side of diff, or NULL for empty
     * @param oldAsPath Treat old buffer as if it had this filename; can be NULL
     * @param newBuffer Raw data for new side of diff, or NULL for empty
     * @param newAsPath Treat buffer as if it had this filename; can be NULL
     * @param options Options for diff, or NULL for default options
     * @param fileCb Callback for "file"; made once if there is a diff; can be NULL
     * @param binaryCb Callback for binary files; can be NULL
     * @param hunkCb Callback for each hunk in diff; can be NULL
     * @param lineCb Callback for each line in diff; can be NULL
     * @return 0 on success, non-zero callback return value, or error code
     */
    public static int buffers(
            @Nullable byte[] oldBuffer,
            @Nullable String oldAsPath,
            @Nullable byte[] newBuffer,
            @Nullable String newAsPath,
            @Nullable Options options,
            FileCb fileCb,
            BinaryCb binaryCb,
            HunkCb hunkCb,
            LineCb lineCb) {
        JFCallback jniFileCb =
                fileCb == null ? null : (pd, progress) -> fileCb.accept(new Delta(pd), progress);
        JJCallback jniBinaryCb =
                binaryCb == null
                        ? null
                        : (pd, pb) -> binaryCb.accept(new Delta(pd), new Binary(pb));
        JJCallback jniHunkCb =
                hunkCb == null ? null : (pd, ph) -> hunkCb.accept(new Delta(pd), new Hunk(ph));
        JJJCallback jniLineCb =
                lineCb == null
                        ? null
                        : (pd, ph, pl) -> lineCb.accept(new Delta(pd), new Hunk(ph), new Line(pl));
        int e =
                jniBuffers(
                        oldBuffer,
                        oldBuffer == null ? 0 : oldBuffer.length,
                        oldAsPath,
                        newBuffer,
                        newBuffer == null ? 0 : newBuffer.length,
                        newAsPath,
                        options == null ? 0 : options.getRawPointer(),
                        jniFileCb,
                        jniBinaryCb,
                        jniHunkCb,
                        jniLineCb);
        Error.throwIfNeeded(e);
        return e;
    }

    /** int git_diff_from_buffer(git_diff **out, const char *content, size_t content_len); */
    static native int jniFromBuffer(AtomicLong out, String content, int contentLen);

    /**
     * Read the contents of a git patch file into a `git_diff` object.
     *
     * <p>The diff object produced is similar to the one that would be produced if you actually
     * produced it computationally by comparing two trees, however there may be subtle differences.
     * For example, a patch file likely contains abbreviated object IDs, so the object IDs in a
     * `git_diff_delta` produced by this function will also be abbreviated.
     *
     * <p>This function will only read patch files created by a git implementation, it will not read
     * unified diffs produced by the `diff` program, nor any other types of patch files.
     *
     * @param content The contents of a patch file
     * @return buffer
     * @throws GitException git errors
     */
    @Nonnull
    public Diff fromBuffer(@Nonnull String content) {
        Diff diff = new Diff(false, 0);
        Error.throwIfNeeded(jniFromBuffer(diff._rawPtr, content, content.length()));
        return diff;
    }

    /** int git_diff_get_stats(git_diff_stats **out, git_diff *diff); */
    static native int jniGetStats(AtomicLong out, long diff);

    /**
     * Accumulate diff statistics for all patches.
     *
     * @return Structure containg the diff statistics.
     * @throws GitException git errors
     */
    @Nonnull
    public Stats getStats() {
        Stats out = new Stats(false, 0);
        Error.throwIfNeeded(jniGetStats(out._rawPtr, getRawPointer()));
        return out;
    }

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
     * Create an e-mail ready patch from a diff.
     *
     * @param opts structure with options to influence content and formatting.
     * @return buffer to store the e-mail patch in
     * @throws GitException git errors
     */
    @Nonnull
    public Buf formatEmail(@Nonnull FormatEmailOptions opts) {
        Buf out = new Buf();
        jniFormatEmail(out, getRawPointer(), opts.getRawPointer());
        return out;
    }

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
     * Create an e-mail ready patch for a commit.
     *
     * <p>Does not support creating patches for merge commits (yet).
     *
     * @param repo containing the commit
     * @param commit pointer to up commit
     * @param patchNo patch number of the commit
     * @param totalPatches total number of patches in the patch set
     * @param flags determines the formatting of the e-mail
     * @param diffOpts structure with options to influence diff or NULL for defaults.
     * @return buffer to store the e-mail patch in
     * @throws GitException git errors
     */
    @Nonnull
    public Buf commitAsEmail(
            @Nonnull Repository repo,
            @Nonnull Commit commit,
            int patchNo,
            int totalPatches,
            @Nonnull FormatEmailFlagT flags,
            @Nullable Options diffOpts) {
        Buf outBuf = new Buf();
        Error.throwIfNeeded(
                jniCommitAsEmail(
                        outBuf,
                        repo.getRawPointer(),
                        commit.getRawPointer(),
                        patchNo,
                        totalPatches,
                        flags.getCode(),
                        diffOpts == null ? 0 : diffOpts.getRawPointer()));
        return outBuf;
    }

    /**
     * int git_diff_format_email_init_options(git_diff_format_email_options *opts, unsigned int
     * version);
     */
    static native int jniFormatEmailInitOptions(long opts, int version);

    /** New and init. */
    static native int jniFormatEmailNewOptions(AtomicLong out, int version);

    /** free(git_fiff_format_email_options *opts) */
    static native void jniFormatEmailOptionsFree(long opts);

    /** int git_diff_patchid_init_options(git_diff_patchid_options *opts, unsigned int version); */
    static native int jniPatchidInitOptions(long opts, int version);

    static native int jniPatchidOptionsNew(AtomicLong outOpts, int version);

    static native int jniPatchidOptionsFree(long opts);

    /** int git_diff_patchid(git_oid *out, git_diff *diff, git_diff_patchid_options *opts); */
    static native int jniPatchid(Oid out, long diff, long opts);

    /**
     * Calculate the patch ID for the given patch.
     *
     * <p>Calculate a stable patch ID for the given patch by summing the hash of the file diffs,
     * ignoring whitespace and line numbers. This can be used to derive whether two diffs are the
     * same with a high probability.
     *
     * <p>Currently, this function only calculates stable patch IDs, as defined in git-patch-id(1),
     * and should in fact generate the same IDs as the upstream git project does.
     *
     * @param opts Options for how to calculate the patch ID. This is intended for future changes,
     *     as currently no options are available.
     * @return patch id
     * @throws GitException git errors
     */
    @Nonnull
    public Oid patchid(@Nullable PatchidOptions opts) {
        Oid oid = new Oid();
        Error.throwIfNeeded(
                jniPatchid(oid, getRawPointer(), opts == null ? 0 : opts.getRawPointer()));
        return oid;
    }
}
