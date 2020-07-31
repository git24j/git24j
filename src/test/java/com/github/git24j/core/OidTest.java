package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

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
    public void fromByteArray() {
        Assert.assertEquals(Oid.of(SHA_A), Oid.of(Oid.hexStringToByteArray(SHA_A)));
    }
}
