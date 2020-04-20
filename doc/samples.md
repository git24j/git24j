# git24j samples

### Setting path and init the library

`git24j` is a jni bindings for libgit2 library, its search paths are defined 
through java properties, more specifically, though following two properties:
1. com.github.git24j.lg24j.library_path
2. java.library.path

After setting the right searching path, git24j must be explicitly loaded and
initialized, sample code: 
```
public void static main(String [] argv) {
    System.setProperty("com.github.git24j.lg24j.library_path", "../libgit2/");
    Init.loadLibraries();
    Libgit2.init();
}
```

see also: [TestBase](../src/test/java/com/github/git24j/core/TestBase.java#L21)

### Errors
All libgit2 errors are translated to `GitExceptions`, sample code:

```
try {
   // some git24j operations
} catch (GitException e) {
    if (e.getCode() == GitException.ErrorCode.ENOTFOUND) {
        System.out.println("entity not found");
    }
    if (e.getErrorClass() == GitException.ErrorClass.INDEX) {
        System.out.println("An index error");
    }
}
```
see also [RepositoryTest](../src/test/java/com/github/git24j/core/ReferenceTest.java#186)

### Create Libgit2 objects
Most git24j objects represents a libgit2 struct, and constructors are banned, meaning you 
should only create those object through public exposed static methods, and set properties
later. For example:
```
Repository.InitOptions opts = Repository.InitOptions.defaultOpts();
opts.setDescription("My repository has a custom description");
```
see also [BasicOperationsTest.initWithOptions](../src/test/java/com/github/git24j/core/BasicOperationsTest.java#25)

### Clone
Clone operation is self-explanatory according to it [API](../src/main/java/com/github/git24j/core/Clone.java#226)
```
try (Repository repo = Clone.cloneRepo("http://github.com/abc/awesome", localPath, null)) {
    Assert.assertEquals(localPath, cloned.workdir());
}
```
see also [BasicOperationsTest.simpleClone](../src/test/java/com/github/git24j/core/BasicOperationsTest.java#37)

### Clone and monitor progress
One can monitor clone progress by adding a callback to `Clone.Options`
```
Clone.Options opts = Clone.Options.defaultOpts();
opts.getCheckoutOpts()
                .setProgressCb((path, completedSteps, totalSteps) -> {
                    progressTrack.put(path, completedSteps);
                    System.out.printf("path=%s, step=%d, total steps=%d %n", path, completedSteps, totalSteps);
                });
```
At runtime the output looks like:
> path=null, step=0, total steps=6 
> 
> path=.gitignore, step=1, total steps=6 
> 
> path=README.md, step=2, total steps=6 
> 
> path=a, step=3, total steps=6 
> 
> path=b, step=4, total steps=6 
> 
> path=c, step=5, total steps=6 
> 
> path=d, step=6, total steps=6 

see also [BasicOperationsTest.cloneWithProgressCallbak](../src/test/java/com/github/git24j/core/BasicOperationsTest.java#53)


### Clone (Custom repo and remote)
One can replace the default repository creation process of clone with a customized one. This is done by setting `Options.RepositoryCreateCb`

```
Clone.Options opts = Clone.Options.defaultOpts();
opts.setRemoteCreateCb(((repo, name, url) -> Remote.create(repo, name, URI.create(url))));
opts.setRepositoryCreateCb((path, bare) -> Repository.init(Paths.get(path), true));
```

see also [BasicOperationsTest.cloneWithRepositoryAndRemoteCallback](../src/test/java/com/github/git24j/core/BasicOperationsTest.java#76)


### Open Repository
To open a repository, one can use `Repository.open(...)`
```
try (Repository repo = Repository.open(repoPath)) {
    // repo operations
}
```
more examples [RepositoryTest.java](../src/test/java/com/github/git24j/core/ReferenceTest.java#17)

### Open repository with options
```
# open without search
Repository.openExt(repoPath, EnumSet.of(Repository.OpenFlag.NO_SEARCH), null)
try (Repository repo2 = Repository.openExt(sub, null, "/tmp:/home:/usr"))
```
more details: [RepositoryTest.openExt](../src/test/java/com/github/git24j/core/ReferenceTest.java#68)

### Open bare
```
Repository.openBare(repoPath)
```
more details: [RepositoryTest.openBare](../src/test/java/com/github/git24j/core/ReferenceTest.java#45)

### Find repository
```
Repository.discover(Paths.get("/tmp/foo/bar/sub"), true, "/tmp:/home");
```
more details: [RepositoryTest.discover](../src/test/java/com/github/git24j/core/ReferenceTest.java#85)


## Objects

There are four types of `GitObject`: Commit, Tag, Tree and Blob. They share following common methods:

- `public static GitObject lookup(Repository repository, Oid oid, Type type)`
- `public static GitObject lookupPrefix(Repository repository, Oid oid, int len, Type type)`
- `public Type type()`
- `public Oid id()`
- `public Buf shortId()`
- `public GitObject peel(Type targetType)`
- `public GitObject dup()`
- `public Repository owner()`

### SHAs and OIDs

Commonly used APIS are
- `Oid.of(String)` create Oid object from SHA string
- `Oid.of(byte[])` create Oid object from bytes
- `Oid.toString()` get SHA string of oid

Internally, Oid stores stores the data in a fixed size (length of 20) byte array. And can be interpreted as 40 length characters. 

Note: use `Oid.of(byte[])` judiciously. Don't use it like `Oid.of("c33dfe912b2984d5".getBytes())`. If you would like to get Oid from string, simply use `Oid.of(String)`.
 It is because Oid internally combine two hex chars into one byte. For example, `"a1".getBytes()` will return `byte[] {97, 49}`, but in Oid, "a1" will be combined into one byte, aka `(10 << 4) + 1 = 161`. 

### Lookup

```
Commit commit = Commit.lookup(repo, Oid.of("012345abcde"));
Tree tree = Tree.lookup(repo, Oid.of("abcde012345"));
Blob blob = Blob.lookup(repo, Oid.of("0a1b2c3d4e"));
```

More examples: [CommitTest.java](../src/test/java/com/github/git24j/core/CommitTest.java)

### Casting

GitObject is the super class of `Commit`, `Tag`, `Tree` and `Blob`. So GitObject can be casted to sub types directly, for example
```
 return (Commit) GitObject.lookup(repo, oid, Type.COMMIT);
```

## Blobs

### lookup

```
Blob.lookup(testRepo, Oid.of("012345abcde"));
```
### Content

```
blob1.filteredContent("README.md", true);
```
more details: [BlobTest.filteredContent](../src/test/java/com/github/git24j/core/BlobTest.java)


## Trees

### get tree from commit

```
Tree masterTree = commit.tree();
```
see also: [Commit.tree](../src/test/java/com/github/git24j/core/CommitTest.java#163)

### lookup tree from tree hash directly
```
Tree t1 = Tree.lookup(testRepo, Oid.of("8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e"));
```
see also: [TreeTest.lookup](../src/test/java/com/github/git24j/core/TreeTest.java#13)

### Tree entries

```
Optional<Tree.Entry> maybeE0 = tree.entryByIndex(0);
Optional<Tree.Entry> maybeE1 = tree.entryByName("README.md");
```

### Walking

```
tree.walk(Tree.WalkMode.PRE, ((root, entry) -> {
    entryNames.add(entry.name());
    return 0;
}));
``` 
see also: [TreeTest.walk](../src/test/java/com/github/git24j/core/TreeTest.java#46)

### TreeBuilder

```
Tree.Builder bld = Tree.newBuilder(testRepo, null);
bld.insert("README.md", obj1.id(), FileMode.BLOB);
```
see also: [TreeTest.treeBuilder](../src/test/java/com/github/git24j/core/TreeTest.java#59)


## Commits

### Lookup
```
Commit commit = Commit.lookup(testRepo, MASTER_OID);
```
see also [CommitTest.lookup](../src/test/java/com/github/git24j/core/CommitTest.java#20)

### Properties
```
System.out.printf("      oid: %s %n", commit.id());
System.out.printf(" encoding: %s %n", commit.id());
System.out.printf("  message: %s %n", commit.message());
System.out.printf("  summary: %s %n", commit.summary());
System.out.printf("     time: %s %n", commit.time());
System.out.printf("   offset: %d %n", commit.timeOffset());
System.out.printf("committer: %s %n", commit.committer());
System.out.printf("   author: %s %n", commit.author());
System.out.printf("   header: %s %n", commit.rawHeader());
System.out.printf("  tree_id: %s %n", commit.treeId());
```
see also [CommitTest.lookup](../src/test/java/com/github/git24j/core/CommitTest.java#36)

### Parents
```
commit.parent(0);
commit.nthGenAncestor(1);
```
see also: [CommitTest.lookup](../src/test/java/com/github/git24j/core/CommitTest.java#208)

### Create

```
Commit.create(
    testRepo,
    "NEW_HEAD",
    Signature.now("tester", "test@ab.cc"),
    Signature.now("admin", "admin@ab.cc"),
    null,
    "some commit message",
    masterTree,
    Collections.singletonList(master));
```
see also: [CommitTest.create](../src/test/java/com/github/git24j/core/CommitTest.java#246)

