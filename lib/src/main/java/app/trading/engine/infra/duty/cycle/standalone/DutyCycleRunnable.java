package app.trading.engine.infra.duty.cycle.standalone;

import app.trading.engine.core.api.DutyCycleTask;
import app.trading.engine.core.api.time.Clock;
import org.agrona.concurrent.IdleStrategy;

import static java.lang.Thread.currentThread;

final class DutyCycleRunnable implements Runnable {
    private final Clock clock;
    private final DutyCycleTask task;
    private final IdleStrategy idler;

    DutyCycleRunnable(Clock clock, DutyCycleTask task, IdleStrategy idler) {
        this.clock = clock;
        this.task = task;
        this.idler = idler;
    }

    @Override
    public void run() {
        final var thread = currentThread();
        System.gc(); // Force a gc before we start the duty cycle;
        while (true) {
            if (task.doWork(clock.epochNs())) {
                idler.reset();
            } else {
                idler.idle();
            }
        }
    }
}
