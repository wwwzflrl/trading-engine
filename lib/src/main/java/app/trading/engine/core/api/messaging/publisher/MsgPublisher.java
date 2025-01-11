package app.trading.engine.core.api.messaging.publisher;

import app.trading.engine.core.api.tag.Tags;

import static app.trading.engine.core.noop.Noops.NO_TAGS;

public interface MsgPublisher<T> {

    void  publish(T payload, Tags tags, final long seq);

    default void publish(final T payload, final Tags tags) { publish(payload, tags, 0); }

    default void publish(final T payload) { publish(payload, NO_TAGS); }
}
