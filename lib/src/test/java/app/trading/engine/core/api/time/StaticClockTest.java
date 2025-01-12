package app.trading.engine.core.api.time;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StaticClockTest {
    @Test
    void testMonotonic() {
        long last = 0;
        for (int i = 0; i < 100_000L; i++) {
            final long byClock = StaticClock.INSTANCE.epochNs();
            assertTrue(byClock > last, "Clock must be monotoic");
            last = byClock;
            LockSupport.parkNanos(100L);
        }
    }
}
