package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import static com.github.git24j.core.Internals.OidArray;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Git merge util functions, all are static */
public class Merge {

    /**
     * The file inputs to `git_merge_file`. Callers should populate the `git_merge_file_input`
     * structure with descriptions of the files in each side of the conflict for use in producing
     * the merge file.
     */
    public static class FileInput extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;

        FileInput(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFileInputFree(cPtr);
        }

        @Nonnull
        public static FileInput create(int version) {
            FileInput out = new FileInput(false, 0);
            Error.throwIfNeeded(jniFileInputNew(out._rawPtr, version));
            return out;
        }
    }

    /**
     * Flags for `git_merge` options. A combination of these flags can be passed in via the `flags`
     * value in the `git_merge_options`.
     */
    public enum FlagT implements IBitEnum {
        /**
         * Detect renames that occur between the common ancestor and the "ours" side or the common
         * ancestor and the "theirs" side. This will enable the ability to merge between a modified
         * and renamed file.
         */
        FIND_RENAMES(1 << 0),

        /**
         * If a conflict occurs, exit immediately instead of attempting to continue resolving
         * conflicts. The merge operation will fail with GIT_EMERGECONFLICT and no index will be
         * returned.
         */
        FAIL_ON_CONFLICT(1 << 1),

        /** Do not write the REUC extension on the generated index */
        SKIP_REUC(1 << 2),

        /**
         * If the commits being merged have multiple merge bases, do not build a recursive merge
         * base (by merging the multiple merge bases), instead simply use the first base. This flag
         * provides a similar merge base to `git-merge-resolve`.
         */
        NO_RECURSIVE(1 << 3);
        private final int bit;

        FlagT(int bit) {
            this.bit = bit;
        }

        @Override
        public int getBit() {
            return bit;
        }
    };

    /**
     * Merge file favor options for `git_merge_options` instruct the file-level merging
     * functionality how to deal with conflicting regions of the files.
     */
    public enum FileFavorT {
        /**
         * When a region of a file is changed in both branches, a conflict will be recorded in the
         * index so that `git_checkout` can produce a merge file with conflict markers in the
         * working directory. This is the default.
         */
        NORMAL(0),

        /**
         * When a region of a file is changed in both branches, the file created in the index will
         * contain the "ours" side of any conflicting region. The index will not record a conflict.
         */
        OURS(1),

        /**
         * When a region of a file is changed in both branches, the file created in the index will
         * contain the "theirs" side of any conflicting region. The index will not record a
         * conflict.
         */
        THEIRS(2),

        /**
         * When a region of a file is changed in both branches, the file created in the index will
         * contain each unique line from each side) which has the result of combining both files.
         * The index will not record a conflict.
         */
        UNION(3);
        private final int bit;

        FileFavorT(int bit) {
            this.bit = bit;
        }
    }

    /** File merging flags */
    public enum FileFlagT implements IBitEnum {
        /** Defaults */
        DEFAULT(0),

        /** Create standard conflicted merge files */
        STYLE_MERGE(1 << 0),

        /** Create diff3-style files */
        STYLE_DIFF3(1 << 1),

        /** Condense non-alphanumeric regions for simplified diff file */
        SIMPLIFY_ALNUM(1 << 2),

        /** Ignore all whitespace */
        IGNORE_WHITESPACE(1 << 3),

        /** Ignore changes in amount of whitespace */
        IGNORE_WHITESPACE_CHANGE(1 << 4),

        /** Ignore whitespace at end of line */
        IGNORE_WHITESPACE_EOL(1 << 5),

        /** Use the "patience diff" algorithm */
        DIFF_PATIENCE(1 << 6),

        /** Take extra time to find minimal diff */
        DIFF_MINIMAL(1 << 7);
        private final int bit;

        FileFlagT(int bit) {
            this.bit = bit;
        }

        @Override
        public int getBit() {
            return bit;
        }
    }

    /** options for merging a file */
    public static class FileOptions extends CAutoReleasable {
        FileOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFileOptionsFree(cPtr);
        }

        @Nonnull
        public static FileOptions create(int version) {
            FileOptions opts = new FileOptions(false, 0);
            Error.throwIfNeeded(jniFileOptionsNew(opts._rawPtr, version));
            return opts;
        }
    }

    public static class FileResult extends CAutoReleasable {

        protected FileResult(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFileResultFree(cPtr);
        }
    }

    /** Merging options */
    public static class Options extends CAutoReleasable {
        public static final int CURRENT_VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }

        @Nonnull
        public Options create(int version) {
            Options out = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(out._rawPtr, version));
            return out;
        }
    }

    /** The results of `git_merge_analysis` indicate the merge opportunities. */
    public enum AnalysisT implements IBitEnum {
        /** No merge is possible. (Unused.) */
        NONE(0),

        /**
         * A "normal" merge; both HEAD and the given merge input have diverged from their common
         * ancestor. The divergent commits must be merged.
         */
        NORMAL(1 << 0),

        /**
         * All given merge inputs are reachable from HEAD, meaning the repository is up-to-date and
         * no merge needs to be performed.
         */
        UP_TO_DATE(1 << 1),

        /**
         * The given merge input is a fast-forward from HEAD and no merge needs to be performed.
         * Instead, the client can check out the given merge input.
         */
        FASTFORWARD(1 << 2),

        /**
         * The HEAD of the current repository is "unborn" and does not point to a valid commit. No
         * merge can be performed, but the caller may wish to simply set HEAD to the target
         * commit(s).
         */
        UNBORN(1 << 3);
        private final int bit;

        AnalysisT(int bit) {
            this.bit = bit;
        }

        @Override
        public int getBit() {
            return bit;
        }
    }

    /** The user's stated preference for merges. */
    public enum PreferenceT implements IBitEnum {
        /** No configuration was found that suggests a preferred behavior for merge. */
        NONE(0),

        /**
         * There is a `merge.ff=false` configuration setting, suggesting that the user does not want
         * to allow a fast-forward merge.
         */
        FASTFORWARD(1 << 0),

        /**
         * There is a `merge.ff=only` configuration setting, suggesting that the user only wants
         * fast-forward merges.
         */
        FASTFORWARD_ONLY(1 << 1);
        private final int bit;

        PreferenceT(int bit) {
            this.bit = bit;
        }

        @Override
        public int getBit() {
            return bit;
        }
    }

    /**
     * POJO contains analysis result from {@code Merge::analysis} and {@code Merge::analysisForRef}
     */
    public static class AnalysisPair {
        private final AnalysisT analysis;
        private final PreferenceT preference;

        public AnalysisPair(AnalysisT analysis, PreferenceT preference) {
            this.analysis = analysis;
            this.preference = preference;
        }

        public AnalysisT getAnalysis() {
            return analysis;
        }

        public PreferenceT getPreference() {
            return preference;
        }
    }

    /** int git_merge_file_init_input(git_merge_file_input *opts, unsigned int version); */
    static native int jniFileInitInput(long opts, int version);

    static native int jniFileInputNew(AtomicLong outOpts, int version);

    static native int jniFileInputFree(long optsPtr);

    /** int git_merge_file_init_options(git_merge_file_options *opts, unsigned int version); */
    static native int jniFileInitOptions(long opts, int version);

    static native int jniFileOptionsNew(AtomicLong outOpts, int version);

    static native int jniFileOptionsFree(long opts);

    /** int git_merge_init_options(git_merge_options *opts, unsigned int version); */
    static native int jniInitOptions(long opts, int version);

    static native int jniOptionsNew(AtomicLong outOpts, int version);

    static native void jniOptionsFree(long optsPtr);

    /**
     * int git_merge_base(git_oid *out, git_repository *repo, const git_oid *one, const git_oid
     * *two);
     */
    static native int jniBase(Oid out, long repoPtr, Oid one, Oid two);

    /**
     * Find a merge base between two commits
     *
     * @param repo the repository where the commits exist
     * @param one one of the commits
     * @param two the other commit
     * @return the OID of a merge base between 'one' and 'two'
     * @throws GitException git errors 0 on success, GIT_ENOTFOUND if not found or error code
     */
    @Nonnull
    public static Optional<Oid> base(@Nonnull Repository repo, @Nonnull Oid one, @Nonnull Oid two) {
        Oid out = new Oid();
        int e = jniBase(out, repo.getRawPointer(), one, two);
        if (ENOTFOUND.getCode() == e) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(out);
    }

    /**
     * int git_merge_bases(git_oidarray *out, git_repository *repo, const git_oid *one, const
     * git_oid *two);
     */
    static native int jniBases(OidArray outOids, long repoPtr, Oid one, Oid two);

    /**
     * Find merge bases between two commits
     *
     * @param repo the repository where the commits exist
     * @param one one of the commits
     * @param two the other commit
     * @return list of merge base ids, empty if not found
     * @throws GitException 0 on success
     */
    @Nonnull
    public static List<Oid> bases(@Nonnull Repository repo, @Nonnull Oid one, @Nonnull Oid two) {
        OidArray outOids = new OidArray();
        int e = jniBases(outOids, repo.getRawPointer(), one, two);
        if (ENOTFOUND.getCode() == e) {
            return Collections.emptyList();
        }
        Error.throwIfNeeded(e);
        return outOids.getOids();
    }

    /**
     * int git_merge_trees( git_index **out, git_repository *repo, const git_tree *ancestor_tree,
     * const git_tree *our_tree, const git_tree *their_tree, const git_merge_options *opts);
     */
    static native int jniTrees(
            AtomicLong out,
            long repoPtr,
            long ancestorTree,
            long ourTree,
            long theirTree,
            long opts);

    /**
     * Merge two trees, producing a `git_index` that reflects the result of the merge. The index may
     * be written as-is to the working directory or checked out. If the index is to be converted to
     * a tree, the caller should resolve any conflicts that arose as part of the merge.
     *
     * <p>The returned index must be freed explicitly with `git_index_free`.
     *
     * @param repo repository that contains the given trees
     * @param ancestorTree the common ancestor between the trees (or null if none)
     * @param ourTree the tree that reflects the destination tree
     * @param theirTree the tree to merge in to `our_tree`
     * @param opts the merge tree options (or null for defaults)
     * @return index of the merge
     * @throws GitException git errors
     */
    @Nonnull
    public static Index trees(
            @Nonnull Repository repo,
            @Nullable Tree ancestorTree,
            @Nullable Tree ourTree,
            @Nullable Tree theirTree,
            @Nullable Options opts) {
        Index out = new Index();
        Error.throwIfNeeded(
                jniTrees(
                        out._rawPtr,
                        repo.getRawPointer(),
                        ancestorTree == null ? 0 : ancestorTree.getRawPointer(),
                        ourTree == null ? 0 : ourTree.getRawPointer(),
                        theirTree == null ? 0 : theirTree.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer()));
        return out;
    }

    /**
     * int git_merge_commits(git_index **out, git_repository *repo, const git_commit *our_commit,
     * const git_commit *their_commit, const git_merge_options *opts);
     */
    static native int jniCommits(
            AtomicLong out, long repoPtr, long ourCommit, long theirCommit, long opts);

    /**
     * Merge two commits, producing a `git_index` that reflects the result of the merge. The index
     * may be written as-is to the working directory or checked out. If the index is to be converted
     * to a tree, the caller should resolve any conflicts that arose as part of the merge.
     *
     * <p>The returned index must be freed explicitly with `git_index_free`.
     *
     * @param repo repository that contains the given trees
     * @param ourCommit the commit that reflects the destination tree
     * @param theirCommit the commit to merge in to `our_commit`
     * @param opts the merge tree options (or null for defaults)
     * @return index reflecting the merge result
     * @throws GitException git errors
     */
    @Nonnull
    public static Index commits(
            @Nonnull Repository repo,
            @Nullable Commit ourCommit,
            @Nullable Commit theirCommit,
            @Nullable Options opts) {
        Index out = new Index();
        Error.throwIfNeeded(
                jniCommits(
                        out._rawPtr,
                        repo.getRawPointer(),
                        ourCommit == null ? 0 : ourCommit.getRawPointer(),
                        theirCommit == null ? 0 : theirCommit.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer()));
        return out;
    }
    /**
     * int git_merge_analysis( git_merge_analysis_t *analysis_out, git_merge_preference_t
     * *preference_out, git_repository *repo, const git_annotated_commit **their_heads, size_t
     * their_heads_len);
     */
    static native int jniAnalysis(
            AtomicInteger analysisOut,
            AtomicInteger preferenceOut,
            long repoPtr,
            long[] theirHeads);

    /**
     * Analyzes the given branch(es) and determines the opportunities for merging them into the HEAD
     * of the repository.
     *
     * @param repo the repository to merge
     * @param theirHeads the heads to merge into
     * @return out analysis
     * @throws GitException git errors
     */
    @Nonnull
    public static AnalysisPair analysis(
            @Nonnull Repository repo, @Nonnull List<AnnotatedCommit> theirHeads) {
        AtomicInteger outAnalysis = new AtomicInteger();
        AtomicInteger outPreference = new AtomicInteger();
        Error.throwIfNeeded(
                jniAnalysis(
                        outAnalysis,
                        outPreference,
                        repo.getRawPointer(),
                        theirHeads.stream().mapToLong(AnnotatedCommit::getRawPointer).toArray()));
        return new AnalysisPair(
                IBitEnum.valueOf(outAnalysis.get(), AnalysisT.class),
                IBitEnum.valueOf(outPreference.get(), PreferenceT.class));
    }

    /**
     * int git_merge_analysis_for_ref(git_merge_analysis_t *analysis_out, git_merge_preference_t
     * *preference_out, git_repository *repo, git_reference *our_ref, const git_annotated_commit
     * **their_heads, size_t their_heads_len);
     */
    static native int jniAnalysisForRef(
            AtomicInteger analysisOut,
            AtomicInteger preferenceOut,
            long repoPtr,
            long ourRefPtr,
            long[] theirHeads);
    /**
     * Analyzes the given branch(es) and determines the opportunities for merging them into a
     * reference.
     *
     * @param repo the repository to merge
     * @param ourRef the reference to perform the analysis from
     * @param theirHeads the heads to merge into
     * @return analysis out
     * @throws GitException git errors
     */
    @Nonnull
    public static AnalysisPair analysisForRef(
            @Nonnull Repository repo,
            @Nullable Reference ourRef,
            @Nonnull List<AnnotatedCommit> theirHeads) {
        AtomicInteger outAnalysis = new AtomicInteger();
        AtomicInteger outPreference = new AtomicInteger();
        Error.throwIfNeeded(
                jniAnalysisForRef(
                        outAnalysis,
                        outPreference,
                        repo.getRawPointer(),
                        ourRef == null ? 0 : ourRef.getRawPointer(),
                        theirHeads.stream().mapToLong(AnnotatedCommit::getRawPointer).toArray()));
        return new AnalysisPair(
                IBitEnum.valueOf(outAnalysis.get(), AnalysisT.class),
                IBitEnum.valueOf(outPreference.get(), PreferenceT.class));
    }

    /**
     * int git_merge_base_many(git_oid *out, git_repository *repo, size_t length, const git_oid []
     * input_array);
     */
    static native int jniBaseMany(Oid outOid, long repoPtr, Oid[] inputArray);

    /**
     * Find a merge base given a list of commits
     *
     * @param repo the repository where the commits exist
     * @param inputArray oids of the commits
     * @return the OID of a merge base considering all the commits
     * @throws GitException git errors
     */
    @Nonnull
    public static Optional<Oid> baseMany(@Nonnull Repository repo, @Nonnull Oid[] inputArray) {
        Oid outOid = new Oid();
        int e = jniBaseMany(outOid, repo.getRawPointer(), inputArray);
        if (ENOTFOUND.getCode() == e) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(outOid);
    }

    /**
     * int git_merge_bases_many(git_oidarray *out, git_repository *repo, size_t length, const
     * git_oid [] input_array);
     */
    static native int jniBasesMany(OidArray outOids, long repoPtr, Oid[] inputArray);

    /**
     * Find all merge bases given a list of commits
     *
     * @param repo the repository where the commits exist
     * @param inputArray oids of the commits
     * @return list of merge base commits or empty list if none found
     * @throws GitException git errors
     */
    @Nonnull
    public static List<Oid> basesMany(@Nonnull Repository repo, @Nonnull Oid[] inputArray) {
        OidArray outOids = new OidArray();
        int e = jniBasesMany(outOids, repo.getRawPointer(), inputArray);
        if (ENOTFOUND.getCode() == e) {
            return Collections.emptyList();
        }
        Error.throwIfNeeded(e);
        return outOids.getOids();
    }

    /**
     * int git_merge_base_octopus(git_oid *out, git_repository *repo, size_t length, const git_oid
     * [] input_array);
     */
    static native int jniBaseOctopus(Oid outOid, long repoPtr, Oid[] intputArray);

    /**
     * Find a merge base in preparation for an octopus merge
     *
     * @param repo the repository where the commits exist
     * @param inputArray oids of the commits
     * @return the OID of a merge base considering all the commits or empty if not found
     * @throws GitException git errors
     */
    @Nonnull
    public static Optional<Oid> baseOctopus(@Nonnull Repository repo, @Nonnull Oid[] inputArray) {
        Oid outOid = new Oid();
        int e = jniBaseOctopus(outOid, repo.getRawPointer(), inputArray);
        if (ENOTFOUND.getCode() == e) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(outOid);
    }

    /**
     * int git_merge_file( git_merge_file_result *out, const git_merge_file_input *ancestor, const
     * git_merge_file_input *ours, const git_merge_file_input *theirs, const git_merge_file_options
     * *opts);
     */
    static native int jniFile(
            AtomicLong out, long ancestorPtr, long oursPtr, long theirsPtr, long optsPtr);

    /**
     * Merge two files as they exist in the in-memory data structures, using the given common
     * ancestor as the baseline, producing a `git_merge_file_result` that reflects the merge result.
     * The `git_merge_file_result` must be freed with `git_merge_file_result_free`.
     *
     * <p>Note that this function does not reference a repository and any configuration must be
     * passed as `git_merge_file_options`.
     *
     * @param ancestor The contents of the ancestor file
     * @param ours The contents of the file in "our" side
     * @param theirs The contents of the file in "their" side
     * @param opts The merge file options or `NULL` for defaults
     * @return The merge result
     * @throws GitException git errors
     */
    @Nonnull
    public static FileResult file(
            @Nonnull FileInput ancestor,
            @Nonnull FileInput ours,
            @Nonnull FileInput theirs,
            @Nullable FileOptions opts) {
        FileResult result = new FileResult(false, 0);
        Error.throwIfNeeded(
                jniFile(
                        result._rawPtr,
                        ancestor.getRawPointer(),
                        ours.getRawPointer(),
                        theirs.getRawPointer(),
                        CAutoReleasable.rawPtr(opts)));
        return result;
    }

    /**
     * void git_merge_file_result_free(git_merge_file_result *result); Note: this also frees the
     * resultPtr itself.
     */
    static native void jniFileResultFree(long resultPtr);

    /**
     * int git_merge_create( git_repository *repo, const git_annotated_commit **their_heads, size_t
     * their_heads_len, const git_merge_options *merge_opts, const git_checkout_options
     * *checkout_opts);
     */
    static native int jniMerge(
            long repoPtr, long[] theirHeads, long mergeOptsPtr, long checkoutOpts);

    /**
     * Merges the given commit(s) into HEAD, writing the results into the working directory. Any
     * changes are staged for commit and any conflicts are written to the index. Callers should
     * inspect the repository's index after this completes, resolve any conflicts and prepare a
     * commit.
     *
     * <p>For compatibility with git, the repository is put into a merging state. Once the commit is
     * done (or if the uses wishes to abort), you should clear this state by calling
     * `git_repository_state_cleanup()`.
     *
     * @param repo the repository to merge
     * @param theirHeads the heads to merge into
     * @param mergeOpts merge options
     * @param checkoutOpts checkout options
     * @throws GitException git errors
     */
    public static void merge(
            @Nonnull Repository repo,
            @Nonnull List<AnnotatedCommit> theirHeads,
            @Nullable Options mergeOpts,
            @Nullable Checkout.Options checkoutOpts) {
        Error.throwIfNeeded(
                jniMerge(
                        repo.getRawPointer(),
                        theirHeads.stream().mapToLong(CAutoCloseable::getRawPointer).toArray(),
                        CAutoReleasable.rawPtr(mergeOpts),
                        CAutoReleasable.rawPtr(checkoutOpts)));
    }
}
