package app.trading.engine.core.noop;

import app.trading.engine.core.api.messaging.serialization.SerializablePayload;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public class NoPayload implements SerializablePayload {
    @Override
    public int decode(DirectBuffer buffer, int offset) {
        return offset;
    }

    @Override
    public int encode(MutableDirectBuffer buffer, int offset) {
        return offset;
    }

    @Override
    public int calcNeededBytes() {
        return 0;
    }
}
