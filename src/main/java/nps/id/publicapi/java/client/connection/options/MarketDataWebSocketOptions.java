package nps.id.publicapi.java.client.connection.options;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MarketDataWebSocketOptions implements WebSocketOptions {
    @Value("${market.data.web.socket.sslPort}")
    private int sslPort;
    @Value("${market.data.web.socket.host}")
    private String host;
    @Value("${market.data.web.socket.uri}")
    private String uri;
    @Value("${market.data.heartbeat.outgoing.interval}")
    private int heartbeatOutgoingInterval;
    @Value("${market.data.max.text.message.size}")
    private int maxTextMessageSize;
    @Value("${market.data.max.binary.message.size}")
    private int maxBinaryMessageSize;
    @Value("${market.enable.permessage.conflated}")
    private boolean enablePermessageConflated;
}