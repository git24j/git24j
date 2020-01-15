package com.github.git24j.core;

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
}
