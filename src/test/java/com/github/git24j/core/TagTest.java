package com.github.git24j.core;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
            Tag v02 = Tag.lookupPrefix(testRepo, "d9420a0ba5cbe");
            Assert.assertEquals(v01.id(), v02.id());
        }
    }

    @Test
    public void dup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tag v01 = Tag.lookup(testRepo, Oid.of(TAG_V01_SHA));
            Tag v02 = v01.dup();
            Assert.assertEquals(v01.id(), v02.id());
        }
    }

    @Test
    public void create() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Oid id =
                    Tag.create(
                            testRepo,
                            "unit-test-tag",
                            Commit.lookup(testRepo, Oid.of(TAG_V01_TARGET)),
                            new Signature("tester", "tester@abc.cc", OffsetDateTime.now()),
                            "message",
                            false);
            Assert.assertFalse(id.isEmpty());
        }
    }

    @Test
    public void createFromBuffer() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Oid id =
                    Tag.createFromBuffer(
                            testRepo,
                            "object 67a36754360b373d391af2182f9ad8929fed54d8\n"
                                    + "type commit\n"
                                    + "tag v0.2\n"
                                    + "tagger Shijing Lu <shijing.lu@gmail.com> 1569090490 -0400\n"
                                    + "\n"
                                    + "second tag\n",
                            false);
            Assert.assertFalse(id.isEmpty());
            Assert.assertEquals(id, GitObject.lookup(testRepo, id, GitObject.Type.ANY).id());
        }
    }

    @Test
    public void annotatedCreate() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Oid out =
                    Tag.annotationCreate(
                            testRepo,
                            "unittest-annotated-tag",
                            Commit.lookup(testRepo, Oid.of(TAG_V01_TARGET)),
                            Signature.now("tester", "tester@abc.cc"),
                            "creating annotated tag from test");
            Assert.assertFalse(out.isEmpty());
        }
    }

    @Test
    public void createLightWeight() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Oid id =
                    Tag.createLightWeight(
                            testRepo,
                            "test-tag-lw",
                            Commit.lookup(testRepo, Oid.of(TAG_V01_TARGET)),
                            false);
            Assert.assertFalse(id.isEmpty());
            Assert.assertEquals(id, GitObject.lookup(testRepo, id, GitObject.Type.ANY).id());
        }
    }

    @Test
    public void nameAndTaggerMessage() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tag v01 = Tag.lookup(testRepo, Oid.of(TAG_V01_SHA));
            Assert.assertEquals("v0.1", v01.name());
            Signature tagger = v01.tagger().orElseThrow(RuntimeException::new);
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
            Tag.foreach(
                    testRepo,
                    (name, oid) -> {
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
