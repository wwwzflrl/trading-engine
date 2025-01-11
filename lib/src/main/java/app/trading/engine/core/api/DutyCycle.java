package app.trading.engine.core.api;

public interface DutyCycle extends DutyCycleTask {
    long worked();
    long total();
    long maxCycleTimeNs();
    void resetMetrics();
}
