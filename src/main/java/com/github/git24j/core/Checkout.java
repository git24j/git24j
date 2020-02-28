package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Checkout {

    @FunctionalInterface
    public interface NotifyCb {
        int accept(
                CheckoutNotifyT why,
                String path,
                Diff.File baseline,
                Diff.File target,
                Diff.File workdir);
    }

    @FunctionalInterface
    public interface ProcessCb {
        void accept(String path, int completedSteps, int totalSteps);
    }

    @FunctionalInterface
    public interface PerfdataCb {
        void accept(int mkdirCalls, int statCalls, int chmodCalls);
    }

    // no matching type found for 'git_checkout_notify_t why'
    /**
     * int git_checkout_notify_cb(git_checkout_notify_t why, const char *path, const git_diff_file
     * *baseline, const git_diff_file *target, const git_diff_file *workdir, void *payload);
     */
    /** -------- Jni Signature ---------- */
    /** int git_checkout_init_options(git_checkout_options *opts, unsigned int version); */
    static native int jniInitOptions(long opts, int version);

    static native int jniOptionsNew(AtomicLong outPots, int version);

    /** int git_checkout_head(git_repository *repo, const git_checkout_options *opts); */
    static native int jniHead(long repoPtr, long opts);

    /**
     * Updates files in the index and the working tree to match the content of the commit pointed at
     * by HEAD.
     *
     * <p>Note that this is _not_ the correct mechanism used to switch branches; do not change your
     * `HEAD` and then call this method, that would leave you with checkout conflicts since your
     * working directory would then appear to be dirty. Instead, checkout the target of the branch
     * and then update `HEAD` using `git_repository_set_head` to point to the branch you checked
     * out.
     *
     * @param repo repository to check out (must be non-bare)
     * @param opts specifies checkout options (may be NULL)
     * @return code returned by `notify_cb`
     * @throws GitException GIT_EUNBORNBRANCH if HEAD points to a non existing branch non-zero value
     *     returned by `notify_cb`, or other error code < 0 (use git_error_last for error details)
     */
    public static int head(@Nonnull Repository repo, @Nullable CheckoutOptions opts) {
        int e = jniHead(repo.getRawPointer(), opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return e;
    }

    /**
     * int git_checkout_index(git_repository *repo, git_index *index, const git_checkout_options
     * *opts);
     */
    static native int jniIndex(long repoPtr, long index, long opts);

    /**
     * Updates files in the working tree to match the content of the index.
     *
     * @param repo repository into which to check out (must be non-bare)
     * @param index index to be checked out (or NULL to use repository index)
     * @param opts specifies checkout options (may be NULL)
     * @return non-zero return value from `notify_cb`,
     * @throws GitException git errors
     */
    public static int index(
            @Nonnull Repository repo, @Nullable Index index, @Nullable CheckoutOptions opts) {
        int e =
                jniIndex(
                        repo.getRawPointer(),
                        index == null ? 0 : index.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return e;
    }

    /**
     * int git_checkout_tree(git_repository *repo, const git_object *treeish, const
     * git_checkout_options *opts);
     */
    static native int jniTree(long repoPtr, long treeish, long opts);

    /**
     * Updates files in the index and working tree to match the content of the tree pointed at by
     * the treeish.
     *
     * @param repo repository to check out (must be non-bare)
     * @param treeish a commit, tag or tree which content will be used to update the working
     *     directory (or NULL to use HEAD)
     * @param opts specifies checkout options (may be NULL)
     * @return non-zero return value from `notify_cb`,
     * @throws GitException git errors
     */
    public static int tree(
            @Nonnull Repository repo, @Nullable GitObject treeish, @Nullable CheckoutOptions opts) {
        int e =
                jniTree(
                        repo.getRawPointer(),
                        treeish == null ? 0 : treeish.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer());
        Error.throwIfNeeded(e);
        return e;
    }

    /** -------- Jni Signature ---------- */
    /** unsigned int git_checkout_options_get_version(const git_checkout_options *opts); */
    static native int jniOptionsGetVersion(long opts);

    /**
     * void git_checkout_options_set_version(const git_checkout_options *opts, unsigned int
     * version);
     */
    static native void jniOptionsSetVersion(long opts, int version);

    /** unsigned int git_checkout_options_get_strategy(const git_checkout_options *opts); */
    static native int jniOptionsGetStrategy(long opts);

    /**
     * void git_checkout_options_set_strategy(const git_checkout_options *opts, unsigned int
     * strategy);
     */
    static native void jniOptionsSetStrategy(long opts, int strategy);

    /** int git_checkout_options_get_disable_filters(const git_checkout_options *opts); */
    static native int jniOptionsGetDisableFilters(long opts);

    /**
     * void git_checkout_options_set_disable_filters(const git_checkout_options *opts, int
     * disalbe_filters);
     */
    static native void jniOptionsSetDisableFilters(long opts, int disalbeFilters);

    /** unsigned int git_checkout_options_get_dir_mode(const git_checkout_options *opts); */
    static native int jniOptionsGetDirMode(long opts);

    /**
     * void git_checkout_options_set_dir_mode(const git_checkout_options *opts, unsigned int mode);
     */
    static native void jniOptionsSetDirMode(long opts, int mode);

    /** unsigned int git_checkout_options_get_file_mode(const git_checkout_options *opts); */
    static native int jniOptionsGetFileMode(long opts);

    /**
     * void git_checkout_options_set_file_mode(const git_checkout_options *opts, unsigned int mode);
     */
    static native void jniOptionsSetFileMode(long opts, int mode);

    /** int git_checkout_options_get_file_open_flags(const git_checkout_options *opts); */
    static native int jniOptionsGetFileOpenFlags(long opts);

    /**
     * void git_checkout_options_set_file_open_flags(const git_checkout_options *opts, int flags);
     */
    static native void jniOptionsSetFileOpenFlags(long opts, int flags);

    /** unsigned int git_checkout_options_get_notify_flags(const git_checkout_options *opts); */
    static native int jniOptionsGetNotifyFlags(long opts);

    /**
     * void git_checkout_options_set_notify_flags(const git_checkout_options *opts, unsigned int
     * flags);
     */
    static native void jniOptionsSetNotifyFlags(long opts, int flags);

    static native void jniOptionsSetNotifyCb(long optsPtr, Internals.ISJJJCallback notifyCb);

    static native void jniOptionsSetProcessCb(long optsPtr, ProcessCb processCb);

    static native void jniOptionsSetPerfdataCb(long optsPtr, PerfdataCb perfdataCb);

    /**
     * void git_checkout_options_set_paths(const git_checkout_options *opts, git_strarray paths);
     */
    static native void jniOptionsSetPaths(long opts, String[] paths);

    /**
     * void git_checkout_options_set_baseline(const git_checkout_options *opts, git_tree *baseline);
     */
    static native void jniOptionsSetBaseline(long opts, long baseline);

    /**
     * void git_checkout_options_set_baseline_index(const git_checkout_options *opts, git_index
     * *baseline_index);
     */
    static native void jniOptionsSetBaselineIndex(long opts, long baselineIndex);

    /**
     * void git_checkout_options_set_target_directory(const git_checkout_options *opts, const char
     * *target_directory);
     */
    static native void jniOptionsSetTargetDirectory(long opts, String targetDirectory);

    /**
     * void git_checkout_options_set_ancestor_label(const git_checkout_options *opts, const char
     * *ancestor_label);
     */
    static native void jniOptionsSetAncestorLabel(long opts, String ancestorLabel);

    /**
     * void git_checkout_options_set_our_label(const git_checkout_options *opts, const char
     * *our_label);
     */
    static native void jniOptionsSetOurLabel(long opts, String ourLabel);

    /**
     * void git_checkout_options_set_their_lable(const git_checkout_options *opts, const char
     * *their_label);
     */
    static native void jniOptionsSetTheirLable(long opts, String theirLabel);
}
