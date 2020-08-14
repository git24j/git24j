package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class ProxyTest extends TestBase {
    @Test
    public void options() {
        Proxy.Options options = Proxy.Options.createDefault();
        options.setUrl("test-url");
        Assert.assertEquals("test-url", options.getUrl());
        options.setType(Proxy.Type.AUTO);
        Assert.assertEquals(Proxy.Type.AUTO, options.getType());
        options.setVersion(123);
        Assert.assertEquals(123, options.getVersion());
        AtomicInteger counter = new AtomicInteger();
        options.setCertificateCheck(new Transport.CertificateCheckCb() {
            @Override
            public int accept(Cert cert, boolean valid, String host) {
                Assert.assertEquals(0, cert.getRawPointer());
                Assert.assertTrue(valid);
                Assert.assertEquals("host-test", host);
                counter.incrementAndGet();
                return 0;
            }
        });
        options.setCredentials(new Credential.AcquireCb() {
            @Override
            public Credential accept( String url, String usernameFromUrl, int allowedTypes) throws GitException {
                Assert.assertEquals("url-test", url);
                Assert.assertEquals("username_from_url-test", usernameFromUrl);
                Assert.assertEquals(1, allowedTypes);
                counter.incrementAndGet();
                return new Credential(true, 123L);
            }
        });
        Proxy.jniOptionsTestCallbacks(options.getRawPointer());
        Assert.assertEquals(2, counter.get());
    }
}