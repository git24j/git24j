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
    /* note:
        all these methods may throws GitException,
        but I saw in this project other methods, almost no one wrote throws part
        on method signature, so I don't wrote throws on method signature too.
        better use try catch when calling below methods.
    * */
    public static native void optsGitOptSetMwindowSize(long mWindowSize);
    public static native long optsGitOptGetMwindowSize();
    public static native void optsGitOptSetMWindowMappedLimit(long mWindowMappedLimit);
    public static native long optsGitOptGetMWindowMappedLimit();
    public static native void optsGitOptSetMWindowFileLimit(long mWindowFileLimit);
    public static native long optsGitOptGetMWindowFileLimit();
    public static native String optsGitOptGetSearchPath(int configLevel);
    public static native void optsGitOptSetSearchPath(int configLevel, String path);
    public static native void optsGitOptSetCacheObjectLimit(int type, long size);
    public static native void optsGitOptSetCacheMaxSize(long size);
    public static native void optsGitOptEnableCaching(boolean enable);
    public static native GitCachedMemorySaver optsGitOptGetCachedMemory();
    public static native String optsGitOptGetTemplatePath();
    public static native void optsGitOptSetTemplatePath(String path);
    public static native void optsGitOptSetSslCertLocations(String file, String path);
    public static native void optsGitOptSetUserAgent(String useragent);
    public static native void optsGitOptEnableStrictObjectCreation(boolean enable);
    public static native void optsGitOptEnableStrictSymbolicRefCreation(boolean enable);
    public static native void optsGitOptSetSslCiphers(String sslCiphers);
    public static native String optsGitOptGetUserAgent();
    public static native void optsGitOptEnableOfsDelta(boolean enable);
    public static native void optsGitOptEnableFsyncGitdir(boolean enable);
    public static native long optsGitOptGetWindowsSharemode();
    public static native void optsGitOptSetWindowsSharemode(long createFileShareMode);
    public static native void optsGitOptEnableStrictHashVerification(boolean enable);
    public static native void optsGitOptEnableUnsavedIndexSafety(boolean enable);
    public static native void optsGitOptSetPackMaxObjects(long maxObjects);
    public static native long optsGitOptGetPackMaxObjects();
    public static native void optsGitOptDisablePackKeepFileChecks(boolean disable);
    public static native void optsGitOptEnableHttpExpectContinue(boolean expect);
    public static native void optsGitOptSetOdbPackedPriority(long priority);
    public static native void optsGitOptSetOdbLoosePriority(long priority);
    public static native void optsGitOptSetExtensions(String[] extensionsArray);
    public static native String[] optsGitOptGetExtensions();
    public static native boolean optsGitOptGetOwnerValidation();
    public static native void optsGitOptSetOwnerValidation(boolean validateOwnership);
    public static native String optsGitOptGetHomedir();
    public static native void optsGitOptSetHomedir(String homedir);
    public static native long optsGitOptGetServerConnectTimeout();
    public static native void optsGitOptSetServerConnectTimeout(long timeout);
    public static native long optsGitOptGetServerTimeout();
    public static native void optsGitOptSetServerTimeout(long timeout);

    public static class OptsCons {
        /*

    typedef enum {
            //System-wide on Windows, for compatibility with portable git
            GIT_CONFIG_LEVEL_PROGRAMDATA = 1,

            // System-wide configuration file; /etc/gitconfig on Linux systems
            GIT_CONFIG_LEVEL_SYSTEM = 2,

            //XDG compatible configuration file; typically ~/.config/git/config
            GIT_CONFIG_LEVEL_XDG = 3,

            // User-specific configuration file (also called Global configuration
            // file); typically ~/.gitconfig
            GIT_CONFIG_LEVEL_GLOBAL = 4,

            // Repository specific configuration file; $WORK_DIR/.git/config on
            // non-bare repos
            GIT_CONFIG_LEVEL_LOCAL = 5,

            // Application specific configuration file; freely defined by applications
            GIT_CONFIG_LEVEL_APP = 6,

            // Represents the highest level available config file (i.e. the most
            // specific config file available that actually is loaded)
            GIT_CONFIG_HIGHEST_LEVEL = -1
        } git_config_level_t;

         */
        public static class ConfigLevel {
            public static final int programData = 1;
            public static final int system = 2;
            public static final int xdg = 3;
            public static final int global = 4;
            // Below config levels doesn't support set by optsGitOptSetSearchPath() method
//            public static final int local = 5;
//            public static final int app = 6;
//            public static final int highest = -1;
        }
        /*
        // Basic type (loose or packed) of any Git object.
        typedef enum {
            GIT_OBJECT_ANY =      -2, // Object can be any of the following
            GIT_OBJECT_INVALID =  -1, // Object is invalid.
            GIT_OBJECT_COMMIT =    1, // A commit object.
            GIT_OBJECT_TREE =      2, // A tree (directory listing) object.
            GIT_OBJECT_BLOB =      3, // A file revision object.
            GIT_OBJECT_TAG =       4, // An annotated tag object.
            GIT_OBJECT_OFS_DELTA = 6, // A delta, base is given by an offset.
            GIT_OBJECT_REF_DELTA = 7  // A delta, base is given by object id.
        } git_object_t;

         */
        public static class ObjectType {
            public static final int any = -2;
            public static final int invalid = -1;
            public static final int commit = 1;
            public static final int tree = 2;
            public static final int blob = 3;
            public static final int tag = 4;
            public static final int ofsDelta = 6;
            public static final int refDelta = 7;
        }
    }
    // git_libgit2_opts() end
}
