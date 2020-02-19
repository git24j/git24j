package com.github.git24j.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.CheckForNull;

public interface IBitEnum {
    static <T extends Enum<T> & IBitEnum> int bitOrAll(EnumSet<T> enums) {
        if (enums == null) {
            return 0;
        }
        int res = 0;
        for (T x : enums) {
            res |= x.getBit();
        }
        return res;
    }

    static <T extends Enum<T> & IBitEnum> EnumSet<T> parse(int flags, Class<T> clz) {
        List<T> matched = new ArrayList<>();
        for (T x : clz.getEnumConstants()) {
            int b = x.getBit();
            if ((b < 0 && flags > 0) || (b > 0 && flags < 0)) {
                continue;
            }
            if ((b & flags) != 0) {
                matched.add(x);
            }
        }
        return EnumSet.copyOf(matched);
    }

    static <T extends Enum<T> & IBitEnum> T valueOf(int bit, Class<T> clz, T defaultVal) {
        for (T x : clz.getEnumConstants()) {
            int b = x.getBit();
            if ((b < 0 && bit > 0) || (b > 0 && bit < 0)) {
                continue;
            }
            if ((b & bit) != 0) {
                return x;
            }
        }
        return defaultVal;
    }

    /**
     * Get enum item from int value
     *
     * @param bit bit value
     * @param clz class to parse
     * @param <T>
     * @return enum item
     */
    @CheckForNull
    static <T extends Enum<T> & IBitEnum> T valueOf(int bit, Class<T> clz) {
        return IBitEnum.valueOf(bit, clz, null);
    }

    int getBit();
}
