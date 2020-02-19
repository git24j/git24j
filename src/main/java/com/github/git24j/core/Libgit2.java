package com.github.git24j.core;

import java.util.EnumSet;

public class Libgit2 {
    static native void init();

    static native void shutdown();

    static native Version version();

    static native void jniShadowFree(long ptr);

    static native int features();
    // TODO: git_libgit2_opts
    /// static native int opts();

    /** Wapper of features that returns an enum set. */
    public static EnumSet<GitFeature> featuresSet() {
        return GitFeature.valuesOf(features());
    }
}
