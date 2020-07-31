package com.github.git24j.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TreeTest extends TestBase {
    private static final String MASTER_TREE_SHA = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    private static final String README_SHA_IN_MASTER = "d628ad3b584b5ab3fa93dbdbcc66a15e4413d9b2";
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void lookup() {
        Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
        Tree t1 = Tree.lookup(testRepo, Oid.of(MASTER_TREE_SHA));
        Assert.assertEquals(MASTER_TREE_SHA, t1.id().toString());
        Tree t2 = Tree.lookupPrefix(testRepo, "8c5f4d727b");
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

    @Test
    public void dup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tree t1 = Tree.lookup(testRepo, Oid.of(MASTER_TREE_SHA));
            Tree t2 = t1.dup();
            Assert.assertEquals(t1.id(), t2.id());
        }
    }

    @Test
    public void entry() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tree t1 = Tree.lookup(testRepo, Oid.of(MASTER_TREE_SHA));
            Tree.Entry e1 = t1.entryById(Oid.of(README_SHA_IN_MASTER)).orElse(null);
            Tree.Entry e2 = t1.entryByPath(Paths.get("README.md")).orElse(null);
            Tree.Entry e3 = t1.entryByName("a").orElse(null);
            Assert.assertNotNull(e1);
            Assert.assertNotNull(e2);
            Assert.assertNotNull(e3);
            Assert.assertEquals(e1.id(), e2.id());
            Assert.assertEquals(e1.id(), e2.id());
            Assert.assertEquals("README.md", e1.name());
            Assert.assertEquals(GitObject.Type.BLOB, e1.type());
            Assert.assertEquals(FileMode.BLOB, e1.filemode());
            Assert.assertEquals(FileMode.BLOB, e1.filemodeRaw());
            Tree.Entry e4 = e1.dup();
            Assert.assertEquals(e1.id(), e4.id());
            GitObject o1 = e1.toObject(testRepo);
            Assert.assertEquals(e1.id(), o1.id());
        }
    }

    @Test
    public void update() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Path readme = Paths.get("README.md");
            Tree t1 = Tree.lookup(testRepo, Oid.of(MASTER_TREE_SHA));
            Tree.Update update =
                    Tree.Update.create(
                            Tree.UpdateT.REMOVE,
                            Oid.of(README_SHA_IN_MASTER),
                            FileMode.BLOB,
                            readme);
            Assert.assertNotNull(update);
            Oid o2 = t1.createUpdated(testRepo, t1, Collections.singletonList(update));
            Assert.assertNotNull(o2);
            Tree t2 = Tree.lookup(testRepo, o2);
            Assert.assertFalse(t2.entryByPath(readme).isPresent());
        }
    }
}
