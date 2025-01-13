package app.trading.engine.io.messaging.header;

import org.agrona.MutableDirectBuffer;

public interface MsgHeaderWriter {
    void wrap(MutableDirectBuffer buffer, int offset);
    void id(int value);
    void sender(int value);
    void seq(long value);
    void senderTime(long value);
    static MsgHeaderWriter msgHeaderWriter() { return new MsgHeaderWriterImpl(); }
}
