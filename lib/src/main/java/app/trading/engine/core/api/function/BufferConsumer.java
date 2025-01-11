package app.trading.engine.core.api.function;

import org.agrona.DirectBuffer;

public interface BufferConsumer {
    void accpet(DirectBuffer directBuffer, int offset, int length);
}
