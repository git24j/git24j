package com.github.git24j.core;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MailmapTest extends TestBase {

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void addEntry() {
        Mailmap mm =
                Mailmap.fromBuffer("Other Author <other@author.xx>         <nick2@company.xx>");
        mm.addEntry("test", "test@cc.com", "", "whatever@cc.com");
        Map.Entry<String, String> resolved = mm.resolve("", "whatever@cc.com");
        Assert.assertEquals(resolved.getKey(), "test");
        Assert.assertEquals(resolved.getValue(), "test@cc.com");
    }

    @Test
    public void fromBuffer() {
        Mailmap mm =
                Mailmap.fromBuffer("Other Author <other@author.xx>         <nick2@company.xx>");
        Map.Entry<String, String> resolved = mm.resolve("", "nick2@company.xx");
        Assert.assertEquals(resolved.getKey(), "Other Author");
        Assert.assertEquals(resolved.getValue(), "other@author.xx");
    }

    @Test
    public void fromRepositoryAndResolve() {
        try (Repository testRepo = TestRepo.MAILMAP.tempRepo(folder)) {
            Mailmap mm = Mailmap.fromRepository(testRepo);
            AtomicReference<String> realName = new AtomicReference<>();
            AtomicReference<String> realEmail = new AtomicReference<>();
            mm.resolve(realName, realEmail, "nick1", "bugs@company.xx");
            Assert.assertEquals("Some Dude", realName.get());
            Assert.assertEquals("some@dude.xx", realEmail.get());
        }
    }

    @Test
    public void resolveSignature() {
        try (Repository testRepo = TestRepo.MAILMAP.tempRepo(folder)) {
            Mailmap mm = Mailmap.fromRepository(testRepo);
            Signature sig = new Signature("nick1", "bugs@company.xx", 1577320750L, -240);
            Optional<Signature> outSig = mm.resolveSignature(sig);
            Assert.assertEquals("Some Dude", outSig.map(Signature::getName).orElse(""));
            Assert.assertEquals("some@dude.xx", outSig.map(Signature::getEmail).orElse(""));
        }
    }
}
