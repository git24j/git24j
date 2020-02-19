package com.github.git24j.core;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class MergeTest extends TestBase {
    // git rev-parse HEAD^{tree}
    private static final String MASTER_TREE = "8c5f4d727b339fe7d9ee4d1806aa9ca3a5cc5b3e";
    // git rev-parse "feature/dev^{tree}"
    private static final String DEV_TREE = "3b597d284bc12d61638124054b19889587127208";

    @Rule public TemporaryFolder folder = new TemporaryFolder();
}
