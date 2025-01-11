package app.trading.engine.core.api.messaging;

import app.trading.engine.core.api.tag.Tags;
import org.agrona.DirectBuffer;

public interface MsgHeader { // 5 internally reserved bytes at start
    int sender(); //  1 byte reserved

    long senderTimeNs(); // 2 byte reserved

    long seq(); // 2 byte reserved

    Tags tags();

    DirectBuffer buffer();

    int offset();

    int payloadOffset();

    int fullMsgLength();

    int payloadLength();

    // TODO: Figure it out
    int BLOCK_LENGTH = 25; // One hidden unit16 field of headerLen
}
