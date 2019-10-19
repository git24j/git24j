package com.github.git24j.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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
            if ((x.getBit() & flags) != 0) {
                matched.add(x);
            }
        }
        return EnumSet.copyOf(matched);
    }

    int getBit();
}
