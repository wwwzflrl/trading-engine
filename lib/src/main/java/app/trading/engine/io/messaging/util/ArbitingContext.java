package app.trading.engine.io.messaging.util;

import app.trading.engine.core.api.messaging.MsgHeader;
import app.trading.engine.core.api.messaging.arbiter.MsgHandler;
import app.trading.engine.core.api.messaging.serialization.MsgType;
import app.trading.engine.core.api.messaging.serialization.SerializableMsg;
import app.trading.engine.core.collections.UnsafeArrayList;
import app.trading.engine.io.messaging.header.MsgHeaderReader;
import app.trading.engine.io.messaging.handler.MsgHandlers;
import app.trading.engine.util.constructor.Constructors;
import io.aeron.logbuffer.FragmentHandler;
import org.agrona.DirectBuffer;
import org.agrona.collections.Int2ObjectHashMap;

import java.util.function.Consumer;

public abstract class ArbitingContext implements FragmentHandler {
    protected final MsgHeaderReader header = MsgHeaderReader.newMsgHeaderReader();
    protected final Int2ObjectHashMap<SubscriptionRecord<? extends SerializableMsg>> subscriptions = new Int2ObjectHashMap<>();
    protected boolean dispatched;

    protected void decodeFullHeader(final int length) {
        header.fullMsgLength(length);
        header.decodeFullHeader();
    }

    public boolean dispatched() {
        if (dispatched) {
            dispatched = false;
            return true;
        }
        return false;
    }

    public synchronized <MSG extends SerializableMsg> void subscribe(final Class<MSG> clazz, final MsgHandler<? super MSG> handler, final Consumer<? super MSG> configurator) {
        final var deserializer = Constructors.newInstance(clazz);
        configurator.accept(deserializer);
        final int id = MsgType.id(deserializer.msgType());
        final var existing = subscriptions.get(id);
        if (existing == null) {
            final SubscriptionRecord<MSG> record = new SubscriptionRecord<>(deserializer, handler);
            subscriptions.put(id, record);
        } else if (clazz.isAssignableFrom(existing.message.getClass())) {
            ((SubscriptionRecord<MSG>)existing).addHandler(handler);
        } else {
            throw new IllegalArgumentException(("Inconsistent message decoder class for id: " + id + " - " + clazz + " vs " + existing.message.getClass()));
        }
    }

    public MsgHeader header() { return header; }

    protected static class SubscriptionRecord<T extends SerializableMsg> {
        final T message;
        private MsgHandler<T> handler;

        protected SubscriptionRecord(T message, MsgHandler<? super T> handler) {
            this.message = message;
            this.handler = MsgHandlers.decorate(handler, false);
        }

        public void dispatch(final MsgHeaderReader header, final DirectBuffer buffer, final long nowNs) {
            message.decode(buffer, header.payloadOffset());
            MsgHandlers.invoke(handler, header, message, nowNs);
        }

        void addHandler(final MsgHandler<? super T> handler) {
            this.handler = MsgHandlers.add(this.handler, MsgHandlers.decorate(handler, false), UnsafeArrayList::new);
        }
    }
}
