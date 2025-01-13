package app.trading.engine.io.messaging.util;

import app.trading.engine.core.api.expection.NotRelevant;
import app.trading.engine.core.api.function.BufferConsumer;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class AribtingBufferConsumer extends ArbitingContext implements BufferConsumer {
    protected long nowNs;
    public void setNowNs(final long nowNs) { this.nowNs = nowNs; }


    @Override
    public void accpet(DirectBuffer directBuffer, int offset, int length) {
        header.wrap(directBuffer, offset);
        final var subscription = subscriptions.get(header.peekMsgType());
        if (subscription != null) {
            decodeFullHeader(length);
            dispatchIfRelevant(subscription, directBuffer);
        }
    }

    protected void dispatchIfRelevant(final SubscriptionRecord<?> subscription, final DirectBuffer buffer) {
        try {
            subscription.dispatch(header, buffer, nowNs);
            dispatched = true;
        }  catch (final NotRelevant ignored) {}
    }

    @Override
    public void onFragment(DirectBuffer directBuffer, int offset, int length, Header header) {
        accpet(directBuffer, offset, length);
    }
}
