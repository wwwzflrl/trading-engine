package app.trading.engine.infra.duty.cycle.util;

import app.trading.engine.core.api.DutyCycle;
import app.trading.engine.core.api.DutyCycleTask;
import app.trading.engine.core.api.expection.NotRelevant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static app.trading.engine.core.api.config.StaticConfigs.*;
import static java.util.Arrays.stream;
import static org.agrona.LangUtil.rethrowUnchecked;
import static org.apache.logging.log4j.util.Unbox.box;

public final class Tasks {
    private static final Logger log = LogManager.getLogger(Tasks.class);

    static {
        if (CLOCK_TASKS) {
            log.warn(CONF, "Clocking tasks, expect some  moderate overhead");
        }
    }

    public static boolean attempt(final DutyCycleTask task, final long nowNs) {
        if (CLOCK_TASKS) {
            final long start = System.nanoTime();
            final var rc = attempt0(task, nowNs);
            final var elapsed = System.nanoTime() - start;
            if (elapsed > THRESHOLD_NS) {
                log.warn("Slow DutyCycleTask - name:[{}], elapsed:[{}us]", task, box(elapsed / 1000));
            }
            return rc;
        }
        return attempt0(task, nowNs);
    }

    private static boolean attempt0(final DutyCycleTask task, final long nowNs) {
        try {
            return task.doWork(nowNs);
        } catch (final NotRelevant ignored) {
            return true;
        } catch (final Exception e) {
            rethrowInterruptedException(e);
            log.error("Uncaught expection executing task [{}]", task, e);
            return true;
        }
    }

    public static void rethrowInterruptedException(final Exception e) {
        if (e instanceof InterruptedException) {
            rethrowUnchecked(e);
        }
    }

    public static DutyCycle aggregate(final String name, final DutyCycleTask... tasks) {
        return new DutyCycleImpl(name, tasks);
    }

    public static DutyCycleTask @NotNull [] filter(final Object... candidates) {
        return stream(candidates).filter(DutyCycleTask.class::isInstance).toArray(DutyCycleTask[]::new);
    }

    private Tasks() {}
}
