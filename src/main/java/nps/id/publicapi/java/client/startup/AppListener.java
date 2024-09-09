package nps.id.publicapi.java.client.startup;

import com.nordpool.id.publicapi.v1.ConfigurationRow;
import com.nordpool.id.publicapi.v1.ContractRow;
import com.nordpool.id.publicapi.v1.order.*;
import com.nordpool.id.publicapi.v1.order.request.OrderEntryRequest;
import com.nordpool.id.publicapi.v1.order.request.OrderModificationRequest;
import nps.id.publicapi.java.client.connection.clients.StompClient;
import nps.id.publicapi.java.client.connection.clients.StompClientGenericFactory;
import nps.id.publicapi.java.client.connection.enums.PublishingMode;
import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.storage.SimpleCacheStorage;
import nps.id.publicapi.java.client.connection.subscriptions.exceptions.SubscriptionFailedException;
import nps.id.publicapi.java.client.connection.subscriptions.requests.SubscriptionRequestBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import nps.id.publicapi.java.client.security.options.CredentialsOptions;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
public class AppListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LogManager.getLogger(AppListener.class);

    private final int _demoArea = 2;
    private final String _clientId = UUID.randomUUID() + "-java-demo-client";

    private final StompClientGenericFactory stompClientGenericFactory;

    private final SubscriptionRequestBuilder subscribeRequestBuilder;

    public AppListener(CredentialsOptions credentialsOptions, StompClientGenericFactory stompClientGenericFactory) {
        this.stompClientGenericFactory = stompClientGenericFactory;
        this.subscribeRequestBuilder = SubscriptionRequestBuilder.createBuilder(credentialsOptions.getUserName(), "v1");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            var pmdClient = CreateClient(WebSocketClientTarget.PMD);
            var middlewareClient = CreateClient(WebSocketClientTarget.MIDDLEWARE);

//            // Delivery areas
            SubscribeDeliveryAreas(pmdClient);

            // Configurations
            SubscribeConfigurations(middlewareClient);

            // Order execution report
            SubscribeOrderExecutionReports(middlewareClient, PublishingMode.STREAMING);

            // Contracts
            SubscribeContracts(pmdClient, PublishingMode.CONFLATED);
            SubscribeContracts(middlewareClient, PublishingMode.CONFLATED);

            // Local views
            SubscribeLocalViews(pmdClient, PublishingMode.STREAMING);
            SubscribeLocalViews(middlewareClient, PublishingMode.STREAMING);

            // Private trades
            SubscribePrivateTrades(middlewareClient, PublishingMode.STREAMING);

            // Tickers
            SubscribeTickers(pmdClient, PublishingMode.STREAMING);
            SubscribeTickers(middlewareClient, PublishingMode.STREAMING);

            // MyTickers
            SubscribeMyTickers(pmdClient, PublishingMode.STREAMING);
            SubscribeMyTickers(middlewareClient, PublishingMode.STREAMING);

            // Public statistics
            SubscribePublicStatistics(pmdClient, PublishingMode.CONFLATED);
            SubscribePublicStatistics(middlewareClient, PublishingMode.CONFLATED);

            // Throttling limits
            SubscribeThrottlingLimits(middlewareClient, PublishingMode.CONFLATED);

            // Capacities
            SubscribeCapacities(pmdClient, PublishingMode.STREAMING);
            SubscribeCapacities(middlewareClient, PublishingMode.STREAMING);

            // Order
            // We wait some time in hope to get some example contracts and configurations that are needed for preparing example order request
            Thread.sleep(10000);

            SendOrderRequest(middlewareClient);
            SendOrderModificationRequest(middlewareClient);

            SendInvalidOrderRequest(middlewareClient);
            SendInvalidOrderModificationRequest(middlewareClient);

            System.out.println("============================================================ ");
            System.out.println("Press 'x' key to unsubscribe, logout and close. . . ");
            System.out.println("============================================================ ");

            var key = System.in.read();
            if (key == 120) {
                pmdClient.disconnect();
                middlewareClient.disconnect();
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StompClient CreateClient(WebSocketClientTarget clientTarget) {
        return stompClientGenericFactory.create(_clientId, clientTarget);
    }

    private void SubscribeDeliveryAreas(StompClient stompClient) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createDeliveryAreas();
        stompClient.subscribe(subscription);
    }

    private void SubscribeConfigurations(StompClient stompClient) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createConfiguration();
        stompClient.subscribe(subscription);
    }

    private void SubscribeOrderExecutionReports(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createOrderExecutionReport(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void SubscribeContracts(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createContracts(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void SubscribeLocalViews(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createLocalView(publishingMode, _demoArea);
        stompClient.subscribe(subscription);
    }

    private void SubscribePrivateTrades(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createPrivateTrades(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void SubscribeTickers(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createTicker(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void SubscribeMyTickers(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createMyTicker(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void SubscribePublicStatistics(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createPublicStatistics(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void SubscribeThrottlingLimits(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createThrottlingLimits(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void SubscribeCapacities(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createCapacities(publishingMode, _demoArea);
        stompClient.subscribe(subscription);
    }

    private void SendOrderRequest(StompClient stompClient) {
        var exampleContract = SimpleCacheStorage.getInstance()
                .getFromCache(stompClient.getClientTarget(), ContractRow.class.getName())
                .stream().findFirst();
        if (exampleContract.isEmpty()) {
            logger.warn("[{}]No valid contract to be used for order creation has been found! Check contracts available in SimpleCacheStorage.", stompClient.getClientTarget());
            return;
        }

        var examplePortfolio = SimpleCacheStorage.getInstance()
                .getFromCache(stompClient.getClientTarget(), ConfigurationRow.class.getName())
                .stream().findFirst();
        if (examplePortfolio.isEmpty()) {
            logger.warn("[{}]No valid portfolio to be used for order creation has been found! Check contracts available in SimpleCacheStorage.", stompClient.getClientTarget());
            return;
        }
        var contract = (ContractRow)exampleContract.get();
        var portfolio = ((ConfigurationRow)examplePortfolio.get()).getPortfolios().getFirst();
        var orderRequest = new OrderEntryRequest()
                .withRequestId(UUID.randomUUID().toString())
                .withRejectPartially(false)
                .withOrders(Collections.singletonList(
                        new OrderEntry()
                                .withText("New order")
                                .withClientOrderId(UUID.randomUUID().toString())
                                .withPortfolioId(portfolio.getId())
                                .withSide(OrderSide.SELL)
                                .withContractIds(Collections.singletonList(contract.getContractId()))
                                .withOrderType(OrderType.LIMIT)
                                .withQuantity(3000L)
                                .withState(OrderState.ACTI)
                                .withUnitPrice(2500L)
                                .withTimeInForce(TimeInForce.GFS)
                                .withDeliveryAreaId((long) portfolio.getAreas().getFirst().getAreaId())
                                .withExecutionRestriction(ExecutionRestriction.NON)
                                .withExpireTime(ZonedDateTime.now().plusHours(6))
                ));

        // Store created order in simple cache storage for order modification request
        SimpleCacheStorage.getInstance()
                        .setCache(stompClient.getClientTarget(), OrderEntryRequest.class.getName(), Collections.singletonList(orderRequest), false);

        logger.info("[{}]Attempting to send correct order request.", stompClient.getClientTarget());
        stompClient.send(orderRequest, "orderEntryRequest");
    }

    private void SendOrderModificationRequest(StompClient stompClient) {
        // Get last created order for update purpose
        var lastOrder = SimpleCacheStorage.getInstance()
                .getFromCache(stompClient.getClientTarget(), OrderEntryRequest.class.getName())
                .stream().findFirst();
        if (lastOrder.isEmpty()) {
            logger.warn("[{}]No valid order to be used for order modification has been found!", stompClient.getClientTarget());
            return;
        }
        var lastOrderEntry = ((OrderEntryRequest)lastOrder.get()).getOrders().getFirst();

        var orderModificationRequest = new OrderModificationRequest()
                .withRequestId(UUID.randomUUID().toString())
                .withOrderModificationType(OrderModificationType.DEAC)
                .withOrders(Collections.singletonList(
                        new OrderModification()
                                .withOrderId("")
                                .withRevisionNo(0L)
                                .withText("Modified order")
                                .withClientOrderId(lastOrderEntry.getClientOrderId())
                                .withPortfolioId(lastOrderEntry.getPortfolioId())
                                .withContractIds(lastOrderEntry.getContractIds())
                                .withOrderType(lastOrderEntry.getOrderType())
                                .withQuantity(lastOrderEntry.getQuantity())
                                .withUnitPrice(lastOrderEntry.getUnitPrice())
                                .withTimeInForce(lastOrderEntry.getTimeInForce())
                                .withExecutionRestriction(lastOrderEntry.getExecutionRestriction())
                                .withExpireTime(lastOrderEntry.getExpireTime())
                                .withClipSize(lastOrderEntry.getClipSize())
                                .withClipPriceChange(lastOrderEntry.getClipPriceChange())
                ));

        logger.info("[{}]Attempting to send an correct order modification request.", stompClient.getClientTarget());
        stompClient.send(orderModificationRequest, "orderModificationRequest");
    }

    private void SendInvalidOrderRequest(StompClient stompClient) {
        var invalidOrderRequest = new OrderEntryRequest()
                .withRequestId(String.valueOf(UUID.randomUUID()))
                .withRejectPartially(false)
                .withOrders(Collections.singletonList(new OrderEntry()));

        logger.info("[{}]Attempting to send incorrect order request.", stompClient.getClientTarget());
        stompClient.send(invalidOrderRequest, "orderEntryRequest");
    }

    private void SendInvalidOrderModificationRequest(StompClient stompClient) {
//        var lastOrder = SimpleCacheStorage.getInstance()
//                .getFromCache(stompClient.getClientTarget(), OrderEntryRequest.class.getName())
//                .stream().findFirst();
//        if (lastOrder.isEmpty()) {
//            logger.warn("No valid order to be used for order modification has been found!");
//            return;
//        }
//        var lastOrderEntry = ((OrderEntryRequest)lastOrder.get()).getOrders().getFirst();

        var orderModificationRequest = new OrderModificationRequest()
                .withRequestId(UUID.randomUUID().toString())
                .withOrderModificationType(OrderModificationType.DEAC)
                .withOrders(Collections.singletonList(new OrderModification()));

        logger.info("[{}]Attempting to send an incorrect order modification request.", stompClient.getClientTarget());
        stompClient.send(orderModificationRequest, "orderModificationRequest");
    }
}
