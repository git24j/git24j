package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

public class NoteTest extends TestBase {
    private final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";
    private final Signature SIG = Signature.create("tester", "tester@example.com");

    @Test
    public void create() {

        Oid masterOid = Oid.of(MASTER_HASH);

        String noteMsg = "test notes";
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Oid noteOid =
                    Note.create(testRepo, null, SIG, SIG, Oid.of(MASTER_HASH), noteMsg, false);
            Assert.assertNotNull(noteOid);
            Note readNote = Note.read(testRepo, null, masterOid);
            Assert.assertEquals(noteOid, readNote.id());
            Assert.assertEquals(SIG, readNote.author());
            Assert.assertEquals(SIG, readNote.committer());
            Assert.assertEquals(noteMsg, readNote.message());
            Commit c = Commit.lookupPrefix(testRepo, "8ac409162d27195e5739476fa0061cb74eebdfd9");
            Note.commitRead(testRepo, c, noteOid);
            Note.remove(testRepo, null, SIG, SIG, Oid.of(MASTER_HASH));
            readNote.foreach(testRepo, null, (a, b) -> 0);
        }
    }

    @Test
    public void commitCreate() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Oid masterOid = Oid.of(MASTER_HASH);
            Note.CommitCreateResult result =
                    Note.commitCreate(testRepo, null, SIG, SIG, masterOid, "test notes", true);
            Assert.assertNotNull(result.getBlob());
            Assert.assertNotNull(result.getCommit());
        }
    }

    @Test
    public void defaultRef() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            String defaultRef = Note.defaultRef(testRepo);
            Assert.assertEquals("refs/notes/commits", defaultRef);
        }
    }
}
