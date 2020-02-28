package com.github.git24j.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigTest extends TestBase {
    private static final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";
    private static final String MASTER_PARENT_HASH = "565ddbe0bd55687b43286889a8ead64f68301113";
    private static final String MASTER_TREE_HASH = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    private static final String MERGE_COMMIT = "7dcb276ed40ce9223fd522f5166c25572d10d428";
    private static final Oid MASTER_OID = Oid.of(MASTER_HASH);
    private static final List<String> CONFIG_LINES =
            Arrays.asList(
                    "# a comment",
                    "; also a comment",
                    "[http]",
                    "\tsslVerify = false",
                    "\tcookieFile = /tmp/cookie.txt");

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    private void verifyConfigPath(Path p) {
        Assert.assertTrue(Files.exists(p));
    }

    @Test
    public void find() {
        Config.findGlobal().ifPresent(this::verifyConfigPath);
        Config.findXdg().ifPresent(this::verifyConfigPath);
        Config.findSystem().ifPresent(this::verifyConfigPath);
        Config.findProgramdata().ifPresent(this::verifyConfigPath);
    }

    @Test
    public void openDefault() {
        try (Config cfg = Config.openDefault()) {
            Assert.assertTrue(cfg.getRawPointer() > 0);
        }
    }

    @Test
    public void newConfig() {
        try (Config cfg = Config.newConfig()) {
            Assert.assertTrue(cfg.getRawPointer() > 0);
        }
    }

    @Test
    public void addFileOnDisk() throws IOException {
        File f = folder.newFile("ext.config");
        FileUtils.writeLines(f, CONFIG_LINES);
        try (Config cfg = Config.newConfig()) {
            cfg.addFileOndisk(f.toPath(), ConfigLevel.LOCAL, null, false);
            Assert.assertFalse(cfg.getBool("http.sslVerify").orElse(true));
        }
    }

    @Test
    public void openOnDisk() throws IOException {
        File f = folder.newFile("ext.config");
        FileUtils.writeLines(f, CONFIG_LINES);
        try (Config cfg = Config.openOndisk(f.toPath())) {
            Assert.assertFalse(cfg.getBool("http.sslVerify").orElse(true));
        }
    }

    @Test
    public void openLocal() throws IOException {
        File f = folder.newFile("ext.config");
        FileUtils.writeLines(f, CONFIG_LINES);
        try (Config parent = Config.openOndisk(f.toPath())) {
            Config.openLevel(ConfigLevel.LOCAL, parent);
        }
    }
}
