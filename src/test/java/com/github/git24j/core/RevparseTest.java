package com.github.git24j.core;

import java.util.EnumSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RevparseTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void call() {
        Repository repository = TestRepo.SIMPLE1.tempRepo(folder);
        Revparse.Revspec revspec = Revparse.call(repository, "HEAD");
        Assert.assertTrue(revspec.from.getRawPointer() > 0);
        Assert.assertEquals(EnumSet.of(Revparse.Mode.SINGLE), revspec.flags);
    }

    @Test
    public void single() {
        Repository repository = TestRepo.SIMPLE1.tempRepo(folder);
        GitObject obj = Revparse.single(repository, "HEAD");
        Assert.assertTrue(obj.getRawPointer() > 0);
    }

    @Test
    public void ext() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        Revparse.ExtReturn ret = Revparse.ext(testRepo, "master");
        Assert.assertNotNull(ret.getObj());
        Assert.assertNotNull(ret.getRef());
    }
}
