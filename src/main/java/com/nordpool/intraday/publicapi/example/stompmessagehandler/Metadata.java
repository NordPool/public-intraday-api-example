/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example.stompmessagehandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Metadata {
    private String name;
    private Map<String, List<String>> values;

    private Metadata(Builder builder) {
        name = builder.name;
        values = builder.values;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Map<String, List<String>> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Metadata)) return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(name, metadata.name) &&
                Objects.equals(values, metadata.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, values);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }

    public static final class Builder {
        private String name;
        private Map<String, List<String>> values;

        private Builder() {
        }

        public Builder withName(String val) {
            name = val;
            return this;
        }

        public Builder withValues(Map<String, List<String>> val) {
            values = val;
            return this;
        }

        public Metadata build() {
            return new Metadata(this);
        }
    }
}
