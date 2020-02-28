package com.github.git24j.core;

public class BlameOptions extends CAutoReleasable {
    public static final int VERSION = 1;

    protected BlameOptions(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        Libgit2.jniShadowFree(cPtr);
    }

    public static BlameOptions init(int version) {
        BlameOptions out = new BlameOptions(false, 0);
        Blame.jniOptionsNew(out._rawPtr, version);
        return out;
    }
}
