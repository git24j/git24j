package com.github.git24j.core;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class IndexTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void indexAddAll() throws IOException {
        Path repoPath = tempCopyOf(TestRepo.SIMPLE1, folder.getRoot().toPath());
        FileUtils.writeStringToFile(repoPath.resolve("a").toFile(), "test");
        Map<String, String> cbParams = new HashMap<>();
        try (Repository repo = Repository.open(repoPath.toString())) {
            try (Index index = repo.index()) {
                index.addAll(
                        new String[] {"."}, EnumSet.of(Index.AddOption.DEFAULT), cbParams::put);
                index.write();
            }
        }
        Assert.assertEquals(cbParams.get("a"), ".");
    }

    @Test
    public void indexAddByPath() throws IOException {
        Path repoPath = tempCopyOf(TestRepo.SIMPLE1, folder.getRoot().toPath());
        FileUtils.writeStringToFile(repoPath.resolve("a").toFile(), "add index by path");
        try (Repository repo = Repository.open(repoPath.toString())) {
            try (Index index = repo.index()) {
                index.add("a");
                index.write();
            }
        }
        FileUtils.copyDirectory(repoPath.toFile(), new File("/tmp/test-indexAddByPath"));
    }
}
