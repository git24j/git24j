package com.github.git24j.core;

public class Refspec extends CAutoReleasable {
    protected Refspec(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {}
}
