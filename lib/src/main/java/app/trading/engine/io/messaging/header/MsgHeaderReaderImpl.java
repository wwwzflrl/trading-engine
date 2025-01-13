package app.trading.engine.io.messaging.header;

import app.trading.engine.core.api.tag.Tags;
import app.trading.engine.io.tag.ReadThroughTags;
import org.agrona.DirectBuffer;

/**
 * MsgHeader Context
 * |msgType(4)|offsetOfPayloadLength(1)|sender(4)|seq(8)|senderTimeNs(8)|tags...|body...
 *  MsgHeader.BLOCK_LENGTH = 25, tags, body
 */
class MsgHeaderReaderImpl implements MsgHeaderReader {
    private final ReadThroughTags tags = new ReadThroughTags();
    DirectBuffer buffer;
    int offset;
    int payloadOffset;
    int length;
    @Override
    public int sender() {
        return buffer.getInt(offset + 5);
    }

    @Override
    public long senderTimeNs() {
        return buffer.getLong(offset + 17);
    }

    @Override
    public long seq() {
        return buffer.getLong(offset + 9);
    }

    @Override
    public Tags tags() {
        return tags;
    }

    @Override
    public DirectBuffer buffer() {
        return buffer;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public int payloadOffset() {
        return payloadOffset;
    }

    @Override
    public int fullMsgLength() {
        return length;
    }

    @Override
    public int payloadLength() {
        return length - payloadOffset + offset;
    }

    @Override
    public void wrap(DirectBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
    }

    @Override
    public int peekMsgType() {
        return buffer.getInt(offset);
    }

    @Override
    public void decodeFullHeader() {
        final int blockLen = Byte.toUnsignedInt(buffer.getByte(offset + 4));
        payloadOffset = tags.decode(buffer, offset + blockLen);
    }

    @Override
    public void fullMsgLength(int length) {
        this.length = length;
    }
}
