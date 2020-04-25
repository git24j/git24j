package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

public class Cred extends CAutoReleasable {
    protected Cred(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        // FIXME
        // jniFree(cPtr);
    }

    // no matching type found for 'git_cred_ssh_interactive_callback prompt_callback'
    /**
     * int git_cred_ssh_interactive_new(git_cred **out, const char *username,
     * git_cred_ssh_interactive_callback prompt_callback, void *payload);
     */
    // no matching type found for 'git_cred_sign_callback sign_callback'
    /**
     * int git_cred_ssh_custom_new(git_cred **out, const char *username, const char *publickey,
     * size_t publickey_len, git_cred_sign_callback sign_callback, void *payload);
     */
    /** -------- Jni Signature ---------- */
    /**
     * int git_cred_userpass(git_cred **cred, const char *url, const char *user_from_url, unsigned
     * int allowed_types, void *payload);
     */
    static native int jniUserpass(
            AtomicLong cred, String url, String userFromUrl, int allowedTypes);

    /** int git_cred_has_username(git_cred *cred); */
    static native int jniHasUsername(long cred);

    /**
     * int git_cred_userpass_plaintext_new(git_cred **out, const char *username, const char
     * *password);
     */
    static native int jniUserpassPlaintextNew(AtomicLong out, String username, String password);

    /**
     * int git_cred_ssh_key_new(git_cred **out, const char *username, const char *publickey, const
     * char *privatekey, const char *passphrase);
     */
    static native int jniSshKeyNew(
            AtomicLong out,
            String username,
            String publickey,
            String privatekey,
            String passphrase);

    /** int git_cred_ssh_key_from_agent(git_cred **out, const char *username); */
    static native int jniSshKeyFromAgent(AtomicLong out, String username);

    /** int git_cred_default_new(git_cred **out); */
    static native int jniDefaultNew(AtomicLong out);

    /** int git_cred_username_new(git_cred **cred, const char *username); */
    static native int jniUsernameNew(AtomicLong cred, String username);

    /**
     * int git_cred_ssh_key_memory_new(git_cred **out, const char *username, const char *publickey,
     * const char *privatekey, const char *passphrase);
     */
    static native int jniSshKeyMemoryNew(
            AtomicLong out,
            String username,
            String publickey,
            String privatekey,
            String passphrase);

    /** void git_cred_free(git_cred *cred); */
    static native void jniFree(long cred);

    /**
     * int git_cred_acquire_cb(git_cred **cred, const char *url, const char *username_from_url,
     * unsigned int allowed_types, void *payload);
     */
    static native int jniAcquireCb(
            AtomicLong cred, String url, String usernameFromUrl, int allowedTypes);
}
