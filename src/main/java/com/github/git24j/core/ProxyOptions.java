package com.github.git24j.core;

public class ProxyOptions extends CAutoReleasable {
    protected ProxyOptions(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {

    }
}
