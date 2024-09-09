package nps.id.publicapi.java.client.connection.subscriptions.requests;

import com.nordpool.id.publicapi.v1.*;
import com.nordpool.id.publicapi.v1.statistic.PublicStatisticRow;
import com.nordpool.id.publicapi.v1.throttlinglimit.ThrottlingLimitsMessage;
import nps.id.publicapi.java.client.connection.enums.PublishingMode;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Getter
public class SubscriptionRequest {
    private final String subscriptionId;
    private final String destination;
    private final String type;
    private final boolean isGzipped;
    private final Type dataType;

    public SubscriptionRequest(String subscriptionId, String type, String destination) {
        this(subscriptionId, type, destination, false);
    }

    public SubscriptionRequest(String subscriptionId, String type, String destination, Type dataType) {
        this(subscriptionId, type, destination, false, dataType);
    }

    public SubscriptionRequest(String subscriptionId, String type, String destination, boolean isGzipped) {
        this(subscriptionId, type, destination, isGzipped, null);
    }

    public SubscriptionRequest(String subscriptionId, String type, String destination, boolean isGzipped, Type dataType) {
        this.subscriptionId = subscriptionId;
        this.type = type;
        this.destination = destination;
        this.isGzipped = isGzipped;
        this.dataType = dataType;
    }

    public static SubscriptionRequest deliveryAreas(String subscriptionId, String user, String version) {
        return new SubscriptionRequest(subscriptionId, "delivery_areas", composeDestination(user, version, PublishingMode.STREAMING, "deliveryAreas"), DeliveryAreaRow.class);
    }

    public static SubscriptionRequest configuration(String subscriptionId, String user, String version) {
        return new SubscriptionRequest(subscriptionId, "configuration", composeDestination(user, version, "configuration"), false, ConfigurationRow.class);
    }

    public static SubscriptionRequest orderExecutionReports(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "order_execution_report", composeDestination(user, version, publishingMode, "orderExecutionReport"), OrderExecutionReport.class);
    }

    public static SubscriptionRequest contracts(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "contracts", composeDestination(user, version, publishingMode, "contracts"), ContractRow.class);
    }

    public static SubscriptionRequest localView(String subscriptionId, String user, String version, PublishingMode publishingMode, int deliveryAreaId) {
        return new SubscriptionRequest(subscriptionId, "localview", composeDestination(user, version, publishingMode, "localview/" + deliveryAreaId), false, LocalViewRow.class);
    }

    public static SubscriptionRequest privateTrades(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "privateTrade", composeDestination(user, version, publishingMode, "privateTrade"), PrivateTradeRow.class);
    }

    public static SubscriptionRequest ticker(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "ticker", composeDestination(user, version, publishingMode, "ticker"), PublicTradeRow.class);
    }

    public static SubscriptionRequest myTicker(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "my_ticker", composeDestination(user, version, publishingMode, "myTicker"), PublicTradeRow.class);
    }

    public static SubscriptionRequest publicStatistics(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "publicStatistics", composeDestination(user, version, publishingMode, "publicStatistics"), PublicStatisticRow.class);
    }

    public static SubscriptionRequest throttlingLimits(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "throttlingLimits", composeDestination(user, version, publishingMode, "throttlingLimits"), ThrottlingLimitsMessage.class);
    }

    public static SubscriptionRequest capacities(String subscriptionId, String user, String version, PublishingMode publishingMode, int deliveryAreaId, ArrayList<Integer> additionalDeliveryAreas) {
        var areas = additionalDeliveryAreas.stream().map(String::valueOf).toList();
        var additionalAreasPart = additionalDeliveryAreas.isEmpty()
                ? ""
                : "/" + String.join("-", areas);
        return new SubscriptionRequest(subscriptionId, "capacities", composeDestination(user, version, publishingMode, "capacities" + "/" + deliveryAreaId + additionalAreasPart), CapacityRow.class);
    }

    public static SubscriptionRequest marketInfo(String subscriptionId, String user, String version, PublishingMode publishingMode, int deliveryAreaId) {
        return new SubscriptionRequest(subscriptionId, "market_info", composeDestination(user, version, publishingMode, "marketinfo/" + deliveryAreaId));
    }

    public static SubscriptionRequest heartbeat(String subscriptionId, String user, String version) {
        return new SubscriptionRequest(subscriptionId, "heartbeat", composeDestination(user, version, "heartbeatping"));
    }

    private static String composeDestination(String user, String version, PublishingMode publishingMode, String topic) {
        return composeDestination(user, version, publishingMode.toString().toLowerCase() + "/" + topic);
    }

    private static String composeDestination(String user, String version, String topic) {
        return "/user/" + user + "/" + version + "/" + topic;
    }
}
