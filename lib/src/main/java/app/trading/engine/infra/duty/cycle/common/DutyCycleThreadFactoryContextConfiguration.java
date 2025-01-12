package app.trading.engine.infra.duty.cycle.common;

import app.trading.engine.infra.duty.cycle.util.DutyCycleThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static app.trading.engine.core.api.config.StaticConfigs.CONF;
import static org.apache.logging.log4j.util.Unbox.box;

@Configuration
public class DutyCycleThreadFactoryContextConfiguration {
    private static final Logger log = LogManager.getLogger(DutyCycleThreadFactoryContextConfiguration.class);

    @Bean("commonDutyCycleConfig")
    public DutyCycleConfig config(
            @Value("${cycle.thread.priority:5}") final int priority,
            @Value("${cycle.thread.affinity:#{null}}") final String affinity,
            @Value("${cycle.thread.idler:backoff}") final String idler) {
        final DutyCycleConfig config = new DutyCycleConfig();
        config.affinity = affinity;
        config.idler = idler;
        config.priority = priority;
        logConfigurations(config);
        return config;
    }

    private static void logConfigurations(final DutyCycleConfig config) {
        log.info(CONF, "------ MainDutyCycleThread -------");
        log.info(CONF, "------ priority: {}", box(config.priority));
        log.info(CONF, "------ affinity: {}", config.affinity);
        log.info(CONF, "------ idler: {}", config.idler);
        log.info(CONF, "----------------------------------");
    }

    @Bean("commonDutyCycleThreadFactory")
    public DutyCycleThreadFactory threadFactory(final DutyCycleConfig config) {
        return new DutyCycleThreadFactory(config);
    }
}
