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
