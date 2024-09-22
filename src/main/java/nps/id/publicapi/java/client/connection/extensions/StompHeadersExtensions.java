package nps.id.publicapi.java.client.connection.extensions;

import nps.id.publicapi.java.client.connection.enums.PublishingMode;
import nps.id.publicapi.java.client.connection.messages.Headers;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class StompHeadersExtensions {
    public static Boolean isSnapshot(StompHeaders stompHeaders) {
        var value = stompHeaders.containsKey(Headers.Server.IS_SNAPSHOT)
            ? stompHeaders.getFirst(Headers.Server.IS_SNAPSHOT)
            : null;

        return value != null
                ? value.equals("true")
                : null;
    }

    public static PublishingMode getPublishingMode(StompHeaders stompHeaders) {
        var value = stompHeaders.containsKey(Headers.DESTINATION)
                ? stompHeaders.getFirst(Headers.DESTINATION)
                : null;

        if (value == null) {
            return null;
        }

        return value.contains("/streaming")
                ? PublishingMode.STREAMING
                : PublishingMode.CONFLATED;
    }

    public static String getSequenceNumber(StompHeaders stompHeaders) {
        return stompHeaders.containsKey(Headers.Server.SEQUENCE_NUMBER)
                ? stompHeaders.getFirst(Headers.Server.SEQUENCE_NUMBER)
                : null;
    }

    public static ZonedDateTime getSentAt(StompHeaders stompHeaders) {
        var value = stompHeaders.containsKey(Headers.Server.SENT_AT)
                ? stompHeaders.getFirst(Headers.Server.SENT_AT)
                : null;

        return value != null
                ? ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(value)), ZoneId.of("UTC"))
                : null;
    }
}
