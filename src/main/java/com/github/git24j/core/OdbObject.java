package com.github.git24j.core;

import java.util.Objects;

public class OdbObject extends CAutoReleasable {
    public enum Type implements IBitEnum{
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
}
