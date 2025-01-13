package app.trading.engine.io.messaging.handler;

import app.trading.engine.core.api.messaging.MsgHeader;
import app.trading.engine.core.api.messaging.arbiter.MsgHandler;
import org.apache.logging.log4j.util.StringBuilderFormattable;

import java.util.List;

public class CompositeMsgHandler<T> implements MsgHandler<T>, StringBuilderFormattable {
    protected final List<MsgHandler<? super T>> handlers;

    public CompositeMsgHandler(List<MsgHandler<? super T>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void formatTo(StringBuilder buffer) {
        buffer.append("CompositeMsgHandler[");
        for (final var handler : handlers) {
            buffer.append(handler).append(',');
        }
        buffer.append("]");
    }

    @Override
    public void handle(MsgHeader msgHeader, T message, long nowNs) {
        for (final var handler : handlers) {
            MsgHandlers.invoke(handler, msgHeader, message, nowNs);
        }
    }

    void add(final MsgHandler<? super T> handler) {
        handlers.add(handler);
    }
}
