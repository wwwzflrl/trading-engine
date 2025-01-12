package app.trading.engine.infra.duty.cycle.standalone;

import app.trading.engine.core.api.DutyCycleTask;
import app.trading.engine.core.api.annotations.PartOfMainDutyCycle;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StandaloneDutyCycleContextConfigurationTest {
    AnnotationConfigApplicationContext context;

    @AfterEach
    void cleanUp() { context.close(); }

    @Test
    void testStandaloneConfiguration() {
        context = new AnnotationConfigApplicationContext();
        context.register(TestTaskConfiguration.class);
        context.refresh();
        final TestTask task = context.getBean(TestTask.class);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> task.invocations.get() > 10);
    }


    @Configuration
    @Import(StandaloneDutyCycleContextConfiguration.class)
    static class TestTaskConfiguration {
        @Bean
        @PartOfMainDutyCycle
        DutyCycleTask testTask() { return new TestTask(); }
    }

    static class TestTask implements DutyCycleTask {
        final AtomicInteger invocations = new AtomicInteger();


        @Override
        public boolean doWork(long nowNs) {
            return (invocations.getAndIncrement() & 1) == 0;
        }

        @Override
        public String toString() {
            return "TestTask";
        }
    }
}
