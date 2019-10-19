package com.github.git24j.core;

public class HelloWorld {
    public static native void displayMessage();

    public static native void cut(
            String audioPath, long startTime, long duration, String outputPath);

    public void call() {
        displayMessage();
    }
}
