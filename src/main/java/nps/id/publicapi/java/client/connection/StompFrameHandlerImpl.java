package nps.id.publicapi.java.client.connection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.experimental.ExtensionMethod;
import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.extensions.StompHeadersExtensions;
import nps.id.publicapi.java.client.connection.storage.SimpleCacheStorage;
import nps.id.publicapi.java.client.connection.subscriptions.requests.SubscriptionRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

@ExtensionMethod({java.util.Arrays.class, StompHeadersExtensions.class})
public class StompFrameHandlerImpl implements StompFrameHandler {
    private static final Logger logger = LogManager.getLogger(StompFrameHandlerImpl.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebSocketClientTarget clientTarget;
    private final SubscriptionRequest subscriptionRequest;

    public StompFrameHandlerImpl(WebSocketClientTarget clientTarget, SubscriptionRequest subscriptionRequest) {
        this.clientTarget = clientTarget;
        this.subscriptionRequest = subscriptionRequest;
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return byte[].class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        var payloadBytes = (byte[])payload;

        var sentAt = headers.getSentAt();
        var isSnapshot = headers.isSnapshot();
        var destination = headers.getDestination();
        var publishingMode = headers.getPublishingMode();
        var sequenceNumber = headers.getSequenceNumber();
        var subscription = headers.getSubscription();

        try {
            var responseString = "";

            logger.info("[{}][Frame({}):Metadata] destination={}, sentAt={}, snapshot={}, publishingMode={}, sequenceNumber={}",
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

                SimpleCacheStorage.getInstance()
                        .setCache(typeClass.getName(), message, false);

                responseString = objectMapper.writeValueAsString(message);
                logger.info("[{}][Frame({}):ResponseType] {}", clientTarget, subscription, typeClass);
            } else {
                responseString = new String(payloadBytes, StandardCharsets.UTF_8);
                logger.info("[{}][Frame({}):ResponseType] {}", clientTarget, subscription, String.class.getName());
            }

            // Trimming response content
            responseString = responseString.length() > 250
                    ? responseString.substring(0, 250) + "..."
                    : responseString;

            logger.info("[{}][Frame({}):Response] {}", clientTarget, subscription, responseString);
        }
        catch (Exception e) {
            logger.error("[{}][Frame({}):Error] {}", clientTarget, subscription, e.getMessage());
        }
    }
}