package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.github.git24j.core.Remote.jniFetchOptionsFree;
import static com.github.git24j.core.Remote.jniFetchOptionsGetCallbacks;
import static com.github.git24j.core.Remote.jniFetchOptionsGetCustomHeaders;
import static com.github.git24j.core.Remote.jniFetchOptionsGetDownloadTags;
import static com.github.git24j.core.Remote.jniFetchOptionsGetProxyOpts;
import static com.github.git24j.core.Remote.jniFetchOptionsGetPrune;
import static com.github.git24j.core.Remote.jniFetchOptionsGetUpdateFetchhead;
import static com.github.git24j.core.Remote.jniFetchOptionsGetVersion;
import static com.github.git24j.core.Remote.jniFetchOptionsSetCallbacks;
import static com.github.git24j.core.Remote.jniFetchOptionsSetCustomHeaders;
import static com.github.git24j.core.Remote.jniFetchOptionsSetDownloadTags;
import static com.github.git24j.core.Remote.jniFetchOptionsSetPrune;
import static com.github.git24j.core.Remote.jniFetchOptionsSetUpdateFetchhead;
import static com.github.git24j.core.Remote.jniFetchOptionsSetVersion;

public class FetchOptions extends CAutoReleasable {
    public static final int VERSION = 1;

    protected FetchOptions(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Nonnull
    public static FetchOptions of(int version) {
        FetchOptions opts = new FetchOptions(false, 0);
        Error.throwIfNeeded(Remote.jniFetchOptionsNew(opts._rawPtr, version));
        return opts;
    }

    public static FetchOptions createDefault() {
        return FetchOptions.of(VERSION);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFetchOptionsFree(cPtr);
    }

    public int getVersion() {
        return jniFetchOptionsGetVersion(getRawPointer());
    }

    public void setVersion(int version) {
        jniFetchOptionsSetVersion(getRawPointer(), version);
    }

    /** git_remote_callbacks callbacks */
    @CheckForNull
    public Remote.Callbacks getCallbacks() {
        long ptr = jniFetchOptionsGetCallbacks(getRawPointer());
        if (ptr == 0) {
            return null;
        }
        return new Remote.Callbacks(true, ptr);
    }

    @Nonnull
    public PruneT getPrune() {
        int r = jniFetchOptionsGetPrune(getRawPointer());
        switch (r) {
            case 1:
                return PruneT.PRUNE;
            case 2:
                return PruneT.NO_PRUNE;
            default:
                return PruneT.UNSPECIFIED;
        }
    }

    public void setPrune(PruneT prune) {
        jniFetchOptionsSetPrune(getRawPointer(), prune.ordinal());
    }

    public boolean getUpdateFetchhead() {
        return 0 != jniFetchOptionsGetUpdateFetchhead(getRawPointer());
    }

    public void setUpdateFetchhead(boolean updateFetchhead) {
        jniFetchOptionsSetUpdateFetchhead(getRawPointer(), updateFetchhead ? 1 : 0);
    }

    @Nonnull
    public Remote.AutotagOptionT getDownloadTags() {
        int r = jniFetchOptionsGetDownloadTags(getRawPointer());
        switch (r) {
            case 1:
                return Remote.AutotagOptionT.AUTO;
            case 2:
                return Remote.AutotagOptionT.NONE;
            case 3:
                return Remote.AutotagOptionT.ALL;
            default:
                return Remote.AutotagOptionT.UNSPECIFIED;
        }
    }

    public void setDownloadTags(Remote.AutotagOptionT downloadTags) {
        jniFetchOptionsSetDownloadTags(getRawPointer(), downloadTags.getBit());
    }

    @CheckForNull
    public Proxy.Options getProxyOpts() {
        long ptr = jniFetchOptionsGetProxyOpts(getRawPointer());
        return ptr == 0 ? null : new Proxy.Options(true, ptr);
    }

    /** git_strarray custom_headers */
    @Nonnull
    public List<String> getCustomHeaders() {
        ArrayList<String> out = new ArrayList<>();
        jniFetchOptionsGetCustomHeaders(getRawPointer(), out);
        return out;
    }

    public void setCustomHeaders(@Nonnull String[] customHeaders) {
        jniFetchOptionsSetCustomHeaders(getRawPointer(), customHeaders);
    }

    public void setCallback(Remote.Callbacks callbacks) {
        jniFetchOptionsSetCallbacks(callbacks.getRawPointer(), callbacks);
    }

    public enum PruneT {
        /** Use the setting from the configuration */
        UNSPECIFIED,
        /** Force pruning on */
        PRUNE,
        /** Force pruning off */
        NO_PRUNE,
    }
}
