package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;

public class RefdbTest extends TestBase {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void open() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository testRepo = Repository.open(path)) {
            Refdb refdb = Refdb.open(testRepo);
            Assert.assertNotNull(refdb);
            refdb.compress();
        }
    }
}