package nps.id.publicapi.java.client.connection.messages;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.util.MimeType;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

public final class StompMessageFactory {
    private StompMessageFactory() {}

    public static StompHeaders connectionHeaders(String authToken, long heartbeatOutgoingInterval)
    {
        var stompHeaders = new StompHeaders();
        stompHeaders.put(Headers.Client.ACCEPT_VERSION, Arrays.asList("1.2","1.1","1.0"));
        stompHeaders.put(Headers.Client.AUTHORIZATION_TOKEN, Collections.singletonList(authToken));
        stompHeaders.put(Headers.HEART_BEAT, Collections.singletonList("0," + heartbeatOutgoingInterval));
        return stompHeaders;
    }

    public static StompHeaders subscribeHeaders(String destination, String id) {
        var stompHeaders = new StompHeaders();
        stompHeaders.setDestination(destination);
        stompHeaders.setId(id);
        return  stompHeaders;
    }

    public static StompHeaders sendHeaders(String destination) {
        var contentType = new MimeType("application", "json", StandardCharsets.UTF_8);

        var stompHeaders = new StompHeaders();
        stompHeaders.setDestination(destination);
        stompHeaders.setContentType(contentType);
        return  stompHeaders;
    }
}
