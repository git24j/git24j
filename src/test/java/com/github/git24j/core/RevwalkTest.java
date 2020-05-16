package com.github.git24j.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RevwalkTest extends TestBase {

    @Rule public TemporaryFolder _folder = new TemporaryFolder();

    @Test
    public void hide() {
        try (Repository repo = TestRepo.SIMPLE1.tempRepo(_folder)) {
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
            assertEquals(2, commits.size());
        }
    }

    @Test
    public void hideGlob() {
        try (Repository repo = TestRepo.SIMPLE1.tempRepo(_folder)) {
            Oid head = Revparse.single(repo, "HEAD").id();
            Revwalk revwalk = Revwalk.create(repo);
            revwalk.sorting(EnumSet.of(SortT.TOPOLOGICAL, SortT.TIME, SortT.REVERSE));
            revwalk.push(head);
            // hide all refs that has pattern "refs/heads/feature/*" and their ancestors.
            revwalk.hideGlob("heads/feature/*");
            Oid oid = revwalk.next();
            Map<Oid, Commit> commits = new HashMap<>();
            while (oid != null) {
                Commit commit = Commit.lookup(repo, oid);
                commits.put(oid, commit);
                oid = revwalk.next();
            }
            assertFalse(commits.containsKey(Revparse.single(repo, "refs/heads/feature/dev").id()));
        }
    }

    @Test
    public void hideHead() {
        try (Repository repo = TestRepo.SIMPLE1.tempRepo(_folder)) {
            Revwalk revwalk = Revwalk.create(repo);
            revwalk.sorting(EnumSet.of(SortT.TOPOLOGICAL, SortT.TIME, SortT.REVERSE));
            // hide all refs that has pattern "refs/heads/feature/*" and their ancestors.
            revwalk.hideHead();
            revwalk.hide(Revparse.single(repo, "HEAD~2").id());
            revwalk.pushRef("refs/heads/feature/dev");
            Oid oid = revwalk.next();
            Map<Oid, Commit> commits = new HashMap<>();
            while (oid != null) {
                Commit commit = Commit.lookup(repo, oid);
                commits.put(oid, commit);
                oid = revwalk.next();
            }
            assertFalse(commits.containsKey(Revparse.single(repo, "refs/heads/feature/dev").id()));
        }
    }

    @Test
    public void hideRef() {
        String ref = "refs/heads/feature/dev";
        List<Oid> visited = new ArrayList<>();
        try (Repository repo = TestRepo.SIMPLE1.tempRepo(_folder)) {
            Revwalk revwalk = Revwalk.create(repo);
            revwalk.sorting(EnumSet.of(SortT.TOPOLOGICAL, SortT.TIME, SortT.REVERSE));
            // hide all refs that has pattern "refs/heads/feature/*" and their ancestors.
            revwalk.hideRef(ref);
            revwalk.pushHead();
            // revwalk.pushGlob("refs/heads/master");
            Oid oid = revwalk.next();
            while (oid != null) {
                visited.add(oid);
                oid = revwalk.next();
            }
            int half = visited.size();
            // start loop again
            revwalk.reset();
            revwalk.pushGlob("heads/*");
            revwalk.hideRef(ref);
            oid = revwalk.next();
            while (oid != null) {
                visited.add(oid);
                oid = revwalk.next();
            }
            Assert.assertEquals(half * 2, visited.size());
        }
    }

    @Test
    public void addHideCb() {
        try (Repository repo = TestRepo.SIMPLE1.tempRepo(_folder)) {
            List<Oid> visited = new ArrayList<>();
            List<Oid> hidden = new ArrayList<>();
            Oid oid2Hide = Revparse.single(repo, "HEAD~2").id();
            Revwalk revwalk = Revwalk.create(repo);
            revwalk.sorting(EnumSet.of(SortT.TOPOLOGICAL, SortT.TIME, SortT.REVERSE));

            revwalk.addHideCb(
                    hiddenOid -> {
                        hidden.add(hiddenOid);
                        if (oid2Hide.equals(hiddenOid)) {
                            // return 1 to hide the commit and its parents
                            return 1;
                        }
                        return 0;
                    });
            revwalk.pushRange("HEAD~5..HEAD");
            Oid oid = revwalk.next();
            while (oid != null) {
                visited.add(oid);
                oid = revwalk.next();
            }
            assertEquals(2, visited.size());
            assertTrue(hidden.size() > 0);
        }
    }

    @Test
    public void simplifyFirstParent() {
        try (Repository repo = TestRepo.SIMPLE1.tempRepo(_folder)) {
            Revwalk revwalk = Revwalk.create(repo);
            revwalk.sorting(EnumSet.of(SortT.TOPOLOGICAL, SortT.TIME, SortT.REVERSE));
            revwalk.simplifyFirstParent();
            revwalk.pushHead();
            revwalk.hide(Revparse.single(repo, "HEAD~5").id());
            Oid oid = revwalk.next();
            List<Oid> visited = new ArrayList<>();
            while (oid != null) {
                visited.add(oid);
                oid = revwalk.next();
            }
            Assert.assertEquals(5, visited.size());
        }
    }

    @Test
    public void repository() {
        try (Repository repo = TestRepo.SIMPLE1.tempRepo(_folder)) {
            Revwalk revwalk = Revwalk.create(repo);
            Assert.assertEquals(repo, revwalk.repository());
        }
    }
}
