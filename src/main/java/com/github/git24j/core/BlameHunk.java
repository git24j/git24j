package com.github.git24j.core;

/**
 * Structure that represents a blame hunk.
 *
 * <pre>
 * - `lines_in_hunk` is the number of lines in this hunk
 * - `final_commit_id` is the OID of the commit where this line was last
 *   changed.
 * - `final_start_line_number` is the 1-based line number where this hunk
 *   begins, in the final version of the file
 * - `final_signature` is the author of `final_commit_id`. If
 *   `GIT_BLAME_USE_MAILMAP` has been specified, it will contain the canonical
 *    real name and email address.
 * - `orig_commit_id` is the OID of the commit where this hunk was found.  This
 *   will usually be the same as `final_commit_id`, except when
 *   `GIT_BLAME_TRACK_COPIES_ANY_COMMIT_COPIES` has been specified.
 * - `orig_path` is the path to the file where this hunk originated, as of the
 *   commit specified by `orig_commit_id`.
 * - `orig_start_line_number` is the 1-based line number where this hunk begins
 *   in the file named by `orig_path` in the commit specified by
 *   `orig_commit_id`.
 * - `orig_signature` is the author of `orig_commit_id`. If
 *   `GIT_BLAME_USE_MAILMAP` has been specified, it will contain the canonical
 *    real name and email address.
 * - `boundary` is 1 iff the hunk has been tracked to a boundary commit (the
 *   root, or the commit specified in git_blame_options.oldest_commit)
 * </pre>
 */
public class BlameHunk extends CAutoReleasable {
    protected BlameHunk(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        Libgit2.jniShadowFree(cPtr);
    }
}
