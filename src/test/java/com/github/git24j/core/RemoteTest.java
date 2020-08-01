package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RemoteTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void basics() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Remote.addFetch(testRepo, "origin", "+refs/heads/*:refs/remotes/origin/*");
            Remote r = Remote.lookup(testRepo, "origin");
            Assert.assertTrue(r.getRefspec(0).isPresent());
            Assert.assertFalse(r.getFetchRefspecs().isEmpty());
            Assert.assertTrue(r.getPushRefspecs().isEmpty());
            Remote.AutotagOptionT optionT = r.autotag();
            Assert.assertEquals(Remote.AutotagOptionT.AUTO, optionT);
        }
    }

    @Test
    public void connect() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Remote r = Remote.create(testRepo, "local", testRepo.workdir().toUri());
            Remote.Callbacks callbacks = Remote.Callbacks.createDefault();
            r.connect(Remote.Direction.FETCH, callbacks, null, null);
            Assert.assertTrue(r.connected());
        }
    }

    @Test
    public void create() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Remote r = Remote.create(testRepo, "local", testRepo.workdir().toUri());
            r.fetch(null, null, null);
            Remote r2 = Remote.createAnonymous(testRepo, testRepo.workdir().toUri());
            r2.fetch(null, null, null);
            Remote r3 = Remote.createDetached(testRepo.workdir().toUri());
            try {
                r3.fetch(null, null, null);
                Assert.fail("should have failed for 'cannot download detached remote'");
            } catch (GitException e) {
                Assert.assertEquals(e.getMessage(), "cannot download detached remote");
            }
            Remote r4 =
                    Remote.createWithFetchspec(
                            testRepo, "local2", testRepo.workdir().toUri(), null);
            r4.fetch(null, null, null);

            Remote.CreateOptions opts = Remote.CreateOptions.createDefault();
            // TODO: add getter setters for CreateOptions
            Remote r5 = Remote.createWithOpts(testRepo.workdir().toUri(), opts);
            Assert.assertNotNull(r5);
            // r5.fetch(null, null, null);
        }
    }

    @Test
    public void createWithOpts() {}

    @Test
    public void defaultBranch() {}

    @Test
    public void delete() {}

    @Test
    public void disconnect() {}

    @Test
    public void download() {}

    @Test
    public void dup() {}

    @Test
    public void fetch() {}

    @Test
    public void isValidName() {}

    @Test
    public void list() {}

    @Test
    public void lookup() {}

    @Test
    public void name() {}

    @Test
    public void owner() {}

    @Test
    public void prune() {}

    @Test
    public void pruneRefs() {}

    @Test
    public void push() {}

    @Test
    public void pushurl() {}

    @Test
    public void refspecCount() {}

    @Test
    public void rename() {}

    @Test
    public void setAutotag() {}

    @Test
    public void setPushurl() {}

    @Test
    public void setUrl() {}

    @Test
    public void stats() {}

    @Test
    public void stop() {}

    @Test
    public void updateTips() {}

    @Test
    public void updaload() {}

    @Test
    public void url() {}
}
