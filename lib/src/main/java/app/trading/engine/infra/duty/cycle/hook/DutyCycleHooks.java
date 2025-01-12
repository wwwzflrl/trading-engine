package app.trading.engine.infra.duty.cycle.hook;

import app.trading.engine.core.api.DutyCycleTask;

public interface DutyCycleHooks {
    void register(DutyCycleTask task);

    void remove(DutyCycleTask task);
}
