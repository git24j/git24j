package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

/** Base bridge that manages raw c pointer. */
public abstract class CAutoCloseable implements AutoCloseable {
    /** C Pointer. */
    protected final AtomicLong _rawPtr = new AtomicLong();

    public CAutoCloseable(long rawPointer) {
        _rawPtr.set(rawPointer);
    }

    /**
     * Get raw pointer of the repo.
     *
     * @return pointer value in long
     * @throws IllegalStateException if repository has already been closed.
     */
    long getRawPointer() {
        long ptr = _rawPtr.get();
        if (ptr == 0) {
            throw new IllegalStateException(
                    "Object has invalid memory address, likely it has been closed.");
        }
        return ptr;
    }
}
