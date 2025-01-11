package app.trading.engine.core.api.messaging.publisher.expections;

public class NotConnected extends PublishFailure {
    public static final NotConnected NOT_CONNECTED = new NotConnected("Session isn't connected");

    protected NotConnected(final String message) { super(message); }
}