package com.github.git24j.core;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Mailmap {
    private final AtomicLong _rawPtr = new AtomicLong();

    public Mailmap(long ptr) {
        _rawPtr.set(ptr);
    }

    static native void jniFree(long rawPtr);

    @Override
    protected void finalize() throws Throwable {
        if (_rawPtr.get() > 0) {
            jniFree(_rawPtr.getAndSet(0));
        }
        super.finalize();
    }

    long getRawPointer() {
        return _rawPtr.get();
    }

    static native int jniAddEntry(
            long mmPtr, String realName, String realEmail, String replaceName, String replaceEmail);

    /**
     * Add a single entry to the given mailmap object. If the entry already exists, it will be
     * replaced with the new entry.
     *
     * @param realName the real name to use, or NULL
     * @param realEmail the real email to use, or NULL
     * @param replaceName the name to replace, or NULL
     * @param replaceEmail the email to replace
     * @throws GitException git errors
     */
    public void addEntry(
            String realName, String realEmail, String replaceName, String replaceEmail) {
        Error.throwIfNeeded(
                jniAddEntry(getRawPointer(), realName, realEmail, replaceName, replaceEmail));
    }

    static native int jniFromBuffer(AtomicLong outPtr, String buf);

    /**
     * Create a new mailmap instance containing a single mailmap file
     *
     * @param buf a string to parse the mailmap from
     * @return Mailmap instance
     * @throws GitException git error
     */
    public static Mailmap fromBuffer(String buf) {
        Mailmap mm = new Mailmap(0);
        jniFromBuffer(mm._rawPtr, buf);
        return mm;
    }

    static native int jniFromRepository(AtomicLong outPtr, long repoPtr);

    /**
     * Create a new mailmap instance from a repository, loading mailmap files based on the
     * repository's configuration.
     *
     * <p>Mailmaps are loaded in the following order: 1. '.mailmap' in the root of the repository's
     * working directory, if present. 2. The blob object identified by the 'mailmap.blob' config
     * entry, if set. [NOTE: 'mailmap.blob' defaults to 'HEAD:.mailmap' in bare repositories] 3. The
     * path in the 'mailmap.file' config entry, if set.
     *
     * @param repo repository to load mailmap information from
     * @return Mailmap instance
     * @throws GitException git errors
     */
    public static Mailmap fromRepository(Repository repo) {
        AtomicLong outPtr = new AtomicLong();
        Error.throwIfNeeded(jniFromRepository(outPtr, repo.getRawPointer()));
        return new Mailmap(outPtr.get());
    }

    static native int jniResolve(
            AtomicReference<String> outRealName,
            AtomicReference<String> outRealEmail,
            long mmPtr,
            String name,
            String email);

    /**
     * Resolve a name and email to the corresponding real name and email.
     *
     * @param outRealName placeholder to store the real name, must not be null, can be empty.
     * @param outRealEmail placeholder to store the real email, must not be null, can be empty.
     * @param name the name to look up
     * @param email the email to look up
     * @throws GitException git errors
     */
    public void resolve(
            AtomicReference<String> outRealName,
            AtomicReference<String> outRealEmail,
            String name,
            String email) {
        Error.throwIfNeeded(jniResolve(outRealName, outRealEmail, getRawPointer(), name, email));
    }

    /**
     * Resolve a name and email to the corresponding real name and email.
     *
     * @param name the name to look up, must not be null, can be empty.
     * @param email the email to look up, , must not be null, can be empty.
     * @return an entry whose key is the resolved name (aka real name) and value is the resolve
     *     email.
     * @throws GitException git errors
     */
    public Map.Entry<String, String> resolve(String name, String email) {
        AtomicReference<String> outRealName = new AtomicReference<>();
        AtomicReference<String> outRealEmail = new AtomicReference<>();
        resolve(outRealName, outRealEmail, name, email);
        return new AbstractMap.SimpleImmutableEntry<>(outRealName.get(), outRealEmail.get());
    }

    static native int jniResolveSignature(Signature outSig, long mmPtr, Signature sig);

    /**
     * Resolve a signature to use real names and emails with a mailmap.
     *
     * @param sig signature to resolve
     * @return resolved signature
     * @throws GitException git errors
     */
    public Signature resolveSignature(Signature sig) {
        Signature outSig = new Signature();
        Error.throwIfNeeded(jniResolveSignature(outSig, getRawPointer(), sig));
        return outSig;
    }
}
