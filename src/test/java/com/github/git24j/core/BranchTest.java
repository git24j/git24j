package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;

public class BranchTest extends TestBase {
    private final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void create() {
        // TODO: finish the test
        Path repoPath = TestRepo.SIMPLE1.tempCopy(folder);
        Repository repo = Repository.open(repoPath.toString());
        // Branch.create(repo, )
    }

    @Test
    public void createFromAnnotated() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            try (Revparse.ExtReturn ret = Revparse.ext(testRepo, "master")) {
                Reference ref = ret.getRef();
                AnnotatedCommit ac = AnnotatedCommit.fromRef(testRepo, ref);
                Reference br = Branch.createFromAnnotated(testRepo, "test_branch_ac", ac, true);
                Assert.assertEquals(MASTER_HASH, br.id().toString());
            }
        }
    }
}
