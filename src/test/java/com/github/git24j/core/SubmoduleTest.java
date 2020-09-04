package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SubmoduleTest extends TestBase {

    private Repository openSubmoduleRepo() {
        Path repoPath = TestRepo.SUBMODULE.tempCopy(folder);
        return Repository.open(repoPath.resolve("x"));
    }

    @Test
    public void foreach() {
        try (Repository repo = openSubmoduleRepo()) {
            List<String> submodules = new ArrayList<>();
            List<String> names = new ArrayList<>();
            Submodule.foreach(
                    repo,
                    new Submodule.Callback() {
                        @Override
                        public int accept(Submodule sm, String name) {
                            submodules.add(sm.name());
                            names.add(name);
                            return 0;
                        }
                    });
            Assert.assertEquals(1, submodules.size());
            Assert.assertEquals("s", submodules.get(0));
            Assert.assertEquals("s", names.get(0));
        }
    }

    @Test
    public void addFinalize() {}

    @Test
    public void addSetup() {}

    @Test
    public void addToIndex() {}

    @Test
    public void branch() {
        try (Repository repo = openSubmoduleRepo()) {
            Submodule sub = Submodule.lookup(repo, "s");
            sub.branch();
            Assert.assertNotNull(sub.headId());
            Assert.assertNotNull(sub.indexId());
            Assert.assertNotNull(sub.ignore());
            Assert.assertNotNull(sub.location());
            Assert.assertNotNull(sub.owner().workdir());
            Assert.assertNotNull(sub.path());
            Assert.assertNotNull(sub.url());
            Assert.assertNotNull(Submodule.status(repo, "s", null));
        }
    }

    @Test
    public void fetchRecurseSubmodules() {}

    @Test
    public void init() {}

    @Test
    public void open() {}

    @Test
    public void reload() {}

    @Test
    public void repoInit() {}

    @Test
    public void resolveUrl() {}

    @Test
    public void setBranch() {}

    @Test
    public void setFetchRecurseSubmodules() {}

    @Test
    public void setIgnore() {}

    @Test
    public void setUpdate() {}

    @Test
    public void setUrl() {}

    @Test
    public void sync() {}

    @Test
    public void update() {}

    @Test
    public void updateStrategy() {}

    @Test
    public void url() {}

    @Test
    public void wdId() {}

    @Test
    public void testClone() {}
}
