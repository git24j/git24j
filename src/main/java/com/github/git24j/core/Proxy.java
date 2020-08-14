package com.github.git24j.core;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLong;

public class Proxy {
    /** Type of proxy */
    public enum Type {
        /**
         * Do not attempt to connect through a proxy. If built against libcurl, it itself may
         * attempt to connect to a proxy if the environment variables specify it.
         */
        NONE,
        /** Try to auto-detect the proxy from the git configuration. */
        AUTO,
        /** Connect via the URL given in the options. */
        SPECIFIED;
    }

    public static class Options extends CAutoReleasable {
        public static final int VERSION = 1;

        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }

        @Nonnull
        public static Options create(int version) {
            Options opts = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(opts._rawPtr, version));
            return opts;
        }

        @Nonnull
        public static Options createDefault() {
            return create(VERSION);
        }

        public int getVersion() {
            return jniOptionsGetVersion(getRawPointer());
        }

        @Nonnull
        public Type getType() {
            int t = jniOptionsGetType(getRawPointer());
            switch (t) {
                case 0:
                    return Type.NONE;
                case 1:
                    return Type.AUTO;
                default:
                    return Type.SPECIFIED;
            }
        }

        public String getUrl() {
            return jniOptionsGetUrl(getRawPointer());
        }

        public void setVersion(int version) {
            jniOptionsSetVersion(getRawPointer(), version);
        }

        public void setType(@Nonnull Type type) {
            jniOptionsSetType(getRawPointer(), type.ordinal());
        }

        public void setUrl(String url) {
            jniOptionsSetUrl(getRawPointer(), url);
        }

        public void setCredentials(Credential.AcquireCb acquireCb) {
            jniOptionsSetCredentials(
                    getRawPointer(),
                    (url, usernameFromUrl, allowedTypes) -> {
                        try {
                            Credential out = acquireCb.accept(url, usernameFromUrl, allowedTypes);
                            return out.getRawPointer();
                        } catch (GitException e) {
                            return (long) e.getCode().getCode();
                        }
                    });
        }

        public void setCertificateCheck(Transport.CertificateCheckCb certificateCheckCb) {
            jniOptionsSetCertificateCheck(
                    getRawPointer(),
                    (ptr, valid, host) -> {
                        Cert cert = new Cert(true, ptr);
                        return certificateCheckCb.accept(cert, valid != 0, host);
                    });
        }
    }

    static native int jniOptionsNew(AtomicLong outPtr, int version);
    static native void jniOptionsFree(long optsPtr);
    static native void jniOptionsTestCallbacks(long optsPtr);
    /** -------- Jni Signature ---------- */
    /** int version */
    static native int jniOptionsGetVersion(long optionsPtr);

    /** int type */
    static native int jniOptionsGetType(long optionsPtr);

    /** const char *url */
    static native String jniOptionsGetUrl(long optionsPtr);

    /** int version */
    static native void jniOptionsSetVersion(long optionsPtr, int version);

    /** int type */
    static native void jniOptionsSetType(long optionsPtr, int type);

    /** const char *url */
    static native void jniOptionsSetUrl(long optionsPtr, String url);

    /** git_credential_acquire_cb credentials */
    static native void jniOptionsSetCredentials(
            long optionsPtr, Internals.SSIJCallback credentialCb);

    /** git_transport_certificate_check_cb certificate_check */
    static native void jniOptionsSetCertificateCheck(
            long optionsPtr, Internals.JISCallback certificateCheckCb);
}
