package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

public class Tag extends GitObject {
    Tag(long rawPointer) {
        super(rawPointer);
    }

    public static Tag lookup(Repository repo, Oid oid) {
        return (Tag) GitObject.lookup(repo, oid, Type.TAG);
    }

    /** @deprecated in preference to {@link Tag::lookup} */
    public static Tag lookupPrefix(Repository repo, Oid oid) {
        return (Tag) GitObject.lookup(repo, oid, Type.TAG);
    }

    @Override
    public Tag dup() {
        Tag tag = new Tag(0);
        GitObject.jniDup(tag._rawPtr, getRawPointer());
        return tag;
    }

    static native int jniTarget(AtomicLong outTargetPtr, long tagPtr);
    /**
     *
     *
     * <pre>
     *     git rev-parse 'tag_name^{commit}'
     * </pre>
     *
     * @return Target object
     * @throws GitException git errors
     */
    public GitObject target() {
        GitObject out = new GitObject(0);
        Error.throwIfNeeded(jniTarget(out._rawPtr, getRawPointer()));
        return out;
    }

    static native void jniTargetId(long tagPtr, Oid outOid);
    /**
     * Get the OID of the tagged object of a tag
     *
     * @return target OID
     */
    public Oid targetId() {
        Oid oid = new Oid();
        jniTargetId(getRawPointer(), oid);
        return oid;
    }

    static native int jniTargetType(long tagPtr);

    /**
     * @return target type, this can be {@code Commit}, {@code Tag}, {@code Tree} or {@code Blob}
     */
    public Type targetType() {
        return Type.valueOf(jniTargetType(getRawPointer()));
    }

    static native String jniName(long tagPtr);

    /** @return Get tag name */
    public String name() {
        return jniName(getRawPointer());
    }

    static native void jniTagger(Signature outSig, long tagPtr);

    /** @return Signature of the tagger */
    public Signature tagger() {
        Signature outSig = new Signature();
        jniTagger(outSig, getRawPointer());
        return outSig;
    }

    static native String jniMessage(long tagPtr);

    /**
     *
     *
     * <pre>
     *     git show tag $tag_name
     * </pre>
     *
     * @return message associated with the tag.
     */
    public String message() {
        return jniMessage(getRawPointer());
    }

    static native int jniCreate(
            Oid oid,
            long repoPtr,
            String tagName,
            long targetPtr,
            Signature tagger,
            String message,
            int force);

    /**
     * Create a new tag in the repository from an object
     *
     * <p>A new reference will also be created pointing to this tag object. If `force` is true and a
     * reference already exists with the given name, it'll be replaced.
     *
     * <p>The message will not be cleaned up. This can be achieved through `git_message_prettify()`.
     *
     * <p>The tag name will be checked for validity. You must avoid the characters '~', '^', ':',
     * '\\', '?', '[', and '*', and the sequences ".." and "@{" which have special meaning to
     * revparse.
     *
     * @param repo Repository where to store the tag
     * @param tagName Name for the tag; this name is validated for consistency. It should also not
     *     conflict with an already existing tag name
     * @param target Object to which this tag points. This object must belong to the given `repo`.
     * @param tagger Signature of the tagger for this tag, and of the tagging time
     * @param message Full message for this tag
     * @param force Overwrite existing references
     * @return OID of the * newly created tag. If the tag already exists, this parameter * will be
     *     the oid of the existing tag, and the function will * return a GIT_EEXISTS error code. 0
     *     on success, GIT_EINVALIDSPEC or an error code A tag object is written to the ODB, an
     * @throws GitException GIT_EEXISTS if tag already exists, GIT_EINVALIDSPEC or error writing to
     *     ODB
     */
    public static Oid create(
            Repository repo,
            String tagName,
            GitObject target,
            Signature tagger,
            String message,
            boolean force) {
        Oid outOid = new Oid();
        int e =
                jniCreate(
                        outOid,
                        repo.getRawPointer(),
                        tagName,
                        target.getRawPointer(),
                        tagger,
                        message,
                        force ? 1 : 0);
        Error.throwIfNeeded(e);
        return outOid;
    }

    static native int jniAnnotationCreate(
            Oid oid,
            long repoPtr,
            String tagName,
            long targetPtr,
            Signature tagger,
            String message);

    /**
     * Create a new tag in the object database pointing to a git_object
     *
     * <p>The message will not be cleaned up. This can be achieved through `git_message_prettify()`.
     *
     * @param repo Repository where to store the tag
     * @param tagName Name for the tag
     * @param target Object to which this tag points. This object must belong to the given `repo`.
     * @param tagger Signature of the tagger for this tag, and of the tagging time
     * @param message Full message for this tag
     * @return oid to the newly created tag
     * @throws GitException 0 on success or an error code
     */
    public static Oid annotationCreate(
            Repository repo, String tagName, GitObject target, Signature tagger, String message) {
        Oid outOid = new Oid();
        int e =
                jniAnnotationCreate(
                        outOid,
                        repo.getRawPointer(),
                        tagName,
                        target.getRawPointer(),
                        tagger,
                        message);
        Error.throwIfNeeded(e);
        return outOid;
    }

    static native int jniCreateFromBuffer(Oid oid, long repoPtr, String buffer, int force);

    static native int jniCreateLightWeight(
            Oid oid, long repoPtr, String tagName, long targetPtr, int force);

    static native int jniDelete(long repoPtr, String tagName);
}
