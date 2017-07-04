/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example.startup;

import com.nordpool.id.publicapi.v1.order.OrderEntry;
import com.nordpool.id.publicapi.v1.order.request.OrderEntryRequest;
import com.nordpool.intraday.publicapi.example.service.connection.WebSocketConnector;
import com.nordpool.intraday.publicapi.example.service.subscription.Subscription;
import com.nordpool.intraday.publicapi.example.service.subscription.SubscriptionType;
import com.nordpool.intraday.publicapi.example.service.subscription.TradingService;
import com.nordpool.intraday.publicapi.example.stompmessagehandler.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.lang.Boolean.TRUE;


/**
 * Startup Listener
 */
@Service
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LogManager.getLogger(WebSocketConnector.class);
    private static final String API_VERSION = "v1";
    @Autowired
    private TradingService tradingService;
    @Autowired
    private ApplicationContext context;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Intraday Public API Example application");
        // The current SockJSClient we used captures and prints the exception if unable to connect.
        tradingService.connect();

        tradingService.subscribe(Subscription.newBuilder()
                .withSubscriptionType(SubscriptionType.DELIVERY_AREAS)
                .withVersion(API_VERSION)
                .withStreaming(TRUE)
                .withArea(1)
                .build());

        tradingService.subscribe(Subscription.newBuilder()
                .withSubscriptionType(SubscriptionType.ORDER_EXECUTION_REPORT)
                .withVersion(API_VERSION)
                .withStreaming(TRUE)
                .withArea(1)
                .build());


        tradingService.subscribe(Subscription.newBuilder()
                .withSubscriptionType(SubscriptionType.CONFIGURATION)
                .withVersion(API_VERSION)
                .build());


        tradingService.subscribe(Subscription.newBuilder()
                .withSubscriptionType(SubscriptionType.CONTRACTS)
                .withVersion(API_VERSION)
                .withStreaming(TRUE)
                .build());


        tradingService.subscribe(Subscription.newBuilder()
                .withSubscriptionType(SubscriptionType.LOCALVIEW)
                .withVersion(API_VERSION)
                .withArea(1)
                .withStreaming(TRUE)
                .build());


        tradingService.subscribe(Subscription.newBuilder()
                .withSubscriptionType(SubscriptionType.PRIVATE_TRADE)
                .withVersion(API_VERSION)
                .withStreaming(TRUE)
                .build());


        tradingService.subscribe(Subscription.newBuilder()
                .withSubscriptionType(SubscriptionType.TICKER)
                .withVersion(API_VERSION)
                .withStreaming(TRUE)
                .build());

        tradingService.subscribe(Subscription.newBuilder()
                .withSubscriptionType(SubscriptionType.CAPACITIES)
                .withVersion(API_VERSION)
                .withStreaming(TRUE)
                .withArea(1)
                .withMetadataParameters(Arrays.asList(Metadata.newBuilder()
                        .withName("metadataParameter")
                        .withValues(new HashMap<String, List<String>>(1) {{
                            put("areas", Arrays.asList("39"));
                        }})
                        .build()))
                .build());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("Attempting to send an incorrect order, you will see the rejection message in the log.");
        tradingService.sendEntryOrderRequest(OrderEntryRequest.newBuilder()
                .withRequestId(String.valueOf(UUID.randomUUID()))
                .withOrders(Arrays.asList(OrderEntry.newBuilder().withClientOrderId("Something").build()))
                .build());

        // Wait some time, before disconnecting to receive messages via WebSocket
        LOGGER.info("Will logout in 10 seconds");
        try {
            Thread.sleep(10000);
            tradingService.sendLogoutCommand();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }

        LOGGER.info("Now you can exit the program. Please refer to StartupListener.java for the example scenario.");
    }
}
