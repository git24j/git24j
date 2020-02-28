package com.github.git24j.core;

public enum CheckoutStrategyT implements IBitEnum {
    /** default is a dry run, no actual updates */
    NONE(0),

    /** Allow safe updates that cannot overwrite uncommitted data */
    SAFE(1 << 0),

    /** Allow all updates to force working directory to look like index */
    FORCE(1 << 1),

    /** Allow checkout to recreate missing files */
    RECREATE_MISSING(1 << 2),

    /** Allow checkout to make safe updates even if conflicts are found */
    ALLOW_CONFLICTS(1 << 4),

    /** Remove untracked files not in index (that are not ignored) */
    REMOVE_UNTRACKED(1 << 5),

    /** Remove ignored files not in index */
    REMOVE_IGNORED(1 << 6),

    /** Only update existing files, don't create new ones */
    UPDATE_ONLY(1 << 7),

    /**
     * Normally checkout updates index entries as it goes; this stops that. Implies
     * `DONT_WRITE_INDEX`.
     */
    DONT_UPDATE_INDEX(1 << 8),

    /** Don't refresh index/config/etc before doing checkout */
    NO_REFRESH(1 << 9),

    /** Allow checkout to skip unmerged files */
    SKIP_UNMERGED(1 << 10),
    /** For unmerged files, checkout stage 2 from index */
    USE_OURS(1 << 11),
    /** For unmerged files, checkout stage 3 from index */
    USE_THEIRS(1 << 12),

    /** Treat pathspec as simple list of exact match file paths */
    DISABLE_PATHSPEC_MATCH(1 << 13),

    /** Ignore directories in use, they will be left empty */
    SKIP_LOCKED_DIRECTORIES(1 << 18),

    /** Don't overwrite ignored files that exist in the checkout target */
    DONT_OVERWRITE_IGNORED(1 << 19),

    /** Write normal merge files for conflicts */
    CONFLICT_STYLE_MERGE(1 << 20),

    /** Include common ancestor data in diff3 format files for conflicts */
    CONFLICT_STYLE_DIFF3(1 << 21),

    /** Don't overwrite existing files or folders */
    DONT_REMOVE_EXISTING(1 << 22),

    /** Normally checkout writes the index upon completion; this prevents that. */
    DONT_WRITE_INDEX(1 << 23),
    /** Recursively checkout submodules with same options (NOT IMPLEMENTED) */
    UPDATE_SUBMODULES(1 << 16),
    /** Recursively checkout submodules if HEAD moved in super repo (NOT IMPLEMENTED) */
    UPDATE_SUBMODULES_IF_CHANGED(1 << 17);
    private final int _bit;

    CheckoutStrategyT(int bit) {
        _bit = bit;
    }

    @Override
    public int getBit() {
        return _bit;
    }
}
