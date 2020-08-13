package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.github.git24j.core.Remote.jniPushOptionsFree;
import static com.github.git24j.core.Remote.jniPushOptionsGetCallbacks;
import static com.github.git24j.core.Remote.jniPushOptionsGetCustomHeaders;
import static com.github.git24j.core.Remote.jniPushOptionsGetPbParallelism;
import static com.github.git24j.core.Remote.jniPushOptionsGetProxyOpts;
import static com.github.git24j.core.Remote.jniPushOptionsGetVersion;
import static com.github.git24j.core.Remote.jniPushOptionsNew;
import static com.github.git24j.core.Remote.jniPushOptionsSetCustomHeaders;
import static com.github.git24j.core.Remote.jniPushOptionsSetPbParallelism;
import static com.github.git24j.core.Remote.jniPushOptionsSetVersion;

public class PushOptions extends CAutoReleasable {
    public final static int VERSION = 1;
    protected PushOptions(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniPushOptionsFree(cPtr);
    }

    public static PushOptions create(int version) {
        PushOptions out = new PushOptions(false, 0);
        Error.throwIfNeeded(jniPushOptionsNew(out._rawPtr, version));
        return out;
    }

    public static PushOptions createDefault() {
        return create(VERSION);
    }

    public int getVersion() {
        return jniPushOptionsGetVersion(getRawPointer());
    }

    public int getPbParallelism() {
        return jniPushOptionsGetPbParallelism(getRawPointer());
    }

    @CheckForNull
    public Remote.Callbacks getCallbacks() {
        long ptr = jniPushOptionsGetCallbacks(getRawPointer());
        if (ptr == 0) {
            return null;
        }
        return new Remote.Callbacks(true, ptr);
    }

    @CheckForNull
    public Proxy.Options getProxyOpts() {
        long ptr = jniPushOptionsGetProxyOpts(getRawPointer());
        return ptr == 0 ? null : new Proxy.Options(true, ptr);
    }

    @Nonnull
    public List<String> getCustomHeaders() {
        List<String> out = new ArrayList<>();
        jniPushOptionsGetCustomHeaders(getRawPointer(), out);
        return out;
    }

    public void setVersion(int version) {
        jniPushOptionsSetVersion(getRawPointer(), version);
    }

    public void setPbParallelism(int pbParallelism) {
        jniPushOptionsSetPbParallelism(getRawPointer(), pbParallelism);
    }

    public void setCustomHeaders(String[] customHeaders) {
        jniPushOptionsSetCustomHeaders(getRawPointer(), customHeaders);
    }
}
