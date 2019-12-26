package com.github.git24j.core;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class Commit extends GitObject {
    Commit(long rawPointer) {
        super(rawPointer);
    }

    /**
     * Lookup a commit object from a repository.
     *
     * @param repo the repo to use when locating the commit.
     * @param oid identity of the commit to locate. If the object is
     *		an annotated tag it will be peeled back to the commit.
     * @return Found commit
     * @throws GitException git error
     * @throws IllegalStateException required objects are not open or have been closed.
     */
    public static Commit lookup(Repository repo, Oid oid) {
        return (Commit) GitObject.lookup(repo, oid, Type.COMMIT);
    }

    /**
     * Lookup a commit object from a repository, given a prefix of its
     * identifier (short id).
     *
     * The returned object should be released with `git_commit_free` when no
     * longer needed.
     *
     * @param repo the repo to use when locating the commit.
     * @param oid identity of the commit to locate. If the object is
     *		an annotated tag it will be peeled back to the commit.
     * @return found commit
     * @throws GitException git errors
     * @deprecated in preference to {@code lookup} which handles if oid is short id already.
     */
    public static Commit lookupPrefix(Repository repo, Oid oid) {
        return lookup(repo, oid);
    }

    static native String jniMessageEncoding(long commitPtr);

    /**
     * Get the encoding for the message of a commit,
     * as a string representing a standard encoding name.
     *
     * The encoding may be NULL if the `encoding` header
     * in the commit is missing; in that case UTF-8 is assumed.
     *
     * @return NULL, or the encoding
     */
    public String messageEncoding() {
        return jniMessageEncoding(getRawPointer());
    }

    /**const char * git_commit_message(const git_commit *commit); */
    // JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessage)(JNIEnv *env, jclass obj, jlong commitPtr);
    static native String jniMessage(long commitPtr);

    /**
     * Get the full message of a commit.
     *
     * The returned message will be slightly prettified by removing any
     * potential leading newlines.
     *
     * @return the message of a commit
     */
    public String message() {
        return jniMessage(getRawPointer());
    }

    /**const char * git_commit_message_raw(const git_commit *commit); */
    //JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessageRaw)(JNIEnv *env, jclass obj, jlong commitPtr);
    native static String jniMessageRaw(long commitPtr);

    /**
     * Get the full raw message of a commit.
     *
     * @return the raw message of a commit
     */
    public String messageRaw() {
        return jniMessageRaw(getRawPointer());
    }

    static native String jniSummary(long commitPtr);

    /**
     * Get the short "summary" of the git commit message.
     *
     * The returned message is the summary of the commit, comprising the
     * first paragraph of the message with whitespace trimmed and squashed.
     *
     * @return the summary of a commit or NULL on error
     */
    public String summary() {
        return jniSummary(getRawPointer());
    }

    static native String jniBody(long commitPtr);

    /**
     * Get the long "body" of the git commit message.
     *
     * The returned message is the body of the commit, comprising
     * everything but the first paragraph of the message. Leading and
     * trailing whitespaces are trimmed.
     *
     * @return the body of a commit or NULL when no the message only
     *   consists of a summary
     */
    public String body() {
        return jniBody(getRawPointer());
    }

    static native long jniTime(long commitPtr);

    /**
     * Get the commit time (i.e. committer time) of a commit.
     *
     * @return the time of a commit
     */
    public Instant time() {
        return Instant.ofEpochSecond(jniTime(getRawPointer()));
    }

    static native int jniTimeOffset(long commitPtr);

    /**
     * Get the commit timezone offset (i.e. committer's preferred timezone) of a commit.
     *
     * @return positive or negative timezone offset, in minutes from UTC
     */
    public int timeOffset() {
        return jniTimeOffset(getRawPointer());
    }


    static native void jniCommitter(long commitPtr, Signature outSig);

    /**
     * Get the committer of a commit.
     *
     * @return the committer of a commit
     */
    public Signature committer() {
        Signature outSig = new Signature();
        jniCommitter(getRawPointer(), outSig);
        return outSig;
    }

    static native void jniAuthor(long commitPtr, Signature outSig);

    /**
     * Get the author of a commit.
     *
     * @return the author of a commit
     */
    public Signature author() {
        Signature outSig = new Signature();
        jniAuthor(getRawPointer(), outSig);
        return outSig;
    }

    static native int jniCommitterWithMailmap(Signature outSig, long commitPtr, long mailmapPtr);

    /**
     * Get the committer of a commit, using the mailmap to map names and email
     * addresses to canonical real names and email addresses.
     *
     * @param mailmap the mailmap to resolve with. (may be null)
     * @return signature that contains the committer identity
     * @throws GitException git errors
     */
    public Signature committerWithMailmap(Mailmap mailmap) {
        Signature outSig = new Signature();
        Error.throwIfNeeded(jniCommitterWithMailmap(outSig, getRawPointer(), mailmap == null ? 0 : mailmap.getRawPointer()));
        return outSig;
    }

    static native int jniAuthorWithMailmap(Signature outSig, long commitPtr, long mailmapPtr);

    /**
     * Get the author of a commit, using the mailmap to map names and email
     * addresses to canonical real names and email addresses.
     *
     * @param mailmap the mailmap to resolve with. (may be NULL)
     * @return signature that contains the author information
     * @throws GitException git errors
     */

    public Signature authorWithMailmap(Mailmap mailmap) {
        Signature outSig = new Signature();
        Error.throwIfNeeded(jniAuthorWithMailmap(outSig, getRawPointer(), mailmap == null ? 0 : mailmap.getRawPointer()));
        return outSig;
    }

    static native String jniRawHeader(long commitPtr);

    /**
     * Get the full raw text of the commit header.
     *
     * @return the header text of the commit
     */
    public String rawHeader() {
        return jniRawHeader(getRawPointer());
    }

    static native int jniTree(AtomicLong outTreePtr, long commitPtr);

    /**
     * Get the tree pointed to by a commit. Equiv to {@code git rev-parse "$COMMIT^{tree}"}
     * or {@code git cat-file $COMMIT | grep tree}
     *
     * @return tree object
     * @throws GitException git errors
     */
    public Tree tree() {
        AtomicLong outTreePtr = new AtomicLong();
        Error.throwIfNeeded(jniTree(outTreePtr, getRawPointer()));
        return new Tree(outTreePtr.get());
    }
}
