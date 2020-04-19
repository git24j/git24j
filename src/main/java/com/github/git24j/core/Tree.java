package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class Tree extends GitObject {

    /**
     * Representation of each one of the entries in a tree object.
     */
    public static class Entry extends CAutoReleasable {
        protected Entry(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniEntryFree(cPtr);
        }

        /**
         * Duplicate a tree entry
         *
         * @return duplicated entry
         * @throws GitException git errors
         */
        @Nonnull
        public Entry dup() {
            Entry out = new Entry(false, 0);
            Error.throwIfNeeded(jniEntryDup(out._rawPtr, this.getRawPointer()));
            return out;
        }

        /**
         * @return the name of the file
         */
        @CheckForNull
        public String name() {
            return jniEntryName(this.getRawPointer());
        }

        /**
         * the id of the object pointed by the entry
         */
        @Nonnull
        public Oid id() {
            return Oid.of(jniEntryId(this.getRawPointer()));
        }

        /**
         * @return type of the object the entry is pointing to
         */
        @Nonnull
        public GitObject.Type type() {
            return GitObject.Type.valueOf(jniEntryType(this.getRawPointer()));
        }

        /**
         * @return the type of the object pointed by the entry
         */
        @Nonnull
        public FileMode filemode() {
            return IBitEnum.valueOf(
                    jniEntryFilemode(this.getRawPointer()), FileMode.class, FileMode.UNREADABLE);
        }

        /**
         * Get the raw UNIX file attributes of a tree entry
         *
         * <p>This function does not perform any normalization and is only useful if you need to be
         * able to recreate the original tree object.
         *
         * @return filemode as an integer
         */
        @Nonnull
        public FileMode filemodeRaw() {
            return IBitEnum.valueOf(
                    jniEntryFilemodeRaw(this.getRawPointer()), FileMode.class, FileMode.UNREADABLE);
        }

        /**
         * Compare this entry to {@code that}
         *
         * @param that entry to compare
         * @return < 0 if this is before that, 0 if equals, >0 if this is after that
         */
        public int cmp(@Nonnull Entry that) {
            return jniEntryCmp(getRawPointer(), that.getRawPointer());
        }

        /**
         * Convert a tree entry to the git_object it points to.
         *
         * @param repo repository where to lookup the pointed object
         * @return converted object
         * @throws GitException git errors
         */
        @Nonnull
        public GitObject toObject(@Nonnull Repository repo) {
            GitObject out = new GitObject(false, 0);
            int e = jniEntryToObject(out._rawPtr, repo.getRawPointer(), this.getRawPointer());
            Error.throwIfNeeded(e);
            return out;
        }
    }

    public enum UpdateT {
        /**
         * Update or insert an entry at the specified path
         */
        UPSERT,
        /**
         * Remove an entry from the specified path
         */
        REMOVE;
    }

    public static class Update extends CAutoReleasable {
        protected Update(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniUpdateFree(cPtr);
        }

        /**
         * Create an Update object.
         */
        @Nonnull
        public static Update create(
                @Nonnull UpdateT updateType,
                @Nonnull Oid oid,
                @Nonnull FileMode fileModeType,
                @Nonnull String path) {
            long ptr = jniUpdateNew(updateType.ordinal(), oid, fileModeType.getBit(), path);
            return new Update(false, ptr);
        }
    }

    public static class Builder extends CAutoReleasable {

        @FunctionalInterface
        public interface FilterCb {
            int accept(Entry entry);
        }

        protected Builder(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniBuilderFree(cPtr);
        }

        /**
         * Selectively remove entries in the tree
         * <p>
         * The `filter` callback will be called for each entry in the tree with a
         * pointer to the entry and the provided `payload`; if the callback returns
         * non-zero, the entry will be filtered (removed from the builder).
         *
         * @param builder  Tree builder
         * @param callback Callback to filter entries
         */
        public void filter(Builder builder, FilterCb callback) {
            jniBuilderFilter(builder.getRawPointer(), ptr -> callback.accept(new Entry(true, ptr)));
        }

    }

    Tree(long rawPointer) {
        super(rawPointer);
    }
    // no matching type found for 'git_filemode_t filemode'
    /**
     * int git_treebuilder_insert(const git_tree_entry **out, git_treebuilder *bld, const char
     * *filename, const git_oid *id, git_filemode_t filemode);
     */
    // no matching type found for 'git_treewalk_cb callback'
    /**
     * int git_tree_walk(const git_tree *tree, git_treewalk_mode mode, git_treewalk_cb callback,
     * void *payload);
     */
    /** -------- Jni Signature ---------- */
    /**
     * int git_tree_lookup(git_tree **out, git_repository *repo, const git_oid *id);
     */
    static native int jniLookup(AtomicLong out, long repoPtr, Oid id);

    /**
     * Look up tree object given its oid
     *
     * @param repo repository to search
     * @param oid  id of the tree object
     * @return found tree object
     * @throws GitException object not found or other git errors
     */
    @Nonnull
    public static Tree lookup(@Nonnull Repository repo, @Nonnull Oid oid) {
        return (Tree) GitObject.lookup(repo, oid, Type.TREE);
    }

    /**
     * int git_tree_lookup_prefix(git_tree **out, git_repository *repo, const git_oid *id, size_t
     * len);
     */
    static native int jniLookupPrefix(AtomicLong out, long repoPtr, Oid id, int len);

    @Nonnull
    public static Tree lookupPrefix(@Nonnull Repository repo, @Nonnull Oid oid) {
        return lookup(repo, oid);
    }

    /**
     * size_t git_tree_entrycount(const git_tree *tree);
     */
    static native int jniEntrycount(long tree);

    /**
     * @return Get the number of entries listed in a tree
     */
    public int entryCount() {
        return jniEntrycount(getRawPointer());
    }

    /**
     * const git_tree_entry * git_tree_entry_byname(const git_tree *tree, const char *filename);
     */
    static native long jniEntryByname(long tree, String filename);

    /**
     * Lookup a tree entry by its filename
     *
     * <p>This returns a git_tree_entry that is owned by the git_tree. You don't have to free it,
     * but you must not use it after the git_tree is released.
     *
     * @param filename the filename of the desired entry
     * @return the tree entry; empty if not found
     */
    @Nonnull
    public Optional<Entry> entryByName(@Nonnull String filename) {
        long ptr = jniEntryByname(getRawPointer(), filename);
        return ptr == 0 ? Optional.empty() : Optional.of(new Entry(true, ptr));
    }

    /**
     * const git_tree_entry * git_tree_entry_byindex(const git_tree *tree, size_t idx);
     */
    static native long jniEntryByindex(long tree, int idx);

    /**
     * Lookup a tree entry by its position in the tree
     *
     * <p>This returns a git_tree_entry that is owned by the git_tree. You don't have to free it,
     * but you must not use it after the git_tree is released.
     *
     * @param idx the position in the entry list
     * @return the tree entry; empty if not found
     */
    @Nonnull
    public Optional<Entry> entryByIndex(int idx) {
        long ptr = jniEntryByindex(getRawPointer(), idx);
        return ptr == 0 ? Optional.empty() : Optional.of(new Entry(true, ptr));
    }

    /**
     * const git_tree_entry * git_tree_entry_byid(const git_tree *tree, const git_oid *id);
     */
    static native long jniEntryByid(long tree, Oid id);

    /**
     * Lookup a tree entry by SHA value.
     *
     * <p>This returns a git_tree_entry that is owned by the git_tree. You don't have to free it,
     * but you must not use it after the git_tree is released.
     *
     * <p>Warning: this must examine every entry in the tree, so it is not fast.
     *
     * @param id the sha being looked for
     * @return the tree entry; empty if not found
     */
    @Nonnull
    public Optional<Entry> entryById(@Nonnull Oid id) {
        long ptr = jniEntryByid(getRawPointer(), id);
        return ptr == 0 ? Optional.empty() : Optional.of(new Entry(true, ptr));
    }

    /**
     * int git_tree_entry_bypath(git_tree_entry **out, const git_tree *root, const char *path);
     */
    static native int jniEntryBypath(AtomicLong out, long root, String path);

    /**
     * Retrieve a tree entry contained in a tree or in any of its subtrees, given its relative path.
     *
     * @param path Path to the contained entry
     * @return found tree entry; empty if not found
     * @throws GitException 0 on success; GIT_ENOTFOUND if the path does not exist
     */
    @Nonnull
    public Optional<Entry> entryByPath(@Nonnull String path) {
        Entry out = new Entry(false, 0);
        int e = jniEntryBypath(out._rawPtr, getRawPointer(), path);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(out);
    }

    /**
     * int git_tree_entry_dup(git_tree_entry **dest, const git_tree_entry *source);
     */
    static native int jniEntryDup(AtomicLong dest, long source);

    /**
     * void git_tree_entry_free(git_tree_entry *entry);
     */
    static native void jniEntryFree(long entry);

    /**
     * const char * git_tree_entry_name(const git_tree_entry *entry);
     */
    static native String jniEntryName(long entry);

    /**
     * const git_oid * git_tree_entry_id(const git_tree_entry *entry);
     */
    static native byte[] jniEntryId(long entry);

    /**
     * git_object_t git_tree_entry_type(const git_tree_entry *entry);
     */
    static native int jniEntryType(long entry);

    /**
     * git_filemode_t git_tree_entry_filemode(const git_tree_entry *entry);
     */
    static native int jniEntryFilemode(long entry);

    /**
     * git_filemode_t git_tree_entry_filemode_raw(const git_tree_entry *entry);
     */
    static native int jniEntryFilemodeRaw(long entry);

    /**
     * int git_tree_entry_cmp(const git_tree_entry *e1, const git_tree_entry *e2);
     */
    static native int jniEntryCmp(long e1, long e2);

    /**
     * int git_tree_entry_to_object(git_object **object_out, git_repository *repo, const
     * git_tree_entry *entry);
     */
    static native int jniEntryToObject(AtomicLong objectOut, long repoPtr, long entry);

    /**
     * int git_tree_dup(git_tree **out, git_tree *source);
     */
    static native int jniDup(AtomicLong out, long source);

    @Override
    public Tree dup() {
        return (Tree) super.dup();
    }

    /**
     * int git_tree_create_updated(git_oid *out, git_repository *repo, git_tree *baseline, size_t
     * nupdates, const git_tree_update *updates);
     */
    static native int jniCreateUpdated(Oid out, long repoPtr, long baseline, long[] updates);

    static native long jniUpdateNew(int updateType, Oid oid, int filemodeType, String path);

    static native void jniUpdateFree(long updatePtr);

    /**
     * Create a tree based on another one with the specified modifications
     *
     * <p>Given the `baseline` perform the changes described in the list of `updates` and create a
     * new tree.
     *
     * <p>This function is optimized for common file/directory addition, removal and replacement in
     * trees. It is much more efficient than reading the tree into a `git_index` and modifying that,
     * but in exchange it is not as flexible.
     *
     * <p>Deleting and adding the same entry is undefined behaviour, changing a tree to a blob or
     * viceversa is not supported.
     *
     * @param repo     the repository in which to create the tree, must be the same as for `baseline`
     * @param baseline the tree to base these changes on
     * @param updates  the list of updates to perform
     * @return id of the new tree
     */
    @Nonnull
    public Oid createUpdated(
            @Nonnull Repository repo, @Nonnull Tree baseline, @Nonnull List<Update> updates) {
        Oid outOid = new Oid();
        int e =
                jniCreateUpdated(
                        outOid,
                        repo.getRawPointer(),
                        baseline.getRawPointer(),
                        updates.stream().mapToLong(CAutoReleasable::getRawPointer).toArray());
        Error.throwIfNeeded(e);
        return outOid;
    }

    // no matching type found for 'git_tree_builder_filter_cb filter'

    /**
     * int git_tree_builder_filter(git_tree_builder *bld, git_tree_builder_filter_cb filter, void *payload);
     */
    static native void jniBuilderFilter(long bldPtr, Internals.JCallback callback);
    /** -------- Jni Signature ---------- */
    /**
     * int git_tree_builder_new(git_tree_builder **out, git_repository *repo, const git_tree *source);
     */
    static native int jniBuilderNew(AtomicLong out, long repoPtr, long source);

    /**
     * Create a new tree builder.
     *
     * The tree builder can be used to create or modify trees in memory and
     * write them as tree objects to the database.
     *
     * If the `source` parameter is not NULL, the tree builder will be
     * initialized with the entries of the given tree.
     *
     * If the `source` parameter is NULL, the tree builder will start with no
     * entries and will have to be filled manually.
     *
     * @param repo Repository in which to store the object
     * @param source Source tree to initialize the builder (optional)
     * @return tree builder
     * @throws GitException git errors
     */

    @Nonnull
    public static Builder newBuilder(@Nonnull Repository repo, @Nullable Tree source) {
        Builder bld = new Builder(false, 0);
        int e = jniBuilderNew(bld._rawPtr, repo.getRawPointer(),  source == null ? 0 : source.getRawPointer());
        Error.throwIfNeeded(e);
        return bld;
    }

    /**
     * int git_tree_builder_clear(git_tree_builder *bld);
     */
    static native int jniBuilderClear(long bld);

    /**
     * size_t git_tree_builder_entrycount(git_tree_builder *bld);
     */
    static native int jniBuilderEntrycount(long bld);

    /**
     * void git_tree_builder_free(git_tree_builder *bld);
     */
    static native void jniBuilderFree(long bld);

    /**
     * const git_tree_entry * git_tree_builder_get(git_tree_builder *bld, const char *filename);
     */
    static native long jniBuilderGet(long bld, String filename);

    /**
     * int git_tree_builder_insert(const git_tree_entry **out, git_tree_builder *bld, const char *filename, const git_oid *id, git_filemode_t filemode);
     */
    static native int jniBuilderInsert(AtomicLong out, long bld, String filename, Oid id, int filemode);

    /**
     * int git_tree_builder_remove(git_tree_builder *bld, const char *filename);
     */
    static native int jniBuilderRemove(long bld, String filename);

    /**
     * int git_tree_builder_write(git_oid *id, git_tree_builder *bld);
     */
    static native int jniBuilderWrite(Oid id, long bld);

    /**
     * int git_tree_builder_write_with_buffer(git_oid *oid, git_tree_builder *bld, git_buf *tree);
     */
    static native int jniBuilderWriteWithBuffer(Oid oid, long bld, Buf tree);

    /**
     * int git_tree_builder_filter_cb(const git_tree_entry *entry, void *payload);
     */
    static native int jniBuilderFilterCb(long entry);


}
