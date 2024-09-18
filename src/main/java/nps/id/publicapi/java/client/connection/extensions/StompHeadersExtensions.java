package nps.id.publicapi.java.client.connection.extensions;

import nps.id.publicapi.java.client.connection.enums.PublishingMode;
import nps.id.publicapi.java.client.connection.messages.Headers;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class StompHeadersExtensions {
    public static boolean isSnapshot(StompHeaders stompHeaders) {
        var value = stompHeaders.getFirst(Headers.Server.IsSnapshot);
        return value.equals("true");
    }

    public static PublishingMode getPublishingMode(StompHeaders stompHeaders) {
        var value = stompHeaders.getFirst(Headers.Destination);
        return value.contains("/streaming")
                ? PublishingMode.STREAMING
                : PublishingMode.CONFLATED;
    }

    public static String getSequenceNumber(StompHeaders stompHeaders) {
        return stompHeaders.getFirst(Headers.Server.SequenceNumber);
    }

    public static LocalDateTime getSentAt(StompHeaders stompHeaders) {
        var value = stompHeaders.getFirst(Headers.Server.SentAt);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(value)), ZoneId.of("UTC"));
    }
}
