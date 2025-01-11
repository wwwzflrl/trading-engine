package app.trading.engine.core.api.messaging.serialization;

public interface SerializableMsg extends SerializablePayload {
    MsgType msgType();
}
