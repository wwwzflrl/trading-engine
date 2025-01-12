package app.trading.engine.infra.duty.cycle.util;

import app.trading.engine.infra.duty.cycle.common.DutyCycleConfig;
import net.openhft.affinity.AffinityLock;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class DutyCycleThreadFactory implements ThreadFactory {
    private final DutyCycleConfig config;

    public DutyCycleThreadFactory(DutyCycleConfig config) {
        this.config = config;
    }

    @Override
    public Thread newThread(@NotNull Runnable runnable) {
        final var thread = new Thread(decorate(runnable));
        thread.setPriority(config.priority);
        return thread;
    }

    Runnable decorate(final Runnable runnable) {
        return () -> {
            // TODO: understand AffinityLock usage, seems to bind to cpu
            // https://github.com/OpenHFT/Java-Thread-Affinity
            // https://blog.csdn.net/w57685321/article/details/111350424
            // https://blog.csdn.net/shenwansangz/article/details/50297637
            https://github.com/peter-lawrey/Java-Thread-Affinity/wiki/Getting-started
            try (final var ignored = AffinityLock.acquireLock(config.affinity)) {
                runnable.run();
            }
        };
    }
}
