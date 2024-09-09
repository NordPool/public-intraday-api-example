package nps.id.publicapi.java.client.connection.clients;

import nps.id.publicapi.java.client.connection.WebSocketConnector;
import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.options.WebSocketOptions;
import nps.id.publicapi.java.client.security.SsoService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class StompClientFactory {

    private final TaskScheduler heartBeatScheduler;
    private final SsoService ssoService;

    public StompClientFactory(TaskScheduler heartBeatScheduler, SsoService ssoService) {
        this.heartBeatScheduler = heartBeatScheduler;
        this.ssoService = ssoService;
    }

    public StompClient create(WebSocketClientTarget clientTarget, String clientId, WebSocketOptions webSocketOptions) {
        var webSocketConnector = new WebSocketConnector(heartBeatScheduler, ssoService, webSocketOptions);
        return new StompClient(webSocketConnector, clientTarget, clientId);
    }
}
