# Various options and callbacks in libgit2

- git_clone_options
    - git_checkout_options
    - git_fetch_options
    + git_repository_create_cb
    + git_remote_create_cb

 - _git_checkout_options_
    + git_checkout_notify_cb
    + git_checkout_progress_cb
    + git_checkout_perfdata_cb

 - _git_fetch_options_
    - git_proxy_options
    + git_remote_callbacks
 - git_push_options
    - git_proxy_options
    + git_remote_callbacks

 - _git_proxy_options_
    - git_credential_acquire_cb
    - git_transport_certificate_check_cb
    + git_credential_acquire_cb
    + git_transport_certificate_check_cb

 + _git_remote_callbacks_
    + git_transport_message_cb
    + git_remote_completion callback
    + git_credential_acquire_cb
    + git_transport_certificate_check_cb
    + git_indexer_progress_cb
    + update_tips
    + git_packbuilder_progress
    + git_push_transfer_progress_cb
    + git_push_update_reference_cb
    + git_push_negotiation
    + git_url_resolve_cb

- git_cherrypick_options
    - git_merge_options
    - git_checkout_options

- git_blame_options
- git_describe_options
- git_describe_format_options
- git_diff_format_email_options
- git_diff_find_options
- git_diff_patchid_options
- _git_merge_options_
- git_merge_file_options
- git_remote_create_options
- git_repository_init_options
- git_status_options
- git_worktree_add_options
- git_worktree_prune_options

- git_diff_options
    + git_diff_notify_cb
    + git_diff_progress_cb

- git_indexer_options
    + git_indexer_progress_cb

- git_rebase_options
    - git_merge_options
    - git_checkout_options

- git_revert_options
    - git_merge_options
    - git_checkout_options

- git_stash_apply_options
    - git_checkout_options
    + git_stash_apply_progress_cb

- git_submodule_update_options
    - git_checkout_options
    - git_fetch_options

- git_apply_options
    + git_apply_delta_cb
    + git_apply_hunk_cb
