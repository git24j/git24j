package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SignatureTest extends TestBase {

    private final String _tester = "tester";
    private final String _email = "tester@cc.com";

    @Rule public TemporaryFolder _folder = new TemporaryFolder();

    @Test
    public void common() {
        Assert.assertEquals(Signature.create(_tester, _email).getName(), _tester);
        Signature sig1 =
                Signature.fromBuffer(String.format("%s <%s> 1590369132 -0400", _tester, _email));
        Assert.assertEquals(sig1.getEmail(), _email);
        Assert.assertEquals(sig1.getName(), _tester);

        Signature sig2 = sig1.dup();
        Assert.assertEquals(sig1, sig2);

        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(_folder)) {
            Signature sig3 = Signature.getDefault(testRepo);
            Assert.assertNotNull(sig3);
        }
    }
}
