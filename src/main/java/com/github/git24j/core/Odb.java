package com.github.git24j.core;

public class Odb extends CAutoCloseable {
    public Odb(long rawPointer) {
        super(rawPointer);
    }

    @Override
    public void close() {
        // TODO: implement this
    }
}
