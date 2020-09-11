# Plan of work

## Core APIs
- [x] Annotated
- [x] Apply
- [x] Attr
- [x] Blame
- [x] Blob
- [x] Branch
- [x] Buf
- [x] Checkout
- [x] Cherrypick
- [x] Clone
- [x] Commit
- [x] Config
- [x] Credential
- [x] Describe
- [x] Diff
- [x] Error
- [x] Fetch
- [x] Filter
- [x] Giterr
- [x] Graph
- [x] Ignore
- [x] Index (IndexEntry)
- [x] Indexer
- [x] Libgit2
- [x] Mailmap
- [x] Merge
- [x] Message
- [x] Note
- [x] Object (GitObject)
- [x] Odb
- [x] Oid
- [x] Oidarray
- [x] Packbuilder
- [x] Patch
- [x] Pathspec
- [x] Proxy
- [x] Push
- [x] Rebase
- [x] Refdb
- [x] Reference
- [x] Reflog
- [x] Refspec
- [x] Remote
- [x] Repository
- [x] Reset
- [x] Revert
- [x] Revparse
- [x] Revwalk
- [x] Signature
- [x] Stash
- [x] Status
- [x] Strarray
- [x] Submodule
- [x] Tag
- [ ] Trace (api incomplete)
- [x] Transaction
- [x] Tree
- [x] Treebuilder (Tree.Builder)
- [x] Worktree



## API polishing
- [ ] Make sure APIs have consistent behavior, for example, return null object vs. throwing `ENOTFOUND` error.
- [ ] Annotate nullabilities
- [x] Return Optionals instead of nullable entities
- [x] Remove usage of `Optional`, upstream api may change from returning nullable to non-null
- [ ] Many APIs can also be part of `Repository`, for example `Commit.lookup(Repository repo)` can be `repo.lookupCommit(...)`
- [ ] Formalize class and enum naming, move some java inner classes to separate class files. Names of classes and enums should follow `git_classname_methodname` convention. 

## Administrative
- [x] Basic make/build/test framework
- [x] Minimum docs
- [x] adding ci
- [ ] docs
- [ ] publish to jitpack.io or github
- [ ] publish to maven central
- [ ] replace most (if not all) FindObjectClass with using global classes


## Future
- [ ] Customized backend

## House Keeping
- [ ] Avoid calling `getObjectClass` excessively by promoting classes of AtomicLong, AtomicReference to constant.
- [ ] Consider simplify `Oid` to just bytes, find out all return (const oid *) cases and have it return jbyteArray instead.
- [x] jni::GetObjectClass segfaults if object is null, so make sure every call is protected.
- [ ] Replace `assert` with better alternatives (many build system disables assert by default).
- [ ] Buf is a stupid idea, replace it with plain String or a POJO
- [ ] *All callbacks should immediately return if consumer is null*
- [ ] All callback should handle null payload case
- [ ] Check all `data_structure **out pattern`, code gen may broken in this case, especially pattern like `jniAbcNew`
- [ ] Make sure `equals` and `hashcode` makes sense. Equality of simple structures should compare by fields instead of raw pointer
- [ ] Change all `AtomicReference<String>` to use jniConstants
- [ ] Change `CURRENT_VERSION` in Options structures to `VERSION`
- [ ] Every DeleteLocalRef after NewStringUTF(...) must perform a null check! 
- [ ] Rewrite the callbacks
- [ ] All places that have `type foo(References **out)`, should return weak reference unless specified
- [x] Deprecate `j_git_oid_to_java`, return `byte[]` directly.
- [ ] Replace Buf with AtomicString for returning String through parameters.
- [x] Change Index to `CAutoReleasable`
