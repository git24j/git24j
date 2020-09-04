package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

public class RebaseTest extends TestBase {
    private static final String SOURCE_SHA = "e5b28427ba064002e0e343e783ea3095018ce72c";
    private static final String TARGET_SHA = "f80e0b10f83e512d1fae0142d000cceba3aca721";

    @Test
    public void open() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            AnnotatedCommit ac1 = AnnotatedCommit.lookup(testRepo, Oid.of(SOURCE_SHA));
            AnnotatedCommit ac2 = AnnotatedCommit.lookup(testRepo, Oid.of(TARGET_SHA));
            Rebase rebase = Rebase.init(testRepo, ac1, ac2, null, null);
            Assert.assertEquals(rebase.ontoName(), rebase.ontoId().toString());
            Assert.assertEquals(SOURCE_SHA, rebase.origHeadId().toString());
            // open
            Rebase rebase2 = Rebase.open(testRepo, null);
            Assert.assertNotNull(rebase2.ontoName());
            Assert.assertNull(rebase2.origHeadName());
            // entry
            Assert.assertEquals(2, rebase.operationEntrycount());
            for (int i = 0; i < 2; i++) {
                Rebase.Operation op = rebase.operationByIndex(i);
                Assert.assertNull(op.getExec());
                Assert.assertNotNull(op.getId().toString());
                Assert.assertEquals(Rebase.OperationT.PICK, op.getType());
            }
            Rebase.Operation nextOp = rebase.next();
            Assert.assertNotNull(nextOp.getId());
            Assert.assertEquals(Rebase.OperationT.PICK, nextOp.getType());
            // rebase continue
            rebase.abort();
            //  Signature sig = Signature.create("tester", "tester@example.com");
            //  Oid rebaseOid1 = rebase.commit(
            //      sig, sig, StandardCharsets.UTF_8, "rebase message1");
            //  Assert.assertNotNull(rebaseOid1);
        }
    }

    @Test
    public void commit() {}

    @Test
    public void finish() {}
}
