package com.github.git24j.core;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class Signature {
    private String name;
    private String email;
    private OffsetDateTime when;

    public Signature(String name, String email, long whenEpocSec, int offsetMin) {
        this.name = name;
        this.email = email;
        this.when =
                Instant.ofEpochSecond(whenEpocSec)
                        .atOffset(ZoneOffset.ofHoursMinutes(0, offsetMin));
    }

    Signature() { }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public OffsetDateTime getWhen() {
        return when;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // TODO: figure out how to deal with sign
    public void setWhen(long whenEpocSec, int offsetMinutes, char sign) {
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(offsetMinutes * 60);
        this.when = Instant.ofEpochSecond(whenEpocSec).atOffset(offset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signature signature = (Signature) o;
        return Objects.equals(name, signature.name)
                && Objects.equals(email, signature.email)
                && Objects.equals(when, signature.when);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, when);
    }
}
