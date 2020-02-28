package com.github.git24j.core;

public enum DiffOptionT implements IBitEnum {
    /** Normal diff, the default */
    NORMAL(0),

    /*
     * Options controlling which files will be in the diff
     */

    /** Reverse the sides of the diff */
    REVERSE(1 << 0),

    /** Include ignored files in the diff */
    INCLUDE_IGNORED(1 << 1),

    /**
     * Even with GIT_DIFF_INCLUDE_IGNORED, an entire ignored directory will be marked with only a
     * single entry in the diff; this flag adds all files under the directory as IGNORED entries,
     * too.
     */
    RECURSE_IGNORED_DIRS(1 << 2),

    /** Include untracked files in the diff */
    INCLUDE_UNTRACKED(1 << 3),

    /**
     * Even with GIT_DIFF_INCLUDE_UNTRACKED, an entire untracked directory will be marked with only
     * a single entry in the diff (a la what core Git does in `git status`); this flag adds *all*
     * files under untracked directories as UNTRACKED entries, too.
     */
    RECURSE_UNTRACKED_DIRS(1 << 4),

    /** Include unmodified files in the diff */
    INCLUDE_UNMODIFIED(1 << 5),

    /**
     * Normally, a type change between files will be converted into a DELETED record for the old and
     * an ADDED record for the new; this options enabled the generation of TYPECHANGE delta records.
     */
    INCLUDE_TYPECHANGE(1 << 6),

    /**
     * Even with GIT_DIFF_INCLUDE_TYPECHANGE, blob->tree changes still generally show as a DELETED
     * blob. This flag tries to correctly label blob->tree transitions as TYPECHANGE records with
     * new_file's mode set to tree. Note: the tree SHA will not be available.
     */
    INCLUDE_TYPECHANGE_TREES(1 << 7),

    /** Ignore file mode changes */
    IGNORE_FILEMODE(1 << 8),

    /** Treat all submodules as unmodified */
    IGNORE_SUBMODULES(1 << 9),

    /** Use case insensitive filename comparisons */
    IGNORE_CASE(1 << 10),

    /**
     * May be combined with `GIT_DIFF_IGNORE_CASE` to specify that a file that has changed case will
     * be returned as an add/delete pair.
     */
    INCLUDE_CASECHANGE(1 << 11),

    /**
     * If the pathspec is set in the diff options, this flags indicates that the paths will be
     * treated as literal paths instead of fnmatch patterns. Each path in the list must either be a
     * full path to a file or a directory. (A trailing slash indicates that the path will _only_
     * match a directory). If a directory is specified, all children will be included.
     */
    DISABLE_PATHSPEC_MATCH(1 << 12),

    /**
     * Disable updating of the `binary` flag in delta records. This is useful when iterating over a
     * diff if you don't need hunk and data callbacks and want to avoid having to load file
     * completely.
     */
    SKIP_BINARY_CHECK(1 << 13),

    /**
     * When diff finds an untracked directory, to match the behavior of core Git, it scans the
     * contents for IGNORED and UNTRACKED files. If *all* contents are IGNORED, then the directory
     * is IGNORED; if any contents are not IGNORED, then the directory is UNTRACKED. This is extra
     * work that may not matter in many cases. This flag turns off that scan and immediately labels
     * an untracked directory as UNTRACKED (changing the behavior to not match core Git).
     */
    ENABLE_FAST_UNTRACKED_DIRS(1 << 14),

    /**
     * When diff finds a file in the working directory with stat information different from the
     * index, but the OID ends up being the same, write the correct stat information into the index.
     * Note: without this flag, diff will always leave the index untouched.
     */
    UPDATE_INDEX(1 << 15),

    /** Include unreadable files in the diff */
    INCLUDE_UNREADABLE(1 << 16),

    /** Include unreadable files in the diff */
    INCLUDE_UNREADABLE_AS_UNTRACKED(1 << 17),

    /*
     * Options controlling how output will be generated
     */

    /**
     * Use a heuristic that takes indentation and whitespace into account which generally can
     * produce better diffs when dealing with ambiguous diff hunks.
     */
    INDENT_HEURISTIC(1 << 18),

    /** Treat all files as text, disabling binary attributes & detection */
    FORCE_TEXT(1 << 20),
    /** Treat all files as binary, disabling text diffs */
    FORCE_BINARY(1 << 21),

    /** Ignore all whitespace */
    IGNORE_WHITESPACE(1 << 22),
    /** Ignore changes in amount of whitespace */
    IGNORE_WHITESPACE_CHANGE(1 << 23),
    /** Ignore whitespace at end of line */
    IGNORE_WHITESPACE_EOL(1 << 24),

    /**
     * When generating patch text, include the content of untracked files. This automatically turns
     * on GIT_DIFF_INCLUDE_UNTRACKED but it does not turn on GIT_DIFF_RECURSE_UNTRACKED_DIRS. Add
     * that flag if you want the content of every single UNTRACKED file.
     */
    SHOW_UNTRACKED_CONTENT(1 << 25),

    /**
     * When generating output, include the names of unmodified files if they are included in the
     * git_diff. Normally these are skipped in the formats that list files (e.g. name-only,
     * name-status, raw). Even with this, these will not be included in patch format.
     */
    SHOW_UNMODIFIED(1 << 26),

    /** Use the "patience diff" algorithm */
    PATIENCE(1 << 28),
    /** Take extra time to find minimal diff */
    MINIMAL(1 << 29),

    /**
     * Include the necessary deflate / delta information so that `git-apply` can apply given diff
     * information to binary files.
     */
    SHOW_BINARY(1 << 30),
    ;

    final int bit;

    DiffOptionT(int bit) {
        this.bit = bit;
    }

    @Override
    public int getBit() {
        return bit;
    }
}
