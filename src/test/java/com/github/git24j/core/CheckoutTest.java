package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

public class CheckoutTest extends TestBase {

    @Test
    public void head() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            int e = Checkout.head(testRepo, Checkout.Options.defaultOptions());
            Assert.assertEquals(0, e);
        }
    }

    @Test
    public void index() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Index idx = testRepo.index();
            Assert.assertEquals(
                    0, Checkout.index(testRepo, idx, Checkout.Options.defaultOptions()));
        }
    }

    @Test
    public void tree() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Assert.assertEquals(0, Checkout.tree(testRepo, null, null));
        }
    }
}
