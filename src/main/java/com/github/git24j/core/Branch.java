package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

public class Branch {
    static final native int jniCreate(
            AtomicLong outRef, long repoPtr, String branchName, long targetPtr, int force);

    /**
     * Create a new branch pointing at a target commit.
     *
     * @param repo Repository
     * @param branchName Name for the branch; this name is validated for consistency. It should also
     *     not conflict with an already existing branch name.
     * @param target Commit to which this branch should point. This object must belong to the given
     *     `repo`.
     * @param force Overwrite existing branch.
     * @return git reference that must be closed by the caller.
     * @throws GitException git error.
     * @throws IllegalStateException if repository has already been closed.
     */
    public static Reference create(Repository repo, String branchName, Commit target, int force) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniCreate(outRef, repo.getRawPointer(), branchName, target.getRawPointer(), force));
        return new Reference(outRef.get());
    }
}
