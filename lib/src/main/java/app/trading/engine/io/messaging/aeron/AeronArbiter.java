package app.trading.engine.io.messaging.aeron;

import app.trading.engine.core.api.DutyCycleTask;
import app.trading.engine.core.api.messaging.MsgHeader;
import app.trading.engine.core.api.messaging.arbiter.MsgArbiter;
import app.trading.engine.core.api.messaging.serialization.SerializableMsg;
import org.agrona.DirectBuffer;

public interface AeronArbiter extends MsgArbiter<SerializableMsg>, DutyCycleTask {
    MsgHeader getHeader();

    void arbit(DirectBuffer buffer, int offset, int length);
}
