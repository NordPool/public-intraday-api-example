package nps.id.publicapi.java.client.connection.clients;

import lombok.Getter;
import nps.id.publicapi.java.client.connection.StompFrameHandlerImpl;
import nps.id.publicapi.java.client.connection.WebSocketConnector;
import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.messages.StompMessageFactory;
import nps.id.publicapi.java.client.connection.subscriptions.exceptions.SubscriptionFailedException;
import nps.id.publicapi.java.client.connection.subscriptions.requests.SubscriptionRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.HashMap;
import java.util.Map;

public class StompClient {
    private static final Logger logger = LogManager.getLogger(StompClient.class);

    private final Map<String, StompSession.Subscription> subscriptions = new HashMap<>();

    private final WebSocketConnector webSocketConnector;

    @Getter
    private final WebSocketClientTarget clientTarget;
    private final String clientId;

    public StompClient(WebSocketConnector webSocketConnector, WebSocketClientTarget clientTarget, String clientId) {
        this.webSocketConnector = webSocketConnector;
        this.clientTarget = clientTarget;
        this.clientId = clientId;
    }

    public Boolean open() {
        webSocketConnector.connect();
        var session = webSocketConnector.getStompSession();
        if (session != null && session.isConnected()) {
            logger.info("[{}][ClientId:{}][SESSION:{}] Connection established", clientTarget, session.getSessionId(), clientId);
            return true;
        }

        return false;
    }

    public StompSession.Subscription subscribe(SubscriptionRequest request) throws SubscriptionFailedException {
        if (!webSocketConnector.isConnected()) {
            throw new SubscriptionFailedException("Failed to subscribe because no connection is established! Connect first!");
        }

        var session = webSocketConnector.getStompSession();
        var subscribeHeaders = StompMessageFactory.subscribeHeaders(request.getDestination(), request.getSubscriptionId());
        var subscription = session.subscribe(subscribeHeaders, new StompFrameHandlerImpl(clientTarget, request));
        subscriptions.put(request.getSubscriptionId(), subscription);
        logger.info("[{}][SubscriptionId:{}] Subscription created", clientTarget, subscription.getSubscriptionId());
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
        logger.info("[{}][SubscriptionId:{}] Unsubscribed", clientTarget, subscriptionId);
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

        logger.info("[{}][ClientId:{}]Connection closed", clientTarget, clientId);
    }
}
