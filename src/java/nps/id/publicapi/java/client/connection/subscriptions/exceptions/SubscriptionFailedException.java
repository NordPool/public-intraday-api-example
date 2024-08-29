package nps.id.publicapi.java.client.connection.subscriptions.exceptions;

public class SubscriptionFailedException extends Exception {
    public SubscriptionFailedException(final String message) {
        super(message);
    }

    public SubscriptionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
