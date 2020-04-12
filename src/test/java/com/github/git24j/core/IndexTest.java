package com.github.git24j.core;

import static com.github.git24j.core.GitObject.Type.TREE;
import static com.github.git24j.core.Index.Capability.IGNORE_CASE;
import static com.github.git24j.core.Index.Capability.NO_SYMLINKS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class IndexTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    private static final String FEATURE_DEV_TREE_SHA = "3b597d284bc12d61638124054b19889587127208";

    @Test
    public void open() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx =
                    Index.open(Paths.get(testRepo.getPath()).resolve("index").toString())) {
                Assert.assertNotNull(idx);
            }
        }
    }

    @Test
    public void updateAll() throws IOException {
        final String fname = "a";
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                Map<String, String> cache = new HashMap<>();
                Path p = testRepo.workdir().resolve(fname);
                Files.write(p, Arrays.asList("line1", "line2"));
                idx.updateAll(Arrays.asList(fname), cache::put);
                idx.write();
                Assert.assertEquals(fname, cache.get(fname));
            }
        }
    }

    @Test
    public void owner() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                Assert.assertEquals(testRepo, idx.owner());
            }
        }
    }

    @Test
    public void caps() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                EnumSet<Index.Capability> caps = EnumSet.of(IGNORE_CASE, NO_SYMLINKS);
                idx.setCaps(caps);
                Assert.assertEquals(caps, idx.caps());
            }
        }
    }

    @Test
    public void version() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                idx.setVersion(3);
                Assert.assertEquals(3, idx.version());
            }
        }
    }

    @Test
    public void read() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                idx.read(true);
            }
        }
    }

    @Test
    public void checksum() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                Oid oid = idx.checksum();
                Assert.assertTrue(oid.toString().length() > 0);
            }
        }
    }

    /**
     *
     *
     * <pre>
     *     $ git read-tree 'feature/dev^{tree}'
     *     $ git write-tree
     *     > 3b597d284bc12d61638124054b19889587127208
     * </pre>
     */
    @Test
    public void readWriteTree() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tree dev = (Tree) Tree.lookup(testRepo, Oid.of(FEATURE_DEV_TREE_SHA), TREE);
            try (Index idx = testRepo.index()) {
                idx.readTree(dev);
                Oid oid1 = idx.writeTree();
                Assert.assertEquals("3b597d284bc12d61638124054b19889587127208", oid1.toString());
                Oid oid2 = idx.writeTreeTo(testRepo);
                Assert.assertEquals(oid1, oid2);
            }
        }
    }

    @Test
    public void entryCount() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                Assert.assertTrue(idx.entryCount() > 0);
            }
        }
    }

    @Test
    public void clear() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                idx.clear();
            }
        }
    }

    @Test
    public void getEntry() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                Index.Entry e1 = Index.Entry.getByIndex(idx, 0);
                Assert.assertNotNull(e1);
                Index.Entry e2 = Index.Entry.getByPath(idx, "a", 0);
                Assert.assertNotNull(e2);
            }
        }
    }

    @Test
    public void remove() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                int originalCount = idx.entryCount();
                idx.remove("a", 0);
                Assert.assertEquals(originalCount - 1, idx.entryCount());
                idx.removeDirectory("non-exist", 0);
                Assert.assertEquals(originalCount - 1, idx.entryCount());
            }
        }
    }

    @Test
    public void add() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                idx.add(Index.Entry.getByIndex(idx, 1));
            }
        }
    }

    @Test
    public void entry() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                Index.Entry entry = Index.Entry.getByIndex(idx, 1);
                Assert.assertEquals(0, entry.state());
                Assert.assertFalse(entry.isConflict());
            }
        }
    }

    @Test
    public void addAll() throws IOException {
        Path repoPath = tempCopyOf(TestRepo.SIMPLE1, folder.getRoot().toPath());
        FileUtils.writeStringToFile(repoPath.resolve("a").toFile(), "test");
        Map<String, String> cbParams = new HashMap<>();
        try (Repository repo = Repository.open(repoPath)) {
            try (Index index = repo.index()) {
                index.addAll(
                        new String[] {"."}, EnumSet.of(Index.AddOption.DEFAULT), cbParams::put);
                index.write();
            }
        }
        Assert.assertEquals(cbParams.get("a"), ".");
    }

    @Test
    public void addByPath() throws IOException {
        Path repoPath = tempCopyOf(TestRepo.SIMPLE1, folder.getRoot().toPath());
        FileUtils.writeStringToFile(repoPath.resolve("a").toFile(), "add index by path");
        try (Repository repo = Repository.open(repoPath)) {
            try (Index index = repo.index()) {
                index.add("a");
                index.write();
            }
        }
        FileUtils.copyDirectory(repoPath.toFile(), new File("/tmp/test-indexAddByPath"));
    }

    @Test
    public void addFromBuffer() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                Index.Entry e = idx.getEntryByIndex(0);
                idx.addFromBuffer(e, "abc".getBytes());
            }
        }
    }

    @Test
    public void removeByPath() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                int originalCount = idx.entryCount();
                idx.removeByPath("a");
                Assert.assertEquals(originalCount - 1, idx.entryCount());
            }
        }
    }

    @Test
    public void find() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                int ia = idx.find("a");
                AtomicInteger outPos = new AtomicInteger();
                int ib = idx.findPrefix("b");
                Assert.assertTrue(ia + ib > 0);
            }
        }
    }

    @Test
    public void conflict() {
        try (Repository testRepo = TestRepo.CONFLICT.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                Index.Conflict conflict = idx.conflictGet("README.md");
                Assert.assertTrue(idx.hasConflicts());
                Assert.assertTrue(conflict.ancestor.isConflict());
                idx.conflictRemove("README.md");
                Assert.assertFalse(idx.hasConflicts());
            }
        }
    }

    @Test
    public void conflictCleanup() {
        try (Repository testRepo = TestRepo.CONFLICT.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                Index.Conflict conflict = idx.conflictGet("README.md");
                idx.conflictAdd(conflict);
                Assert.assertTrue(idx.hasConflicts());
                idx.conflictCleanup();
                Assert.assertFalse(idx.hasConflicts());
            }
        }
    }

    @Test
    public void conflictIterator() {
        try (Repository testRepo = TestRepo.CONFLICT.tempRepo(folder)) {
            try (Index idx = testRepo.index()) {
                Index.ConflictIterator iter = idx.conflictIteratorNew();
                Index.Conflict conflict = iter.next();
                Assert.assertTrue(conflict.our.isConflict());
                Assert.assertNull(iter.next());
            }
        }
    }
}
