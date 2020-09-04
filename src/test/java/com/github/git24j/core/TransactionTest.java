package com.github.git24j.core;

import org.junit.Test;

public class TransactionTest extends TestBase {

    private static final String FEATURE_DEV_SHA = "e5b28427ba064002e0e343e783ea3095018ce72c";

    @Test
    public void create() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Transaction tx = Transaction.create(testRepo);
            tx.lockRef("testref");
            tx.setTarget(
                    "testref",
                    Oid.of(FEATURE_DEV_SHA),
                    Signature.create("tester", "tester@abc.com"),
                    "message");
            tx.setSymbolicTarget(
                    "testref",
                    "ref3-target",
                    Signature.create("tester", "tester@abc.com"),
                    "message");
            tx.remove("testref");
            tx.commit();
        }
    }
}
