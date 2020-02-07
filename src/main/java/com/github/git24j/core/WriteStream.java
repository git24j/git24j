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
        if (_rawPtr.get() > 0) {
            long ptr = _rawPtr.getAndSet(0);
            jniClose(ptr);
            jniFree(ptr);
        }
    }

    public int write(byte[] content) {
        return jniWrite(getRawPointer(), content);
    }

    public void free() {
        if (_rawPtr.get() > 0) {
            jniFree(_rawPtr.getAndSet(0));
        }
    }
}
