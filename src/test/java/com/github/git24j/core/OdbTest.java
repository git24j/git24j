package com.github.git24j.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class OdbTest extends TestBase {
    private static final String README_SHORT_SHA_HEAD = "08f8e5eba";
    private static final String README_SHA_HEAD = "08f8e5eba8074e2d3d5e17a8902eaea07633d0af";

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
                Assert.assertEquals(OdbObject.Type.BLOB, odbObject.get().type());
            }
        }
    }

    @Test
    public void readPrefix() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder);
                Odb odb = Odb.create(testRepo.workdir().resolve(".git/objects"))) {
            Optional<OdbObject> odbObject = odb.readPrefix(Oid.of(README_SHORT_SHA_HEAD));
            Assert.assertTrue(odbObject.isPresent());
            Assert.assertEquals(README_SHA_HEAD, odbObject.get().id().toString());
            Assert.assertEquals(OdbObject.Type.BLOB, odbObject.get().type());
        }
    }

    @Test
    public void readHeader() {}

    @Test
    public void exists() {}

    @Test
    public void existsPrefix() {}

    @Test
    public void expandIds() {}

    @Test
    public void refresh() {}

    @Test
    public void write() {}

    @Test
    public void openWstream() {}

    @Test
    public void openRstream() {}

    @Test
    public void hash() {}

    @Test
    public void hashfile() {}

    @Test
    public void addBackend() {}

    @Test
    public void addAlternate() {}

    @Test
    public void numBackends() {}

    @Test
    public void getBackend() {}

    @Test
    public void backendLoose() {}

    @Test
    public void backendOnePack() {}
}
