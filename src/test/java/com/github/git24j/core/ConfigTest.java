package com.github.git24j.core;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConfigTest extends TestBase {
    private static final String MASTER_HASH = "476f0c95825ef4479cab580b71f8b85f9dea4ee4";
    private static final String MASTER_PARENT_HASH = "565ddbe0bd55687b43286889a8ead64f68301113";
    private static final String MASTER_TREE_HASH = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    private static final String MERGE_COMMIT = "7dcb276ed40ce9223fd522f5166c25572d10d428";
    private static final Oid MASTER_OID = Oid.of(MASTER_HASH);
    private static final List<String> CONFIG_LINES =
            Arrays.asList(
                    "# a comment",
                    "; also a comment",
                    "[http]",
                    "\tsslVerify = false",
                    "\tcookieFile = /tmp/cookie.txt",
                    "[core]",
                    "\tgitproxy=proxy-command for kernel.org",
                    "\tgitproxy=default-proxy ; for all the rest",
                    "[sendemail]",
                    "\tsmtpserverport = 587",
                    "\n");
    private static final String TEST_CFG_COOKIE_FILE = "/tmp/cookie.txt";

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    private File _testCfgFile;

    private void verifyConfigPath(Path p) {
        assertTrue(Files.exists(p));
    }

    @Before
    public void setUp() throws IOException {
        _testCfgFile = folder.newFile("ext.config");
        FileUtils.writeLines(_testCfgFile, CONFIG_LINES);
    }

    @Test
    public void find() {
        Config.findGlobal().ifPresent(this::verifyConfigPath);
        Config.findXdg().ifPresent(this::verifyConfigPath);
        Config.findSystem().ifPresent(this::verifyConfigPath);
        Config.findProgramdata().ifPresent(this::verifyConfigPath);
    }

    @Test
    public void foreachMatch() {
        Map<String, String> entries = new HashMap<>();
        Map<String, Integer> depLevels = new HashMap<>();
        Config cfg = Config.openDefault();
        cfg.foreachMatch(
                ".*",
                new Config.ForeachCb() {
                    @Override
                    public int accept(Config.Entry entry) {
                        entries.put(entry.getName(), entry.getValue());
                        depLevels.put(entry.getName(), entry.getIncludeDepth() + entry.getLevel());
                        return 0;
                    }
                });

        System.out.println(entries);
        System.out.println(depLevels);
    }

    @Test
    public void openDefault() {
        Config cfg = Config.openDefault();
        assertTrue(cfg.getRawPointer() > 0);
    }

    @Test
    public void newConfig() {
        Config cfg = Config.newConfig();
        assertTrue(cfg.getRawPointer() > 0);
    }

    @Test
    public void addFileOnDisk() {
        Config cfg = Config.newConfig();
        cfg.addFileOndisk(_testCfgFile.toPath(), Config.ConfigLevel.LOCAL, null, false);
        assertFalse(cfg.getBool("http.sslVerify").orElse(true));
    }

    @Test
    public void openOnDisk() {
        Config cfg = Config.openOndisk(_testCfgFile.toPath());
        assertFalse(cfg.getBool("http.sslVerify").orElse(true));
    }

    @Test
    public void openLocal() {
        Config parent = Config.openOndisk(_testCfgFile.toPath());
        Config cfg = Config.openLevel(Config.ConfigLevel.LOCAL, parent);
        assertNotNull(cfg);
        assertEquals(TEST_CFG_COOKIE_FILE, cfg.getStringBuf("http.cookieFile").orElse(null));
    }

    @Test
    public void openGlobal() {
        Config parent = Config.openOndisk(_testCfgFile.toPath());
        Config.openGlobal(parent);
    }

    @Test
    public void snapshot() {
        Config cfg = Config.openOndisk(_testCfgFile.toPath());
        Config snapshot = cfg.snapshot();
        assertEquals(TEST_CFG_COOKIE_FILE, snapshot.getString("http.cookieFile").get());
    }

    @Test
    public void getEntry() {
        Config cfg = Config.openOndisk(_testCfgFile.toPath());
        Config.Entry entry = cfg.getEntry("http.cookieFile").orElse(null);
        assertNotNull(entry);
        assertEquals(TEST_CFG_COOKIE_FILE, entry.getValue());
        assertEquals("http.cookieFile".toLowerCase(), entry.getName().toLowerCase());

        cfg.deleteEntry("http.cookieFile");
        Config.Entry entry2 = cfg.getEntry("http.cookieFile").orElse(null);
        Assert.assertNull(entry2);
    }

    @Test
    public void gettersAndSetters() {
        Config cfg = Config.openOndisk(_testCfgFile.toPath());
        assertEquals(587, cfg.getInt("sendemail.smtpserverport").orElse(0).intValue());
        // boolean
        cfg.setBool("foo.bar.bool", true);
        assertTrue(cfg.getBool("foo.bar.bool").get());
        // integer
        cfg.setInt("foo.bar.int", 123);
        assertEquals(123, cfg.getInt("foo.bar.int").orElse(0).intValue());
        // long
        cfg.setLong("foo.bar.long", 12345L);
        assertEquals(12345L, cfg.getLong("foo.bar.long").get().longValue());
        // string
        cfg.setString("foo.bar.string", "example-string");
        assertEquals("example-string", cfg.getStringBuf("foo.bar.string").get());
        // path
        assertEquals(Paths.get(TEST_CFG_COOKIE_FILE), cfg.getPath("http.cookieFile").get());
    }

    @Test
    public void getMultivarForeach() {
        Config cfg = Config.openOndisk(_testCfgFile.toPath());
        String key = "core.gitproxy";
        Set<String> values = new HashSet<>();
        cfg.getMultivarForeach(
                key,
                null,
                entry -> {
                    assertEquals(key, entry.getName());
                    values.add(entry.getValue());
                    return 0;
                });
        assertEquals(2, values.size());
    }

    @Test
    public void multivarIterator() {
        Config cfg = Config.openOndisk(_testCfgFile.toPath());
        String key = "core.gitproxy";
        Config.Iterator iterator = cfg.multivarIteratorNew(key, null);
        Set<String> values = new HashSet<>();
        for (Config.Entry entry = iterator.next(); entry != null; entry = iterator.next()) {
            assertEquals(key, entry.getName());
            values.add(entry.getValue());
        }
        assertEquals(2, values.size());
    }

    @Test
    public void multivar() {
        Config cfg = Config.openOndisk(_testCfgFile.toPath());
        String key = "core.gitproxy";
        cfg.setMultivar(key, "for kernel.org$", "ssh");
        List<String> values = cfg.getMultivar(key, "h$");
        assertEquals(values, Collections.singletonList("ssh"));

        cfg.deleteMultivar(key, ".*");
        assertTrue(cfg.getMultivar(key, ".*").isEmpty());
    }

    @Test
    public void iterator() {
        Config cfg = Config.openOndisk(_testCfgFile.toPath());
        Config.Iterator iter1 = cfg.iteratorNew();
        Map<String, List<String>> allValues = new HashMap<>();
        for (Config.Entry entry = iter1.next(); entry != null; entry = iter1.next()) {
            allValues
                    .computeIfAbsent(entry.getName(), k -> new ArrayList<>())
                    .add(entry.getValue());
        }
        assertEquals(2, allValues.get("core.gitproxy").size());
    }

    @Test
    public void foreach() {
        Config cfg = Config.openOndisk(_testCfgFile.toPath());
        Map<String, List<String>> allValues = new HashMap<>();
        cfg.foreach(
                entry -> {
                    allValues
                            .computeIfAbsent(entry.getName(), k -> new ArrayList<>())
                            .add(entry.getValue());
                    return 0;
                });
        assertEquals(2, allValues.get("core.gitproxy").size());
    }

    @Test
    public void parses() {
        assertFalse(Config.parseBool("off"));
        assertEquals(2048, Config.parseInt("2k"));
        assertEquals(1073741824L, Config.parseLong("1g"));
        Path p = Paths.get(System.getProperty("user.home"));
        assertEquals(p.resolve("tmp"), Config.parsePath("~/tmp"));
    }

    @Test
    public void lock() {
        Config cfg = Config.openOndisk(_testCfgFile.toPath());
        Transaction tx = cfg.lock();
        assertTrue(tx.getRawPointer() > 0);
    }
}
