package app.trading.engine.io.messaging.header;

import app.trading.engine.core.api.messaging.MsgHeader;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_INT;

public class MsgHeaderWriterImpl implements MsgHeaderWriter {
    private MutableDirectBuffer buffer;
    private int offset;
    @Override
    public void wrap(MutableDirectBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        buffer.putByte(offset + SIZE_OF_INT, (byte) MsgHeader.BLOCK_LENGTH);
    }

    @Override
    public void id(int value) {
        buffer.putInt(offset, value);
    }

    @Override
    public void sender(int value) {
        buffer.putInt(offset + 5, value);
    }

    @Override
    public void seq(long value) {
        buffer.putLong(offset + 9, value);
    }

    @Override
    public void senderTime(long value) {
        buffer.putLong(offset + 17, value);
    }
}
