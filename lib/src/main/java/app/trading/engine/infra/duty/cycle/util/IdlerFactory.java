package app.trading.engine.infra.duty.cycle.util;

import org.agrona.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class IdlerFactory {
    public static IdleStrategy getIdler(final String description) {
        if (BusySpinIdleStrategy.ALIAS.equals(description)) {
            return new BusySpinIdleStrategy();
        }
        if (YieldingIdleStrategy.ALIAS.equals(description)) {
            return new YieldingIdleStrategy();
        }
        if (NoOpIdleStrategy.ALIAS.equals(description)) {
            return new NoOpIdleStrategy();
        }
        if (description.startsWith("sleep")) {
            return parseSleep(description);
        }
        if (description.startsWith("backoff")) {
            return parseBackoff(description);
        }
        throw new IllegalArgumentException("Unable to parse idle strategy" + description);
    }

    private static IdleStrategy parseSleep(final String description) {
        final var parts = description.split("/");
        final var sleepMs = parts.length == 1 ? 1 : Long.parseLong(parts[1]);
        return new SleepingIdleStrategy(MILLISECONDS.toNanos(sleepMs));
    }

    private static IdleStrategy parseBackoff(final String description) {
        final var parts = description.split("/");
        switch (parts.length) {
            case 1:
                return createDefaultBackoff();
            case 5:
                return careatBackoffFromDescriptor(parts);
            default:
                throw new IllegalArgumentException("Not support" + description);
        }
    }

    private static IdleStrategy createDefaultBackoff() { return new BackoffIdleStrategy(10,10, 1000, 1_000_000L); }

    private static IdleStrategy careatBackoffFromDescriptor(final String[] descriptor) {
        final var spins = Integer.parseInt(descriptor[1]);
        final var yields = Integer.parseInt(descriptor[2]);
        final var minSleepNs = Integer.parseInt(descriptor[3]);
        final var maxSleepNs = Integer.parseInt(descriptor[4]);
        return new BackoffIdleStrategy(spins, yields, minSleepNs, maxSleepNs);
    }

    private void IdleStrategy() {};
}
