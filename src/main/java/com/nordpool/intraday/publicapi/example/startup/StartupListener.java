/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example.startup;

import com.nordpool.id.publicapi.v1.order.*;
import com.nordpool.id.publicapi.v1.order.request.OrderEntryRequest;
import com.nordpool.id.publicapi.v1.order.request.OrderModificationRequest;
import com.nordpool.intraday.publicapi.example.service.connection.WebSocketConnector;
import com.nordpool.intraday.publicapi.example.service.subscription.Subscription;
import com.nordpool.intraday.publicapi.example.service.subscription.Topic;
import com.nordpool.intraday.publicapi.example.service.subscription.TradingService;
import com.nordpool.intraday.publicapi.example.stompmessagehandler.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

import static com.nordpool.intraday.publicapi.example.service.subscription.SubscriptionType.*;

/**
 * Startup Listener
 */
@Service
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LogManager.getLogger(WebSocketConnector.class);
    private static final String API_VERSION = "v1";
    @Autowired
    private TradingService tradingService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Intraday Public API Example application");
        // The current SockJSClient we used captures and prints the exception if unable to connect.
        tradingService.connect();

        // Subscribing to different topics
        tradingService.subscribe(Subscription.newBuilder()
                .withTopic(Topic.DELIVERY_AREAS)
                .withVersion(API_VERSION)
                .withSubscriptionType(STREAMING)
                .withArea(1)
                .build());

        tradingService.subscribe(Subscription.newBuilder()
                .withTopic(Topic.ORDER_EXECUTION_REPORT)
                .withVersion(API_VERSION)
                .withSubscriptionType(STREAMING)
                .withArea(1)
                .build());

        tradingService.subscribe(Subscription.newBuilder()
                .withTopic(Topic.CONFIGURATION)
                .withVersion(API_VERSION)
                .withSubscriptionType(EMPTY)
                .withIsGzipped(true)
                .build());

        tradingService.subscribe(Subscription.newBuilder()
                .withTopic(Topic.CONTRACTS)
                .withVersion(API_VERSION)
                .withSubscriptionType(STREAMING)
                .build());

        tradingService.subscribe(Subscription.newBuilder()
                .withTopic(Topic.LOCALVIEW)
                .withVersion(API_VERSION)
                .withArea(1)
                .withSubscriptionType(STREAMING)
                .withIsGzipped(true)
                .build());

        tradingService.subscribe(Subscription.newBuilder()
                .withTopic(Topic.PRIVATE_TRADE)
                .withVersion(API_VERSION)
                .withSubscriptionType(STREAMING)
                .build());

        tradingService.subscribe(Subscription.newBuilder()
                .withTopic(Topic.TICKER)
                .withVersion(API_VERSION)
                .withSubscriptionType(STREAMING)
                .build());

        tradingService.subscribe(Subscription.newBuilder()
                .withTopic(Topic.PUBLIC_STATISTICS)
                .withVersion(API_VERSION)
                .withSubscriptionType(CONFLATED)
                .withArea(1)
                .build());

        tradingService.subscribe(Subscription.newBuilder()
                .withTopic(Topic.CAPACITIES)
                .withVersion(API_VERSION)
                .withSubscriptionType(STREAMING)
                .withArea(1)
                .withMetadataParameters(Arrays.asList(Metadata.newBuilder()
                        .withName("metadataParameter")
                        .withValues(new HashMap<String, List<String>>(1) {{
                            put("areas", Collections.singletonList("39"));
                        }})
                        .build()))
                .build());


        // ---- EXAMPLES - comment / uncomment one by one as needed ----

        /* EXAMPLE #1: Sending an invalid order */
        waitabit(5000);
        LOGGER.info("Attempting to send an incorrect order, you will see the rejection message in the log.");
        tradingService.sendEntryOrderRequest(new OrderEntryRequest()
                .withRequestId(String.valueOf(UUID.randomUUID()))
                .withOrders(Collections.singletonList(new OrderEntry()
                        .withClientOrderId(UUID.randomUUID())))
        );

        /* EXAMPLE #2: Refreshing the token
         * Supposed to be called repeatedly anytime within the expiration time which is ATM 60 minutes.
         */
        LOGGER.info("Attempting to refresh the token...");
        waitabit(5000);
        tradingService.performTokenRefresh();

        // Wait some time, before disconnecting to receive messages via WebSocket
        waitabit(3000);
        LOGGER.info("Attempting to send an incorrect order modification request");
        tradingService.sendModificationOrderRequest(new OrderModificationRequest()
                .withRequestId(String.valueOf(UUID.randomUUID()))
                .withOrderModificationType(OrderModificationType.MODI)
                .withOrders(Arrays.asList(new OrderModification()
                                .withRevisionNo(1L)
                                .withPortfolioId("Test portfolio ID")
                                .withContractIds(Arrays.asList("NI_1381"))
                                .withText("test order modification execution")
                                .withClipSize(10L)
                                .withClipPriceChange(70L)
                                .withOrderType(OrderType.ICEBERG)
                                .withUnitPrice(11L) //  1 ... 1000000
                                .withQuantity(100L) // -999900 ... 999900
                                .withTimeInForce(TimeInForce.GTD) // if "timeInForce"="GTD" then "expireTime" should be passed within >= current_time
                                .withExecutionRestriction(ExecutionRestriction.NON)
                                .withExpireTime(ZonedDateTime.now().plusHours(1))
                                .withClientOrderId(UUID.randomUUID())
                                .withOrderId(String.valueOf(UUID.randomUUID()))
                        )
                )
        );

        // Wait some time, before disconnecting to receive messages via WebSocket
        try {
            LOGGER.fatal("Will wait for Enter to close the subscription.");
            System.in.read(); // wait for user
            System.in.read(); // wait for user
            tradingService.sendLogoutCommand();
        } catch (IOException e) {
            e.printStackTrace();
        }

        waitabit(1000);

        LOGGER.info("Now you can exit the program. Please refer to StartupListener.java for the example scenario.");
    }

    private void waitabit(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
