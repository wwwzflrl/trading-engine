package app.trading.engine.io.tag;

import app.trading.engine.core.math.HashCodes;
import app.trading.engine.util.io.FastBufferConsumer;
import org.agrona.DirectBuffer;

class BinaryTagHashcode extends FastBufferConsumer implements BinaryTagConsumer {
    int result;

    BinaryTagConsumer init(final int count) {
        result = count;
        return this;
    }

    @Override
    public void accept(final int id, DirectBuffer directBuffer, int offset, int length) {
        result = HashCodes.hash(result, id);
        accpet(directBuffer, offset, length);
    }

    @Override
    protected void consume(byte b) {
        result = HashCodes.hash(result, b);
    }

    @Override
    protected void consumeEightBytes(long eightBytes) {
        result = HashCodes.hash(result, Long.hashCode(eightBytes));
    }
}
