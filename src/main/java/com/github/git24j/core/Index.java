package com.github.git24j.core;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

public class Index implements AutoCloseable {
    final AtomicLong _rawPtr = new AtomicLong();

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
     * <p>If you pass `GIT_INDEX_CAPABILITY_FROM_OWNER` for the caps, then capabilities will be read
     * from the config of the owner object, looking at `core.ignorecase`, `core.filemode`,
     * `core.symlinks`.
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
     * <p>Valid return values are 2, 3, or 4. If 3 is returned, an index with version 2 may be
     * written instead, if the extension data in version 3 is not necessary.
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
     * <p>Valid values are 2, 3, or 4. If 2 is given, git_index_write may write an index with
     * version 3 instead, if necessary to accurately represent the index.
     *
     * @param version The new version number
     * @throws GitException git errors
     */
    public void setVersion(int version) {
        Error.throwIfNeeded(jniSetVersion(getRawPointer(), version));
    }

    static native int jniRead(long indexPtr, int force);

    /**
     * Update the contents of an existing index object in memory by reading from the hard disk.
     *
     * <p>If `force` is true, this performs a "hard" read that discards in-memory changes and always
     * reloads the on-disk index data. If there is no on-disk version, the index will be cleared.
     *
     * <p>If `force` is false, this does a "soft" read that reloads the index data from disk only if
     * it has changed since the last time it was loaded. Purely in-memory index data will be
     * untouched. Be aware: if there are changes on disk, unwritten in-memory changes are discarded.
     *
     * @param force if true, always reload, vs. only read if file has changed
     * @throws GitException git errors
     */
    public void read(boolean force) {
        Error.throwIfNeeded(jniRead(getRawPointer(), force ? 0 : 1));
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
        Error.throwIfNeeded(
                jniUpdateAll(getRawPointer(), pathSpec.toArray(new String[0]), callback));
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

    static native void jniChecksum(Oid outOid, long indexPtr);

    /**
     * Get the checksum of the index
     *
     * <p>This checksum is the SHA-1 hash over the index file (except the last 20 bytes which are
     * the checksum itself). In cases where the index does not exist on-disk, it will be zeroed out.
     *
     * @return the checksum of the index
     */
    public Oid checksum() {
        Oid oid = new Oid();
        jniChecksum(oid, getRawPointer());
        return oid;
    }

    static native int jniReadTree(long indexPtr, long treePtr);

    /**
     * Read a tree into the index file with stats, roughly equals to:
     *
     * <pre>
     *     git read-tree [<tree-ish>]
     * </pre>
     *
     * The current index contents will be replaced by the specified tree.
     *
     * @param tree tree to read
     * @throws GitException git errors
     */
    public void readTree(Tree tree) {
        Error.throwIfNeeded(jniReadTree(getRawPointer(), tree.getRawPointer()));
    }

    static native int jniWriteTree(Oid outOid, long indexPtr);

    /**
     * Write the index as a tree, roughly equals to:
     *
     * <pre>
     *     git write-tree
     * </pre>
     *
     * <p>This method will scan the index and write a representation of its current state back to
     * disk; it recursively creates tree objects for each of the subtrees stored in the index, but
     * only returns the OID of the root tree. This is the OID that can be used e.g. to create a
     * commit.
     *
     * <p>The index instance cannot be bare, and needs to be associated to an existing repository.
     *
     * <p>The index must not contain any file in conflict.
     *
     * @return Oid of the tree
     * @throws GitException git errors, for example GIT_EUNMERGED when the index is not clean or an
     *     error code
     */
    public Oid writeTree() {
        Oid outOid = new Oid();
        Error.throwIfNeeded(jniWriteTree(outOid, getRawPointer()));
        return outOid;
    }

    static native int jniWriteTreeTo(Oid outOid, long indexPtr, long repoPtr);

    /**
     * Write the index as a tree to the given repository
     *
     * <p>This method will do the same as `git_index_write_tree`, but letting the user choose the
     * repository where the tree will be written.
     *
     * <p>The index must not contain any file in conflict.
     *
     * @param repo Repository where to write the tree
     * @return oid of the tree written
     * @throws GitException git error like GIT_EUNMERGED when the index is not clean
     */
    public Oid writeTreeTo(Repository repo) {
        Oid outOid = new Oid();
        Error.throwIfNeeded(jniWriteTreeTo(outOid, getRawPointer(), repo.getRawPointer()));
        return outOid;
    }

    static native int jniEntryCount(long indexPtr);
    /* Index entry manipulation */

    /**
     * Get the count of entries currently in the index, roughly equals to:
     *
     * <pre>
     *     $ git ls-files | wc -l
     * </pre>
     *
     * @return integer of count of current entries
     */
    public int entryCount() {
        return jniEntryCount(getRawPointer());
    }

    static native int jniClear(long indexPtr);

    /**
     * Clear the contents (all the entries) of an index object.
     *
     * <p>This clears the index object in memory; changes must be explicitly written to disk for
     * them to take effect persistently.
     *
     * @throws GitException git errors
     */
    public void clear() {
        Error.throwIfNeeded(jniClear(getRawPointer()));
    }

    static native long jniGetByIndex(long indexPtr, int n);

    static native long jniGetByPath(long indexPtr, String path, int stage);

    static native int jniRemove(long indexPtr, String path, int stage);

    /**
     * Remove an entry from the index
     *
     * @param path path to search
     * @param stage stage to search
     * @throws GitException git errors
     */
    public void remove(String path, int stage) {
        Error.throwIfNeeded(jniRemove(getRawPointer(), path, stage));
    }

    static native int jniRemoveDirectory(long indexPtr, String dir, int stage);

    /**
     * Remove all entries from the index under a given directory
     *
     * @param dir container directory path
     * @param stage stage to search
     * @throws GitException git errors
     */
    public void remoteDirectory(String dir, int stage) {
        Error.throwIfNeeded(jniRemoveDirectory(getRawPointer(), dir, stage));
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
        private final AtomicLong _ptr = new AtomicLong();

        Entry(long rawPtr) {
            _ptr.set(rawPtr);
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }

        /**
         * Get an entry to one of the entries in the index
         *
         * <p>The entry is not modifiable and should not be freed. Because the `git_index_entry`
         * struct is a publicly defined struct, you should be able to make your own permanent copy
         * of the data if necessary.
         *
         * @param index an existing index object
         * @param n the position of the entry
         * @return the entry or null if out of bounds
         */
        public static Entry getByIndex(Index index, int n) {
            long ptr = Index.jniGetByIndex(index.getRawPointer(), n);
            if (ptr == 0) {
                return null;
            }
            return new Entry(ptr);
        }

        /**
         * Get one of the entries in the index that matches the given path
         *
         * <p>The entry is not modifiable and should not be freed. Because the `git_index_entry`
         * struct is a publicly defined struct, you should be able to make your own permanent copy
         * of the data if necessary.
         *
         * @param index an existing index object
         * @param path path to search
         * @param stage stage to search
         * @return the entry or null if it was not found
         */
        public static Entry getByPath(Index index, String path, int stage) {
            long ptr = Index.jniGetByPath(index.getRawPointer(), path, stage);
            if (ptr == 0) {
                return null;
            }
            return new Entry(ptr);
        }
    }
}
