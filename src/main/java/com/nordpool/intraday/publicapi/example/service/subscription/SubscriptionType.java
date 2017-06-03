/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example.service.subscription;


import com.nordpool.id.publicapi.v1.*;

public enum SubscriptionType {

    // /user/<username>/<version>/<streaming>/localview/<areaId>
    LOCALVIEW(LocalViewRow.class, "/localview/"),

    // /user/<username>/<version>/capacities/<areaId>
    CAPACITIES(CapacityRow.class, "/capacities/"),

    // /user/<username>/<version>/<streaming>/deliveryAreas
    DELIVERY_AREAS(AreaRow.class, "/deliveryAreas"),

    // /user/<username>/<version>/configuration
    CONFIGURATION(ConfigurationRow.class, "/configuration"),

    // /user/<username>/<version>/<streaming>/contracts
    CONTRACTS(ContractRow.class, "/contracts"),

    // /user/<username>/<version>/<streaming>/orderexecutionreport
    ORDER_EXECUTION_REPORT(OrderExecutionReport.class, "/orderExecutionReport"),

    // /user/<username>/<version>/<streaming>/privateTrade
    PRIVATE_TRADE(PrivateTradeRow.class, "/privateTrade"),

    // /user/<username>/<version>/<streaming>/ticker
    TICKER(PublicTradeRow.class, "/ticker");

    SubscriptionType(Class<?> destinationRow, String topic) {
        this.destinationRow = destinationRow;
        this.topic = topic;
    }

    private Class<?> destinationRow;
    private String topic;

    public Class<?> getDestinationRow() {
        return destinationRow;
    }

    public String getTopic() {
        return topic;
    }
}
