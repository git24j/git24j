package com.github.git24j.core;

import java.util.EnumSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RevparseTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void lookup() {
        try (Repository repository = TestRepo.SIMPLE1.tempRepo(folder)) {
            GitObject h2 = Revparse.single(repository, "HEAD~2");
            GitObject h = Revparse.single(repository, "HEAD");
            Revparse.Revspec revspec = Revparse.lookup(repository, "HEAD^^..HEAD");
            Assert.assertEquals(revspec.getFrom().id(), h2.id());
            Assert.assertEquals(revspec.getTo().id(), h.id());
            Assert.assertEquals(EnumSet.of(Revparse.Mode.RANGE), revspec.getFlags());
        }
    }

    @Test
    public void single() {
        try (Repository repository = TestRepo.SIMPLE1.tempRepo(folder)) {
            GitObject obj = Revparse.single(repository, "HEAD");
            Assert.assertTrue(obj.getRawPointer() > 0);
        }
    }

    @Test
    public void ext() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Revparse.ExtReturn ret = Revparse.ext(testRepo, "master");
            Assert.assertNotNull(ret.getObj());
            Assert.assertNotNull(ret.getRef());
        }
    }
}
