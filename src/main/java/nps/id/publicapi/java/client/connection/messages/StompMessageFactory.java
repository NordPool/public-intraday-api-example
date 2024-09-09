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
        return new StompHeaders() {
            {
                put(Headers.Client.AcceptVersion, Arrays.asList("1.2","1.1","1.0"));
                put(Headers.Client.AuthorizationToken, Collections.singletonList(authToken));
                put(Headers.Heartbeat, Collections.singletonList("0," + heartbeatOutgoingInterval));
            }
        };
    }

    public static StompHeaders subscribeHeaders(String destination, String id) {
        return new StompHeaders() {
            {
                setDestination(destination);
                setId(id);
            }
        };
    }

    public static StompHeaders sendHeaders(String destination) {
        var contentType = new MimeType("application", "json", StandardCharsets.UTF_8);

        return new StompHeaders() {
            {
                setDestination(destination);
                setContentType(contentType);
            }
        };
    }
}
