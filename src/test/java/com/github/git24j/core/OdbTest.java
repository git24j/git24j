package com.github.git24j.core;

import static com.github.git24j.core.GitObject.Type.ANY;
import static com.github.git24j.core.GitObject.Type.BLOB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class OdbTest extends TestBase {
    private static final String README_SHORT_SHA_HEAD = "d628ad3b";
    private static final String A_SHORT_SHA_HEAD = "7898192261";
    private static final String README_SHA_HEAD = "d628ad3b584b5ab3fa93dbdbcc66a15e4413d9b2";

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void create() {
        try (Odb odb = Odb.create()) {
            Assert.assertEquals(0, odb.numBackends());
        }
    }

    @Test
    public void open() throws IOException {
        try (Odb odb = Odb.create(folder.newFolder("test").toPath())) {
            Assert.assertEquals(2, odb.numBackends());
        }
    }

    @Test
    public void addDiskAlternate() throws IOException {
        try (Odb odb = Odb.create()) {
            odb.addDiskAlternate(folder.newFolder("objects").toPath());
            Assert.assertEquals(2, odb.numBackends());
        }
    }

    @Test
    public void read() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Path objectsDir = testRepo.workdir().resolve(".git").resolve("objects");
            try (Odb odb = Odb.create(objectsDir)) {
                Optional<OdbObject> odbObject = odb.read(Oid.of(README_SHA_HEAD));
                Assert.assertTrue(odbObject.isPresent());
                Assert.assertEquals(README_SHA_HEAD, odbObject.get().id().toString());
                Assert.assertEquals(GitObject.Type.BLOB, odbObject.get().type());
            }
        }
    }

    @Test
    public void readPrefix() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Odb odb = Odb.create(testRepo.workdir().resolve(".git/objects"))) {
            Optional<OdbObject> odbObject = odb.readPrefix(README_SHORT_SHA_HEAD);
            Assert.assertNotNull(odb.existsPrefix(README_SHORT_SHA_HEAD).orElse(null));
            Assert.assertTrue(odbObject.isPresent());
            Assert.assertEquals(README_SHA_HEAD, odbObject.get().id().toString());
            Assert.assertEquals(GitObject.Type.BLOB, odbObject.get().type());
        }
    }

    @Test
    public void readHeader() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Odb odb = Odb.create(testRepo.workdir().resolve(".git/objects"))) {
            Oid oidReadMe = Oid.of(README_SHA_HEAD);
            Assert.assertTrue(odb.exists(oidReadMe));
            odb.refresh();
            OdbObject.Header header = odb.readHeader(oidReadMe).orElse(null);
            Assert.assertNotNull(header);
            Assert.assertTrue(header.getLen() > 0);
            Assert.assertEquals(header.getType(), GitObject.Type.BLOB);
        }
    }

    @Test
    public void expandIds() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Odb odb = Odb.create(testRepo.workdir().resolve(".git/objects"))) {
            List<Oid> res =
                    odb.expandIds(Arrays.asList(README_SHORT_SHA_HEAD, A_SHORT_SHA_HEAD), ANY);
            Assert.assertEquals(2, res.size());
            Assert.assertEquals(README_SHA_HEAD, res.get(0).toString());
            // FIXME: git_odb_expand_ids failed to expand Oid of file "a"
            // Assert.assertFalse(res.get(1).isZero());
        }
    }

    @Test
    public void write() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Odb odb = Odb.create(testRepo.workdir().resolve(".git/objects"))) {
            Oid out = odb.write("test data".getBytes(), BLOB);
            Assert.assertFalse(out.isZero());
        }
    }

    @Test
    public void openWstream() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Odb odb = Odb.create(testRepo.workdir().resolve(".git/objects"))) {
            byte[] bytes = "test data".getBytes();
            Odb.Stream ws = odb.openWstream(bytes.length, BLOB);
            ws.write("test ");
            ws.write("data");
            Oid out = ws.finalizeWrite();
            Assert.assertFalse(out.isZero());
            Oid expect = Odb.hash("test data".getBytes(), BLOB);
            Assert.assertEquals(expect, out);
        }
    }

    @Test
    public void openRstream() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Odb odb = Odb.create(testRepo.workdir().resolve(".git/objects"))) {
            Oid oidReadMe = Oid.of(README_SHA_HEAD);
            Odb.RStream rs = odb.openRstream(oidReadMe);
            Assert.assertTrue(rs.getSize() > 0);
            Assert.assertEquals(GitObject.Type.BLOB, rs.getType());
        }
    }

    @Test
    public void hashfile() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Oid out = Odb.hashfile(testRepo.workdir().resolve("README.md"), BLOB);
            Assert.assertEquals(Oid.of(README_SHA_HEAD), out);
        }
    }

    @Ignore
    @Test
    public void addBackend() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Odb odb = Odb.create(testRepo.workdir().resolve(".git/objects"))) {
            odb.addBackend(odb.getBackend(0).get(), 99);
        }
    }

    @Test
    public void addAlternate() throws IOException {
        File odbAlternate = folder.newFolder(".test", "objects");
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Odb odb = Odb.create(testRepo.workdir().resolve(".git/objects"))) {
            odb.addDiskAlternate(odbAlternate.toPath());
        }
    }

    @Test
    public void numBackends() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Odb odb = Odb.create(testRepo.workdir().resolve(".git/objects"))) {
            Assert.assertTrue(odb.numBackends() >= 1);
        }
    }

    @Ignore
    @Test
    public void getBackend() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Odb odb = Odb.create(testRepo.workdir().resolve(".git/objects"))) {
            OdbBackend backend = odb.getBackend(0).orElse(null);
            Assert.assertNotNull(backend);
        }
    }

    @Test
    public void backendLoose() throws IOException {
        Path p = folder.newFolder(".loose", "objects").toPath();
        OdbBackend backend = Odb.backendLoose(p, 1, false, 0, 0);
        Assert.assertNotNull(backend);
    }

    @Test
    public void backendOnePack() throws IOException {
        Path p = folder.newFile(".pack_objects").toPath();
        OdbBackend backend = Odb.backendOnePack(p);
        Assert.assertNotNull(backend);
    }
}
