package com.github.git24j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;

public class Odb extends CAutoCloseable {
    Odb(long rawPointer) {
        super(rawPointer);
    }

    @Override
    protected void releaseOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Create a new object database with no backends.
     *
     * <p>Before the ODB can be used for read/writing, a custom database backend must be manually
     * added using `git_odb_add_backend()`
     *
     * @return object data base
     * @throws GitException git errors
     */
    @Nonnull
    public static Odb open() {
        Odb out = new Odb(0);
        Error.throwIfNeeded(jniNew(out._rawPtr));
        if (out.isNull()) {
            throw new GitException(GitException.ErrorClass.ODB, "Failed to create object database");
        }
        return out;
    }

    /**
     * Create a new object database and automatically add the two default backends:
     *
     * <p>- git_odb_backend_loose: read and write loose object files from disk, assuming
     * `objects_dir` as the Objects folder
     *
     * <p>- git_odb_backend_pack: read objects from packfiles, assuming `objects_dir` as the Objects
     * folder which contains a 'pack/' folder with the corresponding data
     *
     * @param objectsDir path of the backends' "objects" directory.
     * @return object data base
     * @throws GitException fail to open odb error
     */
    @Nonnull
    public static Odb open(@Nonnull Path objectsDir) {
        Odb out = new Odb(0);
        Error.throwIfNeeded(jniOpen(out._rawPtr, objectsDir.toString()));
        if (out.isNull()) {
            throw new GitException(GitException.ErrorClass.ODB, "Failed to create object database");
        }
        return out;
    }

    // no matching type found for 'git_odb_foreach_cb cb'
    /** int git_odb_foreach(git_odb *db, git_odb_foreach_cb cb, void *payload); */
    // no matching type found for 'char *buffer'
    /** int git_odb_stream_read(git_odb_stream *stream, char *buffer, size_t len); */
    // no matching type found for 'git_transfer_progress_cb progress_cb'
    /**
     * int git_odb_write_pack(git_odb_writepack **out, git_odb *db, git_transfer_progress_cb
     * progress_cb, void *progress_payload);
     */
    /** -------- Jni Signature ---------- */
    /** int git_odb_new(git_odb **out); */
    static native int jniNew(AtomicLong out);

    /** int git_odb_open(git_odb **out, const char *objects_dir); */
    static native int jniOpen(AtomicLong out, String objectsDir);

    /** int git_odb_add_disk_alternate(git_odb *odb, const char *path); */
    static native int jniAddDiskAlternate(long odb, String path);

    /**
     * Add an on-disk alternate to an existing Object DB.
     *
     * <p>Note that the added path must point to an `objects`, not to a full repository, to use it
     * as an alternate store.
     *
     * <p>Alternate backends are always checked for objects *after* all the main backends have been
     * exhausted.
     *
     * <p>Writing is disabled on alternate backends.
     *
     * @param path path to the objects folder for the alternate
     * @throws GitException git errors
     */
    public void addDiskAlternate(@Nonnull Path path) {
        Error.throwIfNeeded(jniAddDiskAlternate(getRawPointer(), path.toString()));
    }

    /** void git_odb_free(git_odb *db); */
    static native void jniFree(long db);

    /** int git_odb_read(git_odb_object **out, git_odb *db, const git_oid *id); */
    static native int jniRead(AtomicLong out, long db, Oid id);

    /**
     * Read an object from the database.
     *
     * <p>This method queries all available ODB backends trying to read the given OID.
     *
     * <p>The returned object is reference counted and internally cached, so it should be closed by
     * the user once it's no longer in use.
     *
     * @return the read object
     * @param id identity of the object to read.
     * @throws GitException git errors
     */
    @Nonnull
    public Optional<OdbObject> read(@Nonnull Oid id) {
        OdbObject out = new OdbObject(false, 0);
        int e = jniRead(out._rawPtr, getRawPointer(), id);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(out);
    }

    /**
     * int git_odb_read_prefix(git_odb_object **out, git_odb *db, const git_oid *short_id, size_t
     * len);
     */
    static native int jniReadPrefix(AtomicLong out, long db, Oid shortId, int len);

    /**
     * Read an object from the database, given a prefix of its identifier.
     *
     * <p>This method queries all available ODB backends trying to match the 'len' first hexadecimal
     * characters of the 'short_id'. The remaining (GIT_OID_HEXSZ-len)*4 bits of 'short_id' must be
     * 0s. 'len' must be at least GIT_OID_MINPREFIXLEN, and the prefix must be long enough to
     * identify a unique object in all the backends; the method will fail otherwise.
     *
     * <p>The returned object is reference counted and internally cached, so it should be closed by
     * the user once it's no longer in use.
     *
     * @param shortId a prefix of the id of the object to read.
     * @return - 0 if the object was read; - GIT_ENOTFOUND if the object is not in the database. -
     *     GIT_EAMBIGUOUS if the prefix is ambiguous (several objects match the prefix)
     */
    @Nonnull
    public Optional<OdbObject> readPrefix(Oid shortId) {
        OdbObject out = new OdbObject(false, 0);
        int e = jniReadPrefix(out._rawPtr, getRawPointer(), shortId, shortId.getEffectiveSize());
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(out);
    }

