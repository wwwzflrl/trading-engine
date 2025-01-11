package app.trading.engine.core.api.time;

import app.trading.engine.core.api.annotations.ThreadSafe;

@ThreadSafe
public interface Clock {
    long epochMs();

    long epochNs();

    static Clock system() { return StaticClock.INSTANCE; }
}
