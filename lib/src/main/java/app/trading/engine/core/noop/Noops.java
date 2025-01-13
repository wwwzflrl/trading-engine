package app.trading.engine.core.noop;

import app.trading.engine.core.api.annotations.ThreadSafe;
import app.trading.engine.core.api.messaging.arbiter.MsgHandler;
import app.trading.engine.core.api.messaging.publisher.MsgPublisher;
import app.trading.engine.core.api.messaging.serialization.SerializablePayload;
import app.trading.engine.core.api.tag.Tags;

import java.util.function.Consumer;

@ThreadSafe
public final class Noops {
    public static final Consumer<Object> NO_CONSUMER = o -> {};

    public static final Tags NO_TAGS = new NoTags();
    public static final SerializablePayload NO_PAYLOAD = new NoPayload();
    public static final MsgPublisher<Object> NOOP_PUBLISHER = new NoopPublisher();
    public static final MsgHandler<Object> NO_HANDLER = (_1, _2, _3) -> {};
}
