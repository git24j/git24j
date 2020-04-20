package com.github.git24j.core;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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

    @Test
    public void entries() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            GitObject obj = Revparse.single(testRepo, "HEAD^{tree}");
            Tree tree = (Tree) obj;
            Assert.assertNotNull(tree);
            Assert.assertTrue(tree.entryCount() > 1);
            Optional<Tree.Entry> maybeE0 = tree.entryByIndex(0);
            Assert.assertTrue(maybeE0.isPresent());
            GitObject.Type t = maybeE0.get().type();
            Assert.assertTrue(t == GitObject.Type.BLOB || t == GitObject.Type.TREE);

            Optional<Tree.Entry> maybeE1 = tree.entryByName("README.md");
            Assert.assertTrue(maybeE1.isPresent());
        }
    }

    @Test
    public void walk() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tree tree = (Tree) Revparse.single(testRepo, "HEAD^{tree}");
            Set<String> entryNames = new HashSet<>();
            tree.walk(
                    Tree.WalkMode.PRE,
                    ((root, entry) -> {
                        entryNames.add(entry.name());
                        return 0;
                    }));
            Assert.assertTrue(entryNames.contains("README.md"));
        }
    }

    @Test
    public void treeBuilder() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tree.Builder bld = Tree.newBuilder(testRepo, null);
            GitObject obj1 = Revparse.single(testRepo, "HEAD:README.md");
            bld.insert("README.md", obj1.id(), FileMode.BLOB);
            GitObject obj2 = Revparse.single(testRepo, "HEAD:a");
            bld.insert("a", obj2.id(), FileMode.BLOB);
            Oid oid2 = bld.write();
            Assert.assertTrue(Revparse.single(testRepo, oid2.toString()) instanceof Tree);
        }
    }
}
