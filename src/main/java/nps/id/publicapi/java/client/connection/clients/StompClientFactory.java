package nps.id.publicapi.java.client.connection.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import nps.id.publicapi.java.client.connection.WebSocketConnector;
import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;
import nps.id.publicapi.java.client.connection.options.WebSocketOptions;
import nps.id.publicapi.java.client.connection.storage.SimpleCacheStorage;
import nps.id.publicapi.java.client.security.SsoService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class StompClientFactory {

    private final TaskScheduler heartBeatScheduler;
    private final SsoService ssoService;
    private final SimpleCacheStorage simpleCacheStorage;
    private final ObjectMapper objectMapper;
    private final String clientId;

    public StompClientFactory(SimpleCacheStorage simpleCacheStorage, ObjectMapper objectMapper, TaskScheduler heartBeatScheduler, SsoService ssoService, String clientId) {
        this.simpleCacheStorage = simpleCacheStorage;
        this.objectMapper = objectMapper;
        this.heartBeatScheduler = heartBeatScheduler;
        this.ssoService = ssoService;
        this.clientId = clientId;
    }

    public StompClient create(WebSocketClientTarget clientTarget, WebSocketOptions webSocketOptions) {
        var webSocketConnector = new WebSocketConnector(heartBeatScheduler, ssoService, webSocketOptions);
        return new StompClient(simpleCacheStorage, objectMapper, webSocketConnector, clientTarget, clientId);
    }
}
