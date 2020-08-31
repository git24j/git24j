package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BlameTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void jniFile() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Blame blame = Blame.file(testRepo, "README.md", null);
            Assert.assertFalse(blame.isNull());
            Assert.assertTrue(blame.getHunkCount() > 0);
            Blame.Hunk hunk1 = blame.getHunkByIndex(0);
            Assert.assertNotNull(hunk1);
            System.out.println("--------------------Hunk[0]----------------------");
            System.out.println("               boundary:" + hunk1.getBoundary());
            System.out.println("       getFinalCommitId:" + hunk1.getFinalCommitId());
            System.out.println("      getFinalSignature:" + hunk1.getFinalSignature());
            System.out.println("getFinalStartLineNumber:" + hunk1.getFinalStartLineNumber());
            System.out.println("         getLinesInHunk:" + hunk1.getLinesInHunk());
            System.out.println("        getOrigCommitId:" + hunk1.getOrigCommitId());
            System.out.println("            getOrigPath:" + hunk1.getOrigPath());
            System.out.println("       getOrigSignature:" + hunk1.getOrigSignature());
            System.out.println(" getOrigStartLineNumber:" + hunk1.getOrigStartLineNumber());
            System.out.println("--------------------Hunk@line1---------------------");
            Blame.Hunk hunk2 = blame.getHunkByLine(1);
            Assert.assertNotNull(hunk2);
            System.out.println("               boundary:" + hunk2.getBoundary());
            System.out.println("       getFinalCommitId:" + hunk2.getFinalCommitId());
            System.out.println("      getFinalSignature:" + hunk2.getFinalSignature());
            System.out.println("getFinalStartLineNumber:" + hunk2.getFinalStartLineNumber());
            System.out.println("         getLinesInHunk:" + hunk2.getLinesInHunk());
            System.out.println("        getOrigCommitId:" + hunk2.getOrigCommitId());
            System.out.println("            getOrigPath:" + hunk2.getOrigPath());
            System.out.println("       getOrigSignature:" + hunk2.getOrigSignature());
            System.out.println(" getOrigStartLineNumber:" + hunk2.getOrigStartLineNumber());
        }
    }
}
