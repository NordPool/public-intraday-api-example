/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example.service.subscription;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nordpool.id.publicapi.v1.command.Command;
import com.nordpool.id.publicapi.v1.command.CommandType;
import com.nordpool.id.publicapi.v1.command.TokenRefreshCommand;
import com.nordpool.id.publicapi.v1.order.request.OrderEntryRequest;
import com.nordpool.id.publicapi.v1.order.request.OrderModificationRequest;
import com.nordpool.intraday.publicapi.example.service.connection.WebSocketConnector;
import com.nordpool.intraday.publicapi.example.service.security.SSOService;
import com.nordpool.intraday.publicapi.example.stompmessagehandler.StompFrameHandlerImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TradingService {
    private static final Logger LOGGER = LogManager.getLogger(TradingService.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WebSocketConnector webSocketConnector;

    @Autowired
    private SSOService ssoService;


    public void connect() {
        webSocketConnector.connect();
    }


    public void subscribe(Subscription subscription) {
        StompSession session = getStompSession();
        if (session == null) {
            return;
        }

        String topic = getTopic(subscription);
        if (topic != null) {
            StompHeaders stompHeaders = getHeaders(subscription, topic);
            LOGGER.info("Subscribing to " + topic);
            session.subscribe(stompHeaders, new StompFrameHandlerImpl(subscription));
        } else {
            LOGGER.error("Undefined subscription type");
        }
    }


    public void sendEntryOrderRequest(OrderEntryRequest order) {
        sendMessage("/v1/orderEntryRequest", order);
    }


    public void sendModificationOrderRequest(OrderModificationRequest order) {
        sendMessage("/v1/orderModificationRequest", order);
    }


    public void sendLogoutCommand() {
        sendMessage("/v1/command", new Command(CommandType.LOGOUT));
    }

    /**
     * Performing a token refresh.
     */
    public void performTokenRefresh() {
        String existingToken = ssoService.getToken();
        String newToken = null;
        try {
            newToken = ssoService.getNewToken();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        if (!StringUtils.isEmpty(newToken)) {
            sendMessage("/v1/command",
                    new TokenRefreshCommand().withOldToken(existingToken).withNewToken(newToken).withType(CommandType.TOKEN_REFRESH));
        } else
        {
            LOGGER.error("Could not fetch a new token.");
        }

    }


    private StompHeaders getHeaders(Subscription subscription, String topic) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination(topic);

        if (subscription.getMetadataParameters() != null) {
            subscription.getMetadataParameters().stream().forEach(p -> {
                try {
                    stompHeaders.set(p.getName(), mapper.writeValueAsString(p.getValues()));
                } catch (JsonProcessingException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            });
        }
        return stompHeaders;
    }

    private StompSession getStompSession() {
        StompSession session = webSocketConnector.getSession();

        // Make 15 sec timeout for connection
        int repeatCount = 0;
        while (session == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
            session = webSocketConnector.getSession();
            repeatCount++;
        }
        if (repeatCount > 15) {
            LOGGER.error("Please, connect to remote Web Socket Server");
            return null;
        }
        return session;
    }


    private String getTopic(Subscription subscription) {
        String topic = "/user/" + ssoService.getUser() + "/" + subscription.getVersion() + subscription.getSubscriptionType().getType() + subscription.getTopic().getTopic();
        switch (subscription.getTopic()) {
            // Area aware types
            case CAPACITIES:
            case LOCALVIEW:
            case PUBLIC_STATISTICS:
                return topic + subscription.getArea() + isGzipped(subscription);
            case CONFIGURATION:
            case CONTRACTS:
            case DELIVERY_AREAS:
            case ORDER_EXECUTION_REPORT:
            case PRIVATE_TRADE:
            case TICKER:
            case THROTTLING_LIMITS:
                return topic + isGzipped(subscription);
            default:
                LOGGER.error("Undefined subscription type");
                return null;
        }
    }

    private void sendMessage(String destination, Object msg) {
        LOGGER.info("Sending to: " + destination + " message: " + msg);
        StompSession session = getStompSession();
        if (session == null) {
            return;
        }

        try {
            session.send(destination, mapper.writeValueAsString(msg).getBytes());
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private String isGzipped(Subscription subscription) {
        if (subscription.getGzipped() == null) return "";
        return subscription.getGzipped() ? "/gzip" : "";
    }
}
