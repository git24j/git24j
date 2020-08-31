package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RefspecTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void common() {
        String refspecStr = "+refs/heads/*:refs/remotes/origin/*";
        Refspec spec = Refspec.parse(refspecStr, false);
        Assert.assertEquals("refs/heads/*", spec.src());
        Assert.assertEquals("refs/remotes/origin/*", spec.dst());
        Assert.assertEquals(refspecStr, spec.getString());
        Assert.assertTrue(spec.force());
        Assert.assertEquals(Remote.Direction.PUSH, spec.direction());
        Assert.assertTrue(spec.srcMatches("refs/heads/master"));
        Assert.assertFalse(spec.srcMatches("refs/tags/master"));
        Assert.assertTrue(spec.dstMatches("refs/remotes/origin/test-br"));
        Assert.assertFalse(spec.dstMatches("refs/origin/test-br"));
        Assert.assertEquals("refs/remotes/origin/master", spec.transform("refs/heads/master"));
        Assert.assertEquals("refs/heads/master", spec.rtransform("refs/remotes/origin/master"));

        try {
            spec.transform("refs/remotes/heads/master");
            Assert.fail("should have failed");
        } catch (GitException e) {
            Assert.assertNotNull(e);
            Assert.assertEquals(e.getErrorClass(), GitException.ErrorClass.INVALID);
            Assert.assertTrue(e.getMessage().contains("doesn't match the source"));
        }
    }
}
