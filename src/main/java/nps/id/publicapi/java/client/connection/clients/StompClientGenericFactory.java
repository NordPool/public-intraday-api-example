package nps.id.publicapi.java.client.connection.clients;

import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.options.PmdWebSocketOptions;
import nps.id.publicapi.java.client.connection.options.MiddlewareWebSocketOptions;
import org.springframework.stereotype.Service;

@Service
public class StompClientGenericFactory {
    private final MiddlewareWebSocketOptions middlewareWebSocketOptions;
    private final PmdWebSocketOptions pmdWebSocketOptions;

    private final StompClientFactory stompClientFactory;

    public StompClientGenericFactory(StompClientFactory stompClientFactory, MiddlewareWebSocketOptions middlewareWebSocketOptions, PmdWebSocketOptions pmdWebSocketOptions) {
        this.stompClientFactory = stompClientFactory;
        this.middlewareWebSocketOptions = middlewareWebSocketOptions;
        this.pmdWebSocketOptions = pmdWebSocketOptions;
    }

    public StompClient create(String clientId, WebSocketClientTarget target) {
        var client =  switch (target) {
            case MIDDLEWARE -> stompClientFactory.create(WebSocketClientTarget.MIDDLEWARE, clientId, middlewareWebSocketOptions);
            case PMD -> stompClientFactory.create(WebSocketClientTarget.PMD, clientId, pmdWebSocketOptions);
            default -> throw new UnsupportedOperationException();
        };

        client.open();

        return client;
    }
}