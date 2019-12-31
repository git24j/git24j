package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BranchTest extends TestBase {
    private final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";
    private final String FEATURE_DEV_HASH = "e5b28427ba064002e0e343e783ea3095018ce72c";

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void create() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Branch.create(testRepo, "test_branch", Commit.lookup(testRepo, Oid.of(MASTER_HASH)), false);
            Assert.assertEquals(MASTER_HASH, ref.target().toString());
        }
    }

    @Test
    public void createFromAnnotated() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Revparse.ExtReturn ret = Revparse.ext(testRepo, "master");
            Reference ref = ret.getRef();
            AnnotatedCommit ac = AnnotatedCommit.fromRef(testRepo, ref);
            Reference br = Branch.createFromAnnotated(testRepo, "test_branch_ac", ac, true);
            Assert.assertEquals(MASTER_HASH, br.target().toString());
        }
    }

    @Test
    public void delete() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Branch.lookup(testRepo, "feature/dev", Branch.BranchType.LOCAL);
            Branch.delete(ref);
            Assert.assertNotEquals(FEATURE_DEV_HASH, ref.target().toString());
        }
    }

    @Test
    public void lookup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Branch.lookup(testRepo, "feature/dev", Branch.BranchType.LOCAL);
            Assert.assertNotNull(ref);
            Assert.assertEquals(FEATURE_DEV_HASH, ref.target().toString());
        }
    }

    @Test
    public void iter() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Branch.Iterator iter = Branch.Iterator.create(testRepo, Branch.BranchType.LOCAL);
            Map.Entry<Reference, Branch.BranchType> entry = iter.next();
            Set<String> refsSet = new HashSet<>();
            while (entry != null) {
                refsSet.add(entry.getKey().name());
                entry = iter.next();
            }
            Assert.assertTrue(refsSet.contains("refs/heads/master"));
            Assert.assertTrue(refsSet.contains("refs/heads/feature/dev"));
        }
    }

    @Test
    public void move() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference br0 = Branch.lookup(testRepo, "feature/dev", Branch.BranchType.LOCAL);
            Reference br1 = Branch.move(br0, "feature_dev", true);
            Assert.assertEquals(FEATURE_DEV_HASH, br1.target().toString());
        }
    }

    @Test
    public void name() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference br0 = Branch.lookup(testRepo, "feature/dev", Branch.BranchType.LOCAL);
            String name = Branch.name(br0);
            Assert.assertEquals("feature/dev", name);
        }
    }

    /**TODO: test set upstream*/
    @Test
    public void upstream() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference br0 = Branch.lookup(testRepo, "feature/dev", Branch.BranchType.LOCAL);
            Reference tracking = Branch.upstream(br0);
            Assert.assertNull(tracking);
        }
    }

    @Test
    public void upstreamName() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference br0 = Branch.lookup(testRepo, "feature/dev", Branch.BranchType.LOCAL);
            String upstreamName = Branch.upstreamName(testRepo, "refs/heads/master");
            Assert.assertNull(upstreamName);
        }
    }

    @Test
    public void isHead() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference br0 = Branch.lookup(testRepo, "master", Branch.BranchType.LOCAL);
            Assert.assertTrue(Branch.isHead(br0));
        }
    }

    @Test
    public void isCheckedOut() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference br0 = Branch.lookup(testRepo, "master", Branch.BranchType.LOCAL);
            Assert.assertTrue(Branch.isCheckedOut(br0));
        }
    }

    @Test
    public void remoteName() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            String name = Branch.remoteName(testRepo, "refs/remotes/test/master");
            Assert.assertNull(name);
        }
    }

    @Test
    public void upstreamRemote() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            String name = Branch.upstreamRemote(testRepo, "master");
            Assert.assertNull(name);
        }
    }
}
