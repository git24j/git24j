package com.github.git24j.core;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BlobTest extends TestBase {
    private static final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";
    /** from {@code git rev-parse HEAD:a } */
    private static final String BLOB_A = "78981922613b2afb6025042ff6bd878ac1994e85";
    /** from {@code git ls-tree HEAD^tree | grep README.md } */
    private static final String BLOB_README = "d628ad3b584b5ab3fa93dbdbcc66a15e4413d9b2";

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void lookup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_A));
            Blob blob2 = Blob.lookup(testRepo, Oid.of(BLOB_A.substring(0, 8)));
            Blob blob3 = Blob.lookupPrefix(testRepo, Oid.of(BLOB_A.substring(0, 8)));
            Assert.assertNotNull(blob1);
            Assert.assertNotNull(blob2);
            Assert.assertEquals(blob2.id(), blob3.id());
        }
    }

    @Test
    public void filteredContent() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_README));
            Assert.assertNotNull(blob1);
            Optional<String> maybeContent = blob1.filteredContent("README.md", true);
            Assert.assertTrue(maybeContent.isPresent());
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

    // TODO: unit test following
    //    git_blob_create_fromstream	function
    //    git_blob_create_fromstream_commit	function
    //    git_blob_create_frombuffer	function

//    @Test
//    public void createFromBuffer() {
//        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
//            Oid oid = Blob.createFromBuffer(testRepo, "a\n".getBytes(StandardCharsets.UTF_8));
//            Assert.assertEquals("78981922613b2afb6025042ff6bd878ac1994e85", oid.toString());
//        }
//    }
//
//    @Test
//    public void isBinary() {
//        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
//            Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_A));
//            Assert.assertFalse(blob1.isBinary());
//        }
//    }
//
//    @Test
//    public void dup() {
//        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
//            Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_A));
//            Blob blob2 = blob1.dup();
//            Assert.assertEquals(blob2.id().toString(), BLOB_A);
//        }
//    }
}
