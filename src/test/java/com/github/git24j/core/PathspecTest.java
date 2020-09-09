package com.github.git24j.core;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class PathspecTest extends TestBase {
    // git rev-parse HEAD^{tree}
    private static final String MASTER_TREE = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    // git rev-parse "feature/dev^{tree}"
    private static final String DEV_TREE = "3b597d284bc12d61638124054b19889587127208";

    @Test
    public void create() {
        EnumSet<Pathspec.FlagT> flags = EnumSet.of(Pathspec.FlagT.DEFAULT);
        Pathspec pathspec = Pathspec.create(Arrays.asList("*.js", "*.java", "*.md"));
        // match path
        Assert.assertTrue(pathspec.matchesPath(flags, "abc.java"));
        try (Repository testRepo = creteTestRepo(TestRepo.SIMPLE1)) {
            // match workdir
            List<String> matched = pathspec.matchWorkdir(testRepo, flags);
            Assert.assertEquals(1, matched.size());
            Assert.assertEquals("README.md", matched.get(0));
            Index idx = Index.open(Paths.get(testRepo.getPath()).resolve("index").toString());
            // match index
            List<String> indexMatched = pathspec.matchIndex(idx, flags);
            Assert.assertEquals(1, indexMatched.size());
            Assert.assertEquals("README.md", indexMatched.get(0));
            // match tree
            Tree t1 = Tree.lookup(testRepo, Oid.of(MASTER_TREE));
            Assert.assertEquals("README.md", pathspec.matchTree(t1, flags).get(0));
            // match diff
            Tree head = (Tree) Tree.lookup(testRepo, Oid.of(MASTER_TREE), GitObject.Type.TREE);
            Tree dev = (Tree) Tree.lookup(testRepo, Oid.of(DEV_TREE), GitObject.Type.TREE);
            Diff diff = Diff.treeToTree(testRepo, dev, head, null);
            List<Diff.Delta> diffMatched = pathspec.matchDiff(diff, flags);
            Assert.assertEquals(1, diffMatched.size());
            Assert.assertEquals("README.md", diffMatched.get(0).getOldFile().getPath());
        }
    }
}
