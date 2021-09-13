
# Git24J 
[![Build Status](https://travis-ci.com/git24j/git24j.svg?branch=master)](https://travis-ci.com/git24j/git24j)

> Git24J, (git2 library for java) is a java bindings to the [libgit2](http://libgit2.github.com/) project.

Still in active development, pull requests are welcomed.

## Quick Start

Three steps to get started with `git24j` in a java application:
#### Step 1, add dependency:
```
<dependency>
    <groupId>com.github.git24j</groupId>
    <artifactId>git24j</artifactId>
    <version>${version}</version>
</dependency>
```

The latest `version` can be found from [git2@maven central](https://search.maven.org/artifact/com.github.git24j/git24j/1.0.0.20200914/jar)

#### Step 2, build shared libraries:
```
$ git clone https://github.com/git24j/git24j.git dist/git24j
$ git -C dist/git24j/ submodule sync --recursive
$ git -C dist/git24j/ submodule update --init --recursive
$ make -C dist/git24j/ 
```
#### Step 3, start using git24j
```java
public class Main {
    public static void main(String[] args) {
        // Setup
        Init.loadLibraries(null, null);
        Libgit2.init();

        Path repoDir = Paths.get(System.getProperty("user.dir"));
        try (Repository repo = Repository.open(repoDir)){
            System.out.println(repo.workdir());
        }
        // Optional: Shutdown and clean up libgit2 global states.
        Libgit2.shutdown();
    }
}
``` 

More in-detailed examples:
1. [git24j-maven-example](https://github.com/git24j/git24j-maven-example)
2. [git24j-gradle-example](https://github.com/git24j/git24j-gradle-example)

### Demo of usage in Groovy

```
$ git clone https://github.com/git24j/git24j.git
$ cd git24j
$ git submodule sync --recursive
$ git submodule update --init --recursive
$ make && mvn package
$ groovysh
groovy:000> import java.nio.file.Paths
groovy:000> clone = Paths.get(".")
groovy:000> libgit2 = clone.resolve("target/git2/libgit2.so")
groovy:000> libgit24j = clone.resolve("target/git24j/libgit24j.so")
groovy:000> jarGit24j = clone.resolve("target/git24j-1.0.0.20200914.jar")
groovy:000> this.class.classLoader.rootLoader.addURL(jarGit24j.toUri().toURL())
groovy:000> import com.github.git24j.core.*
groovy:000> Init.loadLibraries(libgit2, libgit24j)
groovy:000> Libgit2.init()
groovy:000> repo = Repository.open(clone)
```

[![asciicast](https://asciinema.org/a/tE0KZN3v5epJyQBLHfmJgy87K.svg)](https://asciinema.org/a/tE0KZN3v5epJyQBLHfmJgy87K)

## Usages
- Checkout [samples.md](doc/samples.md) for some basic git24j usages
- Checkout [API Doc](https://git24j.github.io/) for git24j apis
- Checkout various [unit tests](src/test/java/com/github/git24j/core) for usages of individual APIs.

## Development prerequisites: git, cmake, libssl, maven
```bash
# example setup in ubunut
$ sudo apt-get install openjdk-8-jdk cmake libssl-dev maven
$ export JAVA_HOME='/usr/lib/jvm/java-1.8.0-openjdk-amd64/'
```