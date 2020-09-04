package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DescribeTest extends TestBase {

    private final String FEATURE_DEV_HASH = "e5b28427ba064002e0e343e783ea3095018ce72c";

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void describe() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit c = Commit.lookup(testRepo, Oid.of(FEATURE_DEV_HASH));
            Describe.Result res = Describe.commit(c, null);
            String s1 = res.format(null);
            // git describe e5b28427ba064002e0e343e783ea3095018ce72c
            Assert.assertEquals("annotated_tag_3-2-ge5b2842", s1);
            // git describe
            String s2 = Describe.workdir(testRepo, null).format(null);
            Assert.assertEquals("annotated_tag_2", s2);
        }
    }
}
