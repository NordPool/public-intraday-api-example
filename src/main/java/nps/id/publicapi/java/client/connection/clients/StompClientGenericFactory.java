package nps.id.publicapi.java.client.connection.clients;

import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.options.MarketDataWebSocketOptions;
import nps.id.publicapi.java.client.connection.options.TradingWebSocketOptions;
import org.springframework.stereotype.Service;

@Service
public class StompClientGenericFactory {
    private final TradingWebSocketOptions tradingWebSocketOptions;
    private final MarketDataWebSocketOptions webSocketOptions;

    private final StompClientFactory stompClientFactory;

    public StompClientGenericFactory(StompClientFactory stompClientFactory, TradingWebSocketOptions tradingWebSocketOptions, MarketDataWebSocketOptions webSocketOptions) {
        this.stompClientFactory = stompClientFactory;
        this.tradingWebSocketOptions = tradingWebSocketOptions;
        this.webSocketOptions = webSocketOptions;
    }

    public StompClient create(String clientId, WebSocketClientTarget target) {
        var client =  switch (target) {
            case TRADING -> stompClientFactory.create(WebSocketClientTarget.TRADING, clientId, tradingWebSocketOptions);
            case MARKET_DATA -> stompClientFactory.create(WebSocketClientTarget.MARKET_DATA, clientId, webSocketOptions);
            default -> throw new UnsupportedOperationException();
        };

        client.open();

        return client;
    }
}