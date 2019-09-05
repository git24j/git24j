package org.shijinglu;


import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

@RunWith(JUnit4ClassRunner.class)
public class HelloWorldTest {

    @Test
    public void sayHello() {
        Assert.assertEquals(new HelloWorld().sayHello(), "hello");
    }
}