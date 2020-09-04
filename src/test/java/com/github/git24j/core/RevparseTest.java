package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.EnumSet;

public class RevparseTest extends TestBase {

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
