package app.trading.engine.core.api.messaging.arbiter;

import app.trading.engine.core.api.annotations.NotForHotPath;
import app.trading.engine.core.api.annotations.ThreadSafe;

import java.util.function.Consumer;

import static app.trading.engine.core.noop.Noops.NO_CONSUMER;

@ThreadSafe
@NotForHotPath
public interface MsgArbiter<T> {

    /**
     * It is subsequently found that logic-while-decoding is pretty much a terrible design choice
     * that made usages very hard to trace & understand
     * With the introduction of RawBytes we now have much better way if veteran developers really ssk to work off-heap
     */
    <MSG extends T> void subscribe(Class<MSG> clazz, MsgHandler<? super MSG> handler, Consumer<? super MSG> configurator);

    default <MSG extends T> void subscribe(Class<MSG> clazz, MsgHandler<? super MSG> handler) {
        subscribe(clazz, handler, NO_CONSUMER);
    }
}
