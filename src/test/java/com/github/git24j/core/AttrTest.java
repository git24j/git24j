package com.github.git24j.core;

import org.junit.Test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static com.github.git24j.core.Attr.CheckFlags.CHECK_FILE_THEN_INDEX;
import static com.github.git24j.core.Attr.CheckFlags.CHECK_INCLUDE_HEAD;
import static com.github.git24j.core.Attr.CheckFlags.CHECK_INDEX_THEN_FILE;

public class AttrTest extends TestBase {

    @Test
    public void addMacro() {
        try (Repository testRepo = TestRepo.SIMPLE1.tempRepo(folder)) {
            Attr.addMacro(testRepo, "binary", "-diff -crlf");
            Attr.cacheFlush(testRepo);
            Map<String, String> attributes = new HashMap<>();
            Attr.foreach(
                    testRepo,
                    EnumSet.of(CHECK_INDEX_THEN_FILE),
                    ".",
                    new Attr.ForeachCb() {
                        @Override
                        public int accept(String name, String value) {
                            attributes.put(name, value);
                            return 0;
                        }
                    });
            Attr.value("binary");
            Attr.getAttr(testRepo, EnumSet.of(CHECK_INCLUDE_HEAD), "a", "binary");
            Attr.getMany(testRepo, EnumSet.of(CHECK_FILE_THEN_INDEX), "a", Arrays.asList("a", "b"));
        }
    }
}
