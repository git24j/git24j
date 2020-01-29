package com.github.git24j.core;

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
            long out, long repoPtr, long ancestorTree, long ourTree, long theirTree, long opts);

    /**
     * int git_merge_commits(git_index **out, git_repository *repo, const git_commit *our_commit,
     * const git_commit *their_commit, const git_merge_options *opts);
     */
    static native int jniCommits(
            long out, long repoPtr, long ourCommit, long theirCommit, long opts);

    /** int git_merge_analysis(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len); */
    static native int jniAnalysis(AtomicInteger analysisOut, AtomicInteger preferenceOut, long repoPtr, long[] theirHeads);


}
