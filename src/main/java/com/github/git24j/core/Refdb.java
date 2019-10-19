package com.github.git24j.core;

public class Refdb extends CAutoCloseable {
    public Refdb(long rawPointer) {
        super(rawPointer);
    }

    @Override
    public void close() {
        // TODO: add jniFree
    }
}
