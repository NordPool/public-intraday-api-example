package nps.id.publicapi.java.client.connection.subscriptions.requests;

import com.nordpool.id.publicapi.v1.*;
import com.nordpool.id.publicapi.v1.statistic.PublicStatisticRow;
import com.nordpool.id.publicapi.v1.throttlinglimit.ThrottlingLimitsMessage;
import nps.id.publicapi.java.client.connection.enums.PublishingMode;
import lombok.Getter;
import nps.id.publicapi.java.client.connection.subscriptions.helpers.DestinationHelper;

import java.lang.reflect.Type;
import java.util.List;

@Getter
public class SubscriptionRequest {
    private final String subscriptionId;
    private final String destination;
    private final String type;
    private final Type dataType;

    public SubscriptionRequest(String subscriptionId, String type, String destination, Type dataType) {
        this.subscriptionId = subscriptionId;
        this.type = type;
        this.destination = destination;
        this.dataType = dataType;
    }

    public static SubscriptionRequest deliveryAreas(String subscriptionId, String user, String version) {
        return new SubscriptionRequest(subscriptionId, "delivery_areas", DestinationHelper.composeDestination(user, version, PublishingMode.STREAMING, "deliveryAreas"), DeliveryAreaRow.class);
    }

    public static SubscriptionRequest configuration(String subscriptionId, String user, String version) {
        return new SubscriptionRequest(subscriptionId, "configuration", DestinationHelper.composeDestination(user, version, "configuration"), ConfigurationRow.class);
    }

    public static SubscriptionRequest orderExecutionReports(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "order_execution_report", DestinationHelper.composeDestination(user, version, publishingMode, "orderExecutionReport"), OrderExecutionReport.class);
    }

    public static SubscriptionRequest contracts(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "contracts", DestinationHelper.composeDestination(user, version, publishingMode, "contracts"), ContractRow.class);
    }

    public static SubscriptionRequest localView(String subscriptionId, String user, String version, PublishingMode publishingMode, int deliveryAreaId) {
        return new SubscriptionRequest(subscriptionId, "localview", DestinationHelper.composeDestination(user, version, publishingMode, "localview/" + deliveryAreaId), LocalViewRow.class);
    }

    public static SubscriptionRequest privateTrades(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "privateTrade", DestinationHelper.composeDestination(user, version, publishingMode, "privateTrade"), PrivateTradeRow.class);
    }

    public static SubscriptionRequest ticker(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "ticker", DestinationHelper.composeDestination(user, version, publishingMode, "ticker"), PublicTradeRow.class);
    }

    public static SubscriptionRequest myTicker(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "my_ticker", DestinationHelper.composeDestination(user, version, publishingMode, "myTicker"), PublicTradeRow.class);
    }

    public static SubscriptionRequest publicStatistics(String subscriptionId, String user, String version, PublishingMode publishingMode, int deliveryAreaId) {
        return new SubscriptionRequest(subscriptionId, "publicStatistics", DestinationHelper.composeDestination(user, version, publishingMode, "publicStatistics/" + deliveryAreaId), PublicStatisticRow.class);
    }

    public static SubscriptionRequest throttlingLimits(String subscriptionId, String user, String version, PublishingMode publishingMode) {
        return new SubscriptionRequest(subscriptionId, "throttlingLimits", DestinationHelper.composeDestination(user, version, publishingMode, "throttlingLimits"), ThrottlingLimitsMessage.class);
    }

    public static SubscriptionRequest capacities(String subscriptionId, String user, String version, PublishingMode publishingMode, int deliveryAreaId, List<Integer> additionalDeliveryAreas) {
        var areas = additionalDeliveryAreas.stream().map(String::valueOf).toList();
        var additionalAreasPart = additionalDeliveryAreas.isEmpty()
                ? ""
                : "/" + String.join("-", areas);
        return new SubscriptionRequest(subscriptionId, "capacities", DestinationHelper.composeDestination(user, version, publishingMode, "capacities" + "/" + deliveryAreaId + additionalAreasPart), CapacityRow.class);
    }
}
