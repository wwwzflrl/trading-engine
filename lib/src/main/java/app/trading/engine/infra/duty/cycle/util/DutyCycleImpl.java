package app.trading.engine.infra.duty.cycle.util;

import app.trading.engine.core.api.DutyCycleTask;
import app.trading.engine.core.api.HouseKeeper;
import app.trading.engine.core.collections.UnsafeArrayList;
import org.agrona.collections.Object2LongHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static app.trading.engine.core.api.config.StaticConfigs.CONF;
import static app.trading.engine.infra.duty.cycle.util.Tasks.attempt;

public class DutyCycleImpl extends AbstractDutyCycle implements DutyCycleTask {
    private static final Logger log = LogManager.getLogger(DutyCycleImpl.class);
    private final String name;
    private final List<DutyCycleTask> tasks = new UnsafeArrayList<>();
    private final Object2LongHashMap<HouseKeeper> housekeepers = new Object2LongHashMap<>(-1);

    public DutyCycleImpl(String name, final DutyCycleTask... tasks) {
        this.name = name;
        for (final var task : tasks) {
            if (task instanceof HouseKeeper) {
                housekeepers.put((HouseKeeper) task, 0);
            } else {
                this.tasks.add(task);
            }
        }
        log.info(CONF, "MainDutyCycle created name:[{}], tasks:[{}], houseKeeper:[{}]", name, this.tasks, this.housekeepers);
    }

    @Override
    public boolean doWork(long nowNs) {
        checkDutyCycleTime(nowNs);
        didSomeWork = false;
        for (final var task : tasks) {
            didSomeWork |= attempt(task, nowNs);
        }
        if (didSomeWork) {
            executeOverdueHouseKeepers(nowNs);
        } else {
            attemptHouseKeepers(nowNs);
        }
        return didSomeWork;
    }

    private void executeOverdueHouseKeepers(final long nowNs) {
        final var iterator = housekeepers.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            final var houseKeeper = iterator.getKey();
            final var schedule = iterator.getLongValue();
            if (nowNs >= schedule && isDone(houseKeeper, nowNs)) {
                setNextSchedule(houseKeeper, iterator, nowNs);
            }
        }
    }

    private static boolean isDone(final HouseKeeper houseKeeper, final long nowNs) {
        return !attempt(houseKeeper, nowNs) || houseKeeper.done();
    }

    private static void setNextSchedule(final HouseKeeper houseKeeper, final Object2LongHashMap<HouseKeeper>.EntryIterator iterator, final long nowNs) {
        iterator.setValue(nowNs + houseKeeper.maxIntervalNs());
    }

    private void attemptHouseKeepers(final long nowNs) {
        final var iterator = housekeepers.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            final var houseKeeper = iterator.getKey();
            if (attempt(houseKeeper, nowNs)) {
                didSomeWork = true;
                if (houseKeeper.done()) {
                    setNextSchedule(houseKeeper, iterator, nowNs);
                }
            } else  {
                setNextSchedule(houseKeeper, iterator, nowNs);
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
