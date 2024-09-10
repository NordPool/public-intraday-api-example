package nps.id.publicapi.java.client.connection;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nordpool.id.publicapi.v1.command.Command;
import com.nordpool.id.publicapi.v1.command.CommandType;
import com.nordpool.id.publicapi.v1.command.TokenRefreshCommand;
import jakarta.websocket.ContainerProvider;
import nps.id.publicapi.java.client.connection.messages.StompMessageFactory;
import nps.id.publicapi.java.client.connection.options.WebSocketOptions;
import lombok.Getter;
import nps.id.publicapi.java.client.security.SsoService;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.ee10.websocket.jakarta.client.JakartaWebSocketClientContainer;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class WebSocketConnector {
    private static final Logger logger = LogManager.getLogger(WebSocketConnector.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SecureWebSocketProtocol = "wss";
    private static final String UnsecureWebSocketProtocol = "ws";

    private final TaskScheduler heartBeatScheduler;
    private final SsoService ssoService;

    private final WebSocketOptions webSocketOptions;
    private final WebSocketStompClient webSocketStompClient;

    @Getter
    private StompSession stompSession;

    public WebSocketConnector(TaskScheduler heartBeatScheduler, SsoService ssoService, WebSocketOptions webSocketOptions) {
        this.heartBeatScheduler = heartBeatScheduler;
        this.ssoService = ssoService;
        this.webSocketOptions = webSocketOptions;

        var webSocketContainer = (JakartaWebSocketClientContainer) ContainerProvider.getWebSocketContainer();
        webSocketContainer.setDefaultMaxSessionIdleTimeout(100000);
        webSocketContainer.setDefaultMaxTextMessageBufferSize(webSocketOptions.getMaxTextMessageSize());
        webSocketContainer.setDefaultMaxBinaryMessageBufferSize(webSocketOptions.getMaxBinaryMessageSize());
        var sockJsClient = new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient(webSocketContainer))));
        webSocketStompClient = new WebSocketStompClient(sockJsClient);

        webSocketStompClient.setMessageConverter(new SimpleMessageConverter());
        webSocketStompClient.setInboundMessageSizeLimit(webSocketOptions.getMaxTextMessageSize());
        webSocketStompClient.setDefaultHeartbeat(new long[]{webSocketOptions.getHeartbeatOutgoingInterval(), 0L});
    }

    public void connect() {
        if (webSocketOptions.getHeartbeatOutgoingInterval() > 0) {
            webSocketStompClient.setTaskScheduler(heartBeatScheduler);
        }

        try {
            var uri = constructBaseUri();
            var authToken = ssoService.getAuthToken();
            var connectionHeaders = StompMessageFactory.connectionHeaders(authToken, webSocketOptions.getHeartbeatOutgoingInterval());
            var completableFuture = webSocketStompClient.connectAsync(uri, null, connectionHeaders, new StompSessionHandlerAdapterImpl());
            stompSession = completableFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public boolean isConnected() {
        return stompSession != null && stompSession.isConnected();
    }

    private URI constructBaseUri() throws URISyntaxException {
        var webSocketProtocol = webSocketOptions.isUseSsl() ? SecureWebSocketProtocol : UnsecureWebSocketProtocol;
        var webSocketPort = webSocketOptions.getUsedPort();
        var constructedUri = webSocketProtocol + "://"
                + webSocketOptions.getHost() + ":"
                + webSocketPort
                + webSocketOptions.getUri();
        return new URI(constructedUri);
    }

    public void send(StompHeaders stompHeaders, Object payload) {
        try {
            var payloadBytes = objectMapper.writeValueAsBytes(payload);
            stompSession.send(stompHeaders, payloadBytes);
        } catch (Exception e) {
            logger.error("[SESSION:{}] : An error occurred during sending payload, details: {}", stompSession.getSessionId(), e.getMessage());
        }
    }

    public void logout() {
        try {
            var logoutHeaders = StompMessageFactory.sendHeaders("/v1/command");
            var logoutCommand = new Command(CommandType.LOGOUT);
            var logoutCommandPayload = objectMapper.writeValueAsBytes(logoutCommand);
            stompSession.send(logoutHeaders, logoutCommandPayload);
        } catch (Exception e) {
            logger.error("[SESSION:{}] : An error occurred during logout command, details: {}", stompSession.getSessionId(), e.getMessage());
        }
    }

    public void disconnect() {
        stompSession.disconnect();
    }

    private void periodicallyRefreshToken() throws Exception {
        while (stompSession.isConnected()) {
            var currentAuthToken = ssoService.getCurrentAuthToken();
            var jwt = JWT.decode(currentAuthToken);
            var expirationDate = jwt.getExpiresAt();
            var refreshPeriod = DateUtils.addMinutes(expirationDate, -5);
            var duration = Duration.ofMillis(refreshPeriod.getTime() - new Date().getTime());
            TimeUnit.SECONDS.sleep(duration.getSeconds());

            refreshAccessToken();
        }
    }

    private void refreshAccessToken() throws Exception {
        var previousAuthToken = ssoService.getCurrentAuthToken();
        var currentAuthToken = ssoService.getAuthToken();
        var refreshTokenCommand = new TokenRefreshCommand(previousAuthToken, currentAuthToken, CommandType.TOKEN_REFRESH);
        var refreshTokenCommandPayload = objectMapper.writeValueAsBytes(refreshTokenCommand);
        var refreshTokenHeaders = StompMessageFactory.sendHeaders("/v1/command");
        stompSession.send(refreshTokenHeaders, refreshTokenCommandPayload);
    }

    private final class StompSessionHandlerAdapterImpl extends StompSessionHandlerAdapter {

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            logger.info("[SESSION:{}] Connected successfully", session.getSessionId());
            stompSession = session;

            Runnable runnable = () -> {
                try {
                    periodicallyRefreshToken();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            CompletableFuture.runAsync(runnable);
        }

        @Override
        public void handleException(StompSession session, StompCommand command,
                                    StompHeaders headers, byte[] payload, Throwable exception) {
            logger.error("[SESSION:{}] STOMP exception: {}", session.getSessionId(), exception.getMessage());
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            if (exception instanceof ClosedChannelException || !session.isConnected()) {
                logger.error("[SESSION:{}] Unable to handle message, connection is in closing/closed state", session.getSessionId());
                return;
            }

            if (exception.getMessage().equals("Connection closed")) {
                logger.info("[SESSION:{}] Connection closed", session.getSessionId());
                return;
            }

            logger.error("[SESSION:{}] Unable to handle message due to occurred the error", session.getSessionId(), exception);
        }
    }
}