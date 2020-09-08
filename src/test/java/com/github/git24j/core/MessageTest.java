package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest extends TestBase {

    @Test
    public void trailers() {
        Message.TrailerArray res = Message.trailers("subject\n" +
                "\n" +
                "message\n" +
                "\n" +
                "sign: Alice <alice@example.com>\n" +
                "sign: Bob <bob@example.com>\n");
        Assert.assertEquals(2, res.getTrailers().size());
        Message.Trailer t1 = res.getTrailers().get(0);
        Message.Trailer t2 = res.getTrailers().get(1);
        Assert.assertEquals(t1.getKey(), "sign");
        Assert.assertEquals(t1.getValue(), "Alice <alice@example.com>");
        Assert.assertEquals(t2.getKey(), "sign");
        Assert.assertEquals(t2.getValue(), "Bob <bob@example.com>");
    }

    @Test
    public void prettify() {
        String orig = "subject \n#a comment\t\t\nmessage\t";
        String formatted = Message.prettify(orig, true, '#');
        Assert.assertEquals("subject\nmessage\n", formatted);
    }
}