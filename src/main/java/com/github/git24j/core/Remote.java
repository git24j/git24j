package com.github.git24j.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Remote {
    // no matching type found for 'const git_remote_head ***out'
    /** int git_remote_ls(const git_remote_head ***out, size_t *size, git_remote *remote); */
    /** -------- Jni Signature ---------- */
    /** int git_remote_add_fetch(git_repository *repo, const char *remote, const char *refspec); */
    static native int jniAddFetch(long repoPtr, String remote, String refspec);

    /** int git_remote_add_push(git_repository *repo, const char *remote, const char *refspec); */
    static native int jniAddPush(long repoPtr, String remote, String refspec);

    /** git_remote_autotag_option_t git_remote_autotag(const git_remote *remote); */
    static native int jniAutotag(long remote);

    /**
     * int git_remote_connect(git_remote *remote, git_direction direction, const
     * git_remote_callbacks *callbacks, const git_proxy_options *proxy_opts, const git_strarray
     * *custom_headers);
     */
    static native int jniConnect(
            long remote, int direction, long callbacks, long proxyOpts, String[] customHeaders);

    /** int git_remote_connected(const git_remote *remote); */
    static native int jniConnected(long remote);

    /**
     * int git_remote_create(git_remote **out, git_repository *repo, const char *name, const char
     * *url);
     */
    static native int jniCreate(AtomicLong out, long repoPtr, String name, String url);

    /** int git_remote_create_anonymous(git_remote **out, git_repository *repo, const char *url); */
    static native int jniCreateAnonymous(AtomicLong out, long repoPtr, String url);

    /** int git_remote_create_detached(git_remote **out, const char *url); */
    static native int jniCreateDetached(AtomicLong out, String url);

    /**
     * int git_remote_create_init_options(git_remote_create_options *opts, unsigned int version);
     */
    static native int jniCreateInitOptions(long opts, int version);

    /**
     * int git_remote_create_with_fetchspec(git_remote **out, git_repository *repo, const char
     * *name, const char *url, const char *fetch);
     */
    static native int jniCreateWithFetchspec(
            AtomicLong out, long repoPtr, String name, String url, String fetch);

    /**
     * int git_remote_create_with_opts(git_remote **out, const char *url, const
     * git_remote_create_options *opts);
     */
    static native int jniCreateWithOpts(AtomicLong out, String url, long opts);

    /** int git_remote_default_branch(git_buf *out, git_remote *remote); */
    static native int jniDefaultBranch(Buf out, long remote);

    /** int git_remote_delete(git_repository *repo, const char *name); */
    static native int jniDelete(long repoPtr, String name);

    /** void git_remote_disconnect(git_remote *remote); */
    static native void jniDisconnect(long remote);

    /**
     * int git_remote_download(git_remote *remote, const git_strarray *refspecs, const
     * git_fetch_options *opts);
     */
    static native int jniDownload(long remote, String[] refspecs, long opts);

    /** int git_remote_dup(git_remote **dest, git_remote *source); */
    static native int jniDup(AtomicLong dest, long source);

    /**
     * int git_remote_fetch(git_remote *remote, const git_strarray *refspecs, const
     * git_fetch_options *opts, const char *reflog_message);
     */
    static native int jniFetch(long remote, String[] refspecs, long opts, String reflogMessage);

    /** void git_remote_free(git_remote *remote); */
    static native void jniFree(long remote);

    /** int git_remote_get_fetch_refspecs(git_strarray *array, const git_remote *remote); */
    static native int jniGetFetchRefspecs(List<String> array, long remote);

    /** int git_remote_get_push_refspecs(git_strarray *array, const git_remote *remote); */
    static native int jniGetPushRefspecs(List<String> array, long remote);

    /** const git_refspec * git_remote_get_refspec(const git_remote *remote, size_t n); */
    static native long jniGetRefspec(long remote, int n);

    /** int git_remote_init_callbacks(git_remote_callbacks *opts, unsigned int version); */
    static native int jniInitCallbacks(long opts, int version);

    /** int git_remote_is_valid_name(const char *remote_name); */
    static native int jniIsValidName(String remoteName);

    /** int git_remote_list(git_strarray *out, git_repository *repo); */
    static native int jniList(List<String> out, long repoPtr);

    /** int git_remote_lookup(git_remote **out, git_repository *repo, const char *name); */
    static native int jniLookup(AtomicLong out, long repoPtr, String name);

    /** const char * git_remote_name(const git_remote *remote); */
    static native String jniName(long remote);

    /** git_repository * git_remote_owner(const git_remote *remote); */
    static native long jniOwner(long remote);

    /** int git_remote_prune(git_remote *remote, const git_remote_callbacks *callbacks); */
    static native int jniPrune(long remote, long callbacks);

    /** int git_remote_prune_refs(const git_remote *remote); */
    static native int jniPruneRefs(long remote);

    /**
     * int git_remote_push(git_remote *remote, const git_strarray *refspecs, const git_push_options
     * *opts);
     */
    static native int jniPush(long remote, String[] refspecs, long opts);

    /** const char * git_remote_pushurl(const git_remote *remote); */
    static native String jniPushurl(long remote);

    /** size_t git_remote_refspec_count(const git_remote *remote); */
    static native int jniRefspecCount(long remote);

    /**
     * int git_remote_rename(git_strarray *problems, git_repository *repo, const char *name, const
     * char *new_name);
     */
    static native int jniRename(List<String> problems, long repoPtr, String name, String newName);

    /**
     * int git_remote_set_autotag(git_repository *repo, const char *remote,
     * git_remote_autotag_option_t value);
     */
    static native int jniSetAutotag(long repoPtr, String remote, int value);

    /** int git_remote_set_pushurl(git_repository *repo, const char *remote, const char *url); */
    static native int jniSetPushurl(long repoPtr, String remote, String url);

    /** int git_remote_set_url(git_repository *repo, const char *remote, const char *url); */
    static native int jniSetUrl(long repoPtr, String remote, String url);

    /** const git_transfer_progress * git_remote_stats(git_remote *remote); */
    static native long jniStats(long remote);

    /** void git_remote_stop(git_remote *remote); */
    static native void jniStop(long remote);

    /**
     * int git_remote_update_tips(git_remote *remote, const git_remote_callbacks *callbacks, int
     * update_fetchhead, git_remote_autotag_option_t download_tags, const char *reflog_message);
     */
    static native int jniUpdateTips(
            long remote,
            long callbacks,
            int updateFetchhead,
            int downloadTags,
            String reflogMessage);

    /**
     * int git_remote_upload(git_remote *remote, const git_strarray *refspecs, const
     * git_push_options *opts);
     */
    static native int jniUpload(long remote, String[] refspecs, long opts);

    /** const char * git_remote_url(const git_remote *remote); */
    static native String jniUrl(long remote);
}