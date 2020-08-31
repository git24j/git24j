package com.github.git24j.core;

import static com.github.git24j.core.Status.StatusT.CONFLICTED;
import static com.github.git24j.core.Status.StatusT.CURRENT;
import static com.github.git24j.core.Status.StatusT.IGNORED;
import static com.github.git24j.core.Status.StatusT.INDEX_DELETED;
import static com.github.git24j.core.Status.StatusT.INDEX_MODIFIED;
import static com.github.git24j.core.Status.StatusT.INDEX_NEW;
import static com.github.git24j.core.Status.StatusT.INDEX_RENAMED;
import static com.github.git24j.core.Status.StatusT.INDEX_TYPECHANGE;
import static com.github.git24j.core.Status.StatusT.WT_DELETED;
import static com.github.git24j.core.Status.StatusT.WT_MODIFIED;
import static com.github.git24j.core.Status.StatusT.WT_NEW;
import static com.github.git24j.core.Status.StatusT.WT_RENAMED;
import static com.github.git24j.core.Status.StatusT.WT_TYPECHANGE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * This test demonstrates usage of {@code Status}, similar to
 * https://libgit2.org/libgit2/ex/v1.0.0/status.html
 */
public class StatusExampleTest extends TestBase {

    @Rule public TemporaryFolder _folder = new TemporaryFolder();

    public enum FormatT {
        DEFAULT,
        LONG,
        SHORT,
        PORCELAIN
    }

    private static String showBranch(@Nonnull Repository repo) {
        String r =
                Optional.ofNullable(repo.head())
                        .map(Reference::shorthand)
                        .map(br -> String.format("## %s\n", br))
                        .orElse("HEAD (no branch)");
        return r;
    }

