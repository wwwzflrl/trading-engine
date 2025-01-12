package app.trading.engine.infra.duty.cycle.hook;

import app.trading.engine.core.api.annotations.PartOfMainDutyCycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DutyCycleHookContextConfiguration {
    @PartOfMainDutyCycle
    @Bean("mainDutyCycleHooks")
    public DutyCycleHooks hooks() { return new DutyCycleHooksImpl(); }
}
