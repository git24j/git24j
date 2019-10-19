package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitObjectTest extends TestBase {

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void search() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        GitObject obj = Revparse.single(testRepo, "HEAD");
        Assert.assertEquals(GitObject.Type.COMMIT, obj.type());
        GitObject searched = GitObject.lookup(testRepo, obj.id(), GitObject.Type.COMMIT);
        Assert.assertEquals(GitObject.Type.COMMIT, searched.type());
        Assert.assertEquals(searched.getRawPointer(), obj.getRawPointer());
    }

    @Test
    public void lookup() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        GitObject obj = Revparse.single(testRepo, "HEAD");
        GitObject lookedUp = GitObject.lookupPrefix(testRepo, obj.id(), 7, GitObject.Type.COMMIT);
        Assert.assertEquals(GitObject.Type.COMMIT, lookedUp.type());
        Assert.assertEquals(lookedUp.getRawPointer(), obj.getRawPointer());
    }

    @Test
    public void shortId() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        GitObject obj = Revparse.single(testRepo, "master");
        Buf buf = obj.shortId();
        Assert.assertTrue(buf.getAsize() > 0);
    }

    @Test
    public void owner() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        GitObject obj = Revparse.single(testRepo, "HEAD");
        Repository owner = obj.owner();
        Assert.assertEquals(testRepo.getPath(), owner.getPath());
    }

    @Test
    public void peel() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        GitObject obj = Revparse.single(testRepo, "HEAD");
        try (GitObject peel = obj.peel(GitObject.Type.COMMIT)) {
            Assert.assertNotNull(peel);
        }
    }

    @Test
    public void dup() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        try (GitObject obj = Revparse.single(testRepo, "HEAD")) {
            try (GitObject copy = obj.dup()) {
                Assert.assertEquals(copy.shortId().getPtr(), obj.shortId().getPtr());
            }
        }
    }
}
