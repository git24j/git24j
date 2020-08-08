package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.github.git24j.core.Remote.jniFetchOptionsGetCallbacks;
import static com.github.git24j.core.Remote.jniFetchOptionsGetCustomHeaders;
import static com.github.git24j.core.Remote.jniFetchOptionsGetDownloadTags;
import static com.github.git24j.core.Remote.jniFetchOptionsGetProxyOpts;
import static com.github.git24j.core.Remote.jniFetchOptionsGetPrune;
import static com.github.git24j.core.Remote.jniFetchOptionsGetUpdateFetchhead;
import static com.github.git24j.core.Remote.jniFetchOptionsGetVersion;
import static com.github.git24j.core.Remote.jniFetchOptionsSetCustomHeaders;
import static com.github.git24j.core.Remote.jniFetchOptionsSetDownloadTags;
import static com.github.git24j.core.Remote.jniFetchOptionsSetPrune;
import static com.github.git24j.core.Remote.jniFetchOptionsSetUpdateFetchhead;
import static com.github.git24j.core.Remote.jniFetchOptionsSetVersion;

public class FetchOptions extends CAutoReleasable {
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
        Error.throwIfNeeded(Remote.jniFetchOptionsNew(opts._rawPtr, version));
        return opts;
    }

    public int getVersion() {
        return jniFetchOptionsGetVersion(getRawPointer());
    }

    /**git_remote_callbacks callbacks*/
    @Nonnull
    public Remote.Callbacks getCallbacks() {
        long ptr = jniFetchOptionsGetCallbacks(getRawPointer());
        return new Remote.Callbacks(true, ptr);
    }

    public int getPrune() {
        return jniFetchOptionsGetPrune(getRawPointer());
    }

    public int getUpdateFetchhead() {
        return jniFetchOptionsGetUpdateFetchhead(getRawPointer());
    }

    // TODO: return enum instead
    public int getDownloadTags() {
        return jniFetchOptionsGetDownloadTags(getRawPointer());
    }

    public ProxyOptions getProxyOpts() {
        long ptr = jniFetchOptionsGetProxyOpts(getRawPointer());
        return new ProxyOptions(true, ptr);
    }

    /**git_strarray custom_headers*/
    public List<String> getCustomHeaders() {
        ArrayList<String> out = new ArrayList<>();
        jniFetchOptionsGetCustomHeaders(getRawPointer(), out);
        return out;
    }

    public void setVersion(int version) {
        jniFetchOptionsSetVersion(getRawPointer(), version);
    }

    public void setPrune(int prune) {
        jniFetchOptionsSetPrune(getRawPointer(), prune);
    }

    public void setUpdateFetchhead(int updateFetchhead) {
        jniFetchOptionsSetUpdateFetchhead(getRawPointer(), updateFetchhead);
    }

    public void setDownloadTags(int downloadTags) {
        jniFetchOptionsSetDownloadTags(getRawPointer(), downloadTags);
    }

    public void setCustomHeaders(@Nonnull String[] customHeaders) {
        jniFetchOptionsSetCustomHeaders(getRawPointer(), customHeaders);
    }
}
