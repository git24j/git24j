package com.github.git24j.core;

import static com.github.git24j.core.IBitEnumTest.Example.EIGHT;
import static com.github.git24j.core.IBitEnumTest.Example.FOUR;
import static com.github.git24j.core.IBitEnumTest.Example.ONE;
import static com.github.git24j.core.IBitEnumTest.Example.TWO;
import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import org.junit.Assert;
import org.junit.Test;

public class IBitEnumTest {

    @Test
    public void testFlagsToEnumSet() {
        assertEquals(EnumSet.of(ONE), IBitEnum.parse(1, Example.class));
        assertEquals(EnumSet.of(ONE, TWO), IBitEnum.parse(3, Example.class));
        assertEquals(EnumSet.of(ONE, TWO, FOUR), IBitEnum.parse(7, Example.class));
        assertEquals(EnumSet.of(ONE, TWO, FOUR, EIGHT), IBitEnum.parse(15, Example.class));
        assertEquals(EnumSet.of(TWO, FOUR, EIGHT), IBitEnum.parse(14, Example.class));
        assertEquals(EnumSet.of(FOUR, EIGHT), IBitEnum.parse(12, Example.class));
        assertEquals(EnumSet.of(EIGHT), IBitEnum.parse(8, Example.class));
    }

    @Test
    public void testEnumSetToFlags() {
        assertEquals(1, IBitEnum.bitOrAll(EnumSet.of(ONE)));
        assertEquals(3, IBitEnum.bitOrAll(EnumSet.of(ONE, TWO)));
        assertEquals(7, IBitEnum.bitOrAll(EnumSet.of(ONE, TWO, FOUR)));
        assertEquals(15, IBitEnum.bitOrAll(EnumSet.of(ONE, TWO, FOUR, EIGHT)));
        assertEquals(14, IBitEnum.bitOrAll(EnumSet.of(TWO, FOUR, EIGHT)));
        assertEquals(12, IBitEnum.bitOrAll(EnumSet.of(FOUR, EIGHT)));
        assertEquals(8, IBitEnum.bitOrAll(EnumSet.of(EIGHT)));
    }

    enum Example implements IBitEnum {
        ONE(1 << 0),
        TWO(1 << 1),
        FOUR(1 << 2),
        EIGHT(1 << 3);
        final int _bit;

        Example(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    @Test
    public void valueOf() {
        Assert.assertEquals(FOUR, IBitEnum.valueOf(1 << 2, Example.class));
    }
}