    /**
     * int git_odb_read_header(size_t *len_out, git_object_t *type_out, git_odb *db, const git_oid
     * *id);
     */
    static native int jniReadHeader(AtomicInteger lenOut, AtomicInteger typeOut, long db, Oid id);

    /**
     * Read the header of an object from the database, without reading its full contents.
     *
     * <p>The header includes the length and the type of an object.
     *
     * <p>Note that most backends do not support reading only the header of an object, so the whole
     * object will be read and then the header will be returned.
     *
     * @param id identity of the object to read.
     * @return OdbObject.Type object
     * @throws GitException git errors
     */
    @Nonnull
    public Optional<OdbObject.Header> readHeader(@Nonnull Oid id) {
        AtomicInteger lenOut = new AtomicInteger();
        AtomicInteger typeOut = new AtomicInteger();
        int e = jniReadHeader(lenOut, typeOut, getRawPointer(), id);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(
                new OdbObject.Header(
                        lenOut.get(), IBitEnum.valueOf(typeOut.get(), OdbObject.Type.class)));
    }

    /** int git_odb_exists(git_odb *db, const git_oid *id); */
    static native int jniExists(long db, Oid id);

    /**
     * Determine if the given object can be found in the object database.
     *
     * @param id the object to search for.
     * @return true if the object was found
     */
    public boolean exists(@Nonnull Oid id) {
        return jniExists(getRawPointer(), id) == 1;
    }

    /**
     * int git_odb_exists_prefix(git_oid *out, git_odb *db, const git_oid *short_id, size_t len);
     */
    static native int jniExistsPrefix(Oid out, long db, Oid shortId, int len);

    /**
     * Determine if an object can be found in the object database by an abbreviated object ID.
     *
     * @return The full OID of the found object if just one is found or empty if search failed.
     * @param shortId A prefix of the id of the object to read.
     * @throws GitException GIT_EAMBIGUOUS if multiple or other read errors
     */
    @Nonnull
    public Optional<Oid> existsPrefix(@Nonnull Oid shortId) {
        Oid fullId = new Oid();
        int e = jniExistsPrefix(fullId, getRawPointer(), shortId, shortId.getEffectiveSize());
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(fullId);
    }

    public static class ExpandId {
        private final Oid _oid;
        private final OdbObject.Type _type;

        public ExpandId(@Nullable Oid oid, @Nullable OdbObject.Type type) {
            _oid = oid;
            _type = type;
        }

        public Oid getOid() {
            return _oid;
        }

