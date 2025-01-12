package app.trading.engine.core.api;

/**
 * HouseKeeper will be executed in the following case:
 * 1. task is overdue
 * 2. No normal did any workding during this duty cycle
 */
public interface HouseKeeper extends DutyCycleTask {
    long maxIntervalNs();
    boolean done();
}
