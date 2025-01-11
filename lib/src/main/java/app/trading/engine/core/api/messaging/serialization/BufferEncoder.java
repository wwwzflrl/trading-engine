package app.trading.engine.core.api.messaging.serialization;

import org.agrona.MutableDirectBuffer;

public interface BufferEncoder {
    int encode(MutableDirectBuffer buffer, int offset);
    int calcNeededBytes();
}
