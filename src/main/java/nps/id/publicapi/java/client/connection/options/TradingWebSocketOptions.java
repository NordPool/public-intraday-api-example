package nps.id.publicapi.java.client.connection.options;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class TradingWebSocketOptions implements WebSocketOptions {
    @Value("${trading.web.socket.useSsl}")
    private boolean useSsl;
    @Value("${trading.web.socket.port}")
    private int port;
    @Value("${trading.web.socket.sslPort}")
    private int sslPort;
    @Value("${trading.web.socket.host}")
    private String host;
    @Value("${trading.web.socket.uri}")
    private String uri;
    @Value("${trading.heartbeat.outgoing.interval}")
    private int heartbeatOutgoingInterval;
    @Value("${trading.max.text.message.size}")
    private int maxTextMessageSize;
    @Value("${trading.max.binary.message.size}")
    private int maxBinaryMessageSize;

    @Override
    public int getUsedPort() {
        return this.isUseSsl() ? this.getSslPort() : this.getPort();
    }
}