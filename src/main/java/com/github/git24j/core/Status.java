package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Status {
    // no matching type found for 'git_status_cb callback'
    /* int git_status_foreach(git_repository *repo, git_status_cb callback, void *payload); */
    // no matching type found for 'git_status_cb callback'
    /*
     * int git_status_foreach_ext(git_repository *repo, const git_status_options *opts,
     * git_status_cb callback, void *payload);
     */
    /* -------- Jni Signature ---------- */
    /** int git_status_init_options(git_status_options *opts, unsigned int version); */
    static native int jniInitOptions(long opts, int version);

    /** int git_status_file(unsigned int *status_flags, git_repository *repo, const char *path); */
    static native int jniFile(AtomicInteger statusFlags, long repoPtr, String path);

    /**
     * int git_status_list_new(git_status_list **out, git_repository *repo, const git_status_options
     * *opts);
     */
    static native int jniListNew(AtomicLong out, long repoPtr, long opts);

    /** size_t git_status_list_entrycount(git_status_list *statuslist); */
    static native int jniListEntrycount(long statuslist);

    /** const git_status_entry * git_status_byindex(git_status_list *statuslist, size_t idx); */
    static native long jniByindex(long statuslist, int idx);

    /** void git_status_list_free(git_status_list *statuslist); */
    static native void jniListFree(long statuslist);

    /** int git_status_should_ignore(int *ignored, git_repository *repo, const char *path); */
    static native int jniShouldIgnore(AtomicInteger ignored, long repoPtr, String path);
}
