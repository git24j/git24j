# Plan of work

## Core APIs
- [x] GitObject
- [x] Repository
- [x] Reference
- [x] Commit
- [x] Branch
- [x] Config
- [x] Index
- [x] Diff
- [x] Merge (no test)
- [x] Rebase (no test)
- [x] Remote
- [x] Revwalk
- [x] Submodule
- [x] Worktree
- [x] Note
- [x] Tag
- [x] Status
- [ ] Cred
- [ ] Patch
- [ ] Checkout
- [ ] Signature
- [ ] Refspec



## API polishing
- [ ] Make sure APIs have consistent behavior, for example, return null object vs. throwing `ENOTFOUND` error.
- [ ] Annotate nullabilities
- [ ] Return Optionals instead of nullable entities
- [ ] Many APIs can also be part of `Repository`, for example `Commit.lookup(Repository repo)` can be `repo.lookupCommit(...)`

## Administrative
- [x] Basic make/build/test framework
- [x] Minimum docs
- [x] adding ci
- [ ] docs
- [ ] publish to jitpack.io or github
- [ ] publish to maven central
- [ ] replace most (if not all) FindObjectClass with using global classes
- [ ] bump libgit2 to use HEAD


## Future
- [ ] API for for customized backend

## House Keeping
- [ ] Avoid calling `getObjectClass` excessively by promoting classes of AtomicLong, AtomicReference to constant.
- [ ] Consider simplify `Oid` to just String (aka remove bytes details)
- [x] jni::GetObjectClass segfaults if object is null, so make sure every call is protected.
- [ ] Replace `assert` with better alternatives (many build system disables assert by default).
- [ ] Rename `consumer` in callbacks to `callback`
- [ ] Buf is a stupid idea, replace it with plain String or a POJO
- [ ] Unify all enums