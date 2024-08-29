package nps.id.publicapi.java.client.connection.clients;

import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.options.EdgeWebSocketOptions;
import nps.id.publicapi.java.client.connection.options.MiddlewareWebSocketOptions;
import org.springframework.stereotype.Service;

@Service
public class StompClientGenericFactory {
    private final MiddlewareWebSocketOptions middlewareWebSocketOptions;
    private final EdgeWebSocketOptions edgeWebSocketOptions;

    private final StompClientFactory stompClientFactory;

    public StompClientGenericFactory(StompClientFactory stompClientFactory, MiddlewareWebSocketOptions middlewareWebSocketOptions, EdgeWebSocketOptions edgeWebSocketOptions) {
        this.stompClientFactory = stompClientFactory;
        this.middlewareWebSocketOptions = middlewareWebSocketOptions;
        this.edgeWebSocketOptions = edgeWebSocketOptions;
    }

    public StompClient create(String clientId, WebSocketClientTarget target) {
        var client =  switch (target) {
            case MIDDLEWARE -> stompClientFactory.create(WebSocketClientTarget.MIDDLEWARE, clientId, middlewareWebSocketOptions);
            case EDGE -> stompClientFactory.create(WebSocketClientTarget.EDGE, clientId, edgeWebSocketOptions);
            default -> throw new UnsupportedOperationException();
        };

        client.open();

        return client;
    }
}