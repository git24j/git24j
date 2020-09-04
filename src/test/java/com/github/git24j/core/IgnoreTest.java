package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class IgnoreTest extends TestBase {

    @Test
    public void addRule() {
        try (Repository testRepo = creteTestRepo(TestRepo.SIMPLE1)){
            Ignore.addRule(testRepo, "*.c\ntest_folder/\ntest_file.txt\n");
            boolean f1 =  Ignore.pathIsIgnored(testRepo, "test_file.txt");
            Assert.assertTrue(f1);
            boolean f2 =  Ignore.pathIsIgnored(testRepo, "abd/");
            Assert.assertFalse(f2);
            Ignore.clearInternalRules(testRepo);
        }
    }
}