package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;

public class BasicOperationsTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void initSimple() throws Exception {
        Path path = folder.newFolder("simpleRepo").toPath();
        try (Repository repo = Repository.init(path, false)) {
            Assert.assertTrue(repo.headUnborn());
        }
    }

    @Test
    public void initWithOptions() throws IOException {
        Path path = folder.newFolder("initWithOptions").toPath();
        Repository.InitOptions opts =  Repository.InitOptions.defaultOpts(1);
        try (Repository repo = Repository.initExt(path.toString(), opts)) {
            Assert.assertTrue(repo.headUnborn());
        }
    }
}
