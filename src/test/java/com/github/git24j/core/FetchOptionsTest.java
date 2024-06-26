package com.github.git24j.core;

import org.junit.Test;

import java.util.Arrays;

import static com.github.git24j.core.FetchOptions.PruneT.NO_PRUNE;
import static org.junit.Assert.*;

public class FetchOptionsTest extends TestBase {

    @Test
    public void getterAndSetter() {
        FetchOptions opts = FetchOptions.createDefault();
        opts.setCustomHeaders(new String[] {"test1", "test2"});
        assertEquals(opts.getCustomHeaders(), Arrays.asList("test1", "test2"));
        opts.setPrune(NO_PRUNE);
        assertEquals(NO_PRUNE, opts.getPrune());
        opts.setUpdateFetchhead(false);
        assertFalse(opts.getUpdateFetchhead());
        opts.setDownloadTags(Remote.AutotagOptionT.AUTO);
        assertEquals(Remote.AutotagOptionT.AUTO, opts.getDownloadTags());
        opts.setFollowRedirects(Remote.RedirectT.ALL);
        assertEquals(opts.getFollowRedirects().getBit(), Remote.RedirectT.ALL.getBit());
        opts.setFollowRedirects(Remote.RedirectT.NONE);
        assertEquals(opts.getFollowRedirects().getBit(), Remote.RedirectT.NONE.getBit());
        opts.setFollowRedirects(Remote.RedirectT.INITIAL);
        assertEquals(opts.getFollowRedirects().getBit(), Remote.RedirectT.INITIAL.getBit());
        assertNotEquals(opts.getFollowRedirects().getBit(), Remote.RedirectT.ALL.getBit());

    }
}
