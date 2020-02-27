package com.github.git24j.core;

public class Refdb extends CAutoCloseable {
    public Refdb(long rawPointer) {
        super(rawPointer);
    }

    @Override
    protected void releaseOnce(long cPtr) {
        // FIXME: add jniFree here
    }
}
