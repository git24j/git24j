package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class OidTest {

    private static final String SHA_A = "c33dfe912b2984d56283f0605f02dced514be799";
    private static final String SHA_A_CAP = "C33DFE912B2984D56283F0605F02DCED514BE799";
    private static final String SHA_INVALID = "0123456789abcdefghijklmnopqrstuvwxyzABCD";

    @Test
    public void toAndFromStr() {
        Assert.assertEquals(SHA_A, Oid.of(SHA_A_CAP).toString());
        try {
            Oid.of(SHA_INVALID);
            Assert.fail("Invalid sha should throw an exception");
        } catch (IllegalArgumentException ingnore) {
            // ignore
        }
    }

    @Test
    public void shortId() {
        // "c" -> "", because every two hexidecimal digits are translated into one byte
        Assert.assertEquals("", Oid.of("c").toString());
        Assert.assertEquals("c3", Oid.of("c3").toString());
        Assert.assertEquals("c3", Oid.of("c3d").toString());
        Assert.assertEquals("c3df", Oid.of("c3df").toString());
    }
}