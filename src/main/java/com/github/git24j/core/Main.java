package com.github.git24j.core;

public class Main {
    static {
        Init.loadLibraries();
    }

    public static void main(String[] args) {
        Libgit2.init();
    }

    public String sayHello() {
        return "hello";
    }
}
