package com.github.git24j.core;

public class Checkout {

    /**
     * Checkout options structure
     *
     * Initialize with `GIT_CHECKOUT_OPTIONS_INIT`. Alternatively, you can
     * use `git_checkout_init_options`.
     *
     */
    public static class Options extends CAutoReleasable {
        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }
    }
}
