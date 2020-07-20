package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DiffTest extends TestBase {
    // git rev-parse HEAD^{tree}
    private static final String MASTER_TREE = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    // git rev-parse "feature/dev^{tree}"
    private static final String DEV_TREE = "3b597d284bc12d61638124054b19889587127208";

    private static final String README_BLOB1 = "08f8e5eba8074e2d3d5e17a8902eaea07633d0af";
    private static final String README_BLOB2 = "d628ad3b584b5ab3fa93dbdbcc66a15e4413d9b2";

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void treeToTree() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tree head = (Tree) Tree.lookup(testRepo, Oid.of(MASTER_TREE), GitObject.Type.TREE);
            Tree dev = (Tree) Tree.lookup(testRepo, Oid.of(DEV_TREE), GitObject.Type.TREE);
            Diff diff =
                    Diff.treeToTree(
                            testRepo, dev, head, Diff.Options.create(Diff.Options.CURRENT_VERSION));
            Assert.assertNotNull(diff);
        }
    }

    @Test
    public void blob() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blob blob1 = Blob.lookup(testRepo, Oid.of(README_BLOB1));
            Blob blob2 = Blob.lookup(testRepo, Oid.of(README_BLOB2));
            Path p = Paths.get("README.md");
            Diff.blobs(
                    blob1,
                    p,
                    blob2,
                    p,
                    Diff.Options.create(Diff.Options.CURRENT_VERSION),
                    new Diff.FileCb() {
                        @Override
                        public int accept(Diff.Delta delta, float progress) {
                            System.out.println(delta.getOldFile());
                            System.out.println(delta.getNewFile());
                            System.out.println(delta.getFlags());
                            System.out.println(delta.getNfiles());
                            System.out.println(delta.getStatus());
                            System.out.println(delta.getSimilarity());
                            return 0;
                        }
                    },
                    new Diff.BinaryCb() {
                        @Override
                        public int accept(Diff.Delta delta, Diff.Binary binary) {
                            // TODO: implement binary getters
                            return 0;
                        }
                    },
                    (Diff.HunkCb) (delta, hunk) -> {
                        System.out.println(hunk.getNewLines());
                        System.out.println(hunk.getOldLines());
                        System.out.println(hunk.getHeader());
                        System.out.println(hunk.getHeaderLen());
                        System.out.println(hunk.getNewStart());
                        System.out.println(hunk.getOldStart());
                        return 0;
                    },
                    (delta, hunk, line) -> {
                        System.out.println(line.getNumLines());
                        System.out.println(line.getNewLineno());
                        System.out.println(line.getOldLineno());
                        System.out.println(line.getContentLen());
                        System.out.println(line.getContent());
                        System.out.println(line.getContentOffset());
                        System.out.println(line.getOrigin());
                        return 0;
                    });
        }
    }
}
