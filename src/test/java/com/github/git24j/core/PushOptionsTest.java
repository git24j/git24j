package com.github.git24j.core;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PushOptionsTest extends TestBase {

    @Test
    public void getterSetters() {
        PushOptions opts = PushOptions.createDefault();
        opts.setCustomHeaders(new String[] {"test1", "test2"});
        assertEquals(Arrays.asList("test1", "test2"), opts.getCustomHeaders());
        opts.setPbParallelism(123);
        assertEquals(123, opts.getPbParallelism());
        opts.setVersion(234);
        assertEquals(234, opts.getVersion());
        opts.getCallbacks().setTransportCb(owner -> null);
        opts.getCallbacks().setCompletionCb(completionT -> 0);
    }
}
