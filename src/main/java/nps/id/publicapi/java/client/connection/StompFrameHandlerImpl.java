package nps.id.publicapi.java.client.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.extensions.StompHeadersExtensions;
import nps.id.publicapi.java.client.connection.storage.SimpleCacheStorage;
import nps.id.publicapi.java.client.connection.subscriptions.requests.SubscriptionRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class StompFrameHandlerImpl implements StompFrameHandler {
    private static final Logger LOGGER = LogManager.getLogger(StompFrameHandlerImpl.class);

    private final SimpleCacheStorage simpleCacheStorage;
    private final ObjectMapper objectMapper;
    private final WebSocketClientTarget clientTarget;
    private final SubscriptionRequest subscriptionRequest;

    public StompFrameHandlerImpl(SimpleCacheStorage simpleCacheStorage, ObjectMapper objectMapper, WebSocketClientTarget clientTarget, SubscriptionRequest subscriptionRequest) {
        this.simpleCacheStorage = simpleCacheStorage;
        this.objectMapper = objectMapper;
        this.clientTarget = clientTarget;
        this.subscriptionRequest = subscriptionRequest;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return byte[].class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        var payloadBytes = (byte[])payload;

        var sentAt = StompHeadersExtensions.getSentAt(headers);
        var isSnapshot = StompHeadersExtensions.isSnapshot(headers);
        var publishingMode = StompHeadersExtensions.getPublishingMode(headers);
        var sequenceNumber = StompHeadersExtensions.getSequenceNumber(headers);
        var destination = headers.getDestination();
        var subscription = headers.getSubscription();

        try {
            var responseString = "";

            LOGGER.info("[{}][Frame({}):Metadata] destination={}, sentAt={}, snapshot={}, publishingMode={}, sequenceNumber={}",
                    clientTarget,
                    subscription,
                    destination,
                    sentAt,
                    isSnapshot,
                    publishingMode,
                    sequenceNumber);

            if (subscriptionRequest.getDataType() != null) {
                var typeClass = TypeFactory.rawClass(subscriptionRequest.getDataType());
                var targetType = objectMapper.getTypeFactory()
                        .constructCollectionType(LinkedList.class, typeClass);
                var message = (List<Object>)objectMapper.readValue(payloadBytes, targetType);

                simpleCacheStorage
                        .setCache(typeClass.getName(), message, false);

                responseString = objectMapper.writeValueAsString(message);
                LOGGER.info("[{}][Frame({}):ResponseType] {}", clientTarget, subscription, typeClass);
            } else {
                responseString = new String(payloadBytes, StandardCharsets.UTF_8);
                LOGGER.info("[{}][Frame({}):ResponseType] {}", clientTarget, subscription, String.class.getName());
            }

            // Trimming response content
            responseString = responseString.length() > 250
                    ? responseString.substring(0, 250) + "..."
                    : responseString;

            LOGGER.info("[{}][Frame({}):Response] {}", clientTarget, subscription, responseString);
        }
        catch (Exception e) {
            LOGGER.error("[{}][Frame({}):Error] {}", clientTarget, subscription, e.getMessage());
        }
    }
}