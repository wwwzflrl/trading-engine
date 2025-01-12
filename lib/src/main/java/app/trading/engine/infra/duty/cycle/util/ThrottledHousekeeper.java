package app.trading.engine.infra.duty.cycle.util;

import app.trading.engine.core.api.HouseKeeper;

public abstract class ThrottledHousekeeper implements HouseKeeper {
    protected final long minDelayNs;
    protected long nextNs;

    protected ThrottledHousekeeper(long minDelayNs) {
        this.minDelayNs = minDelayNs;
    }

    @Override
    public boolean doWork(long nowNs) {
        if (nowNs < nextNs) {
            return false;
        }
        if (doActualWork(nowNs)) {
            if (done()) {
                setNextSchedule(nowNs);
            }
            return true;
        }
        setNextSchedule(nowNs);
        return false;
    }

    private void setNextSchedule(final long nowNs) { nextNs = nowNs + minDelayNs; }

    protected abstract boolean doActualWork(final long nowNs);
}
