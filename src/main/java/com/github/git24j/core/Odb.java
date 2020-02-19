package com.github.git24j.core;

public class Odb extends CAutoCloseable {
    public Odb(long rawPointer) {
        super(rawPointer);
    }

    @Override
    protected void releaseOnce(long cPtr) {
        // FIXME: add jniFree here
        // Libgit2.jniShadowFree(cPtr);
    }
}
