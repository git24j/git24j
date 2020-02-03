package com.github.git24j.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/** Memory representation of a set of config files */
public class Config extends CAutoCloseable {

    public Config(long rawPointer) {
        super(rawPointer);
    }

    public void foreachMatch(String regexp, ForeachCb foreachCb) {
        jniForeachMatch(getRawPointer(), regexp, entryPtr -> foreachCb.accept(new Entry(entryPtr)));
    }

    public class Entry {
        private AtomicLong _rawPtr = new AtomicLong();

        public Entry(long ptr) {
            _rawPtr.set(ptr);
        }

        @Override
        protected void finalize() throws Throwable {
            jniEntryFree(_rawPtr.getAndSet(0));
            super.finalize();
        }
    }

    @FunctionalInterface
    public interface ForeachCb {
        int accept(Entry entry);
    }

    @FunctionalInterface
    private interface CallbackJ {
        int accept(long entryPtr);
    }
    /**
     * Locate the path to the global configuration file
     *
     * @return path where global configuration is stored.
     */
    public static Optional<Path> findGlobal() {
        Buf buf = new Buf();
        int e = jniFindGlobal(buf);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return Optional.empty();
        }
        return Optional.of(Paths.get(buf.toString()));
    }

    @Override
    public void close() {
        if (_rawPtr.get() > 0) {
            jniFree(_rawPtr.getAndSet(0));
        }
    }

    /**
     * Get the value of a string config variable.
     *
     * @param name the variable's name
     * @return value of a string config variable
     */
    public Optional<String> getString(String name) {
        Buf buf = new Buf();
        Error.throwIfNeeded(jniGetStringBuf(buf, getRawPointer(), name));
        return buf.getString();
    }

    /** void git_config_entry_free(git_config_entry *entry); */
    static native void jniEntryFree(long entry);

    /** int git_config_find_global(git_buf *out); */
    static native int jniFindGlobal(Buf out);

    /** int git_config_find_xdg(git_buf *out); */
    static native int jniFindXdg(Buf out);

    /**
     * Locate the path to the global configuration file
     *
     * <p>The user or global configuration file is usually located in `$HOME/.gitconfig`.
     *
     * <p>This method will try to guess the full path to that file, if the file exists. The returned
     * path may be used on any `git_config` call to load the global configuration file.
     *
     * <p>This method will not guess the path to the xdg compatible config file
     * (.config/git/config).
     *
     * @return the path to the global configuration file or empty
     * @throws GitException git errors
     */
    public static Optional<Path> findXdg() {
        Buf buf = new Buf();
        int e = jniFindXdg(buf);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return Optional.empty();
        }
        return Optional.of(Paths.get(buf.toString()));
    }

    /** int git_config_find_system(git_buf *out); */
    static native int jniFindSystem(Buf out);

    /**
     * Locate the path to the system configuration file
     *
     * <p>If /etc/gitconfig doesn't exist, it will look for %PROGRAMFILES%\Git\etc\gitconfig.
     *
     * @return found config file
     * @throws GitException git errors
     */
    public static Optional<Path> findSystem() {
        Buf buf = new Buf();
        int e = jniFindSystem(buf);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return Optional.empty();
        }
        return Optional.of(Paths.get(buf.toString()));
    }

    /** int git_config_find_programdata(git_buf *out); */
    static native int jniFindProgramdata(Buf out);

    /**
     * Locate the path to the configuration file in ProgramData
     *
     * <p>Look for the file in %PROGRAMDATA%\Git\config used by portable git.
     *
     * @return the path to the configuration file in ProgramData
     * @throws GitException git errors
     */
    public static Optional<Path> findProgramdata() {
        Buf buf = new Buf();
        int e = jniFindProgramdata(buf);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return Optional.empty();
        }
        return Optional.of(Paths.get(buf.toString()));
    }

    /** int git_config_open_default(git_config **out); */
    static native int jniOpenDefault(AtomicLong out);

    public static Config openDefault() {
        Config cfg = new Config(0);
        Error.throwIfNeeded(jniOpenDefault(cfg._rawPtr));
        return cfg;
    }

    /** int git_config_new(git_config **out); */
    static native int jniNew(AtomicLong out);

    public static Config newConfig() {
        Config cfg = new Config(0);
        Error.throwIfNeeded(jniNew(cfg._rawPtr));
        return cfg;
    }

    /**
     * int git_config_add_file_ondisk(git_config *cfg, const char *path, git_config_level_t level,
     * const git_repository *repo, int force);
     */
    static native int jniAddFileOndisk(long cfg, String path, int level, long repoPtr, int force);

    public enum ConfigLevel {
        /** System-wide on Windows, for compatibility with portable git */
        PROGRAMDATA(1),

        /** System-wide configuration file; /etc/gitconfig on Linux systems */
        SYSTEM(2),

        /** XDG compatible configuration file; typically ~/.config/git/config */
        XDG(3),

        /** User-specific configuration file (also called Global configuration
         * file); typically ~/.gitconfig
         */
        GLOBAL(4),

        /** Repository specific configuration file; $WORK_DIR/.git/config on
         * non-bare repos
         */
        LOCAL(5),

        /** Application specific configuration file; freely defined by applications
         */
        APP(6),

        /** Represents the highest level available config file (i.e. the most
         * specific config file available that actually is loaded)
         */
        HIGHEST(-1),
        ;
        private final int _code ;

        ConfigLevel(int code) {
            _code = code;
        }
    }
    /**
     * Add an on-disk config file instance to an existing config
     *
     * The on-disk file pointed at by `path` will be opened and
     * parsed; it's expected to be a native Git config file following
     * the default Git config syntax (see man git-config).
     *
     * If the file does not exist, the file will still be added and it
     * will be created the first time we write to it.
     *
     * Note that the configuration object will free the file
     * automatically.
     *
     * Further queries on this config object will access each
     * of the config file instances in order (instances with
     * a higher priority level will be accessed first).
     *
     * @param path path to the configuration file to add
     * @param level the priority level of the backend
     * @param force replace config file at the given priority level
     * @param repo optional repository to allow parsing of
     *  conditional includes
     * @return 0 on success, GIT_EEXISTS when adding more than one file
     *  for a given priority level (and force_replace set to 0),
     *  GIT_ENOTFOUND when the file doesn't exist or error code
     */
    public void addFileOndisk(Path path, ConfigLevel level, Repository repo, boolean force) {

    }

    /** int git_config_open_ondisk(git_config **out, const char *path); */
    static native int jniOpenOndisk(AtomicLong out, String path);

    /**
     * int git_config_open_level(git_config **out, const git_config *parent, git_config_level_t
     * level);
     */
    static native int jniOpenLevel(AtomicLong out, long parent, int level);

    /** int git_config_open_global(git_config **out, git_config *config); */
    static native int jniOpenGlobal(AtomicLong out, long config);

    /** int git_config_snapshot(git_config **out, git_config *config); */
    static native int jniSnapshot(AtomicLong out, long config);

    /** void git_config_free(git_config *cfg); */
    static native void jniFree(long cfg);

    /**
     * int git_config_get_entry(git_config_entry **out, const git_config *cfg, const char *name);
     */
    static native int jniGetEntry(AtomicLong out, long cfg, String name);

    /** int git_config_get_int32(int32_t *out, const git_config *cfg, const char *name); */
    static native int jniGetInt32(AtomicInteger out, long cfg, String name);

    /** int git_config_get_int64(int64_t *out, const git_config *cfg, const char *name); */
    static native int jniGetInt64(AtomicLong out, long cfg, String name);

    /** int git_config_get_bool(int *out, const git_config *cfg, const char *name); */
    static native int jniGetBool(AtomicInteger out, long cfg, String name);

    /** int git_config_get_path(git_buf *out, const git_config *cfg, const char *name); */
    static native int jniGetPath(Buf out, long cfg, String name);

    /** int git_config_get_string(const char **out, const git_config *cfg, const char *name); */
    static native int jniGetString(AtomicReference<String> out, long cfg, String name);

    /** int git_config_get_string_buf(git_buf *out, const git_config *cfg, const char *name); */
    static native int jniGetStringBuf(Buf out, long cfg, String name);

    /**
     * int git_config_get_multivar_foreach(const git_config *cfg, const char *name, const char
     * *regexp, git_config_foreach_cb callback, void *payload);
     */
    static native int jniGetMultivarForeach(
            long cfg, String name, String regexp, CallbackJ callback);

    /**
     * int git_config_multivar_iterator_new(git_config_iterator **out, const git_config *cfg, const
     * char *name, const char *regexp);
     */
    static native int jniMultivarIteratorNew(AtomicLong out, long cfg, String name, String regexp);

    /** int git_config_next(git_config_entry **entry, git_config_iterator *iter); */
    static native int jniNext(AtomicLong entry, long iter);

    /** void git_config_iterator_free(git_config_iterator *iter); */
    static native void jniIteratorFree(long iter);

    /** int git_config_set_int32(git_config *cfg, const char *name, int32_t value); */
    static native int jniSetInt32(long cfg, String name, int value);

    /** int git_config_set_int64(git_config *cfg, const char *name, int64_t value); */
    static native int jniSetInt64(long cfg, String name, long value);

    /** int git_config_set_bool(git_config *cfg, const char *name, int value); */
    static native int jniSetBool(long cfg, String name, int value);

    /** int git_config_set_string(git_config *cfg, const char *name, const char *value); */
    static native int jniSetString(long cfg, String name, String value);

    /**
     * int git_config_set_multivar(git_config *cfg, const char *name, const char *regexp, const char
     * *value);
     */
    static native int jniSetMultivar(long cfg, String name, String regexp, String value);

    /** int git_config_delete_entry(git_config *cfg, const char *name); */
    static native int jniDeleteEntry(long cfg, String name);

    /** int git_config_delete_multivar(git_config *cfg, const char *name, const char *regexp); */
    static native int jniDeleteMultivar(long cfg, String name, String regexp);

    /**
     * int git_config_foreach(const git_config *cfg, git_config_foreach_cb callback, void *payload);
     */
    static native int jniForeach(long cfg, CallbackJ callback);

    /** int git_config_iterator_new(git_config_iterator **out, const git_config *cfg); */
    static native int jniIteratorNew(AtomicLong out, long cfg);

    /**
     * int git_config_iterator_glob_new(git_config_iterator **out, const git_config *cfg, const char
     * *regexp);
     */
    static native int jniIteratorGlobNew(AtomicLong out, long cfg, String regexp);

    /**
     * int git_config_foreach_match(const git_config *cfg, const char *regexp, git_config_foreach_cb
     * callback, void *payload);
     */
    static native int jniForeachMatch(long cfg, String regexp, CallbackJ callback);

    /** int git_config_parse_bool(int *out, const char *value); */
    static native int jniParseBool(AtomicInteger out, String value);

    /** int git_config_parse_int32(int32_t *out, const char *value); */
    static native int jniParseInt32(AtomicInteger out, String value);

    /** int git_config_parse_int64(int64_t *out, const char *value); */
    static native int jniParseInt64(AtomicLong out, String value);

    /** int git_config_parse_path(git_buf *out, const char *value); */
    static native int jniParsePath(Buf out, String value);

    /**
     * int git_config_backend_foreach_match(git_config_backend *backend, const char *regexp,
     * git_config_foreach_cb callback, void *payload);
     */
    static native int jniBackendForeachMatch(long backend, String regexp, CallbackJ callback);

    /** int git_config_lock(git_transaction **tx, git_config *cfg); */
    static native int jniLock(AtomicLong tx, long cfg);
}
