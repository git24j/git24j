package com.github.git24j.core;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

/** @author shijing Setup functions */
public class Init {

    // TODD: change me
    private static final String CUSTOM_LIB_PATH_PROP = "com.github.git24j.lg24j.library_path";
    private static final String JAVA_LIB_PATH_PROP = "java.library.path";
    private static final String DEFAULT_LIB_PATH = "target";
    private static AtomicBoolean _loaded = new AtomicBoolean(false);

    /**
     * Search following paths(in order) for the native library, load if found, throw exceptions if
     * not.
     *
     * <ul>
     *   <li>paths defined in {@code com.github.git24j.lg24j.library_path}
     *   <li>paths defined in {@code java.library.path}
     *   <li>hard coded path in ${project.dir}/target/${lib}/
     * </ul>
     *
     * @param lib library to load
     * @throws FileNotFoundException could not load the shared library
     */
    static synchronized void loadLibrary(NativeLib lib) throws FileNotFoundException {
        String customPath = System.getProperty(CUSTOM_LIB_PATH_PROP, null);
        if (customPath != null) {
            System.setProperty(
                    JAVA_LIB_PATH_PROP,
                    customPath + ":" + System.getProperty(JAVA_LIB_PATH_PROP, ""));
        }
        try {
            System.loadLibrary(lib.shortName());
        } catch (UnsatisfiedLinkError ignore) {
            // ignore
        }
        Path libPath = Paths.get(DEFAULT_LIB_PATH, lib.shortName(), lib.mappedLibraryName());
        if (!Files.exists(libPath)) {
            throw new FileNotFoundException("Could not load library: " + lib.mappedLibraryName());
        }
        System.load(libPath.toAbsolutePath().toString());
    }

    /**
     * Load necessary libraries.
     *
     * @throws RuntimeException if
     */
    public static void loadLibraries() {
        if (!_loaded.get()) {
            try {
                loadLibrary(NativeLib.GIT2);
                loadLibrary(NativeLib.GIT24J);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            _loaded.set(true);
        }
    }

    private enum NativeLib {
        /** libgit2 */
        GIT2("git2"),
        /** libgit24j */
        GIT24J("git24j");
        private final String _name;

        NativeLib(String name) {
            _name = name;
        }
        /** platform-specific name of the lib, e.g. libgit2.so (linux) and libgit2.dll (windows). */
        String mappedLibraryName() {
            return System.mapLibraryName(_name);
        }
        /** short name like git2 and git24j. */
        String shortName() {
            return _name;
        }
    }
}
