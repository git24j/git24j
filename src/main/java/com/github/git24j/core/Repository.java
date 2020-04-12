package com.github.git24j.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Repository extends CAutoCloseable {
    @Override
    protected void releaseOnce(long cPtr) {
        jniFree(cPtr);
    }

    public Repository(long rawPointer) {
        super(rawPointer);
    }

    static native int jniOpen(AtomicLong outRepo, String path);

    static native int jniOpenFromWorkTree(AtomicLong outRepo, long wtPtr);

    static native int jniWrapOdb(AtomicLong outRepo, long odbPtr);

    static native int jniDiscover(Buf out, String startPath, int accessFs, String ceilingDirs);

    static native int jniOpenExt(AtomicLong outRepo, String path, int flags, String ceilingDirs);

    static native int jniOpenBare(AtomicLong outRepo, String path);

    static native int jniFree(long repoPtr);

    static native int jniInit(AtomicLong outRepo, String path, int isBare);

    static native int jniInitOptionsInit(InitOptions initOpts, int version);

    static native int jniInitExt(AtomicLong outRepo, String repoPath, InitOptions initOpts);

    static native int jniHead(AtomicLong gitRef, long repoPtr);

    static native int jniHeadForWorktree(AtomicLong outRef, long repoPtr, String name);

    static native int jniHeadDetached(long repoPtr);

    static native int jniHeadUnborn(long repoPtr);

    static native int jniIsEmpty(long repoPtr);

    static native int jniItemPath(Buf buf, long repoPtr, int item);

    static native int jniIndex(AtomicLong outIndex, long repoPtr);

    static native String jniPath(long repoPtr);

    static native String jniWorkdir(long repoPtr);

    static native String jniCommondir(long repoPtr);

    static native int jniSetWorkdir(long repoPtr, String workdir, int updateGitlink);

    static native int jniIsBare(long repoPtr);

    static native int jniIsWorktree(long repoPtr);

    static native int jniConfig(AtomicLong outCfg, long repoPtr);

    static native int jniConfigSnapshot(AtomicLong outCfg, long repoPtr);

    static native int jniOdb(AtomicLong outOdb, long repoPtr);

    static native int jniRefdb(AtomicLong outRefdb, long repoPtr);

    static native int jniMessage(Buf buf, long repoPtr);

    static native int jniMessageRemove(long repoPtr);

    static native int jniStateCleanup(long repoPtr);

    static native int jniFetchheadForeach(long repoPtr, FetchHeadForeachCb cb);

    static native int jniMergeheadForeach(long repoPtr, MergeheadForeachCb cb);

    static native int jniHashfile(Oid oid, long repoPtr, String path, int type, String asPath);

    static native int jniSetHead(long repoPtr, String refName);

    static native int jniSetHeadDetached(long repoPtr, Oid oid);

    static native int jniSetHeadDetachedFromAnnotated(long repoPtr, long commitishPtr);

    static native int jniDetachHead(long repoPtr);

    static native int jniState(long repoPtr);

    static native int jniSetNamespace(long repoPtr, String namespace);

    static native String jniGetNamespace(long repoPtr);

    static native int jniIsShadow(long repoPtr);

    static native int jniIdent(Identity identity, long repoPtr);

    static native int jniSetIdent(long repoPtr, String name, String email);

    static Repository ofRaw(long ptr) {
        return new Repository(ptr);
    }

    /**
     * Open a git repository.
     *
     * @param path the path to the repository
     * @return the repo which that is opened
     * @throws GitException git error.
     */
    @Nonnull
    public static Repository open(@Nonnull Path path) {
        AtomicLong outRepo = new AtomicLong();
        int error = jniOpen(outRepo, path.toString());
        Error.throwIfNeeded(error);
        return new Repository(outRepo.get());
    }

    /**
     * Creates a new Git repository in the given folder.
     *
     * @param path the path to the repository
     * @param isBare if true, a Git repository without a working directory is created at the pointed
     *     path. If false, provided path will be considered as the working directory into which the
     *     .git directory will be created.
     * @return repo just initialized.
     * @throws GitException git error.
     */
    @Nonnull
    public static Repository init(@Nonnull Path path, boolean isBare) {
        Repository repo = new Repository(0);
        Error.throwIfNeeded(jniInit(repo._rawPtr, path.toString(), isBare ? 1 : 0));
        return repo;
    }

    /**
     * Create a new Git repository in the given folder with extended controls.
     *
     * @param path The path to the repository.
     * @param initOpts {@code InitOptions}
     * @return Repo just created or reinitialized.
     * @throws GitException git error
     */
    @Nonnull
    public static Repository initExt(@Nonnull String path, @Nullable InitOptions initOpts) {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniInitExt(out, path, initOpts));
        return new Repository(out.get());
    }

    /**
     * Look for a git repository and return its path if found. The lookup start from base_path and
     * walk across parent directories if nothing has been found. The lookup ends when the first
     * repository is found, or when reaching a directory referenced in ceiling_dirs or when the
     * filesystem changes (in case across_fs is true).
     *
     * <p>The method will automatically detect if the repository is bare (if there is a repository).
     *
     * @param startPath The base path where the lookup starts.
     * @param acrossFs If true, then the lookup will not stop when a filesystem device change is
     *     detected while exploring parent directories.
     * @param ceilingDirs A GIT_PATH_LIST_SEPARATOR separated list of absolute symbolic link free
     *     paths. The lookup will stop when any of this paths is reached. Note that the lookup
     *     always performs on start_path no matter start_path appears in ceiling_dirs ceiling_dirs
     *     might be NULL (which is equivalent to an empty string)
     * @return the found path or empty string
     * @throws GitException git errors
     */
    public static Optional<String> discover(
            @Nonnull Path startPath, boolean acrossFs, @Nullable String ceilingDirs) {
        Buf outBuf = new Buf();
        jniDiscover(outBuf, startPath.toString(), acrossFs ? 1 : 0, ceilingDirs);
        return outBuf.getString();
    }

    /**
     * Find and open a repository with extended controls.
     *
     * @param path Path to open as git repository. If the flags permit "searching", then this can be
     *     a path to a subdirectory inside the working directory of the repository. May be NULL if
     *     flags is GIT_REPOSITORY_OPEN_FROM_ENV.
     * @param flags A combination of the OpenFlag flags.
     * @param ceilingDirs A GIT_PATH_LIST_SEPARATOR delimited list of path prefixes at which the
     *     search for a containing repository should terminate.
     * @return repo just opened. This can actually be NULL if you only want to use the error code to
     *     see if a repo at this path could be opened.
     * @throws GitException git error.
     */
    @Nonnull
    public static Repository openExt(
            @Nonnull Path path, @Nullable EnumSet<OpenFlag> flags, @Nullable String ceilingDirs) {
        AtomicLong out = new AtomicLong();
        int error = jniOpenExt(out, path.toString(), IBitEnum.bitOrAll(flags), ceilingDirs);
        Error.throwIfNeeded(error);
        return new Repository(out.get());
    }

    /**
     * Open a bare repository on the serverside.
     *
     * @param path Direct path to the bare repository
     * @return Pointer to the repo just opened.
     * @throws GitException git error.
     */
    @Nonnull
    public static Repository openBare(@Nonnull Path path) {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniOpenBare(out, path.toString()));
        return new Repository(out.get());
    }

    /**
     * Get the path of the shared common directory for this repository.
     *
     * @return the path to the common dir.
     */
    @Nonnull
    public String getPath() {
        return jniPath(getRawPointer());
    }

    /**
     * Get the path of the working directory for this repository
     *
     * @return the path to the working dir, if it exists
     */
    @Nonnull
    public Path workdir() {
        String wd = jniWorkdir(getRawPointer());
        return Paths.get(wd);
    }

    /**
     * Set the path to the working directory for this repository
     *
     * @param path The path to a working directory
     * @param updateGitLink Create/update gitlink in workdir and set config "core.worktree" (if
     *     workdir is not the parent of the .git directory)
     * @throws GitException git error
     */
    public void setWorkdir(Path path, boolean updateGitLink) {
        Error.throwIfNeeded(jniSetWorkdir(getRawPointer(), path.toString(), updateGitLink ? 1 : 0));
    }

    /**
     * Get the path of the shared common directory for this repository.
     *
     * @return the path to the common dir.
     */
    public String getCommondir() {
        return jniCommondir(getRawPointer());
    }

    /**
     * Check if a repository is bare
     *
     * @return true if the repository is bare
     */
    public boolean isBare() {
        return jniIsBare(getRawPointer()) == 1;
    }

    /**
     * Check if a repository is a linked work tree
     *
     * @return true if the repository is a linked work tree
     */
    public boolean isWorktree() {
        return jniIsWorktree(getRawPointer()) == 1;
    }

    /**
     * Get the configuration file for this repository.
     *
     * @return the loaded configuration
     * @throws GitException git error
     */
    public Config config() {
        AtomicLong outCfg = new AtomicLong();
        Error.throwIfNeeded(jniConfig(outCfg, getRawPointer()));
        return new Config(outCfg.get());
    }

    /**
     * Get a snapshot of the repository's configuration
     *
     * @return the loaded configuration.
     * @throws GitException git error
     */
    public Config configSnapshot() {
        AtomicLong outCfg = new AtomicLong();
        Error.throwIfNeeded(jniConfigSnapshot(outCfg, getRawPointer()));
        return new Config(outCfg.get());
    }

    /**
     * Get the Object Database for this repository.
     *
     * @return the loaded ODB.
     * @throws GitException git error
     */
    public Odb odb() {
        AtomicLong outOdb = new AtomicLong();
        Error.throwIfNeeded(jniOdb(outOdb, getRawPointer()));
        return new Odb(outOdb.get());
    }

    /**
     * Get the Reference Database Backend for this repository.
     *
     * @return loaded refdb
     * @throws GitException git error
     */
    public Refdb refdb() {
        AtomicLong outRefdb = new AtomicLong();
        Error.throwIfNeeded(jniRefdb(outRefdb, getRawPointer()));
        return new Refdb(outRefdb.get());
    }

    /**
     * @return git's prepared message
     * @throws GitException git error
     */
    public Optional<String> message() {
        Buf buf = new Buf();
        Error.throwIfNeeded(jniMessage(buf, getRawPointer()));
        return buf.getString();
    }
    //    static native int jniMessageRemove(long repoPtr);

    /**
     * Remove git's prepared message.
     *
     * @throws GitException git error
     */
    public void messageRemove() {
        Error.throwIfNeeded(jniMessageRemove(getRawPointer()));
    }

    /**
     * Remove all the metadata associated with an ongoing command like merge, revert, cherry-pick,
     * etc. For example: MERGE_HEAD, MERGE_MSG, etc.
     *
     * @throws GitException git error
     */
    public void stateCleanup() {
        Error.throwIfNeeded(jniStateCleanup(getRawPointer()));
    }

    /**
     * Invoke 'callback' for each entry in the given FETCH_HEAD file.
     *
     * @param cb Callback class
     * @throws GitException git error
     */
    public void fetchheadForeach(FetchHeadForeachCb cb) {
        Error.throwIfNeeded(jniFetchheadForeach(getRawPointer(), cb));
    }

    /**
     * If a merge is in progress, invoke 'callback' for each commit ID in the MERGE_HEAD file.
     *
     * @param cb Callback function
     * @throws GitException git error
     */
    public void mergeHeadForeach(MergeheadForeachCb cb) {
        Error.throwIfNeeded(jniMergeheadForeach(getRawPointer(), cb));
    }

    /**
     * Calculate hash of file using repository filtering rules.
     *
     * @param path Path to file on disk whose contents should be hashed. If the repository is not
     *     NULL, this can be a relative path.
     * @param type The object type to hash as (e.g. GIT_OBJECT_BLOB)
     * @param asPath The path to use to look up filtering rules. If this is NULL, then the `path`
     *     parameter will be used instead. If this is passed as the empty string, then no filters
     *     will be applied when calculating the hash.
     * @return Output value of calculated SHA
     * @throws GitException git errors
     */
    public Oid hashfile(Path path, GitObject.Type type, String asPath) {
        Oid oid = new Oid();
        Error.throwIfNeeded(
                jniHashfile(oid, getRawPointer(), path.toString(), type.getCode(), asPath));
        return oid;
    }

    /**
     * Make the repository HEAD point to the specified reference.
     *
     * @param refName Canonical name of the reference the HEAD should point at
     * @throws GitException git errors
     */
    public void setHead(String refName) {
        Error.throwIfNeeded(jniSetHead(getRawPointer(), refName));
    }

    /**
     * Make the repository HEAD directly point to the Commit.
     *
     * @param oid Object id of the Commit the HEAD should point to
     */
    public void setHeadDetached(Oid oid) {
        Error.throwIfNeeded(jniSetHeadDetached(getRawPointer(), oid));
    }

    /** Detach the HEAD. */
    public void detachHead() {
        Error.throwIfNeeded(jniDetachHead(getRawPointer()));
    }

    /**
     * Determines the status of a git repository - ie, whether an operation (merge, cherry-pick,
     * etc) is in progress.
     *
     * @return The state of the repository or null if returned state is not defined (this normally
     *     should not happen, unless git24j lib is behind libgit2.)
     */
    @CheckForNull
    public StateT state() {
        int idx = jniState(getRawPointer());
        return IBitEnum.valueOf(idx, StateT.class);
    }

    /**
     * Get the currently active namespace for this repository
     *
     * @return the active namespace, or NULL if there isn't one
     */
    public String getNamespace() {
        return jniGetNamespace(getRawPointer());
    }

    /**
     * Sets the active namespace for this Git Repository
     *
     * <p>This namespace affects all reference operations for the repo. See `man gitnamespaces`
     *
     * @param namespace The namespace. This should not include the refs folder, e.g. to namespace
     *     all references under `refs/namespaces/foo/`, use `foo` as the namespace.
     * @throws GitException git error
     */
    public void setNamespace(String namespace) {
        Error.throwIfNeeded(jniSetNamespace(getRawPointer(), namespace));
    }

    /** Determine if the repository was a shallow clone. */
    public boolean isShadow() {
        return jniIsShadow(getRawPointer()) == 1;
    }

    //
    //    static native int jniIdent(
    //            AtomicReference<String> outName, AtomicReference<String> outEmail, long repoPtr);
    public Identity ident() {
        Identity identity = new Identity("", "");
        // outName.set("");
        Error.throwIfNeeded(jniIdent(identity, getRawPointer()));
        return identity;
    }

    /**
     * Set the identity to be used for writing reflogs
     *
     * @param name the name to use for the reflog entries
     * @param email the email to use for the reflog entries
     * @throws GitException git error
     */
    public void setIdent(String name, String email) {
        Error.throwIfNeeded(jniSetIdent(getRawPointer(), name, email));
    }

    /**
     * Get raw pointer of the repo.
     *
     * @return pointer value in long
     * @throws IllegalStateException if Repository has been closed
     */
    @Override
    long getRawPointer() {
        long ptr = _rawPtr.get();
        if (ptr == 0) {
            throw new IllegalStateException("Repository has been closed");
        }
        return ptr;
    }

    @Override
    public void close() {
        free();
    }

    /** Close the repository, no-op if not opened. */
    public void free() {
        if (_rawPtr.get() > 0) {
            jniFree(_rawPtr.getAndSet(0));
        }
    }

    /**
     * Retrieve and resolve the reference pointed at by HEAD.
     *
     * @return the reference
     * @throws GitException git error.
     */
    public Reference head() {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniHead(outRef, _rawPtr.get()));
        return new Reference(outRef.get());
    }

    /**
     * Retrieve the referenced HEAD for the worktree
     *
     * @param name name of the worktree to retrieve HEAD for
     * @return HEAD reference.
     * @throws GitException git error.
     */
    public Reference headForWorkTree(String name) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniHeadForWorktree(outRef, _rawPtr.get(), name));
        return new Reference(outRef.get());
    }

    /**
     * Check if a repository's HEAD is detached
     *
     * @return true if HEAD is detached
     * @throws GitException git error.
     */
    public boolean headDetached() {
        int error = jniHeadDetached(_rawPtr.get());
        Error.throwIfNeeded(error);
        return error == 1;
    }

    /**
     * Check if the current branch is unborn
     *
     * @return true if the current branch is unborn
     * @throws GitException git error.
     */
    public boolean headUnborn() {
        int error = jniHeadUnborn(_rawPtr.get());
        Error.throwIfNeeded(error);
        return error == 1;
    }

    /**
     * Check if a repository is empty
     *
     * @return if the repository is empty.
     * @throws GitException git error.
     */
    public boolean isEmpty() {
        int error = jniIsEmpty(_rawPtr.get());
        Error.throwIfNeeded(error);
        return error == 1;
    }

    /**
     * Get the location of a specific repository file or directory.
     *
     * @param item The repository item for which to retrieve the path
     * @return Buffer to store the path at
     * @throws GitException git error.
     */
    public Buf itemPath(Item item) {
        Buf buf = new Buf();
        int error = jniItemPath(buf, _rawPtr.get(), item.ordinal());
        Error.throwIfNeeded(error);
        return buf;
    }

    /**
     * Get the Index file for this repository.
     *
     * @return the loaded index
     * @throws GitException git error.
     */
    public Index index() {
        Index index = new Index();
        int error = jniIndex(index._rawPtr, _rawPtr.get());
        Error.throwIfNeeded(error);
        return index;
    }

    public enum Item {
        GITDIR,
        WORKDIR,
        COMMONDIR,
        INDEX,
        OBJECTS,
        REFS,
        PACKED_REFS,
        REMOTES,
        CONFIG,
        INFO,
        HOOKS,
        LOGS,
        MODULES,
        WORKTREES,
        LAST
    }

    public enum OpenFlag implements IBitEnum {
        NO_FLAG(0),
        /**
         * Only open the repository if it can be immediately found in the start_path. Do not walk up
         * from the start_path looking at parent directories.
         */
        NO_SEARCH(1 << 0),
        /**
         * Unless this flag is set, open will not continue searching across filesystem boundaries
         * (i.e. when `st_dev` changes from the `stat` system call). For example, searching in a
         * user's home directory at "/home/user/source/" will not return "/.git/" as the found repo
         * if "/" is a different filesystem than "/home".
         */
        CROSS_FS(1 << 1),
        /**
         * Open repository as a bare repo regardless of core.bare config, and defer loading config
         * file for faster setup. Unlike `git_repository_open_bare`, this can follow gitlinks.
         */
        BARE(1 << 2),
        /**
         * Do not check for a repository by appending /.git to the start_path; only open the
         * repository if start_path itself points to the git directory.
         */
        NO_DOTGIT(1 << 3),
        /**
         * Find and open a git repository, respecting the environment variables used by the git
         * command-line tools. If set, `git_repository_open_ext` will ignore the other flags and the
         * `ceiling_dirs` argument, and will allow a NULL `path` to use `GIT_DIR` or search from the
         * current directory. The search for a repository will respect $GIT_CEILING_DIRECTORIES and
         * $GIT_DISCOVERY_ACROSS_FILESYSTEM. The opened repository will respect $GIT_INDEX_FILE,
         * $GIT_NAMESPACE, $GIT_OBJECT_DIRECTORY, and $GIT_ALTERNATE_OBJECT_DIRECTORIES. In the
         * future, this flag will also cause `git_repository_open_ext` to respect $GIT_WORK_TREE and
         * $GIT_COMMON_DIR; currently, `git_repository_open_ext` with this flag will error out if
         * either $GIT_WORK_TREE or $GIT_COMMON_DIR is set.
         */
        FROM_ENV(1 << 4);
        private final int bit;

        OpenFlag(int bit) {
            this.bit = bit;
        }

        @Override
        public int getBit() {
            return bit;
        }
    }

    /**
     * Repository state
     *
     * <p>These values represent possible states for the repository to be in, based on the current
     * operation which is ongoing.
     */
    public enum StateT implements IBitEnum {
        NONE(0),
        MERGE(1),
        REVERT(2),
        SEQUENCE(3),
        CHERRYPICK(4),
        CHERRYPICK_SEQUENCE(5),
        BISECT(6),
        REBASE(7),
        REBASE_INTERACTIVE(8),
        REBASE_MERGE(9),
        APPLY_MAILBOX(10),
        APPLY_MAILBOX_OR_REBASE(11);
        private final int _bit;

        StateT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return 0;
        }
    }

    public enum InitFlagT implements IBitEnum {
        BARE(1 << 0),
        NO_REINIT(1 << 1),
        NO_DOTGIT_DIR(1 << 2),
        MKDIR(1 << 3),
        MKPATH(1 << 4),
        EXTERNAL_TEMPLATE(1 << 5),
        RELATIVE_GITLINK(1 << 6);
        private final int _bit;

        InitFlagT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return 0;
        }
    }

    /**
     * Mode options for `git_repository_init_ext`.
     *
     * <p>Set the mode field of the `git_repository_init_options` structure either to the custom
     * mode that you would like, or to one of the following modes:
     *
     * <pre>
     * * SHARED_UMASK - Use permissions configured by umask - the default.
     * * SHARED_GROUP - Use "--shared=group" behavior, chmod'ing the new repo
     *        to be group writable and "g+sx" for sticky group assignment.
     * * SHARED_ALL - Use "--shared=all" behavior, adding world readability.
     * * Anything else - Set to custom value.
     * </pre>
     */
    public enum InitModeT implements IBitEnum {
        /** Use permissions configured by umask - the default. */
        SHARED_UMASK(0),
        /**
         * Use "--shared=group" behavior, chmod'ing the new repo to be group writable and "g+sx" for
         * sticky group assignment.
         */
        SHARED_GROUP(0002775),
        /** Use "--shared=all" behavior, adding world readability. */
        SHARED_ALL(0002777),
        ;
        private final int _bit;

        InitModeT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public static class InitOptions {
        public static final int VERSION = 1;
        private final int version;
        private int flags;
        private int mode;
        private String workdirPath;
        private String description;
        private String templatePath;
        private String initialHead;
        private String originUrl;

        public InitOptions(int version) {
            this.version = version;
        }

        @Nonnull
        public static InitOptions defaultOpts() {
            return new InitOptions(VERSION);
        }

        public int getVersion() {
            return version;
        }

        public int getFlags() {
            return flags;
        }

        public void setFlags(EnumSet<InitFlagT> flags) {
            this.flags = IBitEnum.bitOrAll(flags);
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public void setMode(InitModeT mode) {
            this.mode = mode.getBit();
        }

        public String getWorkdirPath() {
            return workdirPath;
        }

        public void setWorkdirPath(String workdirPath) {
            this.workdirPath = workdirPath;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTemplatePath() {
            return templatePath;
        }

        public void setTemplatePath(String templatePath) {
            this.templatePath = templatePath;
        }

        public String getInitialHead() {
            return initialHead;
        }

        public void setInitialHead(String initialHead) {
            this.initialHead = initialHead;
        }

        public String getOriginUrl() {
            return originUrl;
        }

        public void setOriginUrl(String originUrl) {
            this.originUrl = originUrl;
        }
    }

    public static class Identity {
        private final String name;
        private final String email;

        public Identity(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Identity identity = (Identity) o;
            return Objects.equals(name, identity.name) && Objects.equals(email, identity.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, email);
        }
    }

    public abstract static class FetchHeadForeachCb {
        int accept(String remoteUrl, byte[] oidRaw, int isMerge) {
            return call(remoteUrl, Oid.of(oidRaw), isMerge == 1);
        }

        /**
         * Callback used to iterate over each FETCH_HEAD entry
         *
         * @param remoteUrl The remote URL
         * @param oid The reference target OID
         * @param isMerge Was the reference the result of a merge
         * @return non-zero to terminate the iteration
         */
        public abstract int call(String remoteUrl, Oid oid, boolean isMerge);
    }

    public abstract static class MergeheadForeachCb {
        int accept(byte[] oidRaw) {
            return call(Oid.of(oidRaw));
        }

        /**
         * Callback used to iterate over each MERGE_HEAD entry
         *
         * @param oid The merge OID
         * @return non-zero to terminate the iteration
         */
        public abstract int call(Oid oid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Repository that = (Repository) o;
        if (this._rawPtr.equals(that._rawPtr)) {
            return true;
        }
        return Objects.equals(this.getPath(), that.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath());
    }
}
