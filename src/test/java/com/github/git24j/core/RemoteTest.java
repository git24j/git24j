package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.git24j.core.Remote.CreateOptions.Flag.SKIP_DEFAULT_FETCHSPEC;
import static com.github.git24j.core.Remote.CreateOptions.Flag.SKIP_INSTEADOF;

public class RemoteTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void basics() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Remote.addFetch(testRepo, "origin", "+refs/heads/*:refs/remotes/origin/*");
            Remote r = Remote.lookup(testRepo, "origin");
            Assert.assertTrue(r.getRefspec(0).isPresent());
            Assert.assertFalse(r.getFetchRefspecs().isEmpty());
            Assert.assertTrue(r.getPushRefspecs().isEmpty());
            Remote.AutotagOptionT optionT = r.autotag();
            Assert.assertEquals(Remote.AutotagOptionT.AUTO, optionT);
        }
    }

    @Ignore(value = "requires ssh2 support")
    @Test
    public void connect() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Remote r = Remote.create(testRepo, "local", testRepo.workdir().toUri());
            Remote.Callbacks callbacks = Remote.Callbacks.createDefault();
            r.connect(Remote.Direction.FETCH, callbacks, null, null);
            Assert.assertTrue(r.connected());

            r.disconnect();
            Assert.assertFalse(r.connected());
        }
    }

    @Test
    public void create() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Remote r = Remote.create(testRepo, "local", testRepo.workdir().toUri());
            r.fetch(null, null, null);
            Remote r2 = Remote.createAnonymous(testRepo, testRepo.workdir().toUri());
            r2.fetch(null, null, null);
            Remote r3 = Remote.createDetached(testRepo.workdir().toUri());
            try {
                r3.fetch(null, null, null);
                Assert.fail("should have failed for 'cannot download detached remote'");
            } catch (GitException e) {
                Assert.assertEquals(e.getMessage(), "cannot download detached remote");
            }
            Remote r4 =
                    Remote.createWithFetchspec(
                            testRepo, "local2", testRepo.workdir().toUri(), null);
            r4.fetch(null, null, null);

            Remote.CreateOptions opts = Remote.CreateOptions.createDefault();
            // TODO: add getter setters for CreateOptions
            Remote r5 = Remote.createWithOpts(testRepo.workdir().toUri(), opts);
            Assert.assertNotNull(r5);
            // r5.fetch(null, null, null);
        }
    }

    @Test
    public void createOptions() {
        Remote.CreateOptions opts = Remote.CreateOptions.createDefault();
        opts.setFetchspec("fetch-spec");
        Assert.assertEquals("fetch-spec", opts.getFetchspec());
        opts.setFlags(EnumSet.of(SKIP_INSTEADOF, SKIP_DEFAULT_FETCHSPEC));
        Assert.assertEquals(EnumSet.of(SKIP_INSTEADOF, SKIP_DEFAULT_FETCHSPEC), opts.getFlags());
        opts.setName("test-name");
        Assert.assertEquals("test-name", opts.getName());
        opts.setRepository(null);
        Assert.assertNull(opts.getRepository());
    }

    @Test
    public void createWithOpts() {
        URI url = URI.create("https://github.com/git24j/git24j.git");
        Remote r = Remote.createWithOpts(url, null);
        Assert.assertEquals(url, r.url());
        Assert.assertFalse(r.defaultBranch().isPresent());
    }

    @Test
    public void delete() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Remote.delete(testRepo, "origin");
            Remote r2 = Remote.lookup(testRepo, "origin");
            Assert.assertNull(r2);
        }
    }

    @Test
    public void download() {}

    @Test
    public void dup() {}

    @Test
    public void fetch() {}

    @Test
    public void isValidName() {}

    @Test
    public void list() {}

    @Test
    public void lookup() {}

    @Test
    public void name() {}

    @Test
    public void owner() {}

    @Test
    public void prune() {}

    @Test
    public void pruneRefs() {}

    @Test
    public void push() {}

    @Test
    public void pushurl() {}

    @Test
    public void refspecCount() {}

    @Test
    public void rename() {}

    @Test
    public void setAutotag() {}

    @Test
    public void setPushurl() {}

    @Test
    public void setUrl() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Remote.setUrl(testRepo, "test", URI.create("https://example.com/test.git"));
            Remote r = Remote.lookup(testRepo, "test");
            Assert.assertEquals(URI.create("https://example.com/test.git"), r.url());
        }
    }

    @Test
    public void stats() {}

    @Test
    public void stop() {}

    @Test
    public void updateTips() {}

    @Test
    public void updaload() {}

    @Test
    public void url() {}

    @Test
    public void callbacks() {
        AtomicInteger counter = new AtomicInteger();
        Remote.Callbacks cb = Remote.Callbacks.createDefault();
        // cb->sideband_progress("sideband_progress.str", 1, payload);
        cb.setSidebandProgress(
                message -> {
                    Assert.assertEquals(message, "sideband_progress.str");
                    counter.incrementAndGet();
                    return 0;
                });
        // cb->completion(1, payload);
        cb.setCompletionCb(
                type -> {
                    Assert.assertEquals(1, type.getBit());
                    counter.incrementAndGet();
                    return 0;
                });
        // cb->credentials(NULL, "credentials.url", "credentials.user_name_from_url", 1, payload);
        cb.setCredAcquireCb(
                (url, usernameFromUrl, allowedTypes) -> {
                    Assert.assertEquals("credentials.url", url);
                    Assert.assertEquals("credentials.user_name_from_url", usernameFromUrl);
                    Assert.assertEquals(1, allowedTypes);
                    counter.incrementAndGet();
                    return Optional.empty();
                });
        // cb->certificate_check(NULL, 1, "certificate_check.host", payload);
        cb.setCertificateCheckCb(
                (cert, valid, host) -> {
                    Assert.assertNull(cert);
                    Assert.assertTrue(valid);
                    Assert.assertEquals("certificate_check.host", host);
                    counter.incrementAndGet();
                    return 0;
                });
        // cb->transfer_progress(NULL, payload);
        cb.setTransferProgressCb(
                stats -> {
                    Assert.assertNull(stats);
                    counter.incrementAndGet();
                    return 0;
                });
        // cb->update_tips("update_tips.refname", NULL, NULL, payload);
        cb.setUpdateTipsCb(
                (refname, a, b) -> {
                    Assert.assertEquals("update_tips.refname", refname);
                    Assert.assertNull(a);
                    Assert.assertNull(b);
                    counter.incrementAndGet();
                    return 0;
                });
        //        cb->pack_progress(1, 2, 3, payload);
        cb.setPackProgressCb(
                (stage, current, total) -> {
                    Assert.assertEquals(1, stage);
                    Assert.assertEquals(2, current);
                    Assert.assertEquals(3, total);
                    counter.incrementAndGet();
                    return 0;
                });
        // cb->push_transfer_progress(1, 2, 3, payload);
        cb.setPushTransferProgressCb(
                (current, total, bytes) -> {
                    Assert.assertEquals(1, current);
                    Assert.assertEquals(2, total);
                    Assert.assertEquals(3, bytes);
                    counter.incrementAndGet();
                    return 0;
                });
        // cb->push_update_reference("push_update_reference.refname",
        //                            "push_update_reference.status", payload);
        cb.setPushUpdateReferenceCb(
                new Remote.PushUpdateReferenceCb() {
                    @Override
                    public int accept(String refname, String status) {
                        Assert.assertEquals("push_update_reference.refname", refname);
                        Assert.assertEquals("push_update_reference.status", status);
                        counter.incrementAndGet();
                        return 0;
                    }
                });
        // cb->push_negotiation(NULL, 1, payload);
        cb.setPushNegotiationCb(
                new Remote.PushNegotiationCb() {
                    @Override
                    public int accept(List<Remote.PushUpdate> updates) {
                        Assert.assertEquals(2, updates.size());
                        counter.incrementAndGet();
                        return 0;
                    }
                });
        // cb->transport(NULL, NULL, payload);
        cb.setTransportCb(
                new Remote.TransportCb() {
                    @Override
                    public Optional<Transport> accept(Remote owner) {
                        Assert.assertNull(owner);
                        counter.incrementAndGet();
                        return Optional.empty();
                    }
                });
        // cb->resolve_url(NULL, "resolve_url.url", 1, payload);
        cb.setUrlResolveCbCb(new Remote.UrlResolveCb() {
            @Override
            public int accept(String urlResolved, String url, @Nonnull Remote.Direction direction) {
                Assert.assertNull(urlResolved);
                Assert.assertEquals("resolve_url.url", url);
                Assert.assertEquals(1, direction.ordinal());
                counter.incrementAndGet();
                return 0;
            }
        });
        Remote.jniCallbacksTest(cb.getRawPointer(), cb);
        Assert.assertEquals(12, counter.get());
    }

    @Test
    public void pushUpdate() {
        Remote.PushUpdate pushUpdate = Remote.PushUpdate.create();
        Oid x = Oid.of("0123456789012345678901234567890123456789");
        pushUpdate.setDst(x);
        Assert.assertEquals(x, pushUpdate.getDst());
        pushUpdate.setSrc(x);
        Assert.assertEquals(x, pushUpdate.getSrc());
        pushUpdate.setSrcRefname("src-test");
        Assert.assertEquals("src-test", pushUpdate.getSrcRefname());
        pushUpdate.setDstRefname("dst-test");
        Assert.assertEquals("dst-test", pushUpdate.getDstRefname());
    }
}
