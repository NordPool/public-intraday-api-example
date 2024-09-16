package nps.id.publicapi.java.client.startup;

import com.nordpool.id.publicapi.v1.ConfigurationRow;
import com.nordpool.id.publicapi.v1.ContractRow;
import com.nordpool.id.publicapi.v1.OrderExecutionReport;
import com.nordpool.id.publicapi.v1.contract.ContractState;
import com.nordpool.id.publicapi.v1.contract.ProductType;
import com.nordpool.id.publicapi.v1.order.*;
import com.nordpool.id.publicapi.v1.order.request.OrderEntryRequest;
import com.nordpool.id.publicapi.v1.order.request.OrderModificationRequest;
import com.nordpool.id.publicapi.v1.portfolio.Portfolio;
import nps.id.publicapi.java.client.connection.clients.StompClient;
import nps.id.publicapi.java.client.connection.clients.StompClientGenericFactory;
import nps.id.publicapi.java.client.connection.enums.PublishingMode;
import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.storage.SimpleCacheStorage;
import nps.id.publicapi.java.client.connection.subscriptions.exceptions.SubscriptionFailedException;
import nps.id.publicapi.java.client.connection.subscriptions.helpers.DestinationHelper;
import nps.id.publicapi.java.client.connection.subscriptions.requests.SubscriptionRequestBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import nps.id.publicapi.java.client.security.options.CredentialsOptions;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LogManager.getLogger(AppListener.class);

    private final String _version = "v1";
    private final int _demoArea = 2;
    private final String _clientId = UUID.randomUUID() + "-java-demo-client";

    private final StompClientGenericFactory stompClientGenericFactory;

    private final SubscriptionRequestBuilder subscribeRequestBuilder;

    public AppListener(CredentialsOptions credentialsOptions, StompClientGenericFactory stompClientGenericFactory) {
        this.stompClientGenericFactory = stompClientGenericFactory;
        this.subscribeRequestBuilder = SubscriptionRequestBuilder.createBuilder(credentialsOptions.getUserName(), _version);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            var pmdClient = CreateClient(WebSocketClientTarget.PMD);
            var middlewareClient = CreateClient(WebSocketClientTarget.MIDDLEWARE);

            // Delivery areas
            SubscribeDeliveryAreas(pmdClient);

            // Configurations
            SubscribeConfigurations(middlewareClient);

            // Order execution report
            SubscribeOrderExecutionReports(middlewareClient, PublishingMode.STREAMING);

            // Contracts
            SubscribeContracts(pmdClient, PublishingMode.CONFLATED);

            // Local views
            SubscribeLocalViews(pmdClient, PublishingMode.CONFLATED);

            // Private trades
            SubscribePrivateTrades(middlewareClient, PublishingMode.STREAMING);

            // Tickers
            SubscribeTickers(pmdClient, PublishingMode.CONFLATED);

            // MyTickers
            SubscribeMyTickers(pmdClient, PublishingMode.CONFLATED);

            // Public statistics
            SubscribePublicStatistics(pmdClient, PublishingMode.CONFLATED);

            // Throttling limits
            SubscribeThrottlingLimits(middlewareClient, PublishingMode.CONFLATED);

            // Capacities
            SubscribeCapacities(pmdClient, PublishingMode.CONFLATED);

            // Order
            // We wait some time in hope to get some example contracts and configurations that are needed for preparing example order request
            Thread.sleep(5000);
            SendOrderRequest(middlewareClient);
            // Wait before order modification request
            Thread.sleep(5000);
            SendOrderModificationRequest(middlewareClient);
            // Wait before invalid order request
            Thread.sleep(5000);
            SendInvalidOrderRequest(middlewareClient);
            // Wait before invalid order modification request
            Thread.sleep(5000);
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
        var subscription = subscribeRequestBuilder.createPublicStatistics(publishingMode, _demoArea);
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
        var exampleContract = getExampleContract(stompClient);
        if (exampleContract.isEmpty()) {
            return;
        }

        var contract = exampleContract.get();

        var examplePortfolio = getExamplePortfolioByContract(stompClient, contract);
        if (examplePortfolio.isEmpty()) {
            return;
        }

        var portfolio = examplePortfolio.get();

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
                .setCache(OrderEntryRequest.class.getName(), Collections.singletonList(orderRequest), false);

        logger.info("[{}]Attempting to send correct order request.", stompClient.getClientTarget());
        stompClient.send(orderRequest, DestinationHelper.composeDestination(_version, "orderEntryRequest"));
    }

    private void SendOrderModificationRequest(StompClient stompClient) {
        // Get last created order for update purpose
        var lastOrderEntryRequestOptional = SimpleCacheStorage.getInstance()
                .getFromCache(OrderEntryRequest.class.getName())
                .reversed()
                .stream()
                .findFirst();
        if (lastOrderEntryRequestOptional.isEmpty()) {
            logger.warn("[{}]No valid order to be used for order modification has been found!", stompClient.getClientTarget());
            return;
        }

        var lastOrderEntryRequest = (OrderEntryRequest)lastOrderEntryRequestOptional.get();
        var lastOrderEntry = lastOrderEntryRequest.getOrders().getFirst();

        // Get last order execution report response for above order request (OrderId required for order modification request)
        var lastOrderExecutionReportOptional = SimpleCacheStorage.getInstance()
                .getFromCache(OrderExecutionReport.class.getName())
                .stream()
                .map(c -> (OrderExecutionReport)c)
                .filter(oer -> Objects.equals(oer.getRequestId(), lastOrderEntryRequest.getRequestId()))
                .findFirst();
        if (lastOrderExecutionReportOptional.isEmpty() || lastOrderExecutionReportOptional.get().getOrders().isEmpty()) {
            logger.warn("[{}]No valid order execution report to be used for order modification has been found!", stompClient.getClientTarget());
            return;
        }

        var lastOrderExecutionReportOrderEntry = lastOrderExecutionReportOptional
                .get()
                .getOrders()
                .getFirst();

        var orderModificationRequest = new OrderModificationRequest()
                .withRequestId(UUID.randomUUID().toString())
                .withOrderModificationType(OrderModificationType.DEAC)
                .withOrders(Collections.singletonList(
                        new OrderModification()
                                .withOrderId(lastOrderExecutionReportOrderEntry.getOrderId())
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
        stompClient.send(orderModificationRequest, DestinationHelper.composeDestination(_version, "orderModificationRequest"));
    }

    private void SendInvalidOrderRequest(StompClient stompClient) {
        var exampleContract = getExampleContract(stompClient);
        if (exampleContract.isEmpty()) {
            return;
        }

        var contract = exampleContract.get();

        var examplePortfolio = getExamplePortfolioByContract(stompClient, contract);
        if (examplePortfolio.isEmpty()) {
            return;
        }

        var portfolio = examplePortfolio.get();

        var invalidOrderRequest = new OrderEntryRequest()
                .withRequestId(String.valueOf(UUID.randomUUID()))
                .withRejectPartially(false)
                .withOrders(Collections.singletonList(new OrderEntry()
                                .withClientOrderId(UUID.randomUUID().toString())
                                .withPortfolioId(portfolio.getId()))
                );

        logger.info("[{}]Attempting to send incorrect order request.", stompClient.getClientTarget());
        stompClient.send(invalidOrderRequest, DestinationHelper.composeDestination(_version, "orderEntryRequest"));
    }

    private void SendInvalidOrderModificationRequest(StompClient stompClient) {
        var orderModificationRequest = new OrderModificationRequest()
                .withRequestId(UUID.randomUUID().toString())
                .withOrderModificationType(OrderModificationType.DEAC)
                .withOrders(Collections.singletonList(new OrderModification()
                        .withClientOrderId(UUID.randomUUID().toString()))
                );

        logger.info("[{}]Attempting to send an incorrect order modification request.", stompClient.getClientTarget());
        stompClient.send(orderModificationRequest,  DestinationHelper.composeDestination(_version, "orderModificationRequest"));
    }

    private Optional<ContractRow> getExampleContract(StompClient stompClient) {
        var exampleContract = SimpleCacheStorage.getInstance()
                .getFromCache(ContractRow.class.getName())
                .stream()
                .map(c -> (ContractRow)c)
                .filter(c -> c.getProductType() != ProductType.CUSTOM_BLOCK && c.getDlvryAreaState().stream().anyMatch(s -> s.getState() == ContractState.ACTI))
                .findFirst();
        if (exampleContract.isEmpty()) {
            logger.warn("[{}]No valid contract to be used for order creation has been found! Check contracts available in SimpleCacheStorage.", stompClient.getClientTarget());
        }

        return exampleContract;
    }

    private Optional<Portfolio> getExamplePortfolioByContract(StompClient stompClient, ContractRow contract) {
        var exampleAreas = contract
                .getDlvryAreaState()
                .stream()
                .filter(s -> s.getState() == ContractState.ACTI)
                .collect(Collectors.toList());

        var examplePortfolio = SimpleCacheStorage.getInstance()
                .getFromCache(ConfigurationRow.class.getName())
                .stream()
                .map(c -> (ConfigurationRow)c)
                .findFirst()
                .get()
                .getPortfolios()
                .stream()
                .filter(p -> p.getAreas().stream().anyMatch(a -> exampleAreas.stream().anyMatch(s -> s.getDlvryAreaId() == (long)a.getAreaId())))
                .findFirst();

        if (examplePortfolio.isEmpty()) {
            logger.warn("[{}]No valid portfolio to be used for order creation has been found! Check contracts available in SimpleCacheStorage.", stompClient.getClientTarget());
        }

        return examplePortfolio;
    }
}
