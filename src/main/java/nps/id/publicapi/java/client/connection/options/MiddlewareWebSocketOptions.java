package nps.id.publicapi.java.client.connection.options;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MiddlewareWebSocketOptions implements WebSocketOptions {
    @Value("${middleware.web.socket.useSsl}")
    private boolean useSsl;
    @Value("${middleware.web.socket.port}")
    private int port;
    @Value("${middleware.web.socket.sslPort}")
    private int sslPort;
    @Value("${middleware.web.socket.host}")
    private String host;
    @Value("${middleware.web.socket.uri}")
    private String uri;
    @Value("${middleware.heartbeat.outgoing.interval}")
    private int heartbeatOutgoingInterval;
    @Value("${middleware.max.text.message.size}")
    private int maxTextMessageSize;
    @Value("${middleware.max.binary.message.size}")
    private int maxBinaryMessageSize;

    @Override
    public int getUsedPort() {
        return this.isUseSsl() ? this.getSslPort() : this.getPort();
    }
}