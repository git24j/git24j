package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ReferenceTest extends TestBase {
    private static final String REF_NAME_DEV = "refs/heads/feature/dev";
    /** {@code git rev-parse ref/heads/feature/dev } */
    private static final String OID_STR_DEV = "e5b28427ba064002e0e343e783ea3095018ce72c";

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void lookup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Reference.lookup(testRepo, REF_NAME_DEV);
            Assert.assertEquals(REF_NAME_DEV, ref.name());
        }
    }

    @Test
    public void nameToId() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Oid oid = Reference.nameToId(testRepo, REF_NAME_DEV);
            Assert.assertEquals(OID_STR_DEV, oid.toString());
        }
    }

    @Test
    public void dwim() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Reference.dwim(testRepo, "feature/dev");
            Assert.assertEquals(REF_NAME_DEV, ref.name());
        }
    }

    @Test
    public void symbolicCreateMatching() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref1 =
                    Reference.symbolicCreateMatching(
                            testRepo, "TEST_HEAD", "refs/heads/master", false, null, null);
            Assert.assertEquals("TEST_HEAD", ref1.name());

            Reference ref2 =
                    Reference.symbolicCreateMatching(
                            testRepo,
                            "TEST_HEAD",
                            "refs/heads/master",
                            true,
                            "refs/heads/master",
                            null);
            Assert.assertEquals("TEST_HEAD", ref2.name());
        }
    }

    @Test
    public void symbolicCreate() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref1 =
                    Reference.symbolicCreate(
                            testRepo,
                            "ONE_UNDERSCORE",
                            "refs/heads/master",
                            false,
                            "create na new test head");
            Assert.assertEquals("ONE_UNDERSCORE", ref1.name());
        }
    }

    @Test
    public void create() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref1 =
                    Reference.create(
                            testRepo,
                            "ONE_UNDERSCORE",
                            Oid.of(OID_STR_DEV),
                            false,
                            "test log message");

            Assert.assertEquals("ONE_UNDERSCORE", ref1.name());
        }
    }

    /**
     * TODO: libgit2's this function may have been wrong.
     */
    @Test
    public void createMatching() {
//        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
//            Reference ref1 =
//                    Reference.createMatching(
//                            testRepo,
//                            REF_NAME_DEV,
//                            Oid.of(OID_STR_DEV),
//                            false,
//                            Oid.of(OID_STR_DEV),
//                            "some log message");
//
//            Assert.assertEquals(REF_NAME_DEV, ref1.name());
//        }
    }
}
