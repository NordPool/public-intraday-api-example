/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example.service.subscription;

import com.nordpool.intraday.publicapi.example.stompmessagehandler.Metadata;

import java.util.List;
import java.util.Objects;

public class Subscription {
    private Topic topic;
    private String version;
    private SubscriptionType subscriptionType;
    private Integer area;
    private List<Metadata> metadataParameters;

    private Subscription(Builder builder) {
        topic = builder.topic;
        version = builder.version;
        subscriptionType = builder.subscriptionType;
        area = builder.area;
        metadataParameters = builder.metadataParameters;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Topic getTopic() {
        return topic;
    }

    public String getVersion() {
        return version;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public Integer getArea() {
        return area;
    }

    public List<Metadata> getMetadataParameters() {
        return metadataParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription)) return false;
        Subscription that = (Subscription) o;
        return topic == that.topic &&
                Objects.equals(version, that.version) &&
                Objects.equals(subscriptionType, that.subscriptionType) &&
                Objects.equals(area, that.area) &&
                Objects.equals(metadataParameters, that.metadataParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, version, subscriptionType, area, metadataParameters);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "topic=" + topic +
                ", version='" + version + '\'' +
                ", subscriptionType=" + subscriptionType +
                ", area=" + area +
                ", metadataParameters=" + metadataParameters +
                '}';
    }


    public static final class Builder {
        private Topic topic;
        private String version;
        private SubscriptionType subscriptionType;
        private Integer area;
        private List<Metadata> metadataParameters;

        private Builder() {
        }

        public Builder withTopic(Topic val) {
            topic = val;
            return this;
        }

        public Builder withVersion(String val) {
            version = val;
            return this;
        }

        public Builder withSubscriptionType(SubscriptionType val) {
            subscriptionType = val;
            return this;
        }

        public Builder withArea(Integer val) {
            area = val;
            return this;
        }

        public Builder withMetadataParameters(List<Metadata> val) {
            metadataParameters = val;
            return this;
        }

        public Subscription build() {
            return new Subscription(this);
        }
    }
}
