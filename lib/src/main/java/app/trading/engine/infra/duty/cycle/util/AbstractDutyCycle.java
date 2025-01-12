package app.trading.engine.infra.duty.cycle.util;

import app.trading.engine.core.api.DutyCycle;

public abstract class AbstractDutyCycle implements DutyCycle {
    protected boolean didSomeWork;
    protected long worked;
    protected long total;
    private long lastCycleStart;
    private long maxCycleTimeNs;

    protected void checkDutyCycleTime(final long nowNs) {
        final var diff = nowNs - lastCycleStart;
        lastCycleStart = nowNs;
        if (diff > maxCycleTimeNs) {
            maxCycleTimeNs = diff;
        }
        if (didSomeWork) {
            worked += diff;
        }
        total += diff;
    }

    @Override
    public long worked() {
        return worked;
    }

    @Override
    public long total() {
        return total;
    }

    @Override
    public long maxCycleTimeNs() {
        return maxCycleTimeNs;
    }

    @Override
    public void resetMetrics() {
        worked = 0;
        total = 0;
        maxCycleTimeNs = 0;
    }
}
