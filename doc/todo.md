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
- [x] Rebase
- [x] Remote
- [x] Revwalk  (lack of public interface)
- [x] Submodule  (lack of public interface)
- [x] Worktree  (lack of public interface)
- [x] Note (lack of public interface)
- [x] Tag 
- [x] Status
- [ ] Odb (prioritize)
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
- [ ] API for for customized backend

## House Keeping
- [ ] Avoid calling `getObjectClass` excessively by promoting classes of AtomicLong, AtomicReference to constant.
- [ ] Consider simplify `Oid` to just bytes, find out all return (const oid *) cases and have it return jbyteArray instead.
- [x] jni::GetObjectClass segfaults if object is null, so make sure every call is protected.
- [ ] Replace `assert` with better alternatives (many build system disables assert by default).
- [ ] Rename `consumer` in callbacks to `callback`
- [ ] Buf is a stupid idea, replace it with plain String or a POJO
- [ ] Unify all enums
- [ ] Formalize CAutoReleaseable vs CAutoClosable
- [x] Unify behavior of git objects, aka {@code Commit}, {@code Tag}, {@code Tree} or {@code Blob} 
- [x] Make sure git objects are free-ed with `git_object_free`, make sure they all 
