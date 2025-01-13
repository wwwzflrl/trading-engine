package app.trading.engine.io.messaging.handler;

import app.trading.engine.core.api.config.StaticConfigs;
import app.trading.engine.core.api.expection.NotRelevant;
import app.trading.engine.core.api.messaging.MsgHeader;
import app.trading.engine.core.api.messaging.arbiter.MsgHandler;
import app.trading.engine.core.noop.Noops;
import app.trading.engine.util.util.Unchecked;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.apache.logging.log4j.util.Unbox.box;

public final class MsgHandlers {
    private static final Logger log = LogManager.getLogger(MsgHandlers.class);

    public static <T> MsgHandler<T> decorate(final MsgHandler<? super T> handler, final boolean propagateErrors) {
        if (propagateErrors) {
            return (MsgHandler<T>) handler;
        }
        return (msgHeader, message, nowNs) -> invokeWithErrorHandling(handler, msgHeader, message, nowNs);
    }

    static <T> void invokeWithErrorHandling(final MsgHandler<? super T> handler, final MsgHeader header, final T message, final long nowNs) {
        try {
            handler.handle(header, message, nowNs);
        } catch (final NotRelevant ignored) {
            // do nothing
        } catch (final Exception e) {
            log.error("Uncaught excpection handling message - message:[{}]", message);
        }
    }

    public static <T> void invoke(final MsgHandler<? super T> handler, final MsgHeader header, final T message, final long nowNs) {
        if (StaticConfigs.CLOCK_TASKS) {
            final long start = System.nanoTime();
            handler.handle(header, message, nowNs);
            final var elapsed = System.nanoTime() - start;
            if (elapsed > StaticConfigs.THRESHOLD_NS) {
                log.warn("Slow MsgHandler - name:[{}], [{}us]", handler, box(elapsed / 1000));
            }
        } else {
            handler.handle(header, message, nowNs);
        }
    }

    public static <T> MsgHandler<T> add(
            @NotNull final MsgHandler<T> existing,
            final MsgHandler<? super T> incoming,
            final Supplier<List<MsgHandler<? super T>>> listFactory) {
        if (existing == null || existing == Noops.NO_HANDLER) {
            return Unchecked.cast(incoming);
        }
        if (existing instanceof CompositeMsgHandler) {
            ((CompositeMsgHandler<T>)existing).add(incoming);
            return existing;
        }
        final var composite = new CompositeMsgHandler<>(listFactory.get());
        composite.add(existing);
        composite.add(incoming);
        return composite;
    }

}
