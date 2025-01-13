package app.trading.engine.io.messaging.header;

import app.trading.engine.core.api.messaging.MsgHeader;
import org.agrona.DirectBuffer;

public interface MsgHeaderReader extends MsgHeader {
    void wrap(DirectBuffer buffer, int offset);
    int peekMsgType();
    void decodeFullHeader();
    void fullMsgLength(int length);
    static MsgHeaderReader newMsgHeaderReader() { return new MsgHeaderReaderImpl(); }
}
