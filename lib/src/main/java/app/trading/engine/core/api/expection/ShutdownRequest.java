package app.trading.engine.core.api.expection;

public class ShutdownRequest extends Error {
    public ShutdownRequest(final String message) { super(message);}

    public ShutdownRequest(final String message, final Throwable cause) { super(message, cause);}
}
