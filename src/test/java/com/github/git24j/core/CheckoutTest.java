package com.github.git24j.core;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CheckoutTest extends TestBase {

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void head() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            int e = Checkout.head(testRepo, Checkout.Options.defaultOptions());
            Assert.assertEquals(0, e);
        }
    }

    @Test
    public void index() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Index idx = testRepo.index()) {
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
