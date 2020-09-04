package com.github.git24j.core;

import org.junit.Test;

public class CertTest extends TestBase {

    @Test
    public void hostKeyGetterSetter() {
        Cert.HostKey hostKey = Cert.HostKey.createEmpty();
        hostKey.getHashMd5();
        hostKey.getHashSha1();
        hostKey.getHashSha256();
        hostKey.getParent();
        hostKey.getType();
    }
}
