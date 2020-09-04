package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Test;

public class GraphTest extends TestBase {

    /**
     *
     *
     * <pre>
     *  *   7dcb276
     * |\
     * | * e5b2842 - (x)
     * | * c15d456
     * * | f80e0b1 - (y)
     * * | 0f31627
     * * | 8ac4091
     * * | 003e1a7
     * |/
     * * 67a3675 - (z)
     * </pre>
     */
    @Test
    public void aheadBehind() {
        try (Repository testRepo = creteTestRepo(TestRepo.SIMPLE1)) {
            Oid x = Oid.of("e5b28427ba064002e0e343e783ea3095018ce72c");
            Oid y = Oid.of("f80e0b10f83e512d1fae0142d000cceba3aca721");
            Oid z = Oid.of("67a36754360b373d391af2182f9ad8929fed54d8");
            Graph.Count c = Graph.aheadBehind(testRepo, x, y);
            Assert.assertEquals(2, c.getAhead());
            Assert.assertEquals(4, c.getBehind());
            Assert.assertTrue(Graph.descendantOf(testRepo, x, z));
            Assert.assertTrue(Graph.descendantOf(testRepo, y, z));
        }
    }
}
