package app.trading.engine.infra.duty.cycle.standalone;

import app.trading.engine.core.api.DutyCycle;
import app.trading.engine.core.api.time.Clock;
import app.trading.engine.infra.duty.cycle.common.DutyCycleConfig;
import app.trading.engine.infra.duty.cycle.util.IdlerFactory;
import org.agrona.concurrent.IdleStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.TaskExecutor;

public class MainDutyCycleStarter implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = LogManager.getLogger(MainDutyCycleStarter.class);

    @Override
    public void onApplicationEvent(final @NotNull ContextRefreshedEvent event) {
        final var context = event.getApplicationContext();
        final var executor = context.getBean("mainDutyCycleExecutor", TaskExecutor.class);
        final var cycle = context.getBean("mainDutyCycle", DutyCycle.class);
        final var config = context.getBean("commonDutyCycleConfig", DutyCycleConfig.class);
        final IdleStrategy idler = IdlerFactory.getIdler(config.idler);
        executor.execute(new DutyCycleRunnable(Clock.system(), cycle, idler));
        log.info("======== Main Duty Cycle Started =================");
    }
}
