package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

public class GitObjectTest extends TestBase {

    @Test
    public void search() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        GitObject obj = Revparse.single(testRepo, "HEAD");
        Assert.assertEquals(GitObject.Type.COMMIT, obj.type());
        GitObject searched = GitObject.lookup(testRepo, obj.id(), GitObject.Type.COMMIT);
        Assert.assertEquals(GitObject.Type.COMMIT, searched.type());
        Assert.assertEquals(searched.getRawPointer(), obj.getRawPointer());
        try {
            GitObject.lookupPrefix(testRepo, "123456abcde", GitObject.Type.COMMIT);
            Assert.fail("should have thrown ENOTFOUND error");
        } catch (GitException e) {
            Assert.assertEquals(GitException.ErrorCode.ENOTFOUND, e.getCode());
        }
    }

    @Test
    public void lookup() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        GitObject obj = Revparse.single(testRepo, "HEAD");
        GitObject lookedUp =
                GitObject.lookupPrefix(
                        testRepo, obj.id().toString().substring(0, 10), GitObject.Type.COMMIT);
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
        GitObject peel = obj.peel(GitObject.Type.COMMIT);
        Assert.assertNotNull(peel);
    }

    @Test
    public void dup() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        GitObject obj = Revparse.single(testRepo, "HEAD");
        GitObject obj2 = new GitObject(true, obj.getRawPointer());
        GitObject copy = obj2.dup();
        Assert.assertEquals(copy.shortId().getPtr(), obj.shortId().getPtr());
    }
}
