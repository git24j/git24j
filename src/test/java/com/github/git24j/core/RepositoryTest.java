package com.github.git24j.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RepositoryTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void open() {
        Path repoPath = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repo = Repository.open(repoPath)) {
            Assert.assertTrue(sameFile(repoPath.resolve(".git"), Paths.get(repo.getPath())));
        }
    }

    @Test
    public void openExt() throws IOException {
        Path repoPath = TestRepo.SIMPLE1.tempCopy(folder);
        // open repository without looking at parent directories
        try (Repository repo1 =
                Repository.openExt(repoPath, EnumSet.of(Repository.OpenFlag.NO_SEARCH), null)) {
            Assert.assertEquals(repo1.workdir(), repoPath);
        }
        Path sub = repoPath.resolve("sub");
        Files.createDirectories(sub);
        // walk up parent directories looking for the repository
        try (Repository repo2 = Repository.openExt(sub, null, "/tmp:/home:/usr")) {
            Assert.assertEquals(repo2.workdir(), repoPath);
        }
    }

    @Test
    public void openBare() {
        Path repoPath = TestRepo.SIMPLE1_BARE.tempCopy(folder);
        try (Repository repo = Repository.openBare(repoPath)) {
            Assert.assertTrue(sameFile(repoPath, Paths.get(repo.getPath())));
        }
    }

    @Test
    public void initOptions() {
        Repository.InitOptions initOptions = Repository.InitOptions.defaultOpts();
        Assert.assertEquals(Repository.InitOptions.VERSION, initOptions.getVersion());
    }

    @Test
    public void init() {
        Path repoPath = folder.getRoot().toPath();
        try (Repository repo = Repository.init(repoPath, false)) {
            Assert.assertTrue(repo.isEmpty());
            Assert.assertTrue(repo.headUnborn());
        }
    }

    @Test
    public void iniExt() {
        Path repoPath = folder.getRoot().toPath();
        String initPath = repoPath.toString();
        Repository.InitOptions opts = Repository.InitOptions.defaultOpts();
        try (Repository repo = Repository.initExt(initPath, opts)) {
            Assert.assertNotNull(repo);
            Assert.assertTrue(repo.isEmpty());
            Assert.assertTrue(repo.headUnborn());
        }
        try (Repository repo = Repository.openExt(repoPath, null, null)) {
            Assert.assertNotNull(repo);
            Assert.assertTrue(repo.isEmpty());
            Assert.assertTrue(repo.headUnborn());
        }
    }
//
//    @Test
//    public void discover() throws Exception {
//        Path repoPath = TestRepo.SIMPLE1.tempCopy(folder);
//        Path sub = repoPath.resolve("sub");
//        Files.createDirectories(sub);
//        Optional<String> path1 = Repository.discover(sub, true, "/tmp:/home");
//        Assert.assertEquals(repoPath.resolve(".git/"), path1.map(Paths::get).orElse(null));
//        Optional<String> path2 =
//                Repository.discover(folder.newFolder("not-a-repo").toPath(), false, "/tmp:/home");
//        Assert.assertFalse(path2.isPresent());
//    }
//
//    @Test
//    public void headForWorkTree() {
//        Path path = TestRepo.WORKTREE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            Reference ref = repository.headForWorkTree("wt1");
//            Assert.assertTrue(ref.getRawPointer() > 0);
//        }
//    }

