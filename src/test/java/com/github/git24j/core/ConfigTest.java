package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigTest extends TestBase {
    private final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";
    private final String MASTER_PARENT_HASH = "565ddbe0bd55687b43286889a8ead64f68301113";
    private final String MASTER_TREE_HASH = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    private final String MERGE_COMMIT = "7dcb276ed40ce9223fd522f5166c25572d10d428";
    private final Oid MASTER_OID = Oid.of(MASTER_HASH);

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
        try(Config cfg = Config.openDefault()){
            Assert.assertTrue(cfg.getRawPointer() > 0);
        }
    }

    @Test
    public void newConfig() {
        try(Config cfg = Config.newConfig()){
            Assert.assertTrue(cfg.getRawPointer() > 0);
        }
    }
}
