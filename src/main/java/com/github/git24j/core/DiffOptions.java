package com.github.git24j.core;

import static com.github.git24j.core.Diff.jniFreeOptions;
import static com.github.git24j.core.Diff.*;

/**
 * Flags for diff options. A combination of these flags can be passed in via the `flags` value
 * in the `git_diff_options`.
 */
public class DiffOptions extends CAutoReleasable {
    public static final int CURRENT_VERSION = 1;

    DiffOptions(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Create and initialize a Diff Options object
     *
     * <p>Initializes a `git_diff_options` with default values. Equivalent to creating an
     * instance with GIT_DIFF_OPTIONS_INIT.
     *
     * @param version The struct version; pass `GIT_DIFF_OPTIONS_VERSION`.
     * @throws GitException git errors
     */
    public static DiffOptions create(int version) {
        DiffOptions opts = new DiffOptions(false, 0);
        Error.throwIfNeeded(jniInitOptions(opts._rawPtr, version));
        return opts;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFreeOptions(cPtr);
    }
}
