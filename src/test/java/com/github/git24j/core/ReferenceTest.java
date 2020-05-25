package com.github.git24j.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ReferenceTest extends TestBase {
    private static final String REF_NAME_DEV = "refs/heads/feature/dev";
    /** {@code git rev-parse ref/heads/feature/dev } */
    private static final String OID_STR_DEV = "e5b28427ba064002e0e343e783ea3095018ce72c";

    private static final String TAG_NAME_V01 = "refs/tags/v0.1";
    private static final String OID_STR_TAG01 = "d9420a0ba5cbe2efdb1c3927adc1a2dd9355caff";

    private static final String ANNO_TAG_1_NAME = "refs/tags/annotated_tag_3";
    private static final String ANNO_TAG_1_SHA = "db179e4ff84cab6fad68b0e979f85937c4d85a90";

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
            Assert.assertNull(Reference.nameToId(testRepo, "refs/heads/non-exist-name"));
        }
    }

    @Test
    public void dwim() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Reference.dwim(testRepo, "feature/dev");
            Assert.assertEquals(REF_NAME_DEV, ref.name());
            Assert.assertNull(Reference.dwim(testRepo, "feature/non-exist-name"));
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

    /** TODO: libgit2's this function may have been wrong. */
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

    /** TODO: find a real case that tag can be peeled. */
    @Test
    public void targetPeel() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref1 = Reference.lookup(testRepo, TAG_NAME_V01);
            Oid oid = ref1.targetPeel();
            Assert.assertNull(oid);
        }
    }

    @Test
    public void symbolicTarget() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref1 =
                    Reference.symbolicCreateMatching(
                            testRepo, "TEST_HEAD", "refs/heads/master", false, null, null);
            Assert.assertEquals("refs/heads/master", ref1.symbolicTarget());
        }
    }

    @Test
    public void type() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref1 =
                    Reference.symbolicCreateMatching(
                            testRepo, "TEST_HEAD", "refs/heads/master", false, null, null);
            Assert.assertEquals(Reference.ReferenceType.SYMBOLIC, ref1.type());
            Reference ref2 = Reference.lookup(testRepo, "refs/tags/v0.1");
            Assert.assertEquals(Reference.ReferenceType.DIRECT, ref2.type());
        }
    }

    @Test
    public void resolve() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref1 =
                    Reference.symbolicCreateMatching(
                            testRepo, "TEST_HEAD", TAG_NAME_V01, false, null, null);
            Reference ref2 = Reference.lookup(testRepo, TAG_NAME_V01);
            Reference target = ref1.resolve();
            Assert.assertEquals(ref2.name(), target.name());
        }
    }

    @Test
    public void owner() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Reference.lookup(testRepo, TAG_NAME_V01);
            Assert.assertEquals(ref.owner(), testRepo);
        }
    }

    @Test
    public void symbolicSetTarget() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref1 =
                    Reference.symbolicCreate(
                            testRepo,
                            "ONE_UNDERSCORE",
                            "refs/heads/master",
                            false,
                            "create na new test head");
            Reference ref2 = ref1.symbolicSetTarget(TAG_NAME_V01, "some log messages");
            Assert.assertEquals(ref2.symbolicTarget(), TAG_NAME_V01);
        }
    }

    @Test
    public void setTarget() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Reference.lookup(testRepo, TAG_NAME_V01);
            Reference ref2 = ref.setTarget(Oid.of(OID_STR_DEV), "some log messages");
            Assert.assertEquals(ref2.target().toString(), OID_STR_DEV);
        }
    }

    @Test
    public void rename() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Reference.lookup(testRepo, TAG_NAME_V01);
            Reference ref2 = ref.rename("refs/tags/v2.0", false, "some log messages");
            Assert.assertEquals(ref2.name(), "refs/tags/v2.0");
        }
    }

    @Test
    public void delete() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Reference.lookup(testRepo, TAG_NAME_V01);
            Reference.delete(ref);
            Reference ref2 = Reference.lookup(testRepo, TAG_NAME_V01);
            Assert.assertNull(ref2);
        }
    }

    @Test
    public void remove() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference.remove(testRepo, TAG_NAME_V01);
            Reference ref2 = Reference.lookup(testRepo, TAG_NAME_V01);
            Assert.assertNull(ref2);
        }
    }

    @Test
    public void list() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            List<String> refList = Reference.list(testRepo);
            Assert.assertEquals(6, refList.size());
        }
    }

    @Test
    public void foreach() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            ArrayList<String> list = new ArrayList<>();
            Reference.foreach(
                    testRepo,
                    ref -> {
                        return list.add(ref.name()) ? 0 : 1;
                    });
            Assert.assertEquals(6, list.size());
        }
    }

    @Test
    public void foreachName() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            ArrayList<String> list = new ArrayList<>();
            Reference.foreachName(
                    testRepo,
                    name -> {
                        return list.add(name) ? 0 : 1;
                    });
            Assert.assertEquals(6, list.size());
        }
    }

    @Test
    public void dup() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Reference.lookup(testRepo, REF_NAME_DEV);
            Reference ref2 = ref.dup();
            Assert.assertEquals(ref, ref2);
            Assert.assertEquals(REF_NAME_DEV, ref2.name());
        }
    }

    @Test
    public void cmp() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref = Reference.lookup(testRepo, REF_NAME_DEV);
            Reference ref2 = ref.dup();
            Assert.assertEquals(0, Reference.cmp(ref, ref2));
            Reference ref3 = Reference.lookup(testRepo, REF_NAME_DEV);
            Assert.assertEquals(0, Reference.cmp(ref, ref3));
        }
    }

    @Test
    public void iterator() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            ArrayList<String> refList = new ArrayList<>();

            Reference.Iterator iterator = Reference.iteratorNew(testRepo);
            Reference ref = Reference.next(iterator);
            while (ref != null) {
                refList.add(ref.name());
                ref = Reference.next(iterator);
            }
            Assert.assertEquals(6, refList.size());

            Reference.Iterator iterGlob = Reference.iteratorGlobNew(testRepo, "refs/heads/*");
            refList.clear();
            String refName = Reference.nextName(iterGlob);
            while (refName != null) {
                refList.add(refName);
                refName = Reference.nextName(iterGlob);
            }
            Assert.assertEquals(2, refList.size());
        }
    }

    @Test
    public void iteratorName() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            ArrayList<String> refList = new ArrayList<>();
            Reference.Iterator iterator = Reference.iteratorGlobNew(testRepo, "refs/heads/*");
            String refName = Reference.nextName(iterator);
            while (refName != null) {
                refList.add(refName);
                refName = Reference.nextName(iterator);
            }
            Assert.assertEquals(2, refList.size());
        }
    }

    @Test
    public void foreachGlob() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            ArrayList<String> list = new ArrayList<>();
            Reference.foreachGlob(
                    testRepo,
                    "refs/heads/*",
                    name -> {
                        return list.add(name) ? 0 : 1;
                    });
            Assert.assertEquals(2, list.size());
        }
    }

    @Test
    public void hasLog() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Assert.assertTrue(Reference.hasLog(testRepo, REF_NAME_DEV));
            Assert.assertFalse(Reference.hasLog(testRepo, TAG_NAME_V01));
            Assert.assertFalse(Reference.hasLog(testRepo, ANNO_TAG_1_NAME));
            Assert.assertFalse(Reference.hasLog(testRepo, "master"));
        }
    }

    @Test
    public void ensureLog() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference.ensureLog(testRepo, REF_NAME_DEV);
            Reference.ensureLog(testRepo, TAG_NAME_V01);
            Reference.ensureLog(testRepo, ANNO_TAG_1_NAME);
            Reference.ensureLog(testRepo, "master");
            Assert.assertTrue(Reference.hasLog(testRepo, TAG_NAME_V01));
            Assert.assertTrue(Reference.hasLog(testRepo, ANNO_TAG_1_NAME));
            Assert.assertTrue(Reference.hasLog(testRepo, "master"));
        }
    }

    @Test
    public void isBranch() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Assert.assertTrue(Reference.lookup(testRepo, REF_NAME_DEV).isBranch());
            Assert.assertFalse(Reference.lookup(testRepo, TAG_NAME_V01).isBranch());
        }
    }

    @Test
    public void isRemote() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Assert.assertFalse(Reference.lookup(testRepo, REF_NAME_DEV).isRemote());
            Assert.assertFalse(Reference.lookup(testRepo, TAG_NAME_V01).isRemote());
        }
    }

    @Test
    public void isTag() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Assert.assertFalse(Reference.lookup(testRepo, REF_NAME_DEV).isTag());
            Assert.assertTrue(Reference.lookup(testRepo, TAG_NAME_V01).isTag());
        }
    }

    @Test
    public void isNote() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Assert.assertFalse(Reference.lookup(testRepo, REF_NAME_DEV).isNote());
            Assert.assertFalse(Reference.lookup(testRepo, TAG_NAME_V01).isNote());
        }
    }

    @Test
    public void normalizeName() {
        String refname = Reference.normalizeName(REF_NAME_DEV, EnumSet.of(Reference.Format.NORMAL));
        Assert.assertEquals(REF_NAME_DEV, refname);
    }

    /**
     * TODO: this tests nothing but not throwing unexpected errors. Peel is very confusing, find a
     * useful case.
     */
    @Test
    public void peel() {}

    @Test
    public void isValidName() {
        Assert.assertTrue(Reference.isValidName("refs/heads/master"));
        Assert.assertTrue(Reference.isValidName("DEV_HEAD"));
        Assert.assertFalse(Reference.isValidName("@{NAME}"));
    }

    @Test
    public void shortHand() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Reference ref1 = Reference.lookup(testRepo, ANNO_TAG_1_NAME);
            String shortHand = ref1.shorthand();
            Assert.assertEquals("annotated_tag_3", shortHand);
        }
    }
}
