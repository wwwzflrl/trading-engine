package app.trading.engine.core.noop;

import app.trading.engine.core.api.messaging.publisher.MsgPublisher;
import app.trading.engine.core.api.tag.Tags;

public class NoopPublisher implements MsgPublisher<Object> {
    @Override
    public void publish(Object payload, Tags tags, long seq) {

    }
}
