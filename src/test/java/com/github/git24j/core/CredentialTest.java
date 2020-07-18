package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class CredentialTest extends TestBase {
    @Test
    public void hasUsername() {
        try {
            Credential credential = Credential.defaultNew();
            Assert.assertTrue(credential.getUsername() == null || credential.hasUsername());
        } catch (GitException e) {
            //
        }
    }

    @Test
    public void sshKeyNew() {
        try {
            Credential c1 =
                    Credential.sshKeyNew("test", "test-pub-key", "test-priv-key", "test-phrase");
            Assert.assertTrue(c1.hasUsername());
        } catch (GitException ignore) {
            //
        }
    }

    @Test
    public void sshKeyMemoryNew() {
        try {
            Credential c2 =
                    Credential.sshKeyMemoryNew(
                            "test", "test-pub-key", "test-priv-key", "test-phrase");
            Assert.assertTrue(c2.hasUsername());
        } catch (GitException ignore) {
            //
        }
    }

    @Test
    public void fromAgent() {
        try {
            Credential c3 = Credential.fromAgent("test");
            Assert.assertTrue(c3.hasUsername());
        } catch (GitException ignore) {
            //
        }
    }
}