        public OdbObject.Type getType() {
            return _type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ExpandId expandId = (ExpandId) o;
            return Objects.equals(_oid, expandId._oid) && _type == expandId._type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_oid, _type);
        }
    }

    /** int git_odb_expand_ids(git_odb *db, git_odb_expand_id *ids, size_t count); */
    static native int jniExpandIds(long db, long ids, int count);

    static native long jniExpandIdsNew(Oid[] shortIds);

    static native void jniExpandIdsGetId(Oid outId, long expandIdsPtr, int idx);

    static native int jniExpandIdsGetType(long expandIdsPtr, int idx);

    /**
     * Determine if one or more objects can be found in the object database by their abbreviated
     * object ID and type. The given array will be updated in place: for each abbreviated ID that is
     * unique in the database, and of the given type (if specified), the full object ID, object ID
     * length (`GIT_OID_HEXSZ`) and type will be written back to the array. For IDs that are not
     * found (or are ambiguous), the array entry will be zeroed.
     *
     * <p>Note that since this function operates on multiple objects, the underlying database will
     * not be asked to be reloaded if an object is not found (which is unlike other object database
     * operations.)
     *
     * @param ids An array of short object IDs to search for
     * @throws GitException git errors
     */
    @Nonnull
    public List<ExpandId> expandIds(@Nonnull List<Oid> ids) {
        int len = ids.size();
        long cIdArr = jniExpandIdsNew(ids.toArray(new Oid[0]));
        Error.throwIfNeeded(jniExpandIds(getRawPointer(), cIdArr, len));
        List<ExpandId> expandIds = new ArrayList<>(len);
        for (int i = 0; i < ids.size(); i++) {
            Oid oid = new Oid();
            jniExpandIdsGetId(oid, cIdArr, i);
            OdbObject.Type t =
                    IBitEnum.valueOf(jniExpandIdsGetType(cIdArr, i), OdbObject.Type.class);
            expandIds.add(new ExpandId(oid, t));
        }
        return expandIds;
    }

    /** int git_odb_refresh(const git_odb *db); */
    static native int jniRefresh(long db);

    /**
     * Refresh the object database to load newly added files.
     *
     * <p>If the object databases have changed on disk while the library is running, this function
     * will force a reload of the underlying indexes.
     *
     * <p>Use this function when you're confident that an external application has tampered with the
     * ODB.
     *
     * <p>NOTE that it is not necessary to call this function at all. The library will automatically
     * attempt to refresh the ODB when a lookup fails, to see if the looked up object exists on disk
     * but hasn't been loaded yet.
     *
     * @throws GitException git errors
     */
    public void refresh() {
        Error.throwIfNeeded(jniRefresh(getRawPointer()));
    }

    /**
     * int git_odb_write(git_oid *out, git_odb *odb, const void *data, size_t len, git_object_t
     * type);
     */
    static native int jniWrite(Oid out, long odb, byte[] data, int len, int type);

    /**
     * Write an object directly into the ODB
     *
     * <p>This method writes a full object straight into the ODB. For most cases, it is preferred to
     * write objects through a write stream, which is both faster and less memory intensive,
     * specially for big objects.
     *
     * <p>This method is provided for compatibility with custom backends which are not able to
     * support streaming writes
     *
     * @param out pointer to store the OID result of the write
     * @param data buffer with the data to store
     * @param type type of the data to store
     * @throws GitException git errors
     */
    public void write(@Nonnull Oid out, byte[] data, GitObject.Type type) {
        Error.throwIfNeeded(jniWrite(out, getRawPointer(), data, data.length, type.getCode()));
    }

    public static class Stream extends CAutoCloseable {
        protected Stream(long rawPointer) {
            super(rawPointer);
        }

        @Override
        protected void releaseOnce(long cPtr) {
            jniStreamFree(cPtr);
        }
    }

    /**
     * int git_odb_open_wstream(git_odb_stream **out, git_odb *db, git_off_t size, git_object_t
     * type);
     */
    static native int jniOpenWstream(AtomicLong out, long db, int size, int type);

    // public Stream openWstream(int size, )

    /** int git_odb_stream_write(git_odb_stream *stream, const char *buffer, size_t len); */
    static native int jniStreamWrite(long stream, String buffer, int len);

    /** int git_odb_stream_finalize_write(git_oid *out, git_odb_stream *stream); */
    static native int jniStreamFinalizeWrite(Oid out, long stream);

    /** void git_odb_stream_free(git_odb_stream *stream); */
    static native void jniStreamFree(long stream);

    /**
     * int git_odb_open_rstream(git_odb_stream **out, size_t *len, git_object_t *type, git_odb *db,
     * const git_oid *oid);
     */
    static native int jniOpenRstream(
            AtomicLong out, AtomicInteger len, long type, long db, Oid oid);

    /** int git_odb_hash(git_oid *out, const void *data, size_t len, git_object_t type); */
    static native int jniHash(Oid out, byte[] data, int len, int type);

    /** int git_odb_hashfile(git_oid *out, const char *path, git_object_t type); */
    static native int jniHashfile(Oid out, String path, int type);

    /** int git_odb_object_dup(git_odb_object **dest, git_odb_object *source); */
    static native int jniObjectDup(AtomicLong dest, long source);

    /** void git_odb_object_free(git_odb_object *object); */
    static native void jniObjectFree(long object);

    /** const git_oid * git_odb_object_id(git_odb_object *object); */
    static native long jniObjectId(long object);

    /** const void * git_odb_object_data(git_odb_object *object); */
    static native long jniObjectData(long object);

    /** size_t git_odb_object_size(git_odb_object *object); */
    static native int jniObjectSize(long object);

    /** git_object_t git_odb_object_type(git_odb_object *object); */
    static native int jniObjectType(long object);

    /** int git_odb_add_backend(git_odb *odb, git_odb_backend *backend, int priority); */
    static native int jniAddBackend(long odb, long backend, int priority);

    /** int git_odb_add_alternate(git_odb *odb, git_odb_backend *backend, int priority); */
    static native int jniAddAlternate(long odb, long backend, int priority);

    /** size_t git_odb_num_backends(git_odb *odb); */
    static native int jniNumBackends(long odb);

    /** int git_odb_get_backend(git_odb_backend **out, git_odb *odb, size_t pos); */
    static native int jniGetBackend(AtomicLong out, long odb, int pos);

    /** int git_odb_backend_pack(git_odb_backend **out, const char *objects_dir); */
    static native int jniBackendPack(AtomicLong out, String objectsDir);

    /**
     * int git_odb_backend_loose(git_odb_backend **out, const char *objects_dir, int
     * compression_level, int do_fsync, unsigned int dir_mode, unsigned int file_mode);
     */
    static native int jniBackendLoose(
            AtomicLong out,
            String objectsDir,
            int compressionLevel,
            int doFsync,
            int dirMode,
            int fileMode);

    /** int git_odb_backend_one_pack(git_odb_backend **out, const char *index_file); */
    static native int jniBackendOnePack(AtomicLong out, String indexFile);
}
