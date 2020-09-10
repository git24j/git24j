package com.github.git24j.core;

import org.junit.Test;

public class ResetTest extends TestBase {
    private final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";

    @Test
    public void resetDefault() {
        try (Repository testRepo = creteTestRepo(TestRepo.SIMPLE1)) {
            GitObject header = Revparse.single(testRepo, "HEAD");
            Reset.resetDefault(testRepo, header, new String[] {"*.md"});
            Reset.reset(testRepo, header, Reset.ResetT.SOFT, null);
            AnnotatedCommit ac = AnnotatedCommit.fromRevspec(testRepo, "HEAD");
            Reset.resetFromAnnotated(testRepo, ac, Reset.ResetT.SOFT, null);
            System.out.println(ac);
        }
    }
}
