package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AnnotatedCommitTest extends TestBase {
    private final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void fromRef() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Revparse.ExtReturn ret = Revparse.ext(testRepo, "master");
            Reference ref = ret.getRef();
            AnnotatedCommit ac = AnnotatedCommit.fromRef(testRepo, ref);
            Assert.assertEquals(MASTER_HASH, ac.id().toString());
            Assert.assertEquals("refs/heads/master", ac.ref());
        }
    }

    @Test
    public void fromFetchHead() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            AnnotatedCommit ac =
                    AnnotatedCommit.fromFetchHead(
                            testRepo, "master", "src/test/resources/simple1", Oid.of(MASTER_HASH));
            Assert.assertEquals(MASTER_HASH, ac.id().toString());
        }
    }

    @Test
    public void lookup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            AnnotatedCommit ac = AnnotatedCommit.lookup(testRepo, Oid.of(MASTER_HASH));
            Assert.assertEquals(MASTER_HASH, ac.id().toString());
        }
    }

    @Test
    public void fromRevspec() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            AnnotatedCommit ac = AnnotatedCommit.fromRevspec(testRepo, "HEAD");
            Assert.assertEquals(MASTER_HASH, ac.id().toString());
        }
    }
}
