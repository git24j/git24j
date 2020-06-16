package com.github.git24j.core;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;

/**
 * Base class that auto release native pointers, use this when the pointer carried in an object has
 * the same life cycle as the object. Note: If the pointer is weakly referenced, the finalizer won't
 * try to free the pointer.
 */
public abstract class CAutoReleasable {
    protected final AtomicLong _rawPtr = new AtomicLong();
    protected final boolean _isWeak;

    protected CAutoReleasable(boolean isWeak, long rawPtr) {
        _isWeak = isWeak;
        _rawPtr.set(rawPtr);
    }

    /** Call this to actual free c pointer, this will be called once this is not a weak ref. */
    protected abstract void freeOnce(long cPtr);

    @Override
    protected void finalize() throws Throwable {
        if (!_isWeak && _rawPtr.get() > 0) {
            System.out.printf("deallocating<%d>: %s %n", _rawPtr.get(), this.getClass().asSubclass(this.getClass()));
            freeOnce(_rawPtr.getAndSet(0));
        }
        super.finalize();
    }

    long getRawPointer() {
        long ptr = _rawPtr.get();
        if (_isWeak) {
            return ptr;
        }
        if (ptr == 0) {
            throw new IllegalStateException("Underlying c object has been released");
        }
        return ptr;
    }

    boolean isNull() {
        return _rawPtr.get() == 0;
    }

    static long rawPtr(@Nullable CAutoReleasable obj) {
        return obj == null ? 0 : obj.getRawPointer();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CAutoReleasable that = (CAutoReleasable) o;
        return Objects.equals(_rawPtr, that._rawPtr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_rawPtr);
    }
}
