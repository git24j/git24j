package com.github.git24j.core;

public class Internals {
    @FunctionalInterface
    interface JFCallback {
        int accept(long ptr, float f);
    }

    @FunctionalInterface
    interface JJCallback {
        int accept(long ptr1, long ptr2);
    }

    @FunctionalInterface
    interface JJJCallback {
        int accept(long ptr1, long ptr2, long ptr3);
    }
}
