package com.github.git24j.core;

public enum BranchType implements IBitEnum {
    INVALID(0),
    LOCAL(1),
    REMOTE(2),
    ALL(3);

    private final int _value;

    BranchType(int value) {
        _value = value;
    }

    static BranchType valueOf(int iVal) {
        for (BranchType x : BranchType.values()) {
            if (x._value == iVal) {
                return x;
            }
        }
        return INVALID;
    }

    @Override
    public int getBit() {
        return _value;
    }
}
