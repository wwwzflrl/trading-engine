package app.trading.engine.core.math;

public final class HashCodes {

    /**
     * hash * 31
     */
    public static int hashShift(final int code) {
        return (code << 5) - code;
    }

    public static int hash(final int first, final int second) { return hashShift(first) ^ second; }

    private HashCodes() {};
}
