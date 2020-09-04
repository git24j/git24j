package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

public class ReflogTest extends TestBase {

    @Test
    public void read() {
        Signature sig = Signature.create("tester", "tester@example.com");
        Oid oid = Oid.of("f80e0b10f83e512d1fae0142d000cceba3aca721");
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reflog reflog = Reflog.read(testRepo, "HEAD@{2}");
            reflog.append(oid, sig, "reflog added from test");
            Assert.assertTrue(reflog.entryCount() > 0);
            Reflog.Entry entry = reflog.entryByIndex(0);
            Assert.assertNotNull(entry.committer());
            Assert.assertNotNull(entry.idNew());
            Assert.assertNotNull(entry.idOld());
            Assert.assertNotNull(entry.message());
            reflog.drop(0, false);
            reflog.delete(testRepo, "HEAD@{1}");
            reflog.write();
        }
    }
}
