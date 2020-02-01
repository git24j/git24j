package com.github.git24j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Merge {
    /** int git_merge_file_init_input(git_merge_file_input *opts, unsigned int version); */
    static native int jniFileInitInput(long opts, int version);

    /** int git_merge_file_init_options(git_merge_file_options *opts, unsigned int version); */
    static native int jniFileInitOptions(long opts, int version);

    /** int git_merge_init_options(git_merge_options *opts, unsigned int version); */
    static native int jniInitOptions(long opts, int version);

    /**
     * int git_merge_base(git_oid *out, git_repository *repo, const git_oid *one, const git_oid
     * *two);
     */
    static native int jniBase(Oid out, long repoPtr, Oid one, Oid two);

    /**
     * int git_merge_bases(git_oidarray *out, git_repository *repo, const git_oid *one, const
     * git_oid *two);
     */
    static native int jniBases(long out, long repoPtr, Oid one, Oid two);

    /**
     * int git_merge_trees(git_index **out, git_repository *repo, const git_tree *ancestor_tree,
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
     * int git_merge_commits(git_index **out, git_repository *repo, const git_commit *our_commit,
     * const git_commit *their_commit, const git_merge_options *opts);
     */
    static native int jniCommits(
            AtomicLong out, long repoPtr, long ourCommit, long theirCommit, long opts);
    /**
     * int git_merge_analysis(git_merge_analysis_t *analysis_out, git_merge_preference_t
     * *preference_out, git_repository *repo, const git_annotated_commit **their_heads, size_t
     * their_heads_len);
     */
    static native int jniAnalysis(
            AtomicInteger analysisOut,
            AtomicInteger preferenceOut,
            long repoPtr,
            long[] theirHeads);

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
     * int git_merge_base_many(git_oid *out, git_repository *repo, size_t length, const git_oid []
     * input_array);
     */
    static native int jniBaseMany(Oid outOid, long repoPtr, Oid[] inputArray);

    /** Class to hold */
    static class OidArray {
        private final List<Oid> _oids = new ArrayList<>();

        void add(byte[] oidRaw) {
            _oids.add(Oid.of(oidRaw));
        }
    }

    /**
     * int git_merge_bases_many(git_oidarray *out, git_repository *repo, size_t length, const
     * git_oid [] input_array);
     */
    static native int jniBasesMany(OidArray outOids, long repoPtr, Oid[] inputArray);

    /**
     * int git_merge_base_octopus(git_oid *out, git_repository *repo, size_t length, const git_oid
     * [] input_array);
     */
    static native int jniBaseOctopus(Oid outOid, long repoPtr, Oid[] intputArray);

    /**
     * int git_merge_file(git_merge_file_result *out, const git_merge_file_input *ancestor, const
     * git_merge_file_input *ours, const git_merge_file_input *theirs, const git_merge_file_options
     * *opts);
     */
    static native int jniFile(
            AtomicLong out, long acestorPtr, long oursPtr, long theirsPtr, long optsPtr);

    public class FileResult extends CAutoReleasable {

        protected FileResult(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniFileResultFree(cPtr);
        }
    }
    /**
     * void git_merge_file_result_free(git_merge_file_result *result); Note: this also frees the
     * resultPtr itself.
     */
    static native void jniFileResultFree(long resultPtr);

    /**
     * int git_merge_create(git_repository *repo, const git_annotated_commit **their_heads, size_t
     * their_heads_len, const git_merge_options *merge_opts, const git_checkout_options
     * *checkout_opts);
     */
    static native int jniCreate(
            long repoPtr, long[] theirHeads, long mergeOptsPtr, long checkoutOpts);
}
