package nps.id.publicapi.java.client.connection.options;

public interface WebSocketOptions
{
    int getSslPort();
    String getHost();
    String getUri();
    int getHeartbeatOutgoingInterval();
    int getMaxTextMessageSize();
    int getMaxBinaryMessageSize();
    boolean isEnablePermessageConflated();
}



