package app.trading.engine.core.api.tag;

import app.trading.engine.core.api.expection.Failure;

public class NoSuchTag extends Failure {
    public static final NoSuchTag NO_SUCH_TAG = new NoSuchTag("Requested Tag doesn't exist");

    protected NoSuchTag(final String message) { super(message);}
}
