package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MergeTest extends TestBase {
    // git rev-parse HEAD^{tree}
    private static final String MASTER_TREE = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    // git rev-parse "feature/dev^{tree}"
    private static final String DEV_TREE = "3b597d284bc12d61638124054b19889587127208";
    private static final String SHA_DEV = "e5b28427ba064002e0e343e783ea3095018ce72c";
    private static final String SHA_C = "f80e0b10f83e512d1fae0142d000cceba3aca721";
    private static final String SHA_C_DEV_BASE = "67a36754360b373d391af2182f9ad8929fed54d8";

    @Test
    public void base() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Oid dev = Oid.of(SHA_DEV);
            Oid c = Oid.of(SHA_C);
            Optional<Oid> mergeBase = Optional.ofNullable(Merge.base(testRepo, dev, c));
            Assert.assertEquals(SHA_C_DEV_BASE, mergeBase.map(Oid::toString).orElse(null));
            List<Oid> bases = Merge.bases(testRepo, dev, c);
            Assert.assertEquals(1, bases.size());
            Assert.assertEquals(SHA_C_DEV_BASE, bases.get(0).toString());

            Optional<Oid> base2 = Optional.ofNullable(Merge.baseMany(testRepo, new Oid[] {dev, c}));
            Assert.assertEquals(SHA_C_DEV_BASE, base2.map(Oid::toString).orElse(null));

            List<Oid> bases2 = Merge.basesMany(testRepo, new Oid[] {dev, c});
            Assert.assertEquals(1, bases2.size());
            Assert.assertEquals(SHA_C_DEV_BASE, bases2.get(0).toString());

            Optional<Oid> base4 =
                    Optional.ofNullable(Merge.baseOctopus(testRepo, new Oid[] {dev, c}));
            Assert.assertEquals(SHA_C_DEV_BASE, base4.map(Oid::toString).orElse(null));
        }
    }

    @Test
    public void trees() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tree treeDev =
                    Tree.lookup(testRepo, Oid.of("3b597d284bc12d61638124054b19889587127208"));
            Tree treeC = Tree.lookup(testRepo, Oid.of("f31933f7cb6777551fd7307543105a6337405c7c"));
            Index out = Merge.trees(testRepo, null, treeDev, treeC, null);
            Assert.assertTrue(out.hasConflicts());
            Assert.assertEquals(7, out.entryCount());
        }
    }

    @Test
    public void commits() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Commit commitDev = Commit.lookupPrefix(testRepo, SHA_DEV);
            Commit commitC = Commit.lookupPrefix(testRepo, SHA_C);
            Index out = Merge.commits(testRepo, commitDev, commitC, null);
            Assert.assertTrue(out.hasConflicts());
            Assert.assertEquals(8, out.entryCount());
        }
    }

    @Test
    public void analysis() {
        Path repoPath = TestRepo.SUBMODULE.tempCopy(folder);
        try (Repository testRepo = Repository.open(repoPath.resolve("x"))) {
            AnnotatedCommit ac =
                    AnnotatedCommit.lookup(
                            testRepo, Oid.of("10d86efdfa78ec906933a3414affa592511fd170"));
            Merge.AnalysisPair res = Merge.analysis(testRepo, Collections.singletonList(ac));
            Assert.assertEquals(Merge.AnalysisT.NORMAL, res.getAnalysis());
            Assert.assertEquals(Merge.PreferenceT.NONE, res.getPreference());
        }
    }

    @Test
    public void analysisForRef() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference refDev = Reference.lookup(testRepo, "refs/heads/feature/dev");
            AnnotatedCommit ac = AnnotatedCommit.lookup(testRepo, Oid.of(SHA_C));
            Merge.AnalysisPair res =
                    Merge.analysisForRef(testRepo, refDev, Collections.singletonList(ac));
            Assert.assertEquals(Merge.AnalysisT.NORMAL, res.getAnalysis());
            Assert.assertEquals(Merge.PreferenceT.NONE, res.getPreference());
        }
    }

    @Test
    public void file() {
        Merge.FileInput f0 = Merge.FileInput.createDefault();
        f0.setPtr("line 1: abc\nline 2: wvu");
        Merge.FileInput f1 = Merge.FileInput.createDefault();
        f1.setPtr("line 1: abc\nline 2: def\nline 3:ghi");
        Merge.FileInput f2 = Merge.FileInput.createDefault();
        f2.setPtr("line 1: zyx\nline 2: wvu\nline 3:tsr");
        Merge.FileResult result = Merge.file(f0, f1, f2, null);
        Assert.assertFalse(result.getAutomergeable());
        Assert.assertTrue(result.getPtr().contains("<<<<<<< file.txt\n"));
        Assert.assertTrue(result.getPtr().contains(">>>>>>> file.txt\n"));
    }

    @Test
    public void merge() {
        Path repoPath = TestRepo.SUBMODULE.tempCopy(folder);
        try (Repository testRepo = Repository.open(repoPath.resolve("x"))) {

            AnnotatedCommit ac =
                    AnnotatedCommit.lookup(
                            testRepo, Oid.of("10d86efdfa78ec906933a3414affa592511fd170"));
            Merge.merge(testRepo, Collections.singletonList(ac), null, null);
        }
    }
}
