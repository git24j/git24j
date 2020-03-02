package com.github.git24j.core;

import javax.annotation.Nonnull;
import java.util.Objects;

public class OdbObject extends CAutoReleasable {
    public enum Type implements IBitEnum {
        /** < Object can be any of the following */
        ANY(-2),
        /** < Object is invalid. */
        INVALID(-1),
        /** < A commit object. */
        COMMIT(1),
        /** < A tree (directory listing) object. */
        TREE(2),
        /** < A file revision object. */
        BLOB(3),
        /** < An annotated tag object. */
        TAG(4),
        /** < A delta),base is given by an offset. */
        OFS_DELTA(6),
        /** < A delta),base is given by object id. */
        REF_DELTA(7);
        private final int _bit;

        Type(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return 0;
        }
    }

    public static class Header {
        private final int _len;
        private final Type _type;

        public Header(int len, Type type) {
            _len = len;
            _type = type;
        }

        public int getLen() {
            return _len;
        }

        public Type getType() {
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
            Header header = (Header) o;
            return _len == header._len && _type == header._type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_len, _type);
        }
    }

    protected OdbObject(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        Odb.jniObjectFree(cPtr);
    }

    /**
     * Create a copy of the current odb object.
     *
     * @return duplicate of the {@code OdbObject}
     * @throws GitException git errors
     */
    @Nonnull
    public OdbObject dup() {
        OdbObject out = new OdbObject(false, 0);
        Error.throwIfNeeded(Odb.jniObjectDup(out._rawPtr, getRawPointer()));
        return out;
    }

    /**
     * Return the OID of an ODB object
     *
     * <p>This is the OID from which the object was read from
     *
     * @return the object
     * @throws GitException failed to get id.
     */
    @Nonnull
    public Oid id() {
        Oid out = new Oid();
        Odb.jniObjectId(getRawPointer(), out);
        if (out.isEmpty()) {
            throw new GitException(
                    GitException.ErrorClass.ODB, "Could not get id of the given OdbObject");
        }
        return out;
    }

    /** @return the type of an ODB object */
    @Nonnull
    public Type type() {
        return IBitEnum.valueOf(Odb.jniObjectType(getRawPointer()), Type.class, Type.ANY);
    }
}
