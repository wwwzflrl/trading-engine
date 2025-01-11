package app.trading.engine.core.api.messaging.publisher.expections;

public class OversizePayload extends PublishFailure {
    public static final OversizePayload OVERSIZE_PAYLOAD = new OversizePayload("Payload size is over limit");

    protected OversizePayload(final String message) { super(message); }
}