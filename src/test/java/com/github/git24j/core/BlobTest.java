package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class BlobTest extends TestBase {
    private static final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";
    /** from {@code git rev-parse HEAD:a } */
    private static final String BLOB_A = "78981922613b2afb6025042ff6bd878ac1994e85";
    /** from {@code git ls-tree HEAD^tree | grep README.md } */
    private static final String BLOB_README = "d628ad3b584b5ab3fa93dbdbcc66a15e4413d9b2";

    @Test
    public void lookup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_A));
            Blob blob3 = Blob.lookupPrefix(testRepo, BLOB_A.substring(0, 8));
            Assert.assertNotNull(blob1);
            Assert.assertEquals(blob1.id(), blob3.id());
        }
    }

    @Test
    public void filteredContent() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_README));
            Assert.assertNotNull(blob1);
            String maybeContent = blob1.filteredContent("README.md", true);
            Assert.assertNotNull(maybeContent);
        }
    }

    @Test
    public void id() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_A));
            Assert.assertEquals(blob1.id().toString(), BLOB_A);
        }
    }

    @Test
    public void owner() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_A));
            Assert.assertEquals(testRepo.getRawPointer(), blob1.owner().getRawPointer());
        }
    }

    @Test
    public void rawSize() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_A));
            // git cat-file -s 78981922613b2afb6025042ff6bd878ac1994e85
            Assert.assertEquals(2, blob1.rawSize());
        }
    }

    @Test
    public void createFromWorkdir() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Oid oid = Blob.createFromWorkdir(testRepo, "a");
            // git cat-file -p HEAD^{tree}
            Assert.assertEquals("78981922613b2afb6025042ff6bd878ac1994e85", oid.toString());
        }
    }

    @Test
    public void createFromDir() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            String fa =
                    folder.getRoot()
                            .toPath()
                            .resolve(TestRepo.SIMPLE1.getName())
                            .resolve("a")
                            .toString();
            Oid oid = Blob.createdFromDisk(testRepo, fa);
            // git cat-file -p HEAD^{tree}
            Assert.assertEquals("78981922613b2afb6025042ff6bd878ac1994e85", oid.toString());
        }
    }

    @Test
    public void createFromStream() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            WriteStream ws = Blob.createFromStream(testRepo, null);
            ws.write("a".getBytes());
            Oid oid = Blob.createFromStreamCommit(ws);
            Assert.assertFalse(oid.isEmpty());
        }
    }

    @Test
    public void createFromBuffer() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Oid oid = Blob.createFromBuffer(testRepo, "a\n".getBytes(StandardCharsets.UTF_8));
            Assert.assertEquals("78981922613b2afb6025042ff6bd878ac1994e85", oid.toString());
        }
    }

    @Test
    public void isBinary() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_A));
            Assert.assertFalse(blob1.isBinary());
        }
    }

    @Test
    public void dup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_A));
            Blob blob2 = blob1.dup();
            Assert.assertEquals(blob2.id().toString(), BLOB_A);
        }
    }
}
