package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.time.Instant;

public class CommitTest extends TestBase {
    private final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";
    private final String MASTER_TREE_HASH = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    private final Oid MASTER_OID = Oid.of(MASTER_HASH);

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void lookup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Assert.assertEquals(MASTER_HASH, commit.id().toString());
        }
    }

    @Test
    public void lookupPrefix() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, Oid.of(MASTER_HASH.substring(0, 9)));
            Assert.assertEquals(MASTER_HASH, commit.id().toString());
        }
    }

    @Test
    public void id() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, Oid.of(MASTER_HASH.substring(0, 9)));
            Assert.assertEquals(MASTER_HASH, commit.id().toString());
        }
    }

    @Test
    public void messageEncoding() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, Oid.of(MASTER_HASH.substring(0, 9)));
            String encoding = commit.messageEncoding();
            Assert.assertNull(encoding);
        }
    }

    @Test
    public void message() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            String message = commit.message();
            Assert.assertNotNull(message);
        }
    }

    @Test
    public void messageRaw() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            String message = commit.messageRaw();
            Assert.assertNotNull(message);
        }
    }

    @Test
    public void summary() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            String summary = commit.summary();
            // summary of one line commit message is the same as the message with whitespace trimmed
            Assert.assertEquals(summary, commit.message().trim());
        }
    }

    @Test
    public void body() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            String body = commit.body();
            // body of one line commit message is null
            Assert.assertNull(body);
        }
    }

    @Test
    public void time() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Instant commitTime = commit.time();
            Assert.assertTrue(commitTime.compareTo(Instant.now()) < 0);
        }
    }

    @Test
    public void timeOffset() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            int offset = commit.timeOffset();
            // There are nations using 0.5 GMT
            Assert.assertTrue(offset % 30 == 0);
        }
    }

    @Test
    public void committer() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Signature sig = commit.committer();
            Assert.assertNotNull(sig.getName());
            Assert.assertNotNull(sig.getEmail());
            Assert.assertNotNull(sig.getWhen());
        }
    }

    @Test
    public void author() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Signature sig = commit.author();
            Assert.assertNotNull(sig.getName());
            Assert.assertNotNull(sig.getEmail());
            Assert.assertNotNull(sig.getWhen());
        }
    }

    @Test
    public void committerWithMailmap() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Signature sig = commit.committerWithMailmap(null);
            Assert.assertNotNull(sig.getName());
            Assert.assertNotNull(sig.getEmail());
            Assert.assertNotNull(sig.getWhen());
        }
    }

    @Test
    public void authorWithMailmap() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Signature sig = commit.authorWithMailmap(null);
            Assert.assertNotNull(sig.getName());
            Assert.assertNotNull(sig.getEmail());
            Assert.assertNotNull(sig.getWhen());
        }
    }

    @Test
    public void rawHeader() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            String rh = commit.rawHeader();
            Assert.assertTrue(rh.length() > 0);
        }
    }

    @Test
    public void tree() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Tree masterTree = commit.tree();
            Assert.assertEquals(MASTER_TREE_HASH, masterTree.id().toString());
        }
    }
}
