package com.github.git24j.core;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.git24j.core.Index.Capability.IGNORE_CASE;
import static com.github.git24j.core.Index.Capability.NO_SYMLINKS;

public class IndexTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void open() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx =
                    Index.open(Paths.get(testRepo.getPath()).resolve("index").toString())) {
                Assert.assertNotNull(idx);
            }
        }
    }

    @Test
    public void updateAll() throws IOException {
        final String fname = "a";
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx =testRepo.index()) {
                Map<String, String> cache = new HashMap<>();
                Path p = testRepo.workdir().resolve(fname);
                Files.write(p, Arrays.asList("line1", "line2"));
                idx.updateAll(Arrays.asList(fname), cache::put);
                idx.write();
                Assert.assertEquals(fname, cache.get(fname));
            }
        }
    }

    @Test
    public void owner() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx =testRepo.index()) {
                Assert.assertEquals(testRepo, idx.owner());
            }
        }
    }

    @Test
    public void caps() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx =testRepo.index()) {
                EnumSet<Index.Capability> caps = EnumSet.of(IGNORE_CASE, NO_SYMLINKS);
                idx.setCaps(caps);
                Assert.assertEquals(caps, idx.caps());
            }
        }
    }

    @Test
    public void version() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx =testRepo.index()) {
                idx.setVersion(3);
                Assert.assertEquals(3, idx.version());
            }
        }
    }
    @Test
    public void addAll() throws IOException {
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
    public void addByPath() throws IOException {
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
