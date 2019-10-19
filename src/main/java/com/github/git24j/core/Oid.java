package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

// TODO: change this class to immutable java object
public class Oid {
    public static final int RAWSZ = 20;
    public static final int HEXSZ = RAWSZ * 2;
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    private final AtomicLong rawPtr = new AtomicLong();
    private byte[] id;

    /** TODO: should allow creating Oid from String */
    Oid(long rawPointer) {
        this.id = new byte[RAWSZ];
        this.rawPtr.set(rawPointer);
    }

    Oid() {}

    public static Oid of(byte[] bytes) {
        Oid oid = new Oid();
        oid.id = new byte[RAWSZ];
        // must make a copy arguments can be deleted from c side soon.
        System.arraycopy(bytes, 0, oid.id, 0, RAWSZ);
        return oid;
    }

    public static Oid of(String hexSha) {
        Oid oid = new Oid();
        oid.id = hexStringToByteArray(hexSha.toLowerCase());
        return oid;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
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

    long getRawPointer() {
        return rawPtr.get();
    }

    @Override
    public String toString() {
        return id == null ? "" : bytesToHex(id);
    }
}
