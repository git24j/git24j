package com.github.git24j.core;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

public class Index implements AutoCloseable {
    final AtomicLong _rawPtr = new AtomicLong();

    /** int git_index_open(git_index **out, const char *index_path); */
    // JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniOpen)(JNIEnv *env, jclass obj, jobject
    // outIndexPtr, jstring indexPath)
    static native int jniOpen(AtomicLong outIndexPtr, String indexPath);

    public Index() {}

    public Index(long rawPtr) {
        this._rawPtr.set(rawPtr);
    }

    long getRawPointer() {
        long ptr = _rawPtr.get();
        if (ptr == 0) {
            throw new IllegalStateException("Repository has been closed");
        }
        return ptr;
    }

    public enum Capability implements IBitEnum {
        IGNORE_CASE(1),
        NO_FILEMODE(1 << 1),
        NO_SYMLINKS(1 << 2),
        FROM_OWNER(-1),
        ;

        private final int bit;

        Capability(int bit) {
            this.bit = bit;
        }

        @Override
        public int getBit() {
            return bit;
        }
    }
    /**
     * Create a new bare Git index object as a memory representation of the Git index file in
     * 'indexPath', without a repository to back it.
     *
     * <p>Since there is no ODB or working directory behind this index, any Index methods which rely
     * on these (e.g. index_add_bypath) will fail with the GIT_ERROR error code.
     *
     * <p>If you need to access the index of an actual repository, use the `git_repository_index`
     * wrapper.
     *
     * <p>The index must be freed once it's no longer in use.
     *
     * @param indexPath the path to the index file in disk
     * @return the pointer for the new index
     * @throws GitException git errors
     */
    public static Index open(String indexPath) {
        Index outIdx = new Index();
        Error.throwIfNeeded(jniOpen(outIdx._rawPtr, indexPath));
        return outIdx;
    }

    /** git_repository * git_index_owner(const git_index *index); */
    // JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniOwner)(JNIEnv *env, jclass obj, jlong indexPtr);
    static native long jniOwner(long idxPtr);

    /**
     * Get the repository this index relates to
     *
     * @return the repository that owns the index
     */
    public Repository owner() {
        long ptr = jniOwner(getRawPointer());
        if (ptr == 0) {
            throw new GitException(GitException.ErrorClass.INDEX, "Index owner not found");
        }
        return new Repository(ptr);

    }

    static native int jniCaps(long idxPtr);

    /**
     * Read index capabilities flags.
     *
     * @return A combination of Capability enums
     */
    public EnumSet<Capability> caps() {
        int c = jniCaps(getRawPointer());
        if (c < 0) {
            return EnumSet.of(Capability.FROM_OWNER);
        }
        return IBitEnum.parse(c, Capability.class);
    }

    static native int jniSetCaps(long idxPtr, int caps);

    /**
     * Set index capabilities flags.
     *
     * If you pass `GIT_INDEX_CAPABILITY_FROM_OWNER` for the caps, then
     * capabilities will be read from the config of the owner object,
     * looking at `core.ignorecase`, `core.filemode`, `core.symlinks`.
     *
     * @param caps A combination of Index.Capability values
     * @throws GitException git errors
     */
    public void setCaps(EnumSet<Capability> caps) {
        if (caps.contains(Capability.FROM_OWNER)) {
            Error.throwIfNeeded(jniSetCaps(getRawPointer(), Capability.FROM_OWNER.getBit()));
            return;
        }
        Error.throwIfNeeded(jniSetCaps(getRawPointer(), IBitEnum.bitOrAll(caps)));
    }

    static native int jniVersion(long indexPtr);
    /**
     * Get index on-disk version.
     *
     * Valid return values are 2, 3, or 4.  If 3 is returned, an index
     * with version 2 may be written instead, if the extension data in
     * version 3 is not necessary.
     *
     * @return the index version
     */
    public int version() {
        return jniVersion(getRawPointer());
    }

    static native int jniSetVersion(long indexPtr, int version);
    /**
     * Set index on-disk version.
     *
     * Valid values are 2, 3, or 4.  If 2 is given, git_index_write may
     * write an index with version 3 instead, if necessary to accurately
     * represent the index.
     *
     * @param version The new version number
     * @throws GitException git errors
     */
    public void setVersion(int version) {
        Error.throwIfNeeded(jniSetVersion(getRawPointer(), version));
    }

    static native int jniUpdateAll(long idxPtr, String[] pathSpec, Callback callback);

    /**
     * Update all index entries, which have already been included and match the {@code pathSpec} pattern),
     * to line up with the working directory.
     *
     * <pre>
     *  # show which files are included in existing index,
     *  $ git ls-files --cached
     *  # make a trivial change to one of the file, say "README.md"
     *  $ echo "" >> README.md
     *  # update the index: `updateAll(List.of("README.md", (path, pathSpec)-> {})`
     *  $ git update-index README.md
     * <pre/>
     *
     * @param pathSpec array of path patterns
     * @param callback notification callback for each updated path (also gets index of matching
     *     pathspec entry); can be NULL; return 0 to add, >0 to skip, < 0 to abort scan.
     * @throws GitException git error.
     */
    public void updateAll(List<String> pathSpec, Callback callback) {
        Error.throwIfNeeded(jniUpdateAll(getRawPointer(), pathSpec.toArray(new String[0]), callback));
    }

    static native int jniAdd(long idxPtr, Entry sourceEntry);

