package com.github.git24j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Simple Wrapper around {@code ArrayList} to provide jni interactions */
class StrArray {
    private List<String> _array = new ArrayList<>();

    void add(String val) {
        _array.add(val);
    }

    List<String> getArray() {
        return _array;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StrArray strArray = (StrArray) o;
        return _array.equals(strArray._array);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_array);
    }
}
