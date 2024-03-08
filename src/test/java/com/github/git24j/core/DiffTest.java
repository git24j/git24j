package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

public class DiffTest extends TestBase {
    // git rev-parse HEAD^{tree}
    private static final String MASTER_TREE = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    // git rev-parse "feature/dev^{tree}"
    private static final String DEV_TREE = "3b597d284bc12d61638124054b19889587127208";

    private static final String README_BLOB1 = "08f8e5eba8074e2d3d5e17a8902eaea07633d0af";
    private static final String README_BLOB2 = "d628ad3b584b5ab3fa93dbdbcc66a15e4413d9b2";

    @Test
    public void indexToWorkdirAndHeadToWorkdir() {
        String path = "set to your repo path";
        Repository repo=Repository.open(path);
        Map<Long, String> trace = new HashMap<>();

        Diff diff = Diff.indexToWorkdir(repo, null, Diff.Options.create());

        diff.foreach((Diff.FileCb)
            (delta, progress) -> {
                Diff.File f = delta.getOldFile();
                if(f==null) {
                    f=delta.getNewFile();
                }
                System.out.println(f.getPath());
                return 0;
            },
        null,
                (Diff.HunkCb)
                        (delta, hunk) -> {
                            System.out.println(hunk.getHeader());
                            return 0;
                        },
                (Diff.LineCb)
                        (delta, hunk, line) -> {
            // conflict item will not show at here, try diff workdir to HEAD for it
                            System.out.println(getLineStatus(line.getOldLineno(), line.getNewLineno())+","+line.getOldLineno()+"to"+line.getNewLineno()+":::"+line.getOrigin()+line.getContent());

                            return 0;
                });


        System.out.println("diff head to work dir for some files are not in index but modified");

        //get your HEAD tree Oid
        System.out.println(Revparse.lookup(repo, "HEAD^{tree}").getFrom().id());

        // make diff
        Diff diffTree = Diff.treeToWorkdir(repo, (Tree) Tree.lookup(repo, Revparse.lookup(repo, "HEAD^{tree}").getFrom().id(), GitObject.Type.TREE), Diff.Options.create());
        diffTree.foreach(
                (delta, progress) -> {
                    Diff.File f = delta.getOldFile();
                    if(f==null) {
                        f=delta.getNewFile();
                    }
                    System.out.println(f.getPath());
                    return 0;
                },
                null,
                (delta, hunk) -> {

                    System.out.println(hunk.getHeader());
                    return 0;
                },
                ((delta, hunk, line) ->{
//                    System.out.println(delta.getOldFile().getPath());
                    System.out.println(getLineStatus(line.getOldLineno(), line.getNewLineno())+","+line.getOldLineno()+"to"+line.getNewLineno()+":::"+line.getOrigin()+line.getContent());
                    return 0;
                })
                );
    }
    private String getLineStatus(int oldLineNum, int newLineNum) {
        if (oldLineNum > 0 && newLineNum > 0 && oldLineNum != newLineNum) {
            return "Mod";
        }
        if (oldLineNum < 0 && newLineNum > 0) {
            return "New";
        }
        if (oldLineNum > 0 && newLineNum < 0) {
            return "Del";
        }
        return "Same";  //maybe same newLineNo and oldLineNo, such as context content
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
                                                delta.getOldFile().getPath(),
                                                delta.getNewFile().getPath(),
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
