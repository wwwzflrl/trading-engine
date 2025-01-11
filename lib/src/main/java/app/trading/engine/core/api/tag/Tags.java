package app.trading.engine.core.api.tag;

import app.trading.engine.core.api.function.BufferConsumer;
import app.trading.engine.core.api.messaging.serialization.SerializablePayload;
import app.trading.engine.core.collections.MutableUuid;
import org.apache.logging.log4j.util.StringBuilderFormattable;

public interface Tags extends SerializablePayload, StringBuilderFormattable {
    int getIntTag(TagId id);

    long getLongTag(TagId id);

    MutableUuid getUuidTag(TagId id);

    void getBinaryTag(TagId id, BufferConsumer decoder);
}
