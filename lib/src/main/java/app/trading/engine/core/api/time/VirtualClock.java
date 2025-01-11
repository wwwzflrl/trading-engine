package app.trading.engine.core.api.time;

public class VirtualClock implements Clock {
    private long nowNs;

    public void driveTo(final long nowNs) { this.nowNs = nowNs; }
    @Override
    public long epochMs() {
        return nowNs / 1_000_000L;
    }

    @Override
    public long epochNs() {
        return nowNs;
    }
}
