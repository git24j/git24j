package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

public class Credential {
    // no matching type found for 'git_credential_ssh_interactive_cb prompt_callback'
    /**
     * int git_credential_ssh_interactive_new(git_credential **out, const char *username,
     * git_credential_ssh_interactive_cb prompt_callback, void *payload);
     */
    // no matching type found for 'git_credential_sign_cb sign_callback'
    /**
     * int git_credential_ssh_custom_new(git_credential **out, const char *username, const char
     * *publickey, size_t publickey_len, git_credential_sign_cb sign_callback, void *payload);
     */
    /** -------- Jni Signature ---------- */
    /** void git_credential_free(git_credential *cred); */
    static native void jniFree(long cred);

    /** int git_credential_has_username(git_credential *cred); */
    static native int jniHasUsername(long cred);

    /** const char * git_credential_get_username(git_credential *cred); */
    static native String jniGetUsername(long cred);

    /**
     * int git_credential_userpass_plaintext_new(git_credential **out, const char *username, const
     * char *password);
     */
    static native int jniUserpassPlaintextNew(AtomicLong out, String username, String password);

    /** int git_credential_default_new(git_credential **out); */
    static native int jniDefaultNew(AtomicLong out);

    /** int git_credential_username_new(git_credential **out, const char *username); */
    static native int jniUsernameNew(AtomicLong out, String username);

    /**
     * int git_credential_ssh_key_new(git_credential **out, const char *username, const char
     * *publickey, const char *privatekey, const char *passphrase);
     */
    static native int jniSshKeyNew(
            AtomicLong out,
            String username,
            String publickey,
            String privatekey,
            String passphrase);

    /**
     * int git_credential_ssh_key_memory_new(git_credential **out, const char *username, const char
     * *publickey, const char *privatekey, const char *passphrase);
     */
    static native int jniSshKeyMemoryNew(
            AtomicLong out,
            String username,
            String publickey,
            String privatekey,
            String passphrase);

    /** int git_credential_ssh_key_from_agent(git_credential **out, const char *username); */
    static native int jniSshKeyFromAgent(AtomicLong out, String username);

    /**
     * int git_credential_userpass(git_credential **out, const char *url, const char *user_from_url,
     * unsigned int allowed_types, void *payload);
     */
    static native int jniUserpass(AtomicLong out, String url, String userFromUrl, int allowedTypes, long payloadPtr);

    /**
     * int git_credential_acquire_cb(git_credential **out, const char *url, const char
     * *username_from_url, unsigned int allowed_types, void *payload);
     */
    static native int jniAcquireCb(
            AtomicLong out, String url, String usernameFromUrl, int allowedTypes);
}
