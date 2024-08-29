package nps.id.publicapi.java.client;

import com.nordpool.id.publicapi.v1.ConfigurationRow;
import com.nordpool.id.publicapi.v1.ContractRow;
import com.nordpool.id.publicapi.v1.order.*;
import com.nordpool.id.publicapi.v1.order.request.OrderEntryRequest;
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
            var edgeUiClient = CreateClient(WebSocketClientTarget.EDGE);
            var middlewareUiClient = CreateClient(WebSocketClientTarget.MIDDLEWARE);

            // Delivery areas
            SubscribeDeliveryAreas(edgeUiClient);
            SubscribeDeliveryAreas(middlewareUiClient);

            // Contracts
            SubscribeContracts(edgeUiClient, PublishingMode.CONFLATED);
            SubscribeContracts(middlewareUiClient, PublishingMode.CONFLATED);

            SubscribeContracts(edgeUiClient, PublishingMode.STREAMING);
            SubscribeContracts(middlewareUiClient, PublishingMode.STREAMING);

            // Configurations
            SubscribeConfigurations(edgeUiClient);
            SubscribeConfigurations(middlewareUiClient);

            // Capacities
            SubscribeCapacities(edgeUiClient, PublishingMode.CONFLATED);
            SubscribeCapacities(middlewareUiClient, PublishingMode.CONFLATED);

            SubscribeCapacities(edgeUiClient, PublishingMode.STREAMING);
            SubscribeCapacities(middlewareUiClient, PublishingMode.STREAMING);

            // Order
            // We wait some time in hope to get some example contracts and configurations that are needed for preparing example order request
            Thread.sleep(10000);
            SendOrderRequest(edgeUiClient);
            SendOrderRequest(middlewareUiClient);

            System.out.print("Press 'x' key to continue logout, unsubscribe and close. . . ");
            var key = System.in.read();
            if (key == 120) {
                edgeUiClient.disconnect();
                middlewareUiClient.disconnect();
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

    private void SubscribeContracts(StompClient stompClient, PublishingMode publishingMode) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createContracts(publishingMode);
        stompClient.subscribe(subscription);
    }

    private void SubscribeConfigurations(StompClient stompClient) throws SubscriptionFailedException {
        var subscription = subscribeRequestBuilder.createConfiguration();
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
            logger.warn("No valid contract to be used for order creation has been found! Check contracts available in SimpleCacheStorage.");
            return;
        }

        var examplePortfolio = SimpleCacheStorage.getInstance()
                .getFromCache(stompClient.getClientTarget(), ConfigurationRow.class.getName())
                .stream().findFirst();
        if (examplePortfolio.isEmpty()) {
            logger.warn("No valid portfolio to be used for order creation has been found! Check contracts available in SimpleCacheStorage.");
            return;
        }
        var contract = (ContractRow)exampleContract.get();
        var portfolio = ((ConfigurationRow)examplePortfolio.get()).getPortfolios().getFirst();
        var orderRequest = new OrderEntryRequest()
                .withRequestId(UUID.randomUUID().toString())
                .withRejectPartially(false)
                .withOrders(Collections.singletonList(
                        new OrderEntry()
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

        stompClient.send(orderRequest, "orderEntryRequest");
    }
}
