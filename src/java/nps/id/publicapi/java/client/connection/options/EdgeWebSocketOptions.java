package nps.id.publicapi.java.client.connection.options;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EdgeWebSocketOptions implements WebSocketOptions {
    @Value("${edge.web.socket.useSsl}")
    private boolean useSsl;
    @Value("${edge.web.socket.port}")
    private int port;
    @Value("${edge.web.socket.sslPort}")
    private int sslPort;
    @Value("${edge.web.socket.host}")
    private String host;
    @Value("${edge.web.socket.uri}")
    private String uri;
    @Value("${edge.heartbeat.outgoing.interval}")
    private int heartbeatOutgoingInterval;
    @Value("${edge.max.text.message.size}")
    private int maxTextMessageSize;
    @Value("${edge.max.binary.message.size}")
    private int maxBinaryMessageSize;

    @Override
    public int getUsedPort() {
        return this.isUseSsl() ? this.getSslPort() : this.getPort();
    }
}