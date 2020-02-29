#!/bin/bash

JAR_FILE_PATH='target/google-java-format.jar'
TOP=$(git rev-parse --show-toplevel)

_get_jar() {
    pushd $TOP
    if [[ -f $JAR_FILE_PATH ]]; then
        return
    fi
    curl -o $JAR_FILE_PATH https://repo1.maven.org/maven2/com/google/googlejavaformat/google-java-format/1.7/google-java-format-1.7-all-deps.jar
    popd
}

lint_file() {
    local java_file=$1
    [[ ! -f $java_file ]] && return 1
    java -jar target/google-java-format.jar -i -a $java_file
}

lint_all() {
    pushd $TOP
    while IFS= read -r line; do
        echo java -jar target/google-java-format.jar  -i -a $line; 
        java -jar target/google-java-format.jar  -i -a $line; 
    done < <(find src/main/java/com/github/git24j/core -name '*.java' -print)
    while IFS= read -r line; do
        echo java -jar target/google-java-format.jar  -i -a $line; 
        java -jar target/google-java-format.jar  -i -a $line; 
    done < <(find src/test/java/com/github/git24j/core -name '*.java' -print)
    popd
}

lint_diff() {
    pushd $TOP
    local ndiff=$1
    while IFS= read -r line; do
        echo java -jar target/google-java-format.jar  -i -a $line; 
        java -jar target/google-java-format.jar  -i -a $line; 
    done < <(git diff "HEAD~${ndiff}" HEAD --name-only -- src/main/java/)

    while IFS= read -r line; do
        echo java -jar target/google-java-format.jar  -i -a $line; 
        java -jar target/google-java-format.jar  -i -a $line; 
    done < <(git diff "HEAD~${ndiff}" HEAD --name-only -- src/test/java/)
    popd
}

lint_changes() {
    pushd $TOP
    while IFS= read -r line; do
        echo java -jar target/google-java-format.jar  -i -a $line;
        java -jar target/google-java-format.jar  -i -a $line;
    done < <(git status | grep '.java' | cut -d':' -f2)
    popd
}

_info() {
    echo "Usage: "
    echo "  lint [diff [num] | all | filename | download | changes]"
    echo ""
}

if [[ $1 == "download" ]]; then
    _get_jar
elif [[ $1 == "diff" ]]; then
    lint_diff $2
elif [[ $1 == "all" ]]; then
    lint_all
elif [[ $1 == "changes" ]]; then
    lint_changes
elif [[ -f $1 ]]; then
    lint_file $1
else
    _info
fi
