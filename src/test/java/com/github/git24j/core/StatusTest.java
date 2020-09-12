package com.github.git24j.core;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class StatusTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void options() {
        Status.Options opts = Status.Options.newDefault();
        Tree tree = new Tree(true, 123);
        opts.setBaseline(tree);
        assertEquals(123, opts.getBaseline().getRawPointer());
        EnumSet<Status.OptT> flags =
                EnumSet.of(Status.OptT.OPT_NO_REFRESH, Status.OptT.OPT_RENAMES_FROM_REWRITES);
        opts.setFlags(flags);
        assertEquals(flags, opts.getFlags());

        opts.setVersion(123);
        assertEquals(123, opts.getVersion());

        List<String> pathspec = Arrays.asList("a", "a/*/b", "a/b/*c");
        opts.setPathspec(pathspec);
        assertEquals(pathspec, opts.getPathspec());

        opts.setShow(123);
        assertEquals(123, opts.getShow());
    }

    @Test
    public void file() {
        try (Repository testRepo = TestRepo.MERGE1.tempRepo(folder)) {
            EnumSet<Status.StatusT> statuses = Status.file(testRepo, "a");
            assertEquals(statuses, EnumSet.of(Status.StatusT.CURRENT));
        }
    }

    @Test
    public void shouldIgnore() {
        try (Repository testRepo = TestRepo.MERGE1.tempRepo(folder)) {
            Assert.assertFalse(Status.shouldIgnore(testRepo, "a"));
        }
    }
}
