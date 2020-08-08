package com.github.git24j.core;

import org.junit.Test;

import java.util.Arrays;
import java.util.EnumSet;

import static com.github.git24j.core.Checkout.NotifyT.CONFLICT;
import static com.github.git24j.core.Checkout.NotifyT.UNTRACKED;
import static com.github.git24j.core.Checkout.StrategyT.ALLOW_CONFLICTS;
import static com.github.git24j.core.Checkout.StrategyT.FORCE;
import static com.github.git24j.core.Clone.LocalT.LOCAL_NO_LINKS;
import static org.junit.Assert.*;

public class CloneTest extends TestBase {

    @Test
    public void cloneOptions() {
        Clone.Options opts = Clone.Options.defaultOpts();
        opts.setBare(true);
        assertTrue(opts.getBare());
        opts.setLocal(LOCAL_NO_LINKS);
        assertEquals(LOCAL_NO_LINKS, opts.getLocal());
        // checkout options
        opts.getCheckoutOpts().setStrategy(EnumSet.of(ALLOW_CONFLICTS, FORCE));
        assertEquals(EnumSet.of(ALLOW_CONFLICTS, FORCE), opts.getCheckoutOpts().getStrategy());
        opts.getCheckoutOpts().setDisableFilter(true);
        assertTrue(opts.getCheckoutOpts().getDisableFilter());
        opts.getCheckoutOpts().setDirMode(644);
        assertEquals(644, opts.getCheckoutOpts().getDirMode());
        opts.getCheckoutOpts().setFileMode(755);;
        assertEquals(755, opts.getCheckoutOpts().getFileMode());
        opts.getCheckoutOpts().setOpenFlags(5);
        assertEquals(5, opts.getCheckoutOpts().getOpenFlags());
        opts.getCheckoutOpts().setNotifyFlags(EnumSet.of(UNTRACKED, CONFLICT));
        assertEquals(EnumSet.of(UNTRACKED, CONFLICT), opts.getCheckoutOpts().getNotifyFlags());
        opts.getCheckoutOpts().setPaths(new String[]{"x/y/z", "a/b/c"});
        assertEquals(Arrays.asList("x/y/z", "a/b/c"), opts.getCheckoutOpts().getPaths() );
        Tree fakeTree = new Tree(true, 123L);
        opts.getCheckoutOpts().setBaseline(fakeTree);
        assertEquals(123L , opts.getCheckoutOpts().getBaseline().getRawPointer());
        Index fakeIndex = new Index(1234L);
        opts.getCheckoutOpts().setBaselineIndex(fakeIndex);
        assertEquals(1234L, opts.getCheckoutOpts().getBaselineIndex().getRawPointer());
        fakeIndex._rawPtr.set(0);
        opts.getCheckoutOpts().setTargetDirectory("test/dir");
        assertEquals("test/dir", opts.getCheckoutOpts().getTargetDirectory());
        opts.getCheckoutOpts().setAncestorLabel("test-label");
        assertEquals("test-label", opts.getCheckoutOpts().getAncestorLabel());
        opts.getCheckoutOpts().setOurLabel("our-label");
        assertEquals("our-label", opts.getCheckoutOpts().getOurLabel());
        opts.getCheckoutOpts().setTheirLabel("their-label");
        assertEquals("their-label", opts.getCheckoutOpts().getTheirLabel());
        // opts.getCheckoutOpts().;
        // assertEquals(, opts.getCheckoutOpts().);
    }
}