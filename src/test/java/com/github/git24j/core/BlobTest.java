package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BlobTest extends TestBase {
    private static final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";
    private static final String BLOB_A = "78981922613b2afb6025042ff6bd878ac1994e85";
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void lookup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Blob blob1 = Blob.lookup(testRepo, Oid.of(BLOB_A));
                    Blob blob2 = Blob.lookup(testRepo, Oid.of(BLOB_A.substring(0, 8)));
                    Blob blob3 = Blob.lookupPrefix(testRepo, Oid.of(BLOB_A.substring(0, 8))); ) {
                Assert.assertNotNull(blob1);
                Assert.assertNotNull(blob2);
                Assert.assertEquals(blob2.id(), blob3.id());
            }
        }
    }
    //    git_blob_free	function
    //    git_blob_id	function
    //    git_blob_owner	function
    //    git_blob_rawcontent	function
    //    git_blob_rawsize	function
    //    git_blob_filtered_content	function
    //    git_blob_create_fromworkdir	function
    //    git_blob_create_fromdisk	function
    //    git_blob_create_fromstream	function
    //    git_blob_create_fromstream_commit	function
    //    git_blob_create_frombuffer	function
    //    git_blob_is_binary	function
    //    git_blob_dup	function

}
