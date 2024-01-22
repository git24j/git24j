package com.github.git24j.core;

import java.util.EnumSet;

public class Libgit2 {
    static native void jniShadowFree(long ptr);

    public static native void init();

    public static native void shutdown();

    public static native Version version();

    public static native int features();
    // TODO: git_libgit2_opts
    public static native int opts();

    /** Wapper of features that returns an enum set. */
    public static EnumSet<GitFeature> featuresSet() {
        return GitFeature.valuesOf(features());
    }
}
