package com.github.git24j.core;

import java.util.EnumSet;

public class Libgit2 {
    static native void jniShadowFree(long ptr);

    public static native void init();

    public static native void shutdown();

    public static native Version version();

    public static native int features();

    /** Wapper of features that returns an enum set. */
    public static EnumSet<GitFeature> featuresSet() {
        return GitFeature.valuesOf(features());
    }

    // git_libgit2_opts() start
    static native void optsGitOptSetMwindowSize(long mWindowSize);
    // git_libgit2_opts() end
}