    private static List<String> printLong(@Nonnull Status.StatusList statusList) {
        List<String> staged = new ArrayList<>();
        staged.add("# Changes to be committed:\n");
        staged.add("#   (use \"git reset HEAD <file>...\" to unstage)\n");
        staged.add("#\n");
        List<String> notStaged = new ArrayList<>();
        notStaged.add("# Changes not staged for commit:\n");
        notStaged.add("#   (use \"git add%s <file>...\" to update what will be committed)\n");
        notStaged.add(
                "#   (use \"git checkout -- <file>...\" to discard changes in working directory)\n");
        notStaged.add("#\n");
        List<String> untracked = new ArrayList<>();
        untracked.add("# Ignored files:\n");
        untracked.add("#   (use \"git add -f <file>...\" to include in what will be committed)\n");
        untracked.add("#\n");
        List<String> ignored = new ArrayList<>();
        ignored.add("# Ignored files:\n");
        ignored.add("#   (use \"git add -f <file>...\" to include in what will be committed)\n");
        ignored.add("#\n");
        List<String> conflicted = new ArrayList<>();
        conflicted.add("# Unmerged paths:\n");
        conflicted.add("#   (use \"git add <file>...\" to mark resolution)\n");
        conflicted.add("#\n");

        int entryCnt = statusList.entryCount();
        for (int i = 0; i < entryCnt; i++) {
            Status.Entry entry = statusList.byIndex(i);
            Diff.Delta delta = entry.getHeadToIndex();
            if (delta == null) {
                continue;
            }
            EnumSet<Status.StatusT> flags = entry.getStatus();
            if (flags.contains(WT_NEW)) {
                untracked.add(
                        String.format(
                                "#\t%s%n",
                                Optional.ofNullable(delta.getOldFile())
                                        .map(Diff.File::getPath)
                                        .orElse("")));
                continue;
            }
            if (flags.contains(IGNORED)) {
                ignored.add(
                        String.format(
                                "#\t%s%n",
                                Optional.ofNullable(delta.getOldFile())
                                        .map(Diff.File::getPath)
                                        .orElse("")));
                continue;
            }
            if (flags.contains(CONFLICTED)) {
                conflicted.add(
                        String.format(
                                "#\tboth modified: %s",
                                Optional.of(delta.getOldFile())
                                        .map(Diff.File::getPath)
                                        .orElse("")));
            }
            if (flags.contains(CURRENT)) {
                continue;
            }
            String prefix = null;
            if (flags.contains(INDEX_NEW)) {
                prefix = "new file: ";
            }
            if (flags.contains(INDEX_MODIFIED) || flags.contains(WT_MODIFIED)) {
                prefix = "modified: ";
            }
            if (flags.contains(INDEX_DELETED) || flags.contains(WT_DELETED)) {
                prefix = "deleted: ";
            }
            if (flags.contains(INDEX_RENAMED) || flags.contains(WT_RENAMED)) {
                prefix = "renamed: ";
            }
            if (flags.contains(INDEX_TYPECHANGE) || flags.contains(WT_TYPECHANGE)) {
                prefix = "typechanged: ";
            }
            if (prefix == null) {
                continue;
            }

            String oldPath =
                    Optional.ofNullable(delta.getOldFile()).map(Diff.File::getPath).orElse(null);
            String newPath =
                    Optional.ofNullable(delta.getNewFile()).map(Diff.File::getPath).orElse(null);
            boolean isStaged =
                    Collections.disjoint(
                            flags, EnumSet.of(WT_DELETED, WT_MODIFIED, WT_RENAMED, WT_TYPECHANGE));
            if (oldPath != null && newPath != null && !Objects.equals(oldPath, newPath)) {
                (isStaged ? staged : notStaged)
                        .add(String.format("#\t%s %s -> %s%n", prefix, oldPath, newPath));
            } else {
                (isStaged ? staged : notStaged)
                        .add(
                                String.format(
                                        "#\t%s %s%n", prefix, oldPath != null ? oldPath : newPath));
            }
        }
        List<String> res = new ArrayList<>();
        if (staged.size() > 3) {
            res.addAll(staged);
        }
        if (notStaged.size() > 4) {
            res.addAll(notStaged);
        }
        if (untracked.size() > 3) {
            res.addAll(untracked);
        }
        if (ignored.size() > 3) {
            res.addAll(ignored);
        }
        if (conflicted.size() > 3) {
            res.addAll(conflicted);
        }
        return res;
    }

    public List<String> printSubmodules(@Nonnull Repository repo) {
        List<String> subs = new ArrayList<>();
        subs.add("# Submodules \n");
        Submodule.foreach(
                repo,
                new Submodule.Callback() {
                    @Override
                    public int accept(Submodule sm, String name) {
                        subs.add(String.format("# - submodule '%s' at %s\n", sm.name(), sm.path()));
                        return 0;
                    }
                });
        if (subs.size() > 1) {
            return subs;
        }
        return Collections.emptyList();
    }

    private List<String> status(@Nonnull Repository repo) {
        Status.Options statusOpts = Status.Options.newDefault();
        statusOpts.setShow(Status.ShowT.INDEX_AND_WORKDIR.getBit());
        statusOpts.setFlags(
                EnumSet.of(
                        Status.OptT.OPT_INCLUDE_UNTRACKED,
                        Status.OptT.OPT_RENAMES_HEAD_TO_INDEX,
                        Status.OptT.OPT_SORT_CASE_INSENSITIVELY));
        if (repo.isBare()) {
            Assert.fail("Cannot report status on bare repository: " + repo.workdir());
        }
        Status.StatusList statusList = Status.StatusList.listNew(repo, statusOpts);
        List<String> prints = new ArrayList<>();
        prints.add(showBranch(repo));
        prints.addAll(printSubmodules(repo));
        prints.addAll(printLong(statusList));
        return prints;
    }

    @Test
    public void testStatus() {
        try (Repository testRepo = TestRepo.CONFLICT.tempRepo(_folder)) {
            List<String> prints = status(testRepo);
            Assert.assertTrue(prints.size() > 1);
            prints.forEach(System.out::print);
        }
    }
}
