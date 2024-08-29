package nps.id.publicapi.java.client.connection.messages;

public final class Commands {
    private Commands() {}

    public static final class Client {
        private Client() { }

        public static final String Connect = "CONNECT";
        public static final String Disconnect = "DISCONNECT";
        public static final String Send = "SEND";
        public static final String Subscribe = "SUBSCRIBE";
        public static final String Unsubscribe = "UNSUBSCRIBE";
        public static final String KeepAlive = "KEEPALIVE";
    }

    public static final class Server {
        private Server() { }

        public static final String Connected = "CONNECTED";
        public static final String Message = "MESSAGE";
        public static final String Error = "ERROR";
        public static final String Receipt = "RECEIPT";
    }
}