    /**
     * Add or update an index entry from an in-memory struct.
     *
     * @param sourceEntry new entry object
     * @throws GitException git error
     */
    public void add(Entry sourceEntry) {
        Error.throwIfNeeded(jniAdd(getRawPointer(), sourceEntry));
    }

    static native int jniAddAll(long idxPtr, String[] pathSpec, int flags, Callback callback);
    /**
     * Add or update index entries matching files in the working directory.
     *
     * @param pathSpec array of path patterns
     * @param flags combination of git_index_add_option_t flags
     * @param callback notification callback for each added/updated path (also gets index of
     *     matching pathspec entry); can be NULL; return 0 to add, >0 to skip, < 0 to abort scan.
     * @throws GitException git error
     */
    public void addAll(
            String[] pathSpec, EnumSet<AddOption> flags, BiConsumer<String, String> callback) {
        Error.throwIfNeeded(
                jniAddAll(getRawPointer(), pathSpec, IBitEnum.bitOrAll(flags), callback::accept));
    }

    static native int jniWrite(long idxPtr);
    /**
     * Write an existing index object from memory back to disk using an atomic file lock.
     *
     * @throws GitException git error.
     */
    public void write() {
        Error.throwIfNeeded(jniWrite(getRawPointer()));
    }

    static native String jniPath(long indexPtr);

    public String path() {
        return jniPath(getRawPointer());
    }

    static native void jniFree(long idxPtr);

    /** Delegate {@code git_index_free} Free an existing index object. */
    @Override
    public void close() {
        jniFree(getRawPointer());
        _rawPtr.set(0);
    }

    static native int jniAddByPath(long idxPtr, String path);

    /**
     * Add or update an index entry from a file on disk
     *
     * @param path filename to add
     * @throws GitException git error
     */
    public void add(String path) {
        Error.throwIfNeeded(jniAddByPath(getRawPointer(), path));
    }

    public enum AddOption implements IBitEnum {
        DEFAULT(0),
        FORCE(1 << 0),
        DISABLE_PATHSPEC_MATCH(1 << 1),
        CHECK_PATHSPEC(1 << 2),
        ;

        private final int bit;

        AddOption(int bit) {
            this.bit = bit;
        }

        @Override
        public int getBit() {
            return bit;
        }
    }

    public interface Callback {
        void accept(String path, String pathSpec);
    }

    public static class Entry {
        private final int ctimeSec;
        private final int ctimeNanoSec;
        private final int mtimeSec;
        private final int mtimeNanoSec;
        private final int dev;
        private final int mode;
        private final int uid;
        private final int gid;
        private final int fileSize;
        private final String oid;
        private final int flags;
        private final int flagsExtended;
        private final String path;

        public Entry(
                int ctimeSec,
                int ctimeNanoSec,
                int mtimeSec,
                int mtimeNanoSec,
                int dev,
                int mode,
                int uid,
                int gid,
                int fileSize,
                String oid,
                int flags,
                int flagsExtended,
                String path) {
            this.ctimeSec = ctimeSec;
            this.ctimeNanoSec = ctimeNanoSec;
            this.mtimeSec = mtimeSec;
            this.mtimeNanoSec = mtimeNanoSec;
            this.dev = dev;
            this.mode = mode;
            this.uid = uid;
            this.gid = gid;
            this.fileSize = fileSize;
            this.oid = oid;
            this.flags = flags;
            this.flagsExtended = flagsExtended;
            this.path = path;
        }

        public static class EntryBuilder {
            private int ctimeSec;
            private int ctimeNanoSec;
            private int mtimeSec;
            private int mtimeNanoSec;
            private int dev;
            private int mode;
            private int uid;
            private int gid;
            private int fileSize;
            private String oid;
            private int flags;
            private int flagsExtended;
            private String path;

            public EntryBuilder setCtimeSec(int ctimeSec) {
                this.ctimeSec = ctimeSec;
                return this;
            }

            public EntryBuilder setCtimeNanoSec(int ctimeNanoSec) {
                this.ctimeNanoSec = ctimeNanoSec;
                return this;
            }

            public EntryBuilder setMtimeSec(int mtimeSec) {
                this.mtimeSec = mtimeSec;
                return this;
            }

            public EntryBuilder setMtimeNanoSec(int mtimeNanoSec) {
                this.mtimeNanoSec = mtimeNanoSec;
                return this;
            }

            public EntryBuilder setDev(int dev) {
                this.dev = dev;
                return this;
            }

            public EntryBuilder setMode(int mode) {
                this.mode = mode;
                return this;
            }

            public EntryBuilder setUid(int uid) {
                this.uid = uid;
                return this;
            }

            public EntryBuilder setGid(int gid) {
                this.gid = gid;
                return this;
            }

            public EntryBuilder setFileSize(int fileSize) {
                this.fileSize = fileSize;
                return this;
            }

            public EntryBuilder setOid(String oid) {
                this.oid = oid;
                return this;
            }

            public EntryBuilder setFlags(int flags) {
                this.flags = flags;
                return this;
            }

            public EntryBuilder setFlagsExtended(int flagsExtended) {
                this.flagsExtended = flagsExtended;
                return this;
            }

            public EntryBuilder setPath(String path) {
                this.path = path;
                return this;
            }

            public Entry build() {
                return new Entry(
                        ctimeSec,
                        ctimeNanoSec,
                        mtimeSec,
                        mtimeNanoSec,
                        dev,
                        mode,
                        uid,
                        gid,
                        fileSize,
                        oid,
                        flags,
                        flagsExtended,
                        path);
            }
        }
    }
}
