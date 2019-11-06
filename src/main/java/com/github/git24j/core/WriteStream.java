package com.github.git24j.core;

public class WriteStream extends CAutoCloseable {
    public WriteStream(long rawPointer) {
        super(rawPointer);
    }

    /** TODO: figure out what's meaning of the return. */
    static native int jniWrite(long wsPtr, byte[] content);

    static native int jniFree(long wsPtr);

    static native int jniClose(long wsPtr);

    @Override
    public void close() {
        jniClose(getRawPointer());
    }

    public int write(byte[] content) {
        return jniWrite(getRawPointer(), content);
    }

    public void free() {
        jniFree(getRawPointer());
    }
}
