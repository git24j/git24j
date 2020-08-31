package com.github.git24j.core;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PatchTest extends TestBase {

    // git rev-parse HEAD^{tree}
    private static final String MASTER_TREE = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    // git rev-parse "feature/dev^{tree}"
    private static final String DEV_TREE = "3b597d284bc12d61638124054b19889587127208";

    private static final String README_BLOB1 = "08f8e5eba8074e2d3d5e17a8902eaea07633d0af";
    private static final String README_BLOB2 = "d628ad3b584b5ab3fa93dbdbcc66a15e4413d9b2";

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void getDelta() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Tree head = (Tree) Tree.lookup(testRepo, Oid.of(MASTER_TREE), GitObject.Type.TREE);
            Tree dev = (Tree) Tree.lookup(testRepo, Oid.of(DEV_TREE), GitObject.Type.TREE);
            Diff diff = Diff.treeToTree(testRepo, dev, head, null);
            Patch patch = Patch.fromDiff(diff, 0);
            Assert.assertNotNull(patch);
            Assert.assertEquals(Diff.DeltaT.MODIFIED, patch.getDelta().getStatus());
            Assert.assertTrue(patch.getHunk(0).getLines() > 0);
            Patch.LineStats lineStats = patch.lineStats();
            Assert.assertEquals(13, lineStats.getTotalAdditions());
            Assert.assertEquals(4, lineStats.getTotalDeletions());
            Assert.assertEquals(
                    "commit 0: add .gitignore\n", patch.getLineInHunk(0, 0).getContent());
            Assert.assertEquals(18, patch.numLinesInHunk(0));
            Assert.assertTrue(patch.size(true, true, true) > 0);
            Set<String> hunks = new LinkedHashSet<>();
            Set<String> lines = new LinkedHashSet<>();
            patch.print(
                    new Diff.LineCb() {
                        @Override
                        public int accept(Diff.Delta delta, Diff.Hunk hunk, Diff.Line line) {
                            if (hunk != null) {
                                hunks.add(hunk.getHeader());
                            }
                            if (line != null) {
                                char c = line.getOrigin();
                                if (c == '-' || c == '+') {
                                    lines.add(c + line.getContent());
                                } else {
                                    lines.add(line.getContent());
                                }
                            }
                            return 0;
                        }
                    });
            Assert.assertEquals(20, lines.size());
            Assert.assertEquals(1, hunks.size());
            Assert.assertEquals(patch.toBuf(), lines.stream().collect(Collectors.joining("")));
        }
    }
}
