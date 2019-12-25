
# Git24J 
> Git24J, (git2 library for java) is a java bindings to the [libgit2](http://libgit2.github.com/) project.

Still in active development, pull requests are welcomed.

## Prerequisites: git, cmake, libssl, maven
```bash
# example setup in ubunut
$ sudo apt-get install openjdk-8-jdk cmake libssl-dev maven
$ export JAVA_HOME='/usr/lib/jvm/java-11-openjdk-amd64/' # setting path to build jni

```
## Quick Start

```bash
$ git clone https://github.com/git24j/git24j.git && cd git24j
$ git submodule sync --recursive && git submodule update --init --recursive
$ mvn test
$ mvn install
``` 

