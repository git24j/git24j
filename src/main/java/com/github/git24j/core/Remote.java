package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;

public class Remote extends CAutoReleasable {
    protected Remote(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    public enum AutotagOptionT implements IBitEnum {
        /** Use the setting from the configuration. */
        UNSPECIFIED(0),
        /** Ask the server for tags pointing to objects we're already downloading. */
        AUTO(1),
        /** Don't ask for any tags beyond the refspecs. */
        NONE(2),
        /** Ask for the all the tags. */
        ALL(3);
        private final int _bit;

        AutotagOptionT(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }
    /**
     * Direction of the connection.
     *
     * <p>We need this because we need to know whether we should call git-upload-pack or
     * git-receive-pack on the remote end when get_refs gets called.
     */
    public enum Direction {
        FETCH,
        PUSH;
    }

    /** Remote creation options flags */
    public enum CreateFlags implements IBitEnum {
        /** Ignore the repository apply.insteadOf configuration */
        SKIP_INSTEADOF(1 << 0),

        /** Don't build a fetchspec from the name if none is set */
        SKIP_DEFAULT_FETCHSPEC(1 << 1);
        private final int _bit;

        CreateFlags(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public static class CreateOptions extends CAutoReleasable {
        public enum Flag implements IBitEnum {
            SKIP_INSTEADOF(1<<0),
            SKIP_DEFAULT_FETCHSPEC (1 << 1);
            private final int _bit;

            Flag(int bit) {
                _bit = bit;
            }

            @Override
            public int getBit() {
                return _bit;
            }
        }
        public static final int VERSION = 1;

        protected CreateOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniCreateOptionsFree(cPtr);
        }

        @Nonnull
        public static CreateOptions create(int version) {
            CreateOptions opts = new CreateOptions(false, 0);
            Error.throwIfNeeded(jniCreateOptionsNew(opts._rawPtr, version));
            return opts;
        }

        @Nonnull
        public static CreateOptions createDefault() {
            return create(VERSION);
        }

        public int getVersion() {
            return jniCreateOptionsGetVersion(getRawPointer());
        }

        @Nullable
        public Repository getRepository() {
            long repoPtr = jniCreateOptionsGetRepository(getRawPointer());
            if (repoPtr <= 0) {
                return null;
            }
            return new Repository(repoPtr);
        }

        @Nullable
        public String getName() {
            return jniCreateOptionsGetName(getRawPointer());
        }

        public String getFetchspec() {
            return jniCreateOptionsGetFetchspec(getRawPointer());
        }

        public EnumSet<Flag> getFlags() {
            return IBitEnum.parse(jniCreateOptionsGetFlags(getRawPointer()), Flag.class);
        }

        public void setVersion(int version) {
            jniCreateOptionsSetVersion(getRawPointer(), version);
        }

        public void setRepository(@Nullable Repository repository) {
            jniCreateOptionsSetRepository(getRawPointer(), repository == null ? 0 : repository.getRawPointer());
        }

        public void setName(String name) {
            jniCreateOptionsSetName(getRawPointer(), name);
        }

        public void setFetchspec(String fetchspec) {
            jniCreateOptionsSetFetchspec(getRawPointer(), fetchspec);
        }

        public void setFlags(EnumSet<Flag> flags) {
            jniCreateOptionsSetFlags(getRawPointer(), IBitEnum.bitOrAll(flags));
        }
    }

    public static class FetchOptions extends CAutoReleasable {
        protected FetchOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }

        @Nonnull
        public static FetchOptions of(int version) {
            FetchOptions opts = new FetchOptions(false, 0);
            Error.throwIfNeeded(jniFetchOptionsNew(opts._rawPtr, version));
            return opts;
        }
    }

    public static class PushOptions extends CAutoReleasable {
        protected PushOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
    }

    public static class PushUpdate extends CAutoReleasable {
        protected PushUpdate(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
    }

    @FunctionalInterface
    public interface CredAcquireCb {
        /**
         * Signature of a function which acquires a credential object.
         *
         * @param url The resource for which we are demanding a credential.
         * @param usernameFromUrl The username that was embedded in a "user\@host" remote url, or
         *     NULL if not included.
         * @param allowedTypes A bitmask stating which cred types are OK to return.
         * @return Credential or empty
         */
        Optional<Cred> acquire(String url, String usernameFromUrl, int allowedTypes);
    }

    @FunctionalInterface
    public interface TransportMessageCb {
        /**
         * Type for messages delivered by the transport. Return a negative value to cancel the
         * network operation.
         *
         * @param message The message from the transport
         */
        int accept(String message);
    }

    @FunctionalInterface
    public interface TransportCertificateCheckCb {
        /**
         * Callback for the user's custom certificate checks.
         *
         * @param cert The host certificate
         * @param valid Whether the libgit2 checks (OpenSSL or WinHTTP) think this certificate is
         *     valid
         * @param host Hostname of the host libgit2 connected to
         * @return 0 to proceed with the connection, < 0 to fail the connection or > 0 to indicate
         *     that the callback refused to act and that the existing validity determination should
         *     be honored
         */
        int accept(Cert cert, boolean valid, String host);
    }

    @FunctionalInterface
    public interface TransferProgressCb {
        /**
         * Type for progress callbacks during indexing. Return a value less than zero to cancel the
         * transfer.
         *
         * @param stats Structure containing information about the state of the transfer
         */
        int accept(TransferProgress stats);
    }

    @FunctionalInterface
    public interface UpdateTipsCb {
        /** Callback when a reference is updated locally */
        int accept(String refname, Oid a, Oid b);
    }

    @FunctionalInterface
    public interface PackProgressCb {
        /** Packbuilder progress notification function */
        int accept(int stage, long current, long total);
    }

    @FunctionalInterface
    public interface PushTransferProgressCb {
        /** Push network progress notification function */
        int accept(long current, long total, int bytes);
    }

    @FunctionalInterface
    public interface UpdateReferenceCb {
        /**
         * Callback used to inform of the update status from the remote.
         *
         * <p>Called for each updated reference on push. If `status` is not `NULL`, the update was
         * rejected by the remote server and `status` contains the reason given.
         *
         * @param refname refname specifying to the remote ref
         * @param status status message sent from the remote
         * @return 0 on success, other value indicates an error
         */
        int accept(String refname, String status);
    }

    @FunctionalInterface
    public interface PushNegotiationCb {
        /**
         * Callback used to inform of upcoming updates.
         *
         * @param updates an array containing the updates which will be sent as commands to the
         *     destination.
         */
        int accept(List<PushUpdate> updates);
    }

    @FunctionalInterface
    public interface TransportCb {
        /**
         * Callback that can create the transport to use for this operation. Leave empty to
         * auto-detect.
         */
        Optional<Transport> accept(@Nonnull Remote owner);
    }

    static native void jniCallbacksFree(long cbsPtr);

    static native void jniCallbacksSetCallbackObject(long cbsPtr, Callbacks cbsObject);

    public static final class Callbacks extends CAutoReleasable {
        public static final int VERSION = 1;
        private CredAcquireCb _credAcquireCb;
        private TransportMessageCb _transportMsg;
        private TransportCertificateCheckCb _certificateCheckCb;
        private TransferProgressCb _transferProgressCb;
        private UpdateTipsCb _updateTipsCb;
        private PackProgressCb _packProgressCb;
        private PushTransferProgressCb _pushTransferProgressCb;
        private UpdateReferenceCb _updateReferenceCb;
        private PushNegotiationCb _pushNegotiationCb;
        private TransportCb _transportCb;

        public void setCredAcquireCb(CredAcquireCb credAcquireCb) {
            jniCallbacksSetCallbackObject(getRawPointer(), this);
            _credAcquireCb = credAcquireCb;
        }

        public void setTransportMsg(TransportMessageCb transportMsg) {
            jniCallbacksSetCallbackObject(getRawPointer(), this);
            _transportMsg = transportMsg;
        }

        public void setCertificateCheckCb(TransportCertificateCheckCb certificateCheckCb) {
            jniCallbacksSetCallbackObject(getRawPointer(), this);
            _certificateCheckCb = certificateCheckCb;
        }

        public void setTransferProgressCb(TransferProgressCb transferProgressCb) {
            jniCallbacksSetCallbackObject(getRawPointer(), this);
            _transferProgressCb = transferProgressCb;
        }

        public void setUpdateTipsCb(UpdateTipsCb updateTipsCb) {
            jniCallbacksSetCallbackObject(getRawPointer(), this);
            _updateTipsCb = updateTipsCb;
        }

        public void setPackProgressCb(PackProgressCb packProgressCb) {
            jniCallbacksSetCallbackObject(getRawPointer(), this);
            _packProgressCb = packProgressCb;
        }

        public void setPushTransferProgressCb(PushTransferProgressCb pushTransferProgressCb) {
            jniCallbacksSetCallbackObject(getRawPointer(), this);
            _pushTransferProgressCb = pushTransferProgressCb;
        }

        public void setUpdateReferenceCb(UpdateReferenceCb updateReferenceCb) {
            jniCallbacksSetCallbackObject(getRawPointer(), this);
            _updateReferenceCb = updateReferenceCb;
        }

        public void setPushNegotiationCb(PushNegotiationCb pushNegotiationCb) {
            jniCallbacksSetCallbackObject(getRawPointer(), this);
            _pushNegotiationCb = pushNegotiationCb;
        }

        public void setTransportCb(TransportCb transportCb) {
            jniCallbacksSetCallbackObject(getRawPointer(), this);
            _transportCb = transportCb;
        }

        protected Callbacks(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniCallbacksFree(cPtr);
        }

        @Nonnull
        public static Callbacks create(int version) {
            Callbacks cb = new Callbacks(false, 0);
            Error.throwIfNeeded(jniCallbacksNew(cb._rawPtr, version));
            return cb;
        }

        public static Callbacks createDefault() {
            return create(VERSION);
        }

        /**
         * Get the credential and return its c pointer.
         *
         * @return 0 if no credential was acquired, > 0 if credentials acquired successfully < 0
         *     indicate an error
         */
        long acquireCred(String url, String usernameFromUrl, int allowedTypes) {
            if (_credAcquireCb != null) {
                return _credAcquireCb
                        .acquire(url, usernameFromUrl, allowedTypes)
                        .map(CAutoReleasable::getRawPointer)
                        .orElse(0L);
            }
            return 0;
        }

        int transportMessage(String message) {
            if (_transportMsg != null) {
                return _transportMsg.accept(message);
            }
            return 0;
        }

        int transportMessageCheck(long certPtr, int valid, String host) {
            if (_certificateCheckCb != null) {
                return _certificateCheckCb.accept(new Cert(true, 0), valid == 1, host);
            }
            return 0;
        }

        int transferProgress(long progressPtr) {
            if (_transferProgressCb != null) {
                return _transferProgressCb.accept(new TransferProgress(true, progressPtr));
            }
            return 0;
        }

        int updateTips(String refname, byte[] ida, byte[] idb) {
            if (_updateTipsCb != null) {
                return _updateTipsCb.accept(refname, Oid.of(ida), Oid.of(idb));
            }
            return 0;
        }

        int packProgress(int stage, long current, long total) {
            if (_packProgressCb != null) {
                return _packProgressCb.accept(stage, current, total);
            }
            return 0;
        }

        int pushTransferProgress(long current, long total, int bytes) {
            if (_pushTransferProgressCb != null) {
                return _pushTransferProgressCb.accept(current, total, bytes);
            }
            return 0;
        }

        int pushUpdateReference(String refname, String status) {
            if (_updateReferenceCb != null) {
                return _updateReferenceCb.accept(refname, status);
            }
            return 0;
        }

        int pushNegotiation(long[] updates) {
            if (_pushNegotiationCb != null) {
                return _pushNegotiationCb.accept(
                        Arrays.stream(updates)
                                .mapToObj(p -> new PushUpdate(true, p))
                                .collect(Collectors.toList()));
            }
            return 0;
        }

        /**
         * According to remote.c:
         *
         * <pre>
         *     if (!t && transport && (error = transport(&t, remote, payload)) < 0)
         * 		return error;
         * </pre>
         *
         * HACK: We would like to return < 0 values for error > 0 values for valid pointer 0 as not
         * able to allocate.
         *
         * @param ownerPtr
         * @return <0 for error,
         */
        long transport(long ownerPtr) {
            if (_transportCb != null) {
                return _transportCb
                        .accept(new Remote(true, ownerPtr))
                        .map(CAutoReleasable::getRawPointer)
                        .orElse(0L);
            }
            return 0L;
        }
    }
    // no matching type found for 'const git_remote_head ***out'
    /** int git_remote_ls(const git_remote_head ***out, size_t *size, git_remote *remote); */
    /** -------- Jni Signature ---------- */
    /** int git_remote_add_fetch(git_repository *repo, const char *remote, const char *refspec); */
    static native int jniAddFetch(long repoPtr, String remote, String refspec);

    /**
     * Add a fetch refspec to the remote's configuration
     *
     * <p>Add the given refspec to the fetch list in the configuration. No loaded remote instances
     * will be affected.
     *
     * @param repo the repository in which to change the configuration
     * @param remote the name of the remote to change
     * @param refspec the new fetch refspec
     * @throws GitException GIT_EINVALIDSPEC if refspec is invalid or an error value
     */
    public static void addFetch(
            @Nonnull Repository repo, @Nonnull String remote, @Nonnull String refspec) {
        Error.throwIfNeeded(jniAddFetch(repo.getRawPointer(), remote, refspec));
    }

    /** int git_remote_add_push(git_repository *repo, const char *remote, const char *refspec); */
    static native int jniAddPush(long repoPtr, String remote, String refspec);

    /**
     * Add a push refspec to the remote's configuration
     *
     * <p>Add the given refspec to the push list in the configuration. No loaded remote instances
     * will be affected.
     *
     * @param repo the repository in which to change the configuration
     * @param remote the name of the remote to change
     * @param refspec the new push refspec
     * @throws GitException GIT_EINVALIDSPEC if refspec is invalid or an error value
     */
    public static void addPush(
            @Nonnull Repository repo, @Nonnull String remote, @Nonnull String refspec) {
        Error.throwIfNeeded(jniAddPush(repo.getRawPointer(), remote, refspec));
    }

    /** git_remote_autotag_option_t git_remote_autotag(const git_remote *remote); */
    static native int jniAutotag(long remote);

    /**
     * Retrieve the tag auto-follow setting
     *
     * @return the auto-follow setting
     * @throws GitException if auto tag settings is not recognizable.
     */
    @Nonnull
    AutotagOptionT autotag() {
        int t = jniAutotag(getRawPointer());
        AutotagOptionT ret = IBitEnum.valueOf(t, AutotagOptionT.class);
        if (ret == null) {
            throw new GitException(
                    GitException.ErrorClass.CONFIG,
                    "remote autotag(" + t + ") is not recognizable");
        }
        return ret;
    }

    /**
     *
     *
     * <pre>
     * int git_remote_connect(
     *     git_remote *remote,
     *     git_direction direction,
     *     const git_remote_callbacks *callbacks,
     *     const git_proxy_options *proxy_opts,
     *     const git_strarray *custom_headers);
     * </pre>
     */
    static native int jniConnect(
            long remote, int direction, long callbacks, long proxyOpts, String[] customHeaders);

    /**
     * Open a connection to a remote
     *
     * <p>The transport is selected based on the URL. The direction argument is due to a limitation
     * of the git protocol (over TCP or SSH) which starts up a specific binary which can only do the
     * one or the other.
     *
     * @param direction GIT_DIRECTION_FETCH if you want to fetch or GIT_DIRECTION_PUSH if you want
     *     to push
     * @param callbacks the callbacks to use for this connection
     * @param proxyOpts proxy settings
     * @param customHeaders extra HTTP headers to use in this connection
     * @throws GitException git errors
     */
    public void connect(
            @Nonnull Direction direction,
            @Nullable Callbacks callbacks,
            @Nullable Proxy.Options proxyOpts,
            @Nullable List<String> customHeaders) {
        Error.throwIfNeeded(
                jniConnect(
                        getRawPointer(),
                        direction.ordinal(),
                        callbacks == null ? 0 : Callbacks.createDefault().getRawPointer(),
                        proxyOpts == null ? 0 : proxyOpts.getRawPointer(),
                        customHeaders == null
                                ? new String[0]
                                : customHeaders.toArray(new String[0])));
    }

    /** int git_remote_connected(const git_remote *remote); */
    static native int jniConnected(long remote);

    /**
     * Check whether the remote is connected
     *
     * <p>Check whether the remote's underlying transport is connected to the remote host.
     *
     * @return if it's connected
     */
    public boolean connected() {
        return jniConnected(getRawPointer()) == 1;
    }

    /**
     * int git_remote_create(git_remote **out, git_repository *repo, const char *name, const char
     * *url);
     */
    static native int jniCreate(AtomicLong out, long repoPtr, String name, String url);

    /**
     * Add a remote with the default fetch refspec to the repository's configuration.
     *
     * @param repo the repository in which to create the remote
     * @param name the remote's name
     * @param url the remote's url
     * @return the resulting remote
     * @throws GitException GIT_EINVALIDSPEC, GIT_EEXISTS or an error code
     */
    @Nonnull
    public static Remote create(@Nonnull Repository repo, @Nonnull String name, @Nonnull URI url) {
        Remote remote = new Remote(false, 0);
        Error.throwIfNeeded(jniCreate(remote._rawPtr, repo.getRawPointer(), name, url.toString()));
        return remote;
    }

    /** int git_remote_create_anonymous(git_remote **out, git_repository *repo, const char *url); */
    static native int jniCreateAnonymous(AtomicLong out, long repoPtr, String url);

    /**
     * Create an anonymous remote
     *
     * <p>Create a remote with the given url in-memory. You can use this when you have a URL instead
     * of a remote's name.
     *
     * @param repo the associated repository
     * @param url the remote repository's URL
     * @return remote objects
     * @throws GitException git errors
     */
    @Nonnull
    public static Remote createAnonymous(@Nonnull Repository repo, @Nonnull URI url) {
        Remote remote = new Remote(false, 0);
        Error.throwIfNeeded(
                jniCreateAnonymous(remote._rawPtr, repo.getRawPointer(), url.toString()));
        return remote;
    }

    /** int git_remote_create_detached(git_remote **out, const char *url); */
    static native int jniCreateDetached(AtomicLong out, String url);

    /**
     * Create a remote without a connected local repo
     *
     * <p>Create a remote with the given url in-memory. You can use this when you have a URL instead
     * of a remote's name.
     *
     * <p>Contrasted with git_remote_create_anonymous, a detached remote will not consider any repo
     * configuration values (such as insteadof url substitutions).
     *
     * @param url the remote repository's URL
     * @return the remote object
     * @throws GitException git errors
     */
    @Nonnull
    public static Remote createDetached(@Nonnull URI url) {
        Remote remote = new Remote(false, 0);
        Error.throwIfNeeded(jniCreateDetached(remote._rawPtr, url.toString()));
        return remote;
    }

    static native int jniCreateOptionsNew(AtomicLong outOpts, int version);

    static native int jniCreateOptionsFree(long optsPtr);
    /**
     * int git_remote_create_with_fetchspec(git_remote **out, git_repository *repo, const char
     * *name, const char *url, const char *fetch);
     */
    static native int jniCreateWithFetchspec(
            AtomicLong out, long repoPtr, String name, String url, String fetch);

    /** unsigned int version */
    static native int jniCreateOptionsGetVersion(long create_optionsPtr);

    /** git_repository *repository */
    static native long jniCreateOptionsGetRepository(long create_optionsPtr);

    /** const char *name */
    static native String jniCreateOptionsGetName(long create_optionsPtr);

    /** const char *fetchspec */
    static native String jniCreateOptionsGetFetchspec(long create_optionsPtr);

    /** unsigned int flags */
    static native int jniCreateOptionsGetFlags(long create_optionsPtr);

    /** unsigned int version */
    static native void jniCreateOptionsSetVersion(long create_optionsPtr, int version);

    /** git_repository *repository */
    static native void jniCreateOptionsSetRepository(long create_optionsPtr, long repository);

    /** const char *name */
    static native void jniCreateOptionsSetName(long create_optionsPtr, String name);

    /** const char *fetchspec */
    static native void jniCreateOptionsSetFetchspec(long create_optionsPtr, String fetchspec);

    /** unsigned int flags */
    static native void jniCreateOptionsSetFlags(long create_optionsPtr, int flags);

    /**
     * Add a remote with the provided fetch refspec (or default if NULL) to the repository's
     * configuration.
     *
     * @param repo the repository in which to create the remote
     * @param name the remote's name
     * @param url the remote's url
     * @param fetch the remote fetch value
     * @return remote object
     * @throws GitException GIT_EINVALIDSPEC, GIT_EEXISTS or an error code
     */
    @Nonnull
    public static Remote createWithFetchspec(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull URI url,
            @Nullable String fetch) {
        Remote remote = new Remote(false, 0);
        Error.throwIfNeeded(
                jniCreateWithFetchspec(
                        remote._rawPtr, repo.getRawPointer(), name, url.toString(), fetch));
        return remote;
    }

    /**
     * int git_remote_create_with_opts(git_remote **out, const char *url, const
     * git_remote_create_options *opts);
     */
    static native int jniCreateWithOpts(AtomicLong out, String url, long opts);

    /**
     * Create a remote, with options.
     *
     * <p>This function allows more fine-grained control over the remote creation.
     *
     * <p>Passing NULL as the opts argument will result in a detached remote.
     *
     * @param url the remote's url
     * @param opts the remote creation options
     * @return remote object
     * @throws GitException GIT_EINVALIDSPEC, GIT_EEXISTS or an error code
     */
    @Nonnull
    public static Remote createWithOpts(@Nonnull URI url, @Nullable CreateOptions opts) {
        Remote remote = new Remote(false, 0);
        Error.throwIfNeeded(
                jniCreateWithOpts(
                        remote._rawPtr, url.toString(), opts == null ? 0 : opts.getRawPointer()));
        return remote;
    }

    /** int git_remote_default_branch(git_buf *out, git_remote *remote); */
    static native int jniDefaultBranch(Buf out, long remote);

    /**
     * Retrieve the name of the remote's default branch
     *
     * <p>The default branch of a repository is the branch which HEAD points to. If the remote does
     * not support reporting this information directly, it performs the guess as git does; that is,
     * if there are multiple branches which point to the same commit, the first one is chosen. If
     * the master branch is a candidate, it wins.
     *
     * <p>This function must only be called after connecting.
     *
     * @return reference name of the branch.
     * @throws GitException GIT_ENOTFOUND if the remote does not have any references or none of them
     *     point to HEAD's commit, or an error message.
     */
    public Optional<String> defaultBranch() {
        Buf out = new Buf();
        int e = jniDefaultBranch(out, getRawPointer());
        if (ENOTFOUND.getCode() == e) {
            return Optional.empty();
        }
        return out.getString();
    }

    /** int git_remote_delete(git_repository *repo, const char *name); */
    static native int jniDelete(long repoPtr, String name);

    /**
     * Delete an existing persisted remote.
     *
     * <p>All remote-tracking branches and configuration settings for the remote will be removed.
     *
     * @param repo the repository in which to act
     * @param name the name of the remote to delete
     * @throws GitException an error code.
     */
    public void delete(@Nonnull Repository repo, @Nonnull String name) {
        Error.throwIfNeeded(jniDelete(repo.getRawPointer(), name));
    }

    /** void git_remote_disconnect(git_remote *remote); */
    static native void jniDisconnect(long remote);

    /**
     * Disconnect from the remote
     *
     * <p>Close the connection to the remote.
     */
    public void disconnect() {
        jniDisconnect(getRawPointer());
    }

    /**
     * int git_remote_download(git_remote *remote, const git_strarray *refspecs, const
     * git_fetch_options *opts);
     */
    static native int jniDownload(long remote, String[] refspecs, long opts);

    /**
     * Download and index the packfile
     *
     * <p>Connect to the remote if it hasn't been done yet, negotiate with the remote git which
     * objects are missing, download and index the packfile.
     *
     * <p>The .idx file will be created and both it and the packfile with be renamed to their final
     * name.
     *
     * @param refspecs the refspecs to use for this negotiation and download. Use NULL or an empty
     *     array to use the base refspecs
     * @param opts the options to use for this fetch
     * @throws GitException git errors
     */
    public void download(@Nonnull String[] refspecs, @Nullable FetchOptions opts) {
        Error.throwIfNeeded(
                jniDownload(getRawPointer(), refspecs, opts == null ? 0 : opts.getRawPointer()));
    }

    /** int git_remote_dup(git_remote **dest, git_remote *source); */
    static native int jniDup(AtomicLong dest, long source);

    /**
     * Create a copy of an existing remote. All internal strings are also duplicated. Callbacks are
     * not duplicated.
     *
     * @return Remote object
     */
    @Nonnull
    public Remote dup() {
        Remote out = new Remote(false, 0);
        Error.throwIfNeeded(jniDup(out._rawPtr, getRawPointer()));
        return out;
    }

    /**
     * int git_remote_fetch(git_remote *remote, const git_strarray *refspecs, const
     * git_fetch_options *opts, const char *reflog_message);
     */
    static native int jniFetch(long remote, String[] refspecs, long opts, String reflogMessage);

    /**
     * Download new data and update tips
     *
     * <p>Convenience function to connect to a remote, download the data, disconnect and update the
     * remote-tracking branches.
     *
     * @param refspecs the refspecs to use for this fetch. Pass an empty array to use the base
     *     refspecs.
     * @param opts options to use for this fetch
     * @param reflogMessage The message to insert into the reflogs. If NULL, the default is "fetch"
     * @throws GitException git errors
     */
    public void fetch(
            @Nullable String[] refspecs,
            @Nullable FetchOptions opts,
            @Nullable String reflogMessage) {
        Error.throwIfNeeded(
                jniFetch(
                        getRawPointer(),
                        refspecs == null ? new String[0] : refspecs,
                        opts == null ? 0 : opts.getRawPointer(),
                        reflogMessage));
    }

    /** void git_remote_free(git_remote *remote); */
    static native void jniFree(long remote);

    /** int git_remote_get_fetch_refspecs(git_strarray *array, const git_remote *remote); */
    static native int jniGetFetchRefspecs(List<String> array, long remote);

    /** Get the remote's list of fetch refspecs */
    public List<String> getFetchRefspecs() {
        List<String> out = new ArrayList<>();
        Error.throwIfNeeded(jniGetFetchRefspecs(out, getRawPointer()));
        return out;
    }

    /** int git_remote_get_push_refspecs(git_strarray *array, const git_remote *remote); */
    static native int jniGetPushRefspecs(List<String> array, long remote);

    /** Get the remote's list of push refspecs */
    public List<String> getPushRefspecs() {
        List<String> out = new ArrayList<>();
        Error.throwIfNeeded(jniGetPushRefspecs(out, getRawPointer()));
        return out;
    }

    /** const git_refspec * git_remote_get_refspec(const git_remote *remote, size_t n); */
    static native long jniGetRefspec(long remote, int n);

    /**
     * Get a refspec from the remote
     *
     * @param n the refspec to get
     * @return the nth refspec
     */
    public Optional<Refspec> getRefspec(int n) {
        long ptr = jniGetRefspec(getRawPointer(), n);
        if (ptr == 0) {
            return Optional.empty();
        }
        return Optional.of(new Refspec(false, ptr));
    }

    static native int jniCallbacksNew(AtomicLong outCb, int version);

    /** int git_remote_is_valid_name(const char *remote_name); */
    static native int jniIsValidName(String remoteName);

    /**
     * Ensure the remote name is well-formed.
     *
     * @return true if the reference name is acceptable;
     */
    public boolean isValidName(String remoteName) {
        return jniIsValidName(remoteName) == 1;
    }

    /** int git_remote_list(git_strarray *out, git_repository *repo); */
    static native int jniList(List<String> out, long repoPtr);

    /**
     * Get a list of the configured remotes for a repo
     *
     * <p>The string array must be freed by the user.
     *
     * @param repo the repository to query
     * @return a list of the names of the remotes 0
     * @throws GitException git errors
     */
    @Nonnull
    public static List<String> list(@Nonnull Repository repo) {
        List<String> out = new ArrayList<>();
        Error.throwIfNeeded(jniList(out, repo.getRawPointer()));
        return out;
    }

    /** int git_remote_lookup(git_remote **out, git_repository *repo, const char *name); */
    static native int jniLookup(AtomicLong out, long repoPtr, String name);

    /**
     * Get the information for a particular remote
     *
     * <p>The name will be checked for validity. See `git_tag_create()` for rules about valid names.
     *
     * @param repo the associated repository
     * @param name the remote's name
     * @return remote object
     * @throws GitException , GIT_ENOTFOUND, GIT_EINVALIDSPEC or an error code
     */
    @Nonnull
    public static Remote lookup(@Nonnull Repository repo, @Nonnull String name) {
        // FIXME: jniLookup returns a weak reference, a bug?
        Remote out = new Remote(true, 0);
        Error.throwIfNeeded(jniLookup(out._rawPtr, repo.getRawPointer(), name));
        return out;
    }

    /** const char * git_remote_name(const git_remote *remote); */
    static native String jniName(long remote);

    /**
     * Get the remote's name
     *
     * @return a pointer to the name or empty for in-memory remotes
     */
    public Optional<String> name() {
        return Optional.ofNullable(jniName(getRawPointer()));
    }

    /** git_repository * git_remote_owner(const git_remote *remote); */
    static native long jniOwner(long remote);

    /**
     * Get the remote's repository
     *
     * @return a pointer to the repository
     */
    public Optional<Repository> owner() {
        long ptr = jniOwner(getRawPointer());
        if (ptr == 0) {
            return Optional.empty();
        }
        return Optional.of(new Repository(ptr));
    }

    /** int git_remote_prune(git_remote *remote, const git_remote_callbacks *callbacks); */
    static native int jniPrune(long remote, long callbacks);

    /**
     * Prune tracking refs that are no longer present on remote
     *
     * @param callbacks callbacks to use for this prune
     * @throws GitException 0 or an error code
     */
    public void prune(@Nullable Callbacks callbacks) {
        Error.throwIfNeeded(
                jniPrune(getRawPointer(), callbacks == null ? 0 : callbacks.getRawPointer()));
    }

    /** int git_remote_prune_refs(const git_remote *remote); */
    static native int jniPruneRefs(long remote);

    /**
     * Retrieve the ref-prune setting
     *
     * @return the ref-prune setting
     */
    public int pruneRefs() {
        return jniPruneRefs(getRawPointer());
    }

    /**
     * int git_remote_push(git_remote *remote, const git_strarray *refspecs, const git_push_options
     * *opts);
     */
    static native int jniPush(long remote, String[] refspecs, long opts);

    /**
     * Perform a push
     *
     * <p>Peform all the steps from a push.
     *
     * @param refspecs the refspecs to use for pushing. If NULL or an empty array, the configured
     *     refspecs will be used
     * @param opts options to use for this push
     */
    public void push(@Nonnull List<String> refspecs, @Nullable PushOptions opts) {
        Error.throwIfNeeded(
                jniPush(
                        getRawPointer(),
                        refspecs.toArray(new String[0]),
                        opts == null ? 0 : opts.getRawPointer()));
    }

    /** const char * git_remote_pushurl(const git_remote *remote); */
    static native String jniPushurl(long remote);

    /**
     * Get the remote's url for pushing
     *
     * <p>If url.*.pushInsteadOf has been configured for this URL, it will return the modified URL.
     *
     * @return the url or empty if no special url for pushing is set
     */
    public Optional<URI> pushurl() {
        return Optional.ofNullable(jniPushurl(getRawPointer())).map(URI::create);
    }

    /** size_t git_remote_refspec_count(const git_remote *remote); */
    static native int jniRefspecCount(long remote);

    /**
     * Get the number of refspecs for a remote
     *
     * @return the amount of refspecs configured in this remote
     */
    public int refspecCount() {
        return jniRefspecCount(getRawPointer());
    }

    /**
     * int git_remote_rename(git_strarray *problems, git_repository *repo, const char *name, const
     * char *new_name);
     */
    static native int jniRename(List<String> problems, long repoPtr, String name, String newName);

    /**
     * Give the remote a new name
     *
     * <p>All remote-tracking branches and configuration settings for the remote are updated.
     *
     * <p>The new name will be checked for validity. See `git_tag_create()` for rules about valid
     * names.
     *
     * <p>No loaded instances of a the remote with the old name will change their name or their list
     * of refspecs.
     *
     * @param repo the repository in which to rename
     * @param name the current name of the remote
     * @param newName the new name the remote should bear
     * @return non-default refspecs cannot be renamed for further processing by the caller.
     * @throws GitException GIT_EINVALIDSPEC, GIT_EEXISTS or an error code
     */
    @Nonnull
    public static List<String> rename(
            @Nonnull Repository repo, @Nonnull String name, @Nonnull String newName) {
        List<String> problems = new ArrayList<>();
        Error.throwIfNeeded(jniRename(problems, repo.getRawPointer(), name, newName));
        return problems;
    }

    /**
     * int git_remote_set_autotag(git_repository *repo, const char *remote,
     * git_remote_autotag_option_t value);
     */
    static native int jniSetAutotag(long repoPtr, String remote, int value);
    /**
     * Set the remote's tag following setting.
     *
     * <p>The change will be made in the configuration. No loaded remotes will be affected.
     *
     * @param repo the repository in which to make the change
     * @param remote the name of the remote
     * @param value the new value to take.
     * @throws GitException git errors
     */
    public static void setAutotag(
            @Nonnull Repository repo, @Nonnull String remote, @Nonnull AutotagOptionT value) {
        Error.throwIfNeeded(jniSetAutotag(repo.getRawPointer(), remote, value.getBit()));
    }

    /** int git_remote_set_pushurl(git_repository *repo, const char *remote, const char *url); */
    static native int jniSetPushurl(long repoPtr, String remote, String url);

    /**
     * Set the remote's url for pushing in the configuration.
     *
     * <p>Remote objects already in memory will not be affected. This assumes the common case of a
     * single-url remote and will otherwise return an error.
     *
     * @param repo the repository in which to perform the change
     * @param remote the remote's name
     * @param url the url to set, set null to delete the remote
     * @throws GitException git errors
     */
    public static void setPushurl(
            @Nonnull Repository repo, @Nonnull String remote, @Nullable URI url) {
        Error.throwIfNeeded(
                jniSetPushurl(repo.getRawPointer(), remote, url == null ? null : url.toString()));
    }

    /** int git_remote_set_url(git_repository *repo, const char *remote, const char *url); */
    static native int jniSetUrl(long repoPtr, String remote, String url);

    /**
     * Set the remote's url in the configuration
     *
     * <p>Remote objects already in memory will not be affected. This assumes the common case of a
     * single-url remote and will otherwise return an error.
     *
     * @param repo the repository in which to perform the change
     * @param remote the remote's name
     * @param url the url to set, null to delete the remote from config
     * @throws GitException git errors
     */
    public static void setUrl(@Nonnull Repository repo, @Nonnull String remote, @Nullable URI url) {
        Error.throwIfNeeded(
                jniSetUrl(repo.getRawPointer(), remote, url == null ? null : url.toString()));
    }

    /** const git_transfer_progress * git_remote_stats(git_remote *remote); */
    static native long jniStats(long remote);

    /** Get the statistics structure that is filled in by the fetch operation. */
    @CheckForNull
    public TransferProgress stats() {
        long ptr = jniStats(getRawPointer());
        if (ptr == 0) {
            return null;
        }
        return new TransferProgress(true, ptr);
    }

    /** void git_remote_stop(git_remote *remote); */
    static native void jniStop(long remote);

    /**
     * Cancel the operation
     *
     * <p>At certain points in its operation, the network code checks whether the operation has been
     * cancelled and if so stops the operation.
     */
    public void stop() {
        jniStop(getRawPointer());
    }

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
     * Update the tips to the new state
     *
     * @param reflogMessage The message to insert into the reflogs. If NULL and fetching, the
     *     default is "fetch <name>", where <name> is the name of the remote (or its url, for
     *     in-memory remotes). This parameter is ignored when pushing.
     * @param callbacks pointer to the callback structure to use
     * @param updateFetchhead whether to write to FETCH_HEAD. Pass 1 to behave like git.
     * @param downloadTags what the behaviour for downloading tags is for this fetch. This is
     *     ignored for push. This must be the same value passed to `git_remote_download()`.
     * @throws GitException git errors
     */
    public void updateTips(
            @Nullable String reflogMessage,
            @Nullable Callbacks callbacks,
            boolean updateFetchhead,
            @Nonnull AutotagOptionT downloadTags) {
        Error.throwIfNeeded(
                jniUpdateTips(
                        getRawPointer(),
                        callbacks == null ? 0 : callbacks.getRawPointer(),
                        updateFetchhead ? 1 : 0,
                        downloadTags.getBit(),
                        reflogMessage));
    }

    /**
     * int git_remote_upload(git_remote *remote, const git_strarray *refspecs, const
     * git_push_options *opts);
     */
    static native int jniUpload(long remote, String[] refspecs, long opts);

    /**
     * Create a packfile and send it to the server
     *
     * <p>Connect to the remote if it hasn't been done yet, negotiate with the remote git which
     * objects are missing, create a packfile with the missing objects and send it.
     *
     * @param refspecs the refspecs to use for this negotiation and upload. Use NULL or an empty
     *     array to use the base refspecs
     * @param opts the options to use for this push
     * @throws GitException git errors
     */
    public void updaload(@Nonnull List<String> refspecs, @Nullable PushOptions opts) {
        Error.throwIfNeeded(
                jniUpload(
                        getRawPointer(),
                        refspecs.toArray(new String[0]),
                        opts == null ? 0 : opts.getRawPointer()));
    }

    /** const char * git_remote_url(const git_remote *remote); */
    static native String jniUrl(long remote);

    /**
     * Get the remote's url
     *
     * <p>If url.*.insteadOf has been configured for this URL, it will return the modified URL.
     *
     * @return URI that represents the url
     * @throws IllegalStateException if remote url is not a valid URI
     */
    @Nonnull
    public URI url() {
        return URI.create(jniUrl(getRawPointer()));
    }

    static native int jniFetchOptionsNew(AtomicLong outPtr, int version);
}
