package com.github.git24j.core;

import java.time.Instant;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CommitTest extends TestBase {
    private final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";
    private final String MASTER_PARENT_HASH = "565ddbe0bd55687b43286889a8ead64f68301113";
    private final String MASTER_TREE_HASH = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    private final String MERGE_COMMIT = "7dcb276ed40ce9223fd522f5166c25572d10d428";
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
    public void properties() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = (Commit) Revparse.single(testRepo, "master");
            Assert.assertEquals(MASTER_HASH, commit.id().toString());
            System.out.printf("      oid: %s %n", commit.id());
            System.out.printf(" encoding: %s %n", commit.id());
            System.out.printf("  message: %s %n", commit.message());
            System.out.printf("  summary: %s %n", commit.summary());
            System.out.printf("     time: %s %n", commit.time());
            System.out.printf("   offset: %d %n", commit.timeOffset());
            System.out.printf("committer: %s %n", commit.committer());
            System.out.printf("   author: %s %n", commit.author());
            System.out.printf("   header: %s %n", commit.rawHeader());
            System.out.printf("  tree_id: %s %n", commit.treeId());
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
            Signature sig = commit.committerWithMailmap(null).orElseThrow(RuntimeException::new);
            Assert.assertNotNull(sig.getName());
            Assert.assertNotNull(sig.getEmail());
            Assert.assertNotNull(sig.getWhen());
        }
    }

    @Test
    public void authorWithMailmap() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Signature sig = commit.authorWithMailmap(null).orElseThrow(RuntimeException::new);
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

    @Test
    public void treeId() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Oid oid = commit.treeId();
            Assert.assertEquals(MASTER_TREE_HASH, oid.toString());
        }
    }

    @Test
    public void parentCount() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, Oid.of(MERGE_COMMIT));
            int pc = commit.parentCount();
            Assert.assertEquals(2, pc);
        }
    }

    @Test
    public void parent() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Commit parentCommit = commit.parent(0);
            Assert.assertEquals(MASTER_PARENT_HASH, parentCommit.id().toString());
        }
    }

    @Test
    public void parentId() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Oid oid = commit.parentId(0);
            Assert.assertEquals(MASTER_PARENT_HASH, oid.toString());
        }
    }

    @Test
    public void nthGenAncestor() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Commit ancestor = commit.nthGenAncestor(1);
            Assert.assertEquals(MASTER_PARENT_HASH, ancestor.id().toString());
        }
    }

    @Test
    public void headerField() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            String parent = commit.headerField("parent").orElse("");
            String author = commit.headerField("author").orElse("");
            Assert.assertEquals(MASTER_PARENT_HASH, parent);
            Assert.assertEquals("Shijing Lu", author.substring(0, 10));
        }
    }

    @Test
    public void create() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit master = Commit.lookup(testRepo, MASTER_OID);
            Tree masterTree =
                    (Tree) Tree.lookup(testRepo, Oid.of(MASTER_TREE_HASH), GitObject.Type.TREE);
            Oid commit2 =
                    Commit.create(
                            testRepo,
                            "NEW_HEAD",
                            Signature.now("tester", "test@ab.cc"),
                            Signature.now("admin", "admin@ab.cc"),
                            null,
                            "some commit message",
                            masterTree,
                            Collections.singletonList(master));
            Assert.assertEquals(
                    MASTER_HASH, Commit.lookup(testRepo, commit2).parentId(0).toString());
        }
    }

    @Test
    public void amend() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            String newMessage = "test me";
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Oid oid = Commit.amend(commit, null, null, null, null, newMessage, null);
            Commit commit2 = Commit.lookup(testRepo, oid);
            String message = commit2.message();
            Assert.assertEquals(newMessage, message);
        }
    }

    @Test
    public void createBuffer() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit master = Commit.lookup(testRepo, MASTER_OID);
            Tree masterTree =
                    (Tree) Tree.lookup(testRepo, Oid.of(MASTER_TREE_HASH), GitObject.Type.TREE);
            Buf buf =
                    Commit.createBuffer(
                            testRepo,
                            Signature.now("tester", "test@ab.cc"),
                            Signature.now("admin", "admin@ab.cc"),
                            null,
                            "some commit message",
                            masterTree,
                            Collections.singletonList(master));
            String bufStr = buf.getPtr();
            /*
            tree 8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e
            parent 476f0c95825ef4479cab580b71f8b85f9dea4ee4
            author tester <test@ab.cc> 1577663527 -0500
            committer admin <admin@ab.cc> 1577663527 -0500

            some commit message
            */
            Assert.assertTrue(bufStr.contains("tree " + MASTER_TREE_HASH));
            Assert.assertTrue(bufStr.contains("parent " + MASTER_HASH));
            Assert.assertTrue(bufStr.contains("author tester"));
            Assert.assertTrue(bufStr.contains("committer admin"));
            Assert.assertTrue(bufStr.contains("some commit message"));
        }
    }

    @Test
    public void createSignature() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit master = Commit.lookup(testRepo, MASTER_OID);
            Tree masterTree =
                    (Tree) Tree.lookup(testRepo, Oid.of(MASTER_TREE_HASH), GitObject.Type.TREE);
            Buf buf =
                    Commit.createBuffer(
                            testRepo,
                            Signature.now("tester", "test@ab.cc"),
                            Signature.now("admin", "admin@ab.cc"),
                            null,
                            "some commit message",
                            masterTree,
                            Collections.singletonList(master));
            Oid oid = Commit.createWithSignature(testRepo, buf.getPtr(), "test signature", null);
            Assert.assertEquals(MASTER_HASH, Commit.lookup(testRepo, oid).parentId(0).toString());
        }
    }

    @Test
    public void dup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commit = Commit.lookup(testRepo, MASTER_OID);
            Commit commit2 = commit.dup();
            String parent = commit2.headerField("parent").orElse("");
            String author = commit2.headerField("author").orElse("");
            Assert.assertEquals(MASTER_PARENT_HASH, parent);
            Assert.assertEquals("Shijing Lu", author.substring(0, 10));
        }
    }
}
