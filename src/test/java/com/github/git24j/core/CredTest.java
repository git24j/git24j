package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

public class CredTest extends TestBase {

    @Test
    public void userpassPlaintextNew() {
        Cred cred = Cred.userpassPlaintextNew("user", "passwd");
        Assert.assertTrue(cred.getRawPointer() > 0);
    }

    @Test
    public void sshKeyNew() {
        Cred cred = Cred.sshKeyNew("user", "/tmp/abc.pub", "/tmp/abc.priv", "passphrase");
        Assert.assertTrue(cred.getRawPointer() > 0);
    }

    @Test
    public void sshKeyFromAgent() {
        Cred cred = Cred.sshKeyFromAgent("user");
        Assert.assertTrue(cred.getRawPointer() > 0);
    }

    @Test
    public void defaultNew() {
        Cred cred = Cred.defaultNew();
        Assert.assertTrue(cred.getRawPointer() > 0);
    }

    @Test
    public void usernameNew() {
        Cred cred = Cred.usernameNew("tester");
        Assert.assertTrue(cred.getRawPointer() > 0);
    }

    //    @Test
    //    public void sshKeyMemoryNew() {
    //        Cred cred = Cred.sshKeyMemoryNew("username", "publickey", "privatekey", "passphrase");
    //        Assert.assertTrue(cred.getRawPointer() > 0);
    //    }
}
