package nps.id.publicapi.java.client.connection.options;

public interface WebSocketOptions
{
    boolean isUseSsl();
    int getPort();
    int getSslPort();
    String getHost();
    String getUri();
    int getHeartbeatOutgoingInterval();
    int getMaxTextMessageSize();
    int getMaxBinaryMessageSize();

    int getUsedPort();
}



