package com.github.git24j.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/** Memory representation of a set of config files */
public class Config extends CAutoCloseable {

    public Config(long rawPointer) {
        super(rawPointer);
    }

    static native void jniFree(long ptr);

    static native int jniGetStringBuf(Buf buf, long cfgPtr, String name);

    static native int jniFindGlobal(Buf buf);

    /**
     * Locate the path to the global configuration file
     *
     * @return path where global configuration is stored.
     */
    public static Optional<Path> findGlobal() {
        Buf buf = new Buf();
        Error.throwIfNeeded(jniFindGlobal(buf));
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return Optional.empty();
        }
        return Optional.of(Paths.get(buf.getPtr().substring(0, buf.getSize())));
    }

    @Override
    public void close() {
        if (_rawPtr.get() > 0) {
            jniFree(_rawPtr.getAndSet(0));
        }
    }

    /**
     * Get the value of a string config variable.
     *
     * @param name the variable's name
     * @return value of a string config variable
     */
    public Optional<String> getString(String name) {
        Buf buf = new Buf();
        Error.throwIfNeeded(jniGetStringBuf(buf, getRawPointer(), name));
        return buf.getString();
    }
}
