package com.github.git24j.core;

import java.util.EnumSet;

public class Cert extends CAutoReleasable {
    protected Cert(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    public enum T {
        /** No information about the certificate is available. This may happen when using curl. */
        NONE,
        /** The `data` argument to the callback will be a pointer to the DER-encoded data. */
        X509,
        /**
         * The `data` argument to the callback will be a pointer to a `git_cert_hostkey` structure.
         */
        HOSTKEY_LIBSSH2,
        /**
         * The `data` argument to the callback will be a pointer to a `git_strarray` with
         * `name:content` strings containing information about the certificate. This is used when
         * using curl.
         */
        STRARRAY,
    }

    public enum SshT implements IBitEnum {
        /** MD5 is available */
        MD5(1 << 0),
        /** SHA-1 is available */
        SHA1(1 << 1),
        /** SHA-256 is available */
        SHA256(1 << 2);

        private final int _bit;

        SshT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public static class HostKey extends CAutoReleasable {
        protected HostKey(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }

        /** Note: returned Cert only valid when HostKey is not freed */
        public Cert getParent() {
            long ptr = jniHostkeyGetParent(getRawPointer());
            return new Cert(true, ptr);
        }

        public EnumSet<SshT> getType() {
            return IBitEnum.parse(jniHostkeyGetType(getRawPointer()), SshT.class);
        }

        public byte[] getHashMd5() {
            return jniHostkeyGetHashMd5(getRawPointer());
        }

        public byte[] getHashSha1() {
            return jniHostkeyGetHashSha1(getRawPointer());
        }

        public byte[] getHashSha256() {
            return jniHostkeyGetHashSha256(getRawPointer());
        }
    }

    /** git_cert parent */
    static native long jniHostkeyGetParent(long hostkeyPtr);
    /** git_cert_ssh_t type */
    static native int jniHostkeyGetType(long hostkeyPtr);
    /** unsigned char hash_md5[16] */
    static native byte[] jniHostkeyGetHashMd5(long hostkeyPtr);
    /** unsigned char hash_sha1[20] */
    static native byte[] jniHostkeyGetHashSha1(long hostkeyPtr);
    /** unsigned char hash_sha256[32] */
    static native byte[] jniHostkeyGetHashSha256(long hostkeyPtr);

    @Override
    protected void freeOnce(long cPtr) {
        Libgit2.jniShadowFree(cPtr);
    }
}
