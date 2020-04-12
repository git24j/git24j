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

### Clone with callbacks
