package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

public class Transaction extends CAutoReleasable {
    protected Transaction(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    /** -------- Jni Signature ---------- */
    /** int git_transaction_new(git_transaction **out, git_repository *repo); */
    static native int jniNew(AtomicLong out, long repoPtr);

    /** int git_transaction_lock_ref(git_transaction *tx, const char *refname); */
    static native int jniLockRef(long tx, String refname);

    /** int git_transaction_set_target(git_transaction *tx, const char *refname, const git_oid *target, const git_signature *sig, const char *msg); */
    static native int jniSetTarget(long tx, String refname, Oid target, long sig, String msg);

    /** int git_transaction_set_symbolic_target(git_transaction *tx, const char *refname, const char *target, const git_signature *sig, const char *msg); */
    static native int jniSetSymbolicTarget(long tx, String refname, String target, long sig, String msg);

    /** int git_transaction_set_reflog(git_transaction *tx, const char *refname, const git_reflog *reflog); */
    static native int jniSetReflog(long tx, String refname, long reflog);

    /** int git_transaction_remove(git_transaction *tx, const char *refname); */
    static native int jniRemove(long tx, String refname);

    /** int git_transaction_commit(git_transaction *tx); */
    static native int jniCommit(long tx);

    /** void git_transaction_free(git_transaction *tx); */
    static native void jniFree(long tx);


}
