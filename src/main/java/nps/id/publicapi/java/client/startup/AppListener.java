package nps.id.publicapi.java.client.startup;

import com.nordpool.id.publicapi.v1.ConfigurationRow;
import com.nordpool.id.publicapi.v1.ContractRow;
import com.nordpool.id.publicapi.v1.OrderExecutionReport;
import com.nordpool.id.publicapi.v1.contract.ContractState;
import com.nordpool.id.publicapi.v1.contract.ProductType;
import com.nordpool.id.publicapi.v1.order.*;
import com.nordpool.id.publicapi.v1.order.request.OrderEntryRequest;
import com.nordpool.id.publicapi.v1.order.request.OrderModificationRequest;
import nps.id.publicapi.java.client.connection.clients.StompClient;
import nps.id.publicapi.java.client.connection.clients.StompClientGenericFactory;
import nps.id.publicapi.java.client.connection.enums.PublishingMode;
import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.storage.SimpleCacheStorage;
import nps.id.publicapi.java.client.connection.subscriptions.exceptions.SubscriptionFailedException;
import nps.id.publicapi.java.client.connection.subscriptions.helpers.DestinationHelper;
import nps.id.publicapi.java.client.connection.subscriptions.requests.SubscriptionRequestBuilder;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import nps.id.publicapi.java.client.security.options.CredentialsOptions;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
public class AppListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LogManager.getLogger(AppListener.class);

    private static final String VERSION = "v1";
    private static final int DEMO_AREA = 2;

    private final SimpleCacheStorage simpleCacheStorage;
    private final StompClientGenericFactory stompClientGenericFactory;

    private final SubscriptionRequestBuilder subscribeRequestBuilder;

    public AppListener(SimpleCacheStorage simpleCacheStorage, CredentialsOptions credentialsOptions, StompClientGenericFactory stompClientGenericFactory) {
        this.simpleCacheStorage = simpleCacheStorage;
        this.stompClientGenericFactory = stompClientGenericFactory;
        this.subscribeRequestBuilder = SubscriptionRequestBuilder.createBuilder(credentialsOptions.getUserName(), VERSION);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            var marketDataClient = createClient(WebSocketClientTarget.MARKET_DATA);
            var tradingClient = createClient(WebSocketClientTarget.TRADING);

            // Delivery areas
            subscribeDeliveryAreas(marketDataClient);

            // Configurations
            subscribeConfigurations(tradingClient);

            // Order execution report
            subscribeOrderExecutionReports(tradingClient, PublishingMode.STREAMING);

            // Contracts
            subscribeContracts(marketDataClient, PublishingMode.CONFLATED);

            // Local views
            subscribeLocalViews(marketDataClient, PublishingMode.CONFLATED);

            // Private trades
            subscribePrivateTrades(tradingClient, PublishingMode.STREAMING);

            // Tickers
            subscribeTickers(marketDataClient, PublishingMode.CONFLATED);

            // MyTickers
            subscribeMyTickers(marketDataClient, PublishingMode.CONFLATED);

            // Public statistics
            subscribePublicStatistics(marketDataClient, PublishingMode.CONFLATED);

            // Throttling limits
            subscribeThrottlingLimits(tradingClient, PublishingMode.CONFLATED);

            // Capacities
            subscribeCapacities(marketDataClient, PublishingMode.CONFLATED);

            // Order
            // We wait some time in hope to get some example contracts and configurations that are needed for preparing example order request

            waitMillis(5000);
            sendOrderRequest(tradingClient);
            // Wait before order modification request
            waitMillis(5000);
            sendOrderModificationRequest(tradingClient);
            // Wait before invalid order request
            waitMillis(3000);
            sendInvalidOrderRequest(tradingClient);
            // Wait before invalid order modification request
            waitMillis(3000);
            sendInvalidOrderModificationRequest(tradingClient);

            waitMillis(3000);

            LOGGER.info("============================================================");
            LOGGER.info("Press 'x' key to unsubscribe, logout and close. . .");
            LOGGER.info("============================================================");

            // Set clients disconnection behaviour while closing app with 'x' key
            var key = System.in.read();
            if (key == 120) {
                marketDataClient.disconnect();
                tradingClient.disconnect();
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitMillis(long milliseconds) {
        Awaitility
                .await()
                .pollDelay(Duration.ofMillis(milliseconds))
                .until(() -> true);
    }

    private StompClient createClient(WebSocketClientTarget clientTarget) {
        return stompClientGenericFactory.create(clientTarget);
    }

    private void subscribeDeliveryAreas(StompClient stompClient) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createDeliveryAreas();
        stompClient.subscribe(subscription);
    }

    private void subscribeConfigurations(StompClient stompClient) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createConfiguration();
        stompClient.subscribe(subscription);
    }

    private void subscribeOrderExecutionReports(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createOrderExecutionReport(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void subscribeContracts(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createContracts(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void subscribeLocalViews(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createLocalView(publishingMode, DEMO_AREA);
        stompClient.subscribe(subscription);
    }

    private void subscribePrivateTrades(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createPrivateTrades(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void subscribeTickers(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createTicker(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void subscribeMyTickers(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createMyTicker(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void subscribePublicStatistics(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createPublicStatistics(publishingMode, DEMO_AREA);
        stompClient.subscribe(subscription);
    }

    private void subscribeThrottlingLimits(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createThrottlingLimits(publishingMode);
        stompClient.subscribe(subscription);

        // Set automatic unsubscription of throttling limit topic after 10s
        try (ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor()) {
            Runnable runnable = () -> {
                try {
                    stompClient.unsubscribe(subscription.getSubscriptionId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            executorService.schedule(runnable, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.info("[{}] An error occurred during throttling limits unsubscription, details: {}", stompClient.getClientTarget(), e.getMessage());
        }
    }

    private void subscribeCapacities(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createCapacities(publishingMode, DEMO_AREA);
        stompClient.subscribe(subscription);
    }

    private void sendOrderRequest(StompClient stompClient) {
        var exampleData = getExampleContractPortfolioAndArea(stompClient);
        if (exampleData == null) {
            return;
        }
        var contractId = exampleData.getLeft();
        var portfolioId = exampleData.getMiddle();
        var areaId = exampleData.getRight();

        var orderRequest = new OrderEntryRequest()
                .withRequestId(UUID.randomUUID().toString())
                .withRejectPartially(false)
                .withOrders(Collections.singletonList(
                        new OrderEntry()
                                .withText("New order")
                                .withClientOrderId(UUID.randomUUID().toString())
                                .withPortfolioId(portfolioId)
                                .withSide(OrderSide.SELL)
                                .withContractIds(Collections.singletonList(contractId))
                                .withOrderType(OrderType.LIMIT)
                                .withQuantity(3000L)
                                .withState(OrderState.ACTI)
                                .withUnitPrice(2500L)
                                .withTimeInForce(TimeInForce.GFS)
                                .withDeliveryAreaId(areaId)
                                .withExecutionRestriction(ExecutionRestriction.NON)
                                .withExpireTime(ZonedDateTime.now().plusHours(6))
                ));

        // Store created order in simple cache storage for order modification request
        simpleCacheStorage
                .setCache(OrderEntryRequest.class.getName(), Collections.singletonList(orderRequest), false);

        LOGGER.info("[{}] Attempting to send correct order request.", stompClient.getClientTarget());
        stompClient.send(orderRequest, DestinationHelper.composeDestination(VERSION, "orderEntryRequest"));
    }

    private void sendOrderModificationRequest(StompClient stompClient) {
        // Get last created order for update purpose
        var lastOrderEntryRequestOptional = simpleCacheStorage
                .getFromCache(OrderEntryRequest.class.getName())
                .reversed()
                .stream()
                .findFirst();
        if (lastOrderEntryRequestOptional.isEmpty()) {
            LOGGER.warn("[{}] No valid order to be used for order modification has been found in cache!", stompClient.getClientTarget());
            return;
        }

        var lastOrderEntryRequest = (OrderEntryRequest)lastOrderEntryRequestOptional.get();
        var lastOrderEntry = lastOrderEntryRequest.getOrders().getFirst();

        // Get last order execution report response for above order request (OrderId required for order modification request)
        var lastOrderExecutionReportOptional = simpleCacheStorage
                .getFromCache(OrderExecutionReport.class.getName())
                .reversed()
                .stream()
                .map(c -> (OrderExecutionReport)c)
                .filter(oer -> oer.getOrders().size() == 1 && Objects.equals(oer.getOrders().getFirst().getClientOrderId(), lastOrderEntry.getClientOrderId()))
                .findFirst();
        if (lastOrderExecutionReportOptional.isEmpty() || lastOrderExecutionReportOptional.get().getOrders().isEmpty()) {
            LOGGER.warn("[{}] No valid order execution report to be used for order modification has been found in cache!", stompClient.getClientTarget());
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

        LOGGER.info("[{}] Attempting to send an correct order modification request.", stompClient.getClientTarget());
        stompClient.send(orderModificationRequest, DestinationHelper.composeDestination(VERSION, "orderModificationRequest"));
    }

    private void sendInvalidOrderRequest(StompClient stompClient) {
        var exampleData = getExampleContractPortfolioAndArea(stompClient);
        if (exampleData == null) {
            return;
        }
        var portfolioId = exampleData.getMiddle();

        var invalidOrderRequest = new OrderEntryRequest()
                .withRequestId(String.valueOf(UUID.randomUUID()))
                .withRejectPartially(false)
                .withOrders(Collections.singletonList(new OrderEntry()
                                .withClientOrderId(UUID.randomUUID().toString())
                                .withPortfolioId(portfolioId))
                );

        LOGGER.info("[{}] Attempting to send incorrect order request.", stompClient.getClientTarget());
        stompClient.send(invalidOrderRequest, DestinationHelper.composeDestination(VERSION, "orderEntryRequest"));
    }

    private void sendInvalidOrderModificationRequest(StompClient stompClient) {
        var orderModificationRequest = new OrderModificationRequest()
                .withRequestId(UUID.randomUUID().toString())
                .withOrderModificationType(OrderModificationType.DEAC)
                .withOrders(Collections.singletonList(new OrderModification()
                        .withClientOrderId(UUID.randomUUID().toString()))
                );

        LOGGER.info("[{}] Attempting to send an incorrect order modification request.", stompClient.getClientTarget());
        stompClient.send(orderModificationRequest,  DestinationHelper.composeDestination(VERSION, "orderModificationRequest"));
    }

    private Triple<String, String, Long> getExampleContractPortfolioAndArea(StompClient stompClient) {
        var random = new Random();

        var exampleContracts = simpleCacheStorage
                .getFromCache(ContractRow.class.getName())
                .stream()
                .map(c -> (ContractRow)c)
                .filter(c -> c.getProductType() != ProductType.CUSTOM_BLOCK && c.getDlvryAreaState().stream().anyMatch(s -> s.getState() == ContractState.ACTI))
                .toList();
        if (exampleContracts.isEmpty()) {
            LOGGER.warn("[{}] No valid contract to be used for order creation has been found in cache!", stompClient.getClientTarget());
            return null;
        }

        var exampleRandomContract = exampleContracts.get(random.nextInt(exampleContracts.size()));

        var exampleAreas = exampleRandomContract
                .getDlvryAreaState()
                .stream()
                .filter(s -> s.getState() == ContractState.ACTI)
                .toList();

        var examplePortfolios = simpleCacheStorage
                .getFromCache(ConfigurationRow.class.getName())
                .stream()
                .map(c -> ((ConfigurationRow)c).getPortfolios())
                .flatMap(List::stream)
                .filter(p -> p
                        .getAreas()
                        .stream()
                        .anyMatch(a -> exampleAreas
                                .stream()
                                .anyMatch(s -> s.getDlvryAreaId() == (long)a.getAreaId())
                        )
                )
                .toList();

        if (examplePortfolios.isEmpty()) {
            LOGGER.warn("[{}] No valid portfolio to be used for order creation has been found in cache!", stompClient.getClientTarget());
            return null;
        }

        var exampleRandomPortfolioForContract = examplePortfolios.get(random.nextInt(examplePortfolios.size()));

        var deliveryAreaPortfolio = exampleRandomPortfolioForContract
                .getAreas()
                .stream()
                .filter(a -> exampleAreas
                        .stream()
                        .anyMatch(s -> s.getDlvryAreaId() == (long)a.getAreaId()))
                .findFirst()
                .get();

        return Triple.of(exampleRandomContract.getContractId(), exampleRandomPortfolioForContract.getId(), (long)deliveryAreaPortfolio.getAreaId());
    }
}
