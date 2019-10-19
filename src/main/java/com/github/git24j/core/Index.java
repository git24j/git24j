package com.github.git24j.core;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

public class Index implements AutoCloseable {
    private final AtomicLong idxPtr;

    public Index(AtomicLong idxPtr) {
        this.idxPtr = idxPtr;
    }

    static native int jniUpdateAll(
            long idxPtr, String[] pathSpec, BiConsumer<String, String> callback);

    static native int jniAdd(long idxPtr, Entry sourceEntry);

    static native int jniAddByPath(long idxPtr, String path);

    static native int jniAddAll(long idxPtr, String[] pathSpec, int flags, Callback callback);

    static native int jniWrite(long idxPtr);

    static native void jniFree(long idxPtr);

    /**
     * Update all index entries to match the working directory
     *
     * @param pathSpec array of path patterns
     * @param callback notification callback for each updated path (also gets index of matching
     *     pathspec entry); can be NULL; return 0 to add, >0 to skip, < 0 to abort scan.
     * @throws GitException git error.
     */
    public void updateAll(String[] pathSpec, BiConsumer<String, String> callback) {
        Error.throwIfNeeded(jniUpdateAll(idxPtr.get(), pathSpec, callback));
    }

    /** See also {@link #updateAll(String[], BiConsumer)}. */
    public void updateAll(List<String> pathSpec, BiConsumer<String, String> callback) {
        updateAll(pathSpec.toArray(new String[0]), callback);
    }

    /**
     * Write an existing index object from memory back to disk using an atomic file lock.
     *
     * @throws GitException git error.
     */
    public void write() {
        Error.throwIfNeeded(jniWrite(idxPtr.get()));
    }

    /** Delegate {@code git_index_free} Free an existing index object. */
    @Override
    public void close() {
        jniFree(idxPtr.get());
        idxPtr.set(0);
    }

    /**
     * Add or update an index entry from an in-memory struct.
     *
     * @param sourceEntry new entry object
     * @throws GitException git error
     */
    public void add(Entry sourceEntry) {
        Error.throwIfNeeded(jniAdd(idxPtr.get(), sourceEntry));
    }

    /**
     * Add or update an index entry from a file on disk
     *
     * @param path filename to add
     * @throws GitException git error
     */
    public void add(String path) {
        Error.throwIfNeeded(jniAddByPath(idxPtr.get(), path));
    }

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
                jniAddAll(idxPtr.get(), pathSpec, IBitEnum.bitOrAll(flags), callback::accept));
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
