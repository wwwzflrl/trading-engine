package app.trading.engine.infra.duty.cycle.standalone;

import app.trading.engine.core.api.expection.ShutdownRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.LockSupport;

import static app.trading.engine.infra.duty.cycle.standalone.StandaloneDutyCycleContextConfiguration.mainDutyCycle;

class MainDutyCycleExecutor implements TaskExecutor, ApplicationListener<ContextClosedEvent>, AutoCloseable, PriorityOrdered {
    private static final Logger log = LogManager.getLogger(MainDutyCycleExecutor.class);
    private final ConfigurableApplicationContext context;
    private final ThreadFactory factory;

    MainDutyCycleExecutor(ConfigurableApplicationContext context, ThreadFactory factory) {
        this.context = context;
        this.factory = factory;
    }

    @Override
    public void close() throws Exception {
        doClose();
    }

    @Override
    public void onApplicationEvent(final @NotNull ContextClosedEvent event) {
        doClose();
    }

    private synchronized void doClose() {
        if (mainDutyCycle != null) {
            mainDutyCycle.interrupt();
            if (Thread.currentThread() != mainDutyCycle) {
                awaitQuiescence();
            }
        }
    }

    private static void awaitQuiescence() {
        while (true) {
            if (mainDutyCycle.isAlive()) {
                log.info("Waiting quiescence of main dutycycle thread");
                LockSupport.parkNanos(1_000_000_000L);
            } else {
                log.info("successfully stopped main-duty-cycle thread");
                mainDutyCycle = null;
                return;
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void execute(Runnable task) {
        if (mainDutyCycle == null) {
            mainDutyCycle = factory.newThread(task);
            mainDutyCycle.setName("main-duty-cycle");
            mainDutyCycle.setUncaughtExceptionHandler((thread, throwable) -> {
                if (throwable instanceof ShutdownRequest) {
                    log.info("main-duty-cycle is terminating per request", throwable);
                } else {
                    log.fatal("main-duty-cycle thread is dead", throwable);
                }
                context.close();
            });
            mainDutyCycle.start();
        } else {
            throw new IllegalStateException("Double submission of main-duty-cycle");
        }
    }
}
