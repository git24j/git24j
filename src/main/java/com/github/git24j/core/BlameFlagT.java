package com.github.git24j.core;

public enum BlameFlagT implements IBitEnum {
    /** Normal blame, the default */
    NORMAL(0),
    /** Track lines that have moved within a file (like `git blame -M`). NOT IMPLEMENTED. */
    TRACK_COPIES_SAME_FILE(1 << 0),
    /**
     * Track lines that have moved across files in the same commit (like `git blame -C`). NOT
     * IMPLEMENTED.
     */
    TRACK_COPIES_SAME_COMMIT_MOVES(1 << 1),
    /**
     * Track lines that have been copied from another file that exists in the same commit (like
     * `git blame -CC`). Implies SAME_FILE. NOT IMPLEMENTED.
     */
    TRACK_COPIES_SAME_COMMIT_COPIES(1 << 2),
    /**
     * Track lines that have been copied from another file that exists in *any* commit (like
     * `git blame -CCC`). Implies SAME_COMMIT_COPIES. NOT IMPLEMENTED.
     */
    TRACK_COPIES_ANY_COMMIT_COPIES(1 << 3),
    /** Restrict the search of commits to those reachable following only the first parents. */
    FIRST_PARENT(1 << 4),
    /**
     * Use mailmap file to map author and committer names and email addresses to canonical real
     * names and email addresses. The mailmap will be read from the working directory, or HEAD
     * in a bare repository.
     */
    USE_MAILMAP(1 << 5);
    private final int _bit;

    BlameFlagT(int bit) {
        _bit = bit;
    }

    @Override
    public int getBit() {
        return _bit;
    }
}