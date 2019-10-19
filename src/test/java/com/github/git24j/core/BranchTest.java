package com.github.git24j.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;

public class BranchTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void create() {
        // TODO: finish the test
        Path repoPath = TestRepo.SIMPLE1.tempCopy(folder);
        Repository repo = Repository.open(repoPath.toString());
        // Branch.create(repo, )
    }
}
