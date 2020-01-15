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
     * <pre>
     *     git show tag $tag_name
     * </pre>
     * @return message associated with the tag.
     */
    public String message() {
        return jniMessage(getRawPointer());
    }
}
