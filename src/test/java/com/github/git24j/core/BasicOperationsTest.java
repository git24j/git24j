package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static com.github.git24j.core.Repository.InitFlagT.MKPATH;

public class BasicOperationsTest extends TestBase {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void initSimple() throws Exception {
        Path path = folder.newFolder("simpleRepo").toPath();
        try (Repository repo = Repository.init(path, false)) {
            Assert.assertTrue(repo.headUnborn());
        }
    }

    @Test
    public void initWithOptions() throws IOException {
        Repository.InitOptions opts = Repository.InitOptions.defaultOpts();
        opts.setDescription("My repository has a custom description");

        opts.setFlags(EnumSet.of(MKPATH));
        try (Repository repo =
                     Repository.initExt(folder.newFolder("tmp").getAbsolutePath(), opts)) {
            Assert.assertTrue(repo.headUnborn());
        }
    }

    @Test
    public void simpleClone() throws IOException {
        Path localPath = folder.newFolder("newClone2").toPath();
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Repository cloned =
                         Clone.cloneRepo(testRepo.workdir().toString(), localPath, null)) {
                Assert.assertEquals(localPath, cloned.workdir());
            }
        }
    }


    @Test
    public void cloneWithCallback() throws Exception {
        Clone.Options opts = Clone.Options.defaultOpts();
        Map<String, Integer> progressTrack = new HashMap<>();
        opts.getCheckoutOpts()
                .setProgressCb((path, completedSteps, totalSteps) -> {
                    progressTrack.put(path, completedSteps);
                    System.out.println(
                            "path="
                                    + path
                                    + ", step: "
                                    + completedSteps
                                    + ", total steps = "
                                    + totalSteps);
                });

        Path localPath = folder.newFolder("newClone2").toPath();
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Repository cloned =
                         Clone.cloneRepo(testRepo.workdir().toString(), localPath, opts)) {
            }
            Assert.assertTrue(progressTrack.size() > 0);
        }
    }
}
