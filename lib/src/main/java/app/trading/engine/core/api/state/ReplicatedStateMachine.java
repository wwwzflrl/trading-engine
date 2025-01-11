package app.trading.engine.core.api.state;

import app.trading.engine.core.api.messaging.arbiter.MsgArbiter;
import app.trading.engine.core.api.messaging.serialization.SerializableMsg;

import java.util.Set;

public interface ReplicatedStateMachine extends MsgArbiter<SerializableMsg> {
    LivecycleStage stage();

    void addLifecycleListener(Set<LivecycleStage> stages, LifecycleListener listener);
}
