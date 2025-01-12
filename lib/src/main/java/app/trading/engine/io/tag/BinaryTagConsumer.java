package app.trading.engine.io.tag;

import org.agrona.DirectBuffer;

interface BinaryTagConsumer {
    void accept(int id, DirectBuffer directBuffer, int offset, int length);
}
