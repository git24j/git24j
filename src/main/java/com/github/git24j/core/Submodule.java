package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Submodule {
    // no matching type found for 'git_submodule_cb callback'
/** int git_submodule_foreach(git_repository *repo, git_submodule_cb callback, void *payload); */
/** -------- Jni Signature ---------- */
    /** int git_submodule_add_finalize(git_submodule *submodule); */
    static native int jniAddFinalize(long submodule);

    /** int git_submodule_add_setup(git_submodule **out, git_repository *repo, const char *url, const char *path, int use_gitlink); */
    static native int jniAddSetup(AtomicLong out, long repoPtr, String url, String path, int useGitlink);

    /** int git_submodule_add_to_index(git_submodule *submodule, int write_index); */
    static native int jniAddToIndex(long submodule, int writeIndex);

    /** const char * git_submodule_branch(git_submodule *submodule); */
    static native String jniBranch(long submodule);

    /** git_submodule_recurse_t git_submodule_fetch_recurse_submodules(git_submodule *submodule); */
    static native int jniFetchRecurseSubmodules(long submodule);

    /** void git_submodule_free(git_submodule *submodule); */
    static native void jniFree(long submodule);

    /** const git_oid * git_submodule_head_id(git_submodule *submodule); */
    static native void jniHeadId(long submodule, Oid outOid);

    /** git_submodule_ignore_t git_submodule_ignore(git_submodule *submodule); */
    static native int jniIgnore(long submodule);

    /** const git_oid * git_submodule_index_id(git_submodule *submodule); */
    static native void jniIndexId(long submodule, Oid outOid);

    /** int git_submodule_init(git_submodule *submodule, int overwrite); */
    static native int jniInit(long submodule, int overwrite);

    /** int git_submodule_location(unsigned int *location_status, git_submodule *submodule); */
    static native int jniLocation(AtomicInteger locationStatus, long submodule);

    /** int git_submodule_lookup(git_submodule **out, git_repository *repo, const char *name); */
    static native int jniLookup(AtomicLong out, long repoPtr, String name);

    /** const char * git_submodule_name(git_submodule *submodule); */
    static native String jniName(long submodule);

    /** int git_submodule_open(git_repository **repo, git_submodule *submodule); */
    static native int jniOpen(AtomicLong repo, long submodule);

    /** git_repository * git_submodule_owner(git_submodule *submodule); */
    static native long jniOwner(long submodule);

    /** const char * git_submodule_path(git_submodule *submodule); */
    static native String jniPath(long submodule);

    /** int git_submodule_reload(git_submodule *submodule, int force); */
    static native int jniReload(long submodule, int force);

    /** int git_submodule_repo_init(git_repository **out, const git_submodule *sm, int use_gitlink); */
    static native int jniRepoInit(AtomicLong out, long sm, int useGitlink);

    /** int git_submodule_resolve_url(git_buf *out, git_repository *repo, const char *url); */
    static native int jniResolveUrl(Buf out, long repoPtr, String url);

    /** int git_submodule_set_branch(git_repository *repo, const char *name, const char *branch); */
    static native int jniSetBranch(long repoPtr, String name, String branch);

    /** int git_submodule_set_fetch_recurse_submodules(git_repository *repo, const char *name, git_submodule_recurse_t fetch_recurse_submodules); */
    static native int jniSetFetchRecurseSubmodules(long repoPtr, String name, int fetchRecurseSubmodules);

    /** int git_submodule_set_ignore(git_repository *repo, const char *name, git_submodule_ignore_t ignore); */
    static native int jniSetIgnore(long repoPtr, String name, int ignore);

    /** int git_submodule_set_update(git_repository *repo, const char *name, git_submodule_update_t update); */
    static native int jniSetUpdate(long repoPtr, String name, int update);

    /** int git_submodule_set_url(git_repository *repo, const char *name, const char *url); */
    static native int jniSetUrl(long repoPtr, String name, String url);

    /** int git_submodule_status(unsigned int *status, git_repository *repo, const char *name, git_submodule_ignore_t ignore); */
    static native int jniStatus(AtomicInteger status, long repoPtr, String name, int ignore);

    /** int git_submodule_sync(git_submodule *submodule); */
    static native int jniSync(long submodule);

    /** int git_submodule_update(git_submodule *submodule, int init, git_submodule_update_options *options); */
    static native int jniUpdate(long submodule, int init, long options);

    /** int git_submodule_update_init_options(git_submodule_update_options *opts, unsigned int version); */
    static native int jniUpdateInitOptions(long opts, int version);

    /** git_submodule_update_t git_submodule_update_strategy(git_submodule *submodule); */
    static native int jniUpdateStrategy(long submodule);

    /** const char * git_submodule_url(git_submodule *submodule); */
    static native String jniUrl(long submodule);

    /** const git_oid * git_submodule_wd_id(git_submodule *submodule); */
    static native long jniWdId(long submodule);


}
