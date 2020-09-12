package com.github.git24j.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class StashTest extends TestBase {
    private final Signature signature = Signature.create("tester", "tester@example.com");

    private Repository prepareRepo() {
        Repository testRepo = creteTestRepo(TestRepo.SIMPLE1);
        try {
            FileUtils.writeStringToFile(
                    testRepo.workdir().resolve("new_file.txt").toFile(),
                    "some content",
                    StandardCharsets.UTF_8);
            return testRepo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void saveAndForeach() {
        try (Repository testRepo = prepareRepo()) {
            Oid stashId =
                    Stash.save(
                            testRepo,
                            signature,
                            "stash",
                            EnumSet.of(Stash.Flags.INCLUDE_UNTRACKED));
            Assert.assertFalse(stashId.isEmpty());
            List<Map.Entry<String, String>> stashList = new ArrayList<>();
            Stash.foreach(
                    testRepo,
                    (index, message, stashId1) -> {
                        stashList.add(new AbstractMap.SimpleEntry<>(message, stashId1.toString()));
                        return 0;
                    });
            Assert.assertEquals(1, stashList.size());
            Assert.assertEquals(stashId.toString(), stashList.get(0).getValue());
        }
    }

    @Test
    public void drop() {
        try (Repository testRepo = prepareRepo()) {
            Stash.save(testRepo, signature, "stash", EnumSet.of(Stash.Flags.INCLUDE_UNTRACKED));
            Stash.drop(testRepo, 0);
            Stash.foreach(
                    testRepo,
                    (index, message, stashId1) -> {
                        Assert.fail("all stashed states should have been dropped");
                        return 0;
                    });
        }
    }

    @Test
    public void pop() {
        try (Repository testRepo = prepareRepo()) {
            Stash.save(testRepo, signature, "stash", EnumSet.of(Stash.Flags.INCLUDE_UNTRACKED));
            Stash.pop(testRepo, 0, null);
            EnumSet<Status.StatusT> status = Status.file(testRepo, "new_file.txt");
            Assert.assertEquals(EnumSet.of(Status.StatusT.WT_NEW), status);
        }
    }

    @Test
    public void apply() {
        try (Repository testRepo = prepareRepo()) {
            Stash.save(testRepo, signature, "stash", EnumSet.of(Stash.Flags.INCLUDE_UNTRACKED));
            Stash.apply(testRepo, 0, null);
            EnumSet<Status.StatusT> status = Status.file(testRepo, "new_file.txt");
            Assert.assertEquals(EnumSet.of(Status.StatusT.WT_NEW), status);
            List<Map.Entry<String, String>> stashList = new ArrayList<>();
            Stash.foreach(
                    testRepo,
                    (index, message, stashId1) -> {
                        stashList.add(new AbstractMap.SimpleEntry<>(message, stashId1.toString()));
                        return 0;
                    });
            Assert.assertEquals(1, stashList.size());
        }
    }
}
