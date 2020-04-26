package com.github.git24j.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class RevwalkTest extends TestBase {

    @Rule public TemporaryFolder _folder = new TemporaryFolder();

    @Test
    public void hide() {
        try(Repository repo = TestRepo.SIMPLE1.tempRepo(_folder)){
            Revwalk revwalk = Revwalk.create(repo);

        }
    }

    @Test
    public void hideGlob() {}

    @Test
    public void hideHead() {}

    @Test
    public void testHideHead() {}

    @Test
    public void hideRef() {}

    @Test
    public void testHideRef() {}

    @Test
    public void create() {}

    @Test
    public void next() {}

    @Test
    public void push() {}

    @Test
    public void pushGlob() {}

    @Test
    public void pushHead() {}

    @Test
    public void pushRange() {}

    @Test
    public void pushRef() {}

    @Test
    public void repository() {}

    @Test
    public void reset() {}

    @Test
    public void simplifyFirstParent() {}

    @Test
    public void sorting() {}
}