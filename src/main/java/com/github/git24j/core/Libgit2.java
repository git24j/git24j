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
    static native long optsGitOptGetMwindowSize();
    static native void optsGitOptSetMWindowMappedLimit(long mWindowMappedLimit);
    static native long optsGitOptGetMWindowMappedLimit();
    static native void optsGitOptSetMWindowFileLimit(long mWindowFileLimit);
    static native long optsGitOptGetMWindowFileLimit();
    static native String optsGitOptGetSearchPath(long sysdir);
    static native void optsGitOptSetSearchPath(long sysdir, String path);
    static native void optsGitOptSetCacheObjectLimit(long type, long size);
    static native void optsGitOptSetCacheMaxSize(long size);
    static native void optsGitOptEnableCaching(boolean enable);
    static native GitCachedMemorySaver optsGitOptGetCachedMemory();
    static native String optsGitOptGetTemplatePath();
    static native void optsGitOptSetTemplatePath(String path);
    static native void optsGitOptSetSslCertLocations(String file, String path);
    static native void optsGitOptSetUserAgent(String useragent);
    static native void optsGitOptEnableStrictObjectCreation(boolean enable);
    static native void optsGitOptEnableStrictSymbolicRefCreation(boolean enable);
    static native void optsGitOptSetSslCiphers(String sslCiphers);
    static native String optsGitOptGetUserAgent();
    static native void optsGitOptEnableOfsDelta(boolean enable);
    static native void optsGitOptEnableFsyncGitdir(boolean enable);
    static native long optsGitOptGetWindowsSharemode();
    static native void optsGitOptSetWindowsSharemode(long createFileShareMode);
    static native void optsGitOptEnableStrictHashVerification(boolean enable);
    static native void optsGitOptEnableUnsavedIndexSafety(boolean enable);
    static native void optsGitOptSetPackMaxObjects(long maxObjects);
    static native long optsGitOptGetPackMaxObjects();
    static native void optsGitOptDisablePackKeepFileChecks(boolean disable);
    static native void optsGitOptEnableHttpExpectContinue(boolean expect);
    static native void optsGitOptSetOdbPackedPriority(long priority);
    static native void optsGitOptSetOdbLoosePriority(long priority);
    static native void optsGitOptSetExtensions(String[] extensionsArray);
    static native String[] optsGitOptGetExtensions();
    static native boolean optsGitOptGetOwnerValidation();
    static native void optsGitOptSetOwnerValidation(boolean validateOwnership);
    static native String optsGitOptGetHomedir();
    static native void optsGitOptSetHomedir(String homedir);
    static native long optsGitOptGetServerConnectTimeout();
    static native void optsGitOptSetServerConnectTimeout(long timeout);
    static native long optsGitOptGetServerTimeout();
    static native void optsGitOptSetServerTimeout(long timeout);
    // git_libgit2_opts() end
}
