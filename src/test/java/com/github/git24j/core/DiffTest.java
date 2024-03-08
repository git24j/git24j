package com.github.git24j.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class DiffTest extends TestBase {
    // git rev-parse HEAD^{tree}
    private static final String MASTER_TREE = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    // git rev-parse "feature/dev^{tree}"
    private static final String DEV_TREE = "3b597d284bc12d61638124054b19889587127208";

    private static final String README_BLOB1 = "08f8e5eba8074e2d3d5e17a8902eaea07633d0af";
    private static final String README_BLOB2 = "d628ad3b584b5ab3fa93dbdbcc66a15e4413d9b2";

    @Test
    public void indexToWorkdir() {
        String path = "set to your repo path";
        Repository repo=Repository.open(path);
        Map<Long, String> trace = new HashMap<>();

        Diff diff = Diff.indexToWorkdir(repo, repo.index(), Diff.Options.create());
        diff.foreach((Diff.FileCb)
            (delta, progress) -> {
                System.out.println();
                trace.put(
                        delta.getRawPointer(),
                        String.format(
                                "\toldfile = %s\n\tnewfile = %s\n\tflags = %s\n\tnfiles = %d\n\tstatus = %s\n\tsimilarity = %d\n",
                                delta.getOldFile(),
                                delta.getNewFile(),
                                delta.getFlags(),
                                delta.getNfiles(),
                                delta.getStatus(),
                                delta.getSimilarity()));
                return 0;
            },
                new Diff.BinaryCb() {
                    @Override
                    public int accept(Diff.Delta delta, Diff.Binary binary) {
                        trace.put(
                                binary.getRawPointer(),
                                String.format(
                                        "\tcontdata = %d\n\tdata = %s\n\tdatalen = %d\n\tinflen = %d\n\tfiletype = %d\n"
                                                + "\tdata = %s\n\tdatalen = %d\n\tinflen = %d\n\tfiletype = %d\n",
                                        binary.getContainsData(),
                                        binary.getNewFile().getData(),
                                        binary.getNewFile().getDatalen(),
                                        binary.getNewFile().getInflatedlen(),
                                        binary.getNewFile().getType(),
                                        binary.getOldFile().getData(),
                                        binary.getOldFile().getDatalen(),
                                        binary.getOldFile().getInflatedlen(),
                                        binary.getOldFile().getType()));
                        return 0;
                    }
                },
                (Diff.HunkCb)
                        (delta, hunk) -> {
                            trace.put(
                                    hunk.getRawPointer(),
                                    String.format(
                                            "\tnewlines = %d\n\toldlines = %d\n\theader = %s\n\theaderlen = %d\n\tnewstart = %d\n\toldstart = %d\n\t",
                                            hunk.getNewLines(),
                                            hunk.getOldLines(),
                                            hunk.getHeader(),
                                            hunk.getHeaderLen(),
                                            hunk.getNewStart(),
                                            hunk.getOldStart()));
                            return 0;
                        },
                (Diff.LineCb)
                        (delta, hunk, line) -> {
                    trace.put(
                            line.getRawPointer(),
                            String.format(
                                    "\tnumlines = %d\n\tnewlineno = %d\n\toldlineno = %d\n\tcontentlen = %d\n\tcontentoffset = %d\n\torigin = %c",
                                    line.getNumLines(),
                                    line.getNewLineno(),
                                    line.getOldLineno(),
                                    line.getContentLen(),
                                    line.getContentOffset(),
                                    line.getOrigin()));
                    return 0;
                });

            System.out.println(trace.size());
            Set<Long> ks = trace.keySet();
            for (Long k : ks) {
                System.out.println(trace.get(k));
            }
    }

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
            String p = "README.md";
            Map<Long, String> trace = new HashMap<>();
            Diff.blobs(
                    blob1,
                    p,
                    blob2,
                    p,
                    Diff.Options.create(Diff.Options.CURRENT_VERSION),
                    (Diff.FileCb)
                            (delta, progress) -> {
                                trace.put(
                                        delta.getRawPointer(),
                                        String.format(
                                                "\toldfile = %s\n\tnewfile = %s\n\tflags = %s\n\tnfiles = %d\n\tstatus = %s\n\tsimilarity = %d\n",
                                                delta.getOldFile(),
                                                delta.getNewFile(),
                                                delta.getFlags(),
                                                delta.getNfiles(),
                                                delta.getStatus(),
                                                delta.getSimilarity()));
                                return 0;
                            },
                    new Diff.BinaryCb() {
                        @Override
                        public int accept(Diff.Delta delta, Diff.Binary binary) {
                            trace.put(
                                    binary.getRawPointer(),
                                    String.format(
                                            "\tcontdata = %d\n\tdata = %s\n\tdatalen = %d\n\tinflen = %d\n\tfiletype = %d\n"
                                                    + "\tdata = %s\n\tdatalen = %d\n\tinflen = %d\n\tfiletype = %d\n",
                                            binary.getContainsData(),
                                            binary.getNewFile().getData(),
                                            binary.getNewFile().getDatalen(),
                                            binary.getNewFile().getInflatedlen(),
                                            binary.getNewFile().getType(),
                                            binary.getOldFile().getData(),
                                            binary.getOldFile().getDatalen(),
                                            binary.getOldFile().getInflatedlen(),
                                            binary.getOldFile().getType()));
                            return 0;
                        }
                    },
                    (Diff.HunkCb)
                            (delta, hunk) -> {
                                trace.put(
                                        hunk.getRawPointer(),
                                        String.format(
                                                "\tnewlines = %d\n\toldlines = %d\n\theader = %s\n\theaderlen = %d\n\tnewstart = %d\n\toldstart = %d\n\t",
                                                hunk.getNewLines(),
                                                hunk.getOldLines(),
                                                hunk.getHeader(),
                                                hunk.getHeaderLen(),
                                                hunk.getNewStart(),
                                                hunk.getOldStart()));
                                return 0;
                            },
                    (delta, hunk, line) -> {
                        trace.put(
                                line.getRawPointer(),
                                String.format(
                                        "\tnumlines = %d\n\tnewlineno = %d\n\toldlineno = %d\n\tcontentlen = %d\n\tcontentoffset = %d\n\torigin = %c",
                                        line.getNumLines(),
                                        line.getNewLineno(),
                                        line.getOldLineno(),
                                        line.getContentLen(),
                                        line.getContentOffset(),
                                        line.getOrigin()));
                        return 0;
                    });
            System.out.println(trace);
        }
    }
}
