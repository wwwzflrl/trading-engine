package app.trading.engine.infra.duty.cycle.standalone;

import app.trading.engine.core.api.DutyCycle;
import app.trading.engine.core.api.HouseKeeper;
import app.trading.engine.core.api.annotations.PartOfMainDutyCycle;
import app.trading.engine.infra.duty.cycle.common.DutyCycleThreadFactoryContextConfiguration;
import app.trading.engine.infra.duty.cycle.util.Tasks;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.TaskExecutor;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;

/**
 * Init Order: Config --> ThreadFactory --> TaskExecutor --> PartOfMainDutyCycle (DutyCycleImpl)
 * Start Order:
 *  MainDutyCycleStarter --> TaskExecutor.execute (bind cpu)
*                             --> DutyCycleRunnable (idle execute Impl)
 *                              --> DutyCycleImpl (execute PartOfMainDutyCycle
 *                                  ---> MainTasks or houseKeeper
 */
@Configuration
@Import({ DutyCycleThreadFactoryContextConfiguration.class})
public class StandaloneDutyCycleContextConfiguration {
    public static Thread mainDutyCycle;

    @Bean("mainDutyCycleExecutor")
    public TaskExecutor executor(
            final ConfigurableApplicationContext context,
            @Qualifier("commonDutyCycleThreadFactory") final ThreadFactory factory) {
        return new MainDutyCycleExecutor(context, factory);
    }

    @Bean("mainDutyCycle")
    public DutyCycle mainDutyCycle(@PartOfMainDutyCycle final Object... tasks) {
        return Tasks.aggregate("MainDutyCycle", Tasks.filter(tasks));
    }

    @Bean("mainDutyCycleStarter")
    public ApplicationListener<ContextRefreshedEvent> starter() {
        return new MainDutyCycleStarter();
    }

    @PartOfMainDutyCycle
    @Bean("mainDutyCycleInterruptChecker")
    public HouseKeeper interruptChecker() { return new ThreadInterruptChecker(); }


}
