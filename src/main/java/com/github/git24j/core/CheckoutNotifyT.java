package com.github.git24j.core;

public enum CheckoutNotifyT implements IBitEnum {
    NONE(0),
    CONFLICT(1 << 0),
    DIRTY(1 << 1),
    UPDATED(1 << 2),
    UNTRACKED(1 << 3),
    IGNORED(1 << 4),
    ALL(0x0FFFF);
    private final int _bit;

    CheckoutNotifyT(int bit) {
        _bit = bit;
    }

    @Override
    public int getBit() {
        return _bit;
    }
}
