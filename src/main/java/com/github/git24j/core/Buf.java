package com.github.git24j.core;

import java.util.Optional;

public class Buf {
    private String ptr;
    private int reserved;
    private int size;

    /** Get internal buffer, generally only the substr up to size is meaningful. */
    String getPtr() {
        return ptr;
    }

    public void setPtr(String ptr) {
        this.ptr = ptr;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Optional<String> getString() {
        if (ptr == null || size == 0) {
            return Optional.empty();
        }
        return Optional.of(ptr.substring(0, size));
    }

    @Override
    public String toString() {
        return getString().orElse("");
    }
}
