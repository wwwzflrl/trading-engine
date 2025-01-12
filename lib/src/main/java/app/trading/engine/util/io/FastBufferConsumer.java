package app.trading.engine.util.io;

import app.trading.engine.core.api.function.BufferConsumer;
import org.agrona.DirectBuffer;

/**
 * Consumer long / then consumer int / then consumer byte
 */
public abstract class FastBufferConsumer implements BufferConsumer {

    @Override
    public void accpet(DirectBuffer buffer, int offset, int length) {
        int remaining;
        for (int i = length >> 3; i > 0; i--) {
            consumeEightBytes(buffer.getLong(offset));
            offset += 8;
        }
        remaining = length & 7; // length & 0111 = remaining / 8, 0-7
        if (remaining >= 4) {
            consumeFourBytes(buffer.getInt(offset));
            offset += 4;
            remaining -= 4;
        }
        for (int i =0; i < remaining; i++) {
            consume(buffer.getByte(offset + i));
        }
    }

    protected void consumeEightBytes(final long eightBytes) {
        consumeFourBytes((int) eightBytes);
        consumeFourBytes((int) (eightBytes >>> 32));
    }

    protected void consumeFourBytes(final int fourBytes) {
        consume((byte) fourBytes);
        consume((byte) (fourBytes >>> 8));
        consume((byte) (fourBytes >>> 16));
        consume((byte) (fourBytes >>> 24));
    }

    protected abstract void consume(byte b);
}
