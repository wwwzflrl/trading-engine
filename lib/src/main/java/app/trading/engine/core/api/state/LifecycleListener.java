package app.trading.engine.core.api.state;

public interface LifecycleListener {
    void on(LivecycleStage stage);
}
