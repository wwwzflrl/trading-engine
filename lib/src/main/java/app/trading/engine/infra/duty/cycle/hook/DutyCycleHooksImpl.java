package app.trading.engine.infra.duty.cycle.hook;

import app.trading.engine.core.api.DutyCycleTask;
import org.agrona.collections.ObjectHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

import static app.trading.engine.infra.duty.cycle.util.Tasks.attempt;

public class DutyCycleHooksImpl implements DutyCycleHooks, DutyCycleTask {
    private static final Logger log = LogManager.getLogger(DutyCycleHooksImpl.class);
    private final Set<DutyCycleTask> tasks = new ObjectHashSet<>();
    @Override
    public boolean doWork(long nowNs) {
        var didSomeWork = false;
        for (final DutyCycleTask task: tasks) {
            didSomeWork |= attempt(task, nowNs);
        }
        return didSomeWork;
    }

    @Override
    public void register(DutyCycleTask task) {
        if (tasks.add(task)) {
            log.info("Task register - task:[{}]", task);
        }
    }

    @Override
    public void remove(DutyCycleTask task) {
        if (tasks.remove(task)) {
            log.info("Task removed - task:[{}]", task);
        }
    }

    @Override
    public String toString() { return "tasks"; }
}
