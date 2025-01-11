package app.trading.engine.core.api.config;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.security.SecureRandom;

import static java.util.concurrent.TimeUnit.MICROSECONDS;

public class StaticConfigs {
    public static final Marker CONF = MarkerManager.getMarker("CONF");

    public static final String INSTANCE_NAME = System.getProperty("InstanceName", "UNKNOWN");

    public static final int INSTANCE_ID = Integer.getInteger("InstanceId", new SecureRandom().nextInt());

    public static final long CLOCK_ALLOWED_DEVIATION_NS = Long.getLong("clock.allowed-deviation-ns", 500L);

    public static final boolean CLOCK_TASKS = Boolean.getBoolean("ClockTasks");

    public static final long THRESHOLD_NS = Long.getLong("SlowTaskThresholdNs", MICROSECONDS.toNanos(1000));

    private StaticConfigs() {}

}
