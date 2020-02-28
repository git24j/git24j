package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BlameTest extends TestBase {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void jniFile() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blame blame = Blame.file(testRepo, "README.md", null);
            Assert.assertFalse(blame.isNull());
            Assert.assertTrue(blame.getHunkCount() > 0);
        }
    }
}