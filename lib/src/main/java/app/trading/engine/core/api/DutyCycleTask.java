package app.trading.engine.core.api;

public interface DutyCycleTask {
    boolean doWork(long nowNs);

    @API
    boolean NOT_LATENCY_CRITICAL = false;
}
