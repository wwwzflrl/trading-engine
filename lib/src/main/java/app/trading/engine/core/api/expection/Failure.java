package app.trading.engine.core.api.expection;

/**
 * A turntime execption used for indication of execution failure
 * A cached exception w/o static trace carries equivalent performance to return an error code
 */
public class Failure extends RuntimeException {
    public static final Failure FAILURE = new Failure("General failure to execute operation");

    /**
     * Disable stack trace,
     * @param message
     */
    protected Failure(final String message) {
        super(message, null, false, false);
    }
}
