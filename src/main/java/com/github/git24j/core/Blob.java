package com.github.git24j.core;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class Blob extends GitObject {
    Blob(long rawPointer) {
        super(rawPointer);
    }

    static native int jniLookup(AtomicLong outBlob, long repoPtr, Oid oid);

    static native int jniLookupPrefix(AtomicLong outBlob, long repoPtr, Oid oid, int len);

    static native int jniFree(long blobPtr);

    static native void jniId(long blobPtr, Oid oid);

    static native long jniOwner(long blobPtr);

    /** TODO: figure out how to wrap (void *) */
    static native long jniRawContent(long blobPtr);

    static native long jniRawSize(long blobPtr);

    static native int jniCreateFromWorkdir(Oid oid, long repoPtr, String relativePath);

    /**
     * Read a file from the working folder of a repository and write it to the Object Database as a
     * loose blob
     *
     * @param repo git repository
     * @param relativePath file from which the blob will be created, relative to the repository's
     *     working dir
     * @return id of the written blob.
     */
    public static Oid createFromWorkdir(Repository repo, String relativePath) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromWorkdir(oid, repo.getRawPointer(), relativePath));
        return oid;
    }

    static native int jniCreateFromDisk(Oid oid, long repoPtr, String path);

    /**
     * Read a file from the filesystem and write its content to the Object Database as a loose blob
     *
     * @param repo repository where the blob will be written. this repository can be bare or not
     * @param path file from which the blob will be created
     * @return the id of the written blob
     * @throws GitException git error
     */
    public static Oid createdFromDisk(Repository repo, String path) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromDisk(oid, repo.getRawPointer(), path));
        return oid;
    }

    static native int jniCreateFromStream(AtomicLong outStream, long repoPtr, String hintPath);

    /**
     * Create a stream to write a new blob into the object db
     *
     * <p>This function may need to buffer the data on disk and will in general not be the right
     * choice if you know the size of the data to write. If you have data in memory, use
     * `git_blob_create_frombuffer()`. If you do not, but know the size of the contents (and don't
     * want/need to perform filtering), use `git_odb_open_wstream()`.
     *
     * <p>Don't close this stream yourself but pass it to `git_blob_create_fromstream_commit()` to
     * commit the write to the object db and get the object id.
     *
     * <p>If the `hintpath` parameter is filled, it will be used to determine what git filters
     * should be applied to the object before it is written to the object database.
     *
     * @param repo Repository where the blob will be written. This repository can be bare or not.
     * @param hintpath If not NULL, will be used to select data filters to apply onto the content of
     *     the blob to be created.
     * @return {@link WriteStream} an object to write data to.
     * @throws GitException git error
     */
    public static WriteStream createFromStream(Repository repo, String hintpath) {
        AtomicLong outWs = new AtomicLong();
        Error.throwIfNeeded(jniCreateFromStream(outWs, repo.getRawPointer(), hintpath));
        return new WriteStream(outWs.get());
    }

    static native int jniCreateFromStreamCommit(Oid oid, long streamPtr);

    /**
     * Close the stream and write the blob to the object db
     *
     * <p>The stream will be closed and freed.
     *
     * @param ws an object to write data to.
     * @return id of the new blob
     * @throws GitException git error
     */
    public static Oid createFromStreamCommit(WriteStream ws) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromStreamCommit(oid, ws.getRawPointer()));
        return oid;
    }

    static native int jniCreateFromBuffer(Oid oid, long repoPtr, final byte[] buf);

    /**
     * Write an in-memory buffer to the ODB as a blob
     *
     * @param repo repository where to blob will be written
     * @param buf data to be written into the blob
     * @return id of the written blob
     */
    public static Oid createFromBuffer(Repository repo, final byte[] buf) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniCreateFromBuffer(oid, repo.getRawPointer(), buf));
        return oid;
    }

    static native int jniIsBinary(long blobPtr);

    static native int jniDup(AtomicLong outDest, long srcPtr);

    public Blob dup() {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniDup(out, getRawPointer()));
        return new Blob(out.get());
    }

    /** Free jni resources. */
    public void free() {
        jniFree(getRawPointer());
    }

    /** Get the id of a blob. */
    @Override
    public Oid id() {
        Oid oid = new Oid();
        jniId(getRawPointer(), oid);
        return oid;
    }

    /** Get the repository that contains the blob. */
    @Override
    public Repository owner() {
        return Repository.ofRaw(jniOwner(getRawPointer()));
    }

    /** @return the size in bytes of the contents of a blob. */
    public long rawSize() {
        return jniRawSize(getRawPointer());
    }

    public boolean isBinary() {
        return jniIsBinary(getRawPointer()) == 1;
    }

    /**
     * Lookup a blob from sha in a repository.
     *
     * @param repo git repo
     * @param oid sha of the blob to search
     * @return found blob or null
     * @throws GitException git error
     */
    public static Blob lookup(Repository repo, Oid oid) {
        AtomicLong out = new AtomicLong();
        if (oid.isShortId()) {
            Error.throwIfNeeded(
                    jniLookupPrefix(out, repo.getRawPointer(), oid, oid.getEffectiveSize()));
        } else {
            Error.throwIfNeeded(jniLookup(out, repo.getRawPointer(), oid));
        }
        return out.get() == 0 ? null : new Blob(out.get());
    }

    /**
     * Lookup a blob from short sha in a repository. You probably want to use {@code Blob::lookup} instead.
     *
     * @param repo git repo
     * @param oid sha of the blob to search.
     * @return found blob or null
     * @throws GitException git error
     */
    public static Blob lookupPrefix(Repository repo, Oid oid) {
        AtomicLong outBlob = new AtomicLong();
        Error.throwIfNeeded(
                jniLookupPrefix(outBlob, repo.getRawPointer(), oid, oid.getEffectiveSize()));
        return outBlob.get() == 0 ? null : new Blob(outBlob.get());
    }
}
