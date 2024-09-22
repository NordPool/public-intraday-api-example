package nps.id.publicapi.java.client.connection.messages;

public final class Headers {
    private Headers() {}

    public static final String HEART_BEAT = "heart-beat";
    public static final String DESTINATION = "destination";
    public static final String CONTENT_TYPE = "content-type";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_LENGTH = "content-length";

    public static final class Client {
        private Client() { }

        public static final String AUTHORIZATION_TOKEN = "X-AUTH-TOKEN";
        public static final String ACCEPT_VERSION = "accept-version";
        public static final String SUBSCRIPTION_ID = "id";
    }

    public static final class Server {
        private Server() { }

        public static final String VERSION = "version";
        public static final String MESSAGE = "message";
        public static final String IS_SNAPSHOT = "x-nps-snapshot";
        public static final String SUBSCRIPTION = "subscription";
        public static final String SEQUENCE_NUMBER = "x-nps-sequenceNo";
        public static final String SENT_AT = "x-nps-sent-at";
    }
}
