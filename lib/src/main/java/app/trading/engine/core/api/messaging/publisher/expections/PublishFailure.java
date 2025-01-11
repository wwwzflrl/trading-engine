package app.trading.engine.core.api.messaging.publisher.expections;

import app.trading.engine.core.api.expection.Failure;

public class PublishFailure extends Failure {
    public static final PublishFailure PUBLISH_FAILURE = new PublishFailure("General publish failure");

    protected PublishFailure(final String message) { super(message);}
}
