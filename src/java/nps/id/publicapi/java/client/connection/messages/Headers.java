package nps.id.publicapi.java.client.connection.messages;

public final class Headers {
    private Headers() {}

    public static final String Heartbeat = "heart-beat";
    public static final String Destination = "destination";
    public static final String ContentType = "content-type";
    public static final String Authorization = "Authorization";
    public static final String ContentLength = "content-length";

    public static final class Client {
        private Client() { }

        public static final String AuthorizationToken = "X-AUTH-TOKEN";
        public static final String AcceptVersion = "accept-version";
        public static final String SubscriptionId = "id";
    }

    public static final class Server {
        private Server() { }

        public static final String Version = "version";
        public static final String Message = "message";
        public static final String IsSnapshot = "x-nps-snapshot";
        public static final String Subscription = "subscription";
        public static final String SequenceNumber = "x-nps-sequenceNo";
        public static final String SentAt = "x-nps-sent-at";
    }
}