//    @Test
//    public void headDetached_headUnborn_isEmpty() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            Assert.assertFalse(repository.headDetached());
//            Assert.assertFalse(repository.headUnborn());
//            Assert.assertFalse(repository.isEmpty());
//        }
//    }
//
//    @Test
//    public void getCommondir() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            Assert.assertTrue(sameFile(path.resolve(".git"), Paths.get(repository.getPath())));
//            Assert.assertTrue(sameFile(path.resolve(".git"), Paths.get(repository.getCommondir())));
//        }
//    }
//
//    @Test
//    public void itemPath() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            Buf buf = repository.itemPath(Repository.Item.COMMONDIR);
//            Assert.assertEquals(buf.getPtr(), repository.getCommondir());
//        }
//    }
//
//    @Test
//    public void workdir() {
//        Path path = TestRepo.WORKTREE1.tempCopy(folder);
//        Path w2 = path.resolve("w2");
//        Assert.assertTrue(w2.toFile().mkdirs());
//        try (Repository repository = Repository.open(path)) {
//            Path wd = repository.workdir();
//            Assert.assertTrue(sameFile(path, wd));
//            Assert.assertNotNull(wd);
//            repository.setWorkdir(w2, true);
//            wd = repository.workdir();
//            Assert.assertTrue(sameFile(w2, wd));
//        }
//    }
//
//    @Test
//    public void setWorkdirBare() {
//        Path path = TestRepo.SIMPLE1_BARE.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            Assert.assertTrue(repository.isBare());
//            repository.setWorkdir(path, true);
//            Assert.assertFalse(repository.isBare());
//        }
//    }
//
//    @Test
//    public void isWorkTree() {
//        Path path = TestRepo.WORKTREE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path.resolve("wt1"))) {
//            Assert.assertTrue(repository.isWorktree());
//        }
//    }
//
//    @Test
//    public void config() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            try (Config cfg = repository.config()) {
//                Assert.assertEquals(
//                        "vim", cfg.getStringBuf("core.editor").flatMap(Buf::getString).orElse(""));
//            }
//        }
//    }
//
//    @Test
//    public void configSnapshot() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            try (Config cfg = repository.configSnapshot()) {
//                Assert.assertEquals(
//                        "vim", cfg.getStringBuf("core.editor").flatMap(Buf::getString).orElse(""));
//            }
//        }
//    }
//
//    @Test
//    public void odb() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            try (Odb odb = repository.odb()) {
//                Assert.assertNotNull(odb);
//            }
//        }
//    }
//
//    @Test
//    public void refdb() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            try (Refdb refdb = repository.refdb()) {
//                Assert.assertNotNull(refdb);
//            }
//        }
//    }
//
//    @Test
//    public void message() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            Optional<String> maybeMsg = repository.message();
//            Assert.assertEquals("prepared commit message file", maybeMsg.orElse("").trim());
//            repository.messageRemove();
//            try {
//                repository.message();
//                Assert.fail("should have throw error, because original valuek");
//            } catch (GitException e) {
//                // ignore
//            }
//        }
//    }
//
//    @Test
//    public void stateCleanup() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            Optional<String> maybeMsg = repository.message();
//            Assert.assertEquals("prepared commit message file", maybeMsg.orElse("").trim());
//            repository.stateCleanup();
//            try {
//                repository.message();
//                Assert.fail("should have throw error, because original valuek");
//            } catch (GitException e) {
//                // ignore
//            }
//        }
//    }
//
//    @Test
//    public void fetchheadForeach() {
//        AtomicLong callCnt = new AtomicLong();
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            repository.fetchheadForeach(
//                    new Repository.FetchHeadForeachCb() {
//                        @Override
//                        public int call(String remoteUrl, Oid oid, boolean isMerge) {
//                            Assert.assertEquals("src/test/resources/simple1", remoteUrl);
//                            Assert.assertEquals(
//                                    "476f0c95825ef4479cab580b71f8b85f9dea4ee4", oid.toString());
//                            Assert.assertTrue(isMerge);
//                            callCnt.incrementAndGet();
//                            return 0;
//                        }
//                    });
//        }
//        Assert.assertEquals(1, callCnt.get());
//    }
//
//    @Test
//    public void mergeheadForeach() {
//        Path path = TestRepo.MERGE1.tempCopy(folder);
//        AtomicInteger callCnt = new AtomicInteger();
//        try (Repository repository = Repository.open(path)) {
//            repository.mergeHeadForeach(
//                    new Repository.MergeheadForeachCb() {
//                        @Override
//                        public int call(Oid oid) {
//                            Assert.assertEquals(
//                                    "476f0c95825ef4479cab580b71f8b85f9dea4ee4", oid.toString());
//                            callCnt.incrementAndGet();
//                            return 0;
//                        }
//                    });
//        }
//        Assert.assertEquals(1, callCnt.get());
//    }
//
//    @Test
//    public void hashFile() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            Oid oid = repository.hashfile(path.resolve("a"), GitObject.Type.BLOB, "");
//            // same value as `git rev-parse HEAD:a`
//            Assert.assertEquals("78981922613b2afb6025042ff6bd878ac1994e85", oid.toString());
//        }
//    }
//
//    @Test
//    public void setHead() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            repository.setHead("refs/heads/feature/dev");
//            Assert.assertFalse(repository.headDetached());
//        }
//    }
//
//    @Test
//    public void setHeadDetached() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            repository.setHeadDetached(Oid.of("565ddbe0bd55687b43286889a8ead64f68301113"));
//            Assert.assertTrue(repository.headDetached());
//        }
//    }
//
//    @Test
//    public void detachHead() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            repository.detachHead();
//            Assert.assertTrue(repository.headDetached());
//        }
//    }
//
//    @Test
//    public void namespace() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            repository.setNamespace("test-ns");
//            Assert.assertEquals("test-ns", repository.getNamespace());
//            Assert.assertFalse(repository.isShadow());
//        }
//    }
//
//    @Test
//    public void identity() {
//        Path path = TestRepo.SIMPLE1.tempCopy(folder);
//        try (Repository repository = Repository.open(path)) {
//            repository.setIdent("test_name", "test@example.com");
//            Repository.Identity id = repository.ident();
//            Assert.assertEquals("test@example.com", id.getEmail());
//            Assert.assertEquals("test_name", id.getName());
//        }
//    }
}
