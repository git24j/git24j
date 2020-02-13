package com.github.git24j.core;

public class Cert extends CAutoReleasable {
    protected Cert(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {

    }
}
