package app.trading.engine.core.api.messaging.serialization;

public interface SerializablePayload extends BufferDecoder, BufferEncoder {
    int MAX_COLLECTION_SIZE = 65536;
}
