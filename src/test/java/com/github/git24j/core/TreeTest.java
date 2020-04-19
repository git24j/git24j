package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TreeTest extends TestBase {
    private static final String MASTER_TREE_SHA = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void lookup() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        Tree t1 = Tree.lookup(testRepo, Oid.of(MASTER_TREE_SHA));
        Assert.assertEquals(MASTER_TREE_SHA, t1.id().toString());
        Tree t2 = Tree.lookupPrefix(testRepo, Oid.of("8c5f4d727b"));
        Assert.assertEquals(MASTER_TREE_SHA, t2.id().toString());
    }
}
