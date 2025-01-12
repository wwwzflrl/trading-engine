package app.trading.engine.infra.duty.cycle.standalone;

import app.trading.engine.core.api.HouseKeeper;
import app.trading.engine.core.api.expection.ShutdownRequest;

import static app.trading.engine.infra.duty.cycle.standalone.StandaloneDutyCycleContextConfiguration.mainDutyCycle;

public class ThreadInterruptChecker implements HouseKeeper {
    @Override
    public long maxIntervalNs() { return 1_000_000_000L; }

    @Override
    public boolean done() {
        return true;
    }

    @Override
    public boolean doWork(long nowNs) {
        if (mainDutyCycle.isInterrupted()) {
            throw new ShutdownRequest("main-duty-cycle interrupted");
        }
        return false;
    }
}
