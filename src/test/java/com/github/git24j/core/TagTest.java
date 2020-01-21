package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    private static final String TAG_V01_SHA = "d9420a0ba5cbe2efdb1c3927adc1a2dd9355caff";
    private static final String TAG_V01_TARGET = "67a36754360b373d391af2182f9ad8929fed54d8";

    @Test
    public void target() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tag v01 = Tag.lookup(testRepo, Oid.of(TAG_V01_SHA));
            Assert.assertEquals(TAG_V01_TARGET, v01.target().id().toString());
            Assert.assertEquals(TAG_V01_TARGET, v01.targetId().toString());
            Assert.assertEquals(GitObject.Type.COMMIT, v01.targetType());
        }
    }

    @Test
    public void nameAndTaggerMessage() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tag v01 = Tag.lookup(testRepo, Oid.of(TAG_V01_SHA));
            Assert.assertEquals("v0.1", v01.name());
            Signature tagger = v01.tagger();
            Assert.assertNotNull(tagger.getName());
            Assert.assertNotNull(tagger.getEmail());
            Assert.assertNotNull(v01.message());
        }
    }

    @Test
    public void listDelete() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            List<String> tags = Tag.list(testRepo);
            Assert.assertTrue(tags.contains("v0.1"));
            tags = Tag.listMatch("v*", testRepo);
            Assert.assertTrue(tags.contains("v0.1"));
            Tag.delete(testRepo, "v0.1");
            Assert.assertFalse(Tag.list(testRepo).contains("v0.1"));
        }
    }

    @Test
    public void foreach() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Map<String, String> tags = new HashMap<>();
            Tag.foreach(testRepo, (name, oid) -> {
                tags.put(name, oid);
                return 0;
            });
            Assert.assertEquals(tags.get("refs/tags/v0.1"), TAG_V01_SHA);
        }
    }

    @Test
    public void peel() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tag v01 = Tag.lookup(testRepo, Oid.of(TAG_V01_SHA));
            GitObject v01Target = v01.peel();
            Assert.assertEquals(TAG_V01_TARGET, v01Target.id().toString());
        }
    }
}
