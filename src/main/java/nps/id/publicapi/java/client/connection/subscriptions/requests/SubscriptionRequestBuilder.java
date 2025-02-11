package nps.id.publicapi.java.client.connection.subscriptions.requests;

import nps.id.publicapi.java.client.connection.enums.PublishingMode;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SubscriptionRequestBuilder {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

    private final String user;
    private final String version;

    public SubscriptionRequestBuilder(String user, String version) {
        this.user = user;
        this.version = version;
    }

    public static SubscriptionRequestBuilder createBuilder(String user, String version) {
        return new SubscriptionRequestBuilder(user, version);
    }

    public SubscriptionRequest createDeliveryAreas() {
        return SubscriptionRequest.deliveryAreas(getSubId(), user, version);
    }

    public SubscriptionRequest createConfiguration() {
        return SubscriptionRequest.configuration(getSubId(), user, version);
    }

    public SubscriptionRequest createOrderExecutionReport(PublishingMode publishingMode) {
        return SubscriptionRequest.orderExecutionReports(getSubId(), user, version, publishingMode);
    }

    public SubscriptionRequest createContracts(PublishingMode publishingMode) {
        return SubscriptionRequest.contracts(getSubId(), user, version, publishingMode);
    }

    public SubscriptionRequest createLocalView(PublishingMode publishingMode, int deliveryAreaId) {
        return SubscriptionRequest.localView(getSubId(), user, version, publishingMode, deliveryAreaId);
    }

    public SubscriptionRequest createPrivateTrades(PublishingMode publishingMode) {
        return  SubscriptionRequest.privateTrades(getSubId(), user, version, publishingMode);
    }

    public SubscriptionRequest createTicker(PublishingMode publishingMode) {
        return SubscriptionRequest.ticker(getSubId(), user, version, publishingMode);
    }

    public SubscriptionRequest createMyTicker(PublishingMode publishingMode) {
        return SubscriptionRequest.myTicker(getSubId(), user, version, publishingMode);
    }

    public SubscriptionRequest createPublicStatistics(PublishingMode publishingMode, int deliveryAreaId) {
        return  SubscriptionRequest.publicStatistics(getSubId(), user, version, publishingMode, deliveryAreaId);
    }

    public SubscriptionRequest createThrottlingLimits(PublishingMode publishingMode) {
        return  SubscriptionRequest.throttlingLimits(getSubId(), user, version, publishingMode);
    }

    public SubscriptionRequest createCapacities(PublishingMode publishingMode, int deliveryAreaId) {
        return createCapacities(publishingMode, deliveryAreaId, Collections.emptyList());
    }

    public SubscriptionRequest createCapacities(PublishingMode publishingMode, int deliveryAreaId, List<Integer> additionalDeliveryAreas) {
        return SubscriptionRequest.capacities(getSubId(), user, version, publishingMode, deliveryAreaId, additionalDeliveryAreas);
    }

    public SubscriptionRequest createAtcCapacities(PublishingMode publishingMode, int deliveryAreaId, List<Integer> additionalDeliveryAreas) {
        return SubscriptionRequest.atcCapacities(getSubId(), user, version, publishingMode, deliveryAreaId, additionalDeliveryAreas);
    }

    private String getSubId() {
        return "sub-" + ATOMIC_INTEGER.incrementAndGet();
    }
}
