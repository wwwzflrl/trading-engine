package app.trading.engine.core.api.time;

import app.trading.engine.core.api.DutyCycleTask;

public interface GenericScheduler<T extends DutyCycleTask> {
    Object schedule(T task, long epochNs, long intervalNs);

    default Object schedule(T task, long epochNs) { return schedule(task, epochNs, 0); }

    boolean cancel(Object schedule);

    boolean reschedule(Object schedule, long epochNs, long periodNs);
}
