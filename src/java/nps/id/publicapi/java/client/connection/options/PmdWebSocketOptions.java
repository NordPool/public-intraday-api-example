package nps.id.publicapi.java.client.connection.options;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PmdWebSocketOptions implements WebSocketOptions {
    @Value("${pmd.web.socket.useSsl}")
    private boolean useSsl;
    @Value("${pmd.web.socket.port}")
    private int port;
    @Value("${pmd.web.socket.sslPort}")
    private int sslPort;
    @Value("${pmd.web.socket.host}")
    private String host;
    @Value("${pmd.web.socket.uri}")
    private String uri;
    @Value("${pmd.heartbeat.outgoing.interval}")
    private int heartbeatOutgoingInterval;
    @Value("${pmd.max.text.message.size}")
    private int maxTextMessageSize;
    @Value("${pmd.max.binary.message.size}")
    private int maxBinaryMessageSize;

    @Override
    public int getUsedPort() {
        return this.isUseSsl() ? this.getSslPort() : this.getPort();
    }
}