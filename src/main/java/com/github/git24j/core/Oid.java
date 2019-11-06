package com.github.git24j.core;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

// TODO: change this class to immutable java object
public class Oid {
    public static final int RAWSZ = 20;
    public static final int HEXSZ = RAWSZ * 2;
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    /** in case of short sha, only up to {@code eSize} bytes are effective */
    private int eSize = RAWSZ;
    private byte[] id = new byte[RAWSZ];

    Oid() { }

    Oid(byte []bytes) {
        eSize = bytes.length;
        System.arraycopy(bytes, 0, this.id, 0, eSize);
    }

    public static Oid of(byte[] bytes) {
        return new Oid(bytes);
    }

    public static Oid of(String hexSha) {
        byte[] bytes = hexStringToByteArray(hexSha.toLowerCase());
        return new Oid(bytes);
    }

    public boolean isShortId() {
        return getEffectiveSize() < RAWSZ;
    }

    private static String bytesToHex(byte[] bytes, int len) {
        int cutoffLen = Math.min(len, bytes.length);
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < cutoffLen; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, bytes.length);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] =
                    (byte)
                            ((Character.digit(s.charAt(i), 16) << 4)
                                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id == null ? "" : bytesToHex(id, eSize);
    }

    /**
     * Get effective size of the oid. Normally, it should be {@code RAWSZ} but can
     * be smaller in case of short sha.
     */
    public int getEffectiveSize() {
        return eSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Oid oid = (Oid) o;
        if (eSize != oid.eSize) {
            return false;
        }
        for (int i = 0; i <eSize; i++) {
            if (id[i] != oid.id[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(eSize);
        result = 31 * result + Objects.hash(toString());
        return result;
    }
}
