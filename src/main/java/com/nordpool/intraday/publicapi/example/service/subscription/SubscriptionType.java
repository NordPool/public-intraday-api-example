package com.nordpool.intraday.publicapi.example.service.subscription;

import org.apache.commons.lang.StringUtils;

public enum SubscriptionType {
    STREAMING("/streaming"),
    CONFLATED("/conflated"),
    EMPTY(StringUtils.EMPTY);

    private String type;

    SubscriptionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
