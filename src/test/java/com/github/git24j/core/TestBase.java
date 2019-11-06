package com.github.git24j.core;

import org.apache.commons.io.IOUtils;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TestBase {
    private static final String RESOURCE_ROOT = "";

    public TestBase() {
        Init.loadLibraries();
        Libgit2.init();
    }

    private static void zipTo(Path zip, Path outputDir) throws IOException {
        ZipFile zipFile = new ZipFile(zip.toFile());
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path entryDestination = outputDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryDestination);
                } else {
                    Files.createDirectories(entryDestination.getParent());
                    InputStream in = zipFile.getInputStream(entry);
                    OutputStream out = Files.newOutputStream(entryDestination);
                    IOUtils.copy(in, out);
                    IOUtils.closeQuietly(in);
                    out.close();
                }
            }
        } finally {
            zipFile.close();
        }
    }

    /** Create a copy of given test repo and return the path. */
    public static Path tempCopyOf(TestRepo repo, Path tempFolder) {
        Path zip =
                Optional.ofNullable(
                                Thread.currentThread()
                                        .getContextClassLoader()
                                        .getResource(repo.zipFileName()))
                        .map(URL::getFile)
                        .map(f -> Paths.get(f))
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Unable to locate test data for: "
                                                        + repo.getName()));
        try {
            zipTo(zip, tempFolder);
            return tempFolder.resolve(repo.getName());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to extract test data to " + tempFolder.toAbsolutePath().toString(), e);
        }
    }

    public enum TestRepo {
        SIMPLE1("simple1"),
        SIMPLE1_BARE("simple1_bare"),
        MERGE1("merge1"),
        WORKTREE1("worktree1");
        private final String name;

        TestRepo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String zipFileName() {
            return name + ".zip";
        }

        public Path tempCopy(TemporaryFolder folder) {
            return TestBase.tempCopyOf(this, folder.getRoot().toPath());
        }

        public Repository tempRepo(TemporaryFolder folder) {
            return Repository.open(this.tempCopy(folder).toString());
        }
    }

    /**
     * Test two paths are effectively the same (respect soft and hardlinks).
     * @param path1
     * @param path2
     * @return
     */
    public static boolean sameFile(Path path1, Path path2) {
        try {
            if (Files.isSameFile(path1, path2)) {
                return true;
            }
            if (Files.getAttribute(path1, "unix:ino") == Files.getAttribute(path2, "unix:ino")) {
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
