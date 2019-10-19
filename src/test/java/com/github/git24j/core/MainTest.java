package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

public class MainTest {

    @Test
    public void sayHello() {
        Assert.assertEquals("hello", new Main().sayHello());
    }
}
