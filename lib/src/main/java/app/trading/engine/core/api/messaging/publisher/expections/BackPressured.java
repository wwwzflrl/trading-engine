package app.trading.engine.core.api.messaging.publisher.expections;

public class BackPressured extends PublishFailure {
    public static final BackPressured BACK_PRESSURED = new BackPressured("Back-pressured by downstream");

    protected BackPressured(final String message) { super(message); }
}
