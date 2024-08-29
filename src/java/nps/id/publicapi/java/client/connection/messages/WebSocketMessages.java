package nps.id.publicapi.java.client.connection.messages;

import java.nio.charset.StandardCharsets;

public final class WebSocketMessages {
    private WebSocketMessages() {}

    public static final byte[] NewLine = "\\n".getBytes(StandardCharsets.UTF_8);
    public static final byte[] SockJsStart = "o".getBytes(StandardCharsets.UTF_8);
    public static final byte[] ServerHeartBeat = "h".getBytes(StandardCharsets.UTF_8);
    public static final byte[] ClientHeartBeat = "[\"\\n\"]".getBytes(StandardCharsets.UTF_8);
    public static final byte[] DisconnectCode = "[\"1000\"]".getBytes(StandardCharsets.UTF_8);
    public static final byte[] ConnectedPrefix = "a[\"CONNECTED".getBytes(StandardCharsets.UTF_8);
    public static final byte[] MessagePrefix = "a[\"MESSAGE".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Logout = "[\"SEND\\ndestination:/v1/command\\ncontent-length:17\\n\\n{\\\"type\\\":\\\"LOGOUT\\\"}\\u0000\"]".getBytes(StandardCharsets.UTF_8);
}
