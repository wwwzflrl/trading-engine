package app.trading.engine.core.api.expection;

/**
 * A runtime exception used to interrupt flow of execution by business logic
 */
public class FlowControl extends RuntimeException {
    public static final FlowControl FLOW_CONTROL = new FlowControl("Intended interruption of flow by logic");

    /**
     * Disable stack trace,
     * @param message
     */
    protected FlowControl(final String message) {
        super(message, null, false, false);
    }
}
