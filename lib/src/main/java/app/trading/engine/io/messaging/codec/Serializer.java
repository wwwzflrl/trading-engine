package app.trading.engine.io.messaging.codec;

import app.trading.engine.core.api.messaging.serialization.BufferEncoder;

public interface Serializer<T> {
    BufferEncoder withSource(T source);
}
