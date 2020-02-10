package com.github.git24j.core;

import java.util.ArrayList;
import java.util.List;

class Internals {
    @FunctionalInterface
    interface JFCallback {
        int accept(long ptr, float f);
    }

    @FunctionalInterface
    interface JJCallback {
        int accept(long ptr1, long ptr2);
    }

    @FunctionalInterface
    interface JJJCallback {
        int accept(long ptr1, long ptr2, long ptr3);
    }

    /** Class to hold */
    static class OidArray {
        private final List<Oid> _oids = new ArrayList<>();

        void add(byte[] oidRaw) {
            _oids.add(Oid.of(oidRaw));
        }

        public List<Oid> getOids() {
            return _oids;
        }
    }
}
