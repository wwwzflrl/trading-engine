package app.trading.engine.core.noop;

import app.trading.engine.core.api.messaging.arbiter.MsgArbiter;
import app.trading.engine.core.api.messaging.arbiter.MsgHandler;

import java.util.function.Consumer;

public class NoopArbiter implements MsgArbiter<Object> {

    @Override
    public <MSG> void subscribe(Class<MSG> clazz, MsgHandler<? super MSG> handler, Consumer<? super MSG> configurator) {

    }
}
