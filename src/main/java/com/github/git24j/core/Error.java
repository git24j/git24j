package com.github.git24j.core;

/** Delegate git_error_* methods. */
public class Error {
    static native void jniClear();

    static native GitException jniLast();

    static native void jniSetStr(int klass, String message);

    /**
     * Helper to throw last GitException if error code is not zero
     *
     * @param error 0 or an error code
     * @throws GitException if there was an error recorded
     */
    public static void throwIfNeeded(int error) {
        if (error < 0) {
            GitException e = jniLast();
            if (e != null) {
                e.setCode(error);
                throw e;
            }
        }
    }
}
