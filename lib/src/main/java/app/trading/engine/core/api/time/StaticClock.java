package app.trading.engine.core.api.time;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.concurrent.locks.LockSupport;

import static app.trading.engine.core.api.config.StaticConfigs.CLOCK_ALLOWED_DEVIATION_NS;
import static java.lang.System.nanoTime;

class StaticClock implements Clock {

    private static final Logger log = LogManager.getLogger(StaticClock.class);

    static final Clock INSTANCE = new StaticClock();

    private static final long OFFSET = new OffsetFinder().findOffset();

    static {
        log.info("StaticClock initialized");
    }

    @Override
    public long epochMs() {
        return epochNs() / 1_000_000L;
    }

    @Override
    public long epochNs() {
        return nanoTime() + OFFSET;
    }

    static class OffsetFinder {
        long nsFromSteadyClock;
        long nsFromHighResClock;
        long derivedEpochNs;
        int success;
        long offset;

        long findOffset() {
            log.info("Aligning clock...");
            while (success++ < 10) {
                verifyOrAdjustOffset();
                LockSupport.parkNanos(10000);
            }
            return offset;
        }

        void verifyOrAdjustOffset() {
            nsFromSteadyClock = nanoTime();
            nsFromHighResClock = getEpochNsFromSystemHighResClock();
            derivedEpochNs = nsFromSteadyClock + offset;
            if (offsetBreachedLowerBound() || offsetBreachedUpperBound()) {
                success = 0;
            }
        }

        boolean offsetBreachedLowerBound() {
            final long lowerBound = nsFromHighResClock - CLOCK_ALLOWED_DEVIATION_NS;
            if (derivedEpochNs > lowerBound) {
                offset = proposedNewOffset();
                log.info("FALSE LOWER offset: {}", offset);
                return false;
            } else {
                offset = Math.max(lowerBound - nsFromSteadyClock, proposedNewOffset());
                log.info("TRUE LOWER offset: {}", offset);
                return true;
            }
        }

        boolean offsetBreachedUpperBound() {
            final long upperBound = nsFromHighResClock + CLOCK_ALLOWED_DEVIATION_NS;
            if (derivedEpochNs < upperBound) {
                offset = proposedNewOffset();
                log.info("FALSE Upper offset: {}", offset);
                return false;
            } else {
                offset = Math.min(upperBound - nsFromSteadyClock, proposedNewOffset());
                log.info("FALSE Upper offset: {}", offset);
                return true;
            }
        }

        long proposedNewOffset() {
            return (offset + nsFromHighResClock - nsFromSteadyClock) >> 1;
        }

        static long getEpochNsFromSystemHighResClock() {
            final Instant now = Instant.now();
            return now.getEpochSecond() * 1_000_000_000L + now.getNano();
        }

    }
}