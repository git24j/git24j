package com.github.git24j.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TransactionTest extends TestBase {

    private final static String FEATURE_DEV_SHA = "e5b28427ba064002e0e343e783ea3095018ce72c";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Test
    public void create() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Transaction tx = Transaction.create(testRepo);
            tx.lockRef("testref");
            tx.setTarget("testref", Oid.of(FEATURE_DEV_SHA), Signature.now("tester", "tester@abc.com"), "message");
            tx.setSymbolicTarget("testref", "ref3-target", Signature.now("tester", "tester@abc.com"), "message");
            tx.remove("testref");
            tx.commit();
        }
    }
}