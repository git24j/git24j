package com.github.git24j.core;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ErrorTest {
    @BeforeClass
    public static void setUpClass() {
        Init.loadLibraries();
        Libgit2.init();
    }

    @AfterClass
    public static void tearDownClass() {
        Libgit2.shutdown();
    }

    @Test
    public void jniSetStr() {
        Error.jniSetStr(GitException.ErrorClass.SSL.ordinal(), "fake error");
        GitException lastError = Error.jniLast();
        Assert.assertEquals(GitException.ErrorClass.SSL, lastError.getErrorClass());
        Assert.assertEquals("fake error", lastError.getMessage());

        Error.jniClear();
        lastError = Error.jniLast();
        Assert.assertNull(lastError);
    }
}
