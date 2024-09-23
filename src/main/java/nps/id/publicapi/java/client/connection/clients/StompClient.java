package nps.id.publicapi.java.client.connection.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import nps.id.publicapi.java.client.connection.StompFrameHandlerImpl;
import nps.id.publicapi.java.client.connection.WebSocketConnector;
import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.messages.StompMessageFactory;
import nps.id.publicapi.java.client.connection.storage.SimpleCacheStorage;
import nps.id.publicapi.java.client.connection.subscriptions.exceptions.SubscriptionFailedException;
import nps.id.publicapi.java.client.connection.subscriptions.requests.SubscriptionRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.HashMap;
import java.util.Map;

public class StompClient {
    private static final Logger LOGGER = LogManager.getLogger(StompClient.class);

    private final Map<String, StompSession.Subscription> subscriptions = new HashMap<>();

    private final SimpleCacheStorage simpleCacheStorage;
    private final ObjectMapper objectMapper;
    private final WebSocketConnector webSocketConnector;

    @Getter
    private final WebSocketClientTarget clientTarget;
    private final String clientId;

    public StompClient(SimpleCacheStorage simpleCacheStorage, ObjectMapper objectMapper, WebSocketConnector webSocketConnector, WebSocketClientTarget clientTarget, String clientId) {
        this.simpleCacheStorage = simpleCacheStorage;
        this.objectMapper = objectMapper;
        this.webSocketConnector = webSocketConnector;
        this.clientTarget = clientTarget;
        this.clientId = clientId;
    }

    public Boolean open() {
        webSocketConnector.connect();
        var session = webSocketConnector.getStompSession();
        if (session != null && session.isConnected()) {
            LOGGER.info("[{}][ClientId:{}][SESSION:{}] Connection established", clientTarget, clientId, session.getSessionId());
            return true;
        }

        return false;
    }

    public StompSession.Subscription subscribe(SubscriptionRequest request) throws SubscriptionFailedException {
        if (!webSocketConnector.isConnected()) {
            throw new SubscriptionFailedException("["+ clientTarget + "][Destination:" + request.getDestination() + "] Failed to subscribe because no connection is established! Connect first!");
        }

        var session = webSocketConnector.getStompSession();
        var subscribeHeaders = StompMessageFactory.subscribeHeaders(request.getDestination(), request.getSubscriptionId());
        var subscription = session.subscribe(subscribeHeaders, new StompFrameHandlerImpl(simpleCacheStorage, objectMapper, clientTarget, request));
        subscriptions.put(request.getSubscriptionId(), subscription);
        LOGGER.info("[{}][SubscriptionId:{}] Subscription created", clientTarget, subscription.getSubscriptionId());
        return subscription;
    }

    public void send(Object payload, String destination) {
        var payloadHeaders = StompMessageFactory.sendHeaders(destination);
        webSocketConnector.send(payloadHeaders, payload);
    }

    public void unsubscribe(String subscriptionId) {
        var subscription = subscriptions.get(subscriptionId);
        subscription.unsubscribe();
        subscriptions.remove(subscriptionId);
        LOGGER.info("[{}][SubscriptionId:{}] Unsubscribed", clientTarget, subscriptionId);
    }

    private void unsubscribeAll() {
        var subscriptionsIds = this.subscriptions.keySet().stream().toList();
        for (var id : subscriptionsIds)
        {
            unsubscribe(id);
        }
    }

    public void disconnect() throws SubscriptionFailedException {
        if (!webSocketConnector.isConnected()) {
            throw new SubscriptionFailedException("Can not logout from closed connection!");
        }

        unsubscribeAll();

        LOGGER.info("[{}][ClientId:{}] Connection closed", clientTarget, clientId);
    }
}
