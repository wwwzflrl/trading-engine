package app.trading.engine.core.api.messaging.publisher;

import app.trading.engine.core.api.messaging.serialization.SerializableMsg;
import org.agrona.DirectBuffer;

public interface OffHeapPublisher extends MsgPublisher<SerializableMsg> {
    void  publish(DirectBuffer buffer, int offset, int length);
}
