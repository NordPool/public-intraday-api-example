/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example.service.connection;

import com.nordpool.intraday.publicapi.example.service.security.SSOService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.jsr356.ClientContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.annotation.PostConstruct;
import javax.websocket.ContainerProvider;
import java.io.IOException;
import java.util.Collections;

@Service
public class WebSocketConnector {
    private static final Logger LOGGER = LogManager.getLogger(WebSocketConnector.class);

    // Web Socket properties
    @Value("${web.socket.protocol}")
    private String protocol;
    @Value("${web.socket.host}")
    private String host;
    @Value("${web.socket.port}")
    private String port;
    @Value("${web.socket.uri}")
    private String uri;

    // Jetty properties
    @Value("${max.text.message.size}")
    private int maxTextMessageSize;

    @Autowired
    private SSOService ssoService;

    @Autowired
    private PropertyValidator validator;

    private StompSession mySession;

    @PostConstruct
    public void validateProperties() {
        validator.validate(protocol, "web.socket.protocol");
        validator.validate(host, "web.socket.host");
        validator.validate(port, "web.socket.port");
        validator.validate(uri, "web.socket.uri");
        validator.validate(maxTextMessageSize, "max.text.message.size");
    }

    public void connect()  {
        ClientContainer clientContainer = ((ClientContainer) ContainerProvider.getWebSocketContainer());
        clientContainer.getClient().getPolicy().setMaxTextMessageSize(maxTextMessageSize);
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(Collections.singletonList(
                new WebSocketTransport(new StandardWebSocketClient(clientContainer))
        )));
        stompClient.setMessageConverter(new SimpleMessageConverter());
        stompClient.setInboundMessageSizeLimit(maxTextMessageSize);

        String url = getUrl();
        LOGGER.info("Connecting via WebSocket to : " + host + ":" + port);
        try {
            stompClient.connect(url, null, getConnectHeaders(), new StompSessionHandlerAdapterImpl(), new Object[]{});
        } catch (IOException e) {
            LOGGER.error(e);
        }


    }


    public StompSession getSession() {
        return mySession;
    }


    private String getUrl() {
        return protocol + "://" + host + ":" + port + uri;
    }

    private StompHeaders getConnectHeaders() throws IOException {
        return new StompHeaders() {{
            put(StompHeaders.LOGIN, Collections.singletonList(""));
            put(StompHeaders.PASSCODE, Collections.singletonList(""));
            put("X-AUTH-TOKEN", Collections.singletonList(ssoService.getToken()));
        }};
    }

    private final class StompSessionHandlerAdapterImpl extends StompSessionHandlerAdapter {
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            LOGGER.info("Connected successfully, SessionID = " + session.getSessionId());
            mySession = session;
        }


        @Override
        public void handleException(StompSession session, StompCommand command,
                                    StompHeaders headers, byte[] payload, Throwable exception) {
            LOGGER.error("STOMP exception: " + exception.getMessage());
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            if (exception.getMessage().equals("Connection closed")) {
                LOGGER.info(exception.getMessage());
            } else {
                LOGGER.error(exception.getMessage(), exception);
            }
        }
    }
}
