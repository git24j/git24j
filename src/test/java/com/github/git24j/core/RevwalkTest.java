package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RevwalkTest extends TestBase {

    @Rule public TemporaryFolder _folder = new TemporaryFolder();

    @Test
    public void hide() {
        try(Repository repo = TestRepo.SIMPLE1.tempRepo(_folder)){
            Revwalk revwalk = Revwalk.create(repo);
            revwalk.sorting(EnumSet.of(SortT.TOPOLOGICAL, SortT.TIME, SortT.REVERSE));
            revwalk.pushHead();
            // hide all commits that precede HEAD~2 (also hid HEAD~2 itself)
            revwalk.hide(Revparse.single(repo, "HEAD~2").id());
            Oid oid = revwalk.next();
            Map<Oid, Commit> commits = new HashMap<>();
            while (oid != null) {
                Commit commit = Commit.lookup(repo, oid);
                commits.put(oid, commit);
                oid = revwalk.next();
            }
            Assert.assertEquals(2, commits.size());
        }
    }

    @Test
    public void hideGlob() {
        try(Repository repo = TestRepo.SIMPLE1.tempRepo(_folder)){
            Revwalk revwalk = Revwalk.create(repo);
            revwalk.sorting(EnumSet.of(SortT.TOPOLOGICAL, SortT.TIME, SortT.REVERSE));
            revwalk.pushHead();
            // hide all refs that has pattern "refs/heads/feature/*" and their ancestors.
            revwalk.hideGlob("heads/feature/*");
            Oid oid = revwalk.next();
            Map<Oid, Commit> commits = new HashMap<>();
            while (oid != null) {
                Commit commit = Commit.lookup(repo, oid);
                commits.put(oid, commit);
                oid = revwalk.next();
            }
            Assert.assertFalse(commits.containsKey(Revparse.single(repo, "refs/heads/feature/dev").id()));
        }
    }

    @Test
    public void hideHead() {
        try(Repository repo = TestRepo.SIMPLE1.tempRepo(_folder)){
            Revwalk revwalk = Revwalk.create(repo);
            revwalk.sorting(EnumSet.of(SortT.TOPOLOGICAL, SortT.TIME, SortT.REVERSE));
            // hide all refs that has pattern "refs/heads/feature/*" and their ancestors.
            revwalk.hideHead();
            revwalk.pushRef("refs/heads/feature/dev");
            Oid oid = revwalk.next();
            Map<Oid, Commit> commits = new HashMap<>();
            while (oid != null) {
                Commit commit = Commit.lookup(repo, oid);
                commits.put(oid, commit);
                oid = revwalk.next();
            }
            Assert.assertFalse(commits.containsKey(Revparse.single(repo, "refs/heads/feature/dev").id()));
        }
    }

    @Test
    public void testHideHead() {}

    @Test
    public void hideRef() {}

    @Test
    public void testHideRef() {}

    @Test
    public void create() {}

    @Test
    public void next() {}

    @Test
    public void push() {}

    @Test
    public void pushGlob() {}

    @Test
    public void pushHead() {}

    @Test
    public void pushRange() {}

    @Test
    public void pushRef() {}

    @Test
    public void repository() {}

    @Test
    public void reset() {}

    @Test
    public void simplifyFirstParent() {}

    @Test
    public void sorting() {}
}