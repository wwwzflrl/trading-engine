package app.trading.engine.core.api.messaging.serialization;

import org.agrona.DirectBuffer;

public interface BufferDecoder {
    int decode(DirectBuffer buffer, int offset);
}
