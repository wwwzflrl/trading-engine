package app.trading.engine.core.api.expection;

/**
 * Indicate that certain message / request isn't relavant to the current context
 */
public class NotRelevant extends Failure {
    public static final NotRelevant NOT_RELEVANT = new NotRelevant("Intended for others");

    protected NotRelevant(final String message) {
        super(message);
    }
}
