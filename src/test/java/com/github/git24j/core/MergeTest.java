package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.*;

import static com.github.git24j.core.Checkout.StrategyT.ALLOW_CONFLICTS;
import static com.github.git24j.core.Checkout.StrategyT.FORCE;

public class MergeTest extends TestBase {
    // git rev-parse HEAD^{tree}
    private static final String MASTER_TREE = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    // git rev-parse "feature/dev^{tree}"
    private static final String DEV_TREE = "3b597d284bc12d61638124054b19889587127208";
    private static final String SHA_DEV = "e5b28427ba064002e0e343e783ea3095018ce72c";
    private static final String SHA_C = "f80e0b10f83e512d1fae0142d000cceba3aca721";
    private static final String SHA_C_DEV_BASE = "67a36754360b373d391af2182f9ad8929fed54d8";

    private static final String repoPath = "set to your repo path";

    /**
     * merge targetBranch into HEAD.
     * note: this is a very base merge demo, even no fast-forward, you shouldn't use these code in production.
     * a better example please check: https://github.com/libgit2/libgit2/blob/main/examples/merge.c
     */
    @Test
    public void mergeTargetBranchIntoHead() {
        String targetBranchShortHandName = "main";
        Repository repo = Repository.open(repoPath);
        Merge.Options mergeOpts = Merge.Options.create();
        Repository.StateT state = repo.state();

        mergeOpts.setFileFlags(1<<1);  //GIT_MERGE_FILE_STYLE_DIFF3
        mergeOpts.setFlags(0);  // 0 should mean no flags?

        Checkout.Options checkoutOpts = Checkout.Options.defaultOptions();
        checkoutOpts.setStrategy(EnumSet.of(FORCE,ALLOW_CONFLICTS));

        assert(state.getBit()==0); //expect 0 NONE
        Reference targetBranchRef = Reference.dwim(repo, targetBranchShortHandName);
        AnnotatedCommit mergeTarget = AnnotatedCommit.fromRef(repo, targetBranchRef);
        System.out.println("mergeTarget.ref():"+mergeTarget.ref());
        System.out.println("mergeTarget.id():"+mergeTarget.id());
//        System.out.println("mergeInto(current branch):"+mergeInto.id());
        ArrayList<AnnotatedCommit> acList = new ArrayList<>();
        acList.add(mergeTarget);
        Merge.AnalysisPair analysis = Merge.analysis(repo, acList);
        //expect NORMAL
        System.out.println("analysis.getAnalysis().name()::"+analysis.getAnalysis().name());
        if(analysis.getAnalysis().getBit()!= Merge.AnalysisT.NORMAL.getBit()) {
            System.out.println("repo state is not NORMAL! merge canceled!");
            return;
        }

        //do merege
        Merge.merge(repo,acList,mergeOpts,checkoutOpts);

        if(repo.index().hasConflicts()) {
            System.out.println("hasConflicts: "+repo.index().hasConflicts());
            System.out.println("Please resolve conflicts then make a commit conclude the merge");
        }else {
            doCommitThenCleanRepoState(repo, mergeTarget);
        }

        //alert: close should in finally block when use lib in production, this is test, so whatever
        repo.close();

    }

    private void doCommitThenCleanRepoState(Repository repo, AnnotatedCommit mergeTarget) {
        Signature sign = Signature.create("me", "me@invalidemailaddress19384774.com");

        // merged node should have two parents, HEAD and targetBranch.
        ArrayList<Commit> commitList = new ArrayList<>();
        Commit commit1 = Commit.lookup(repo,Reference.dwim(repo, "HEAD").id());
        Commit commit2 = Commit.lookup(repo, mergeTarget.id());
        commitList.add(commit1);
        commitList.add(commit2);
        System.out.println("commit ID1:"+commit1.id());
        System.out.println("commit ID2:"+commit2.id());
        String branchFullRefName = Reference.dwim(repo,"HEAD").name();
        System.out.println("branchFullRefName:"+branchFullRefName);

        //write repo's index as a tree
        Tree tree = Tree.lookup(repo, repo.index().writeTree());

        String mergeTargetShorthandName = Reference.dwim(repo, mergeTarget.ref()).shorthand();
        //commit
        Oid resultCommitOid = Commit.create(repo, branchFullRefName, sign, sign, null,
                "merge branch: \""+mergeTargetShorthandName+"\"",
                tree, commitList);  //问题在于这里
        System.out.println("New Commit Oid is:"+resultCommitOid);

        //clear repo state, else repo will stay "Merging" state
        repo.stateCleanup();
        repo.close();
    }

    @Test
    public void printAndAddConflictItems() {
        Repository repo = Repository.open(repoPath);
        Index index = repo.index();
        boolean hasConflict = index.hasConflicts();
        System.out.println("repo has conflict:"+hasConflict);
        if(!hasConflict) {
            System.out.println("No conflict!");
            return;
        }
        Index.ConflictIterator conflictIterator = index.conflictIteratorNew();
        Index.Conflict item = conflictIterator.next();
        while (item!=null) {
            String conflictFilePath = item.our.getPath();
            index.add(conflictFilePath);
            System.out.println("conflict file(both modified):"+conflictFilePath);
            item = conflictIterator.next();
        }
        System.out.println("After add, repo has conflict:"+index.hasConflicts());  //should be false
        Index.ConflictIterator it2 = index.conflictIteratorNew();
        System.out.println("conflict iterator is empty:"+(it2.next()==null)); //should be true
        assert(!index.hasConflicts());
        assert(it2.next()==null);

        //write changes to disk
        index.write();

        repo.close();
    }
    @Test
    public void getRepoState() {
        Repository repo = Repository.open(repoPath);
        Repository.StateT state = repo.state();
        System.out.println(state);
        repo.close();
    }
    @Test
    public void doCommitThenCleanRepoState() {
        //merged node should have two parents, HEAD and merge target branch,
        // `mergeTargetName` should set to merge target branch name
        String mergeTargetName = "main";

        Repository repo = Repository.open(repoPath);
        AnnotatedCommit mergeTarget = AnnotatedCommit.fromRef(repo, Reference.dwim(repo, mergeTargetName));
        Signature sign = Signature.create("me", "me@invalidemailaddress19384774.com");
        ArrayList<Commit> parents = new ArrayList<>();
        parents.add(Commit.lookup(repo, repo.head().id()));  //commit success but parents are wrong.
        parents.add(Commit.lookup(repo, mergeTarget.id()));

        Tree tree = Tree.lookup(repo, repo.index().writeTree());

        //create commit
        Commit.create(repo, repo.head().name(),sign,sign,null,"merge branch: \""+mergeTargetName+"\"",
                tree,parents);

        repo.stateCleanup();
        repo.close();
    }

    @Test
    public void clearIndex() {
        Repository repo = Repository.open(repoPath);
        Index index = repo.index();

        // this operation will clear all items in index,
        // after clear, `git status` will show "deleted" for items which were in last commit,
        // and show "untracked" for which weren't in last commit.
        // if you do commit after clear index, all files will become "untracked"
        index.clear();

//        index.write();  // make change to disk

        repo.close();
    }

    @Test
    public void repoStateCleanup() {
        Repository repo = Repository.open(repoPath);
        repo.stateCleanup();
        repo.close();
    }

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
