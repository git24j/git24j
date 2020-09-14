
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
    <version>1.0.0.20200914</version>
</dependency>
```
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