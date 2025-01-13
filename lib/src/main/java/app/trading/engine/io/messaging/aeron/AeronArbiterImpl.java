package app.trading.engine.io.messaging.aeron;

import app.trading.engine.core.api.messaging.MsgHeader;
import app.trading.engine.core.api.messaging.arbiter.MsgHandler;
import app.trading.engine.core.api.messaging.serialization.SerializableMsg;
import app.trading.engine.io.messaging.util.AribtingBufferConsumer;
import io.aeron.FragmentAssembler;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import org.agrona.DirectBuffer;
import org.apache.logging.log4j.util.StringBuilderFormattable;

import java.util.function.Consumer;

public class AeronArbiterImpl implements AeronArbiter, StringBuilderFormattable {
    private static final int AERON_POLL_BATCH_SIZE = Integer.getInteger("aeronPollBatchSize", 500);
    private final Subscription aeron;
    private final AribtingBufferConsumer listeners;
    private final MsgHeader header;
    private final FragmentHandler assembler;

    public AeronArbiterImpl(Subscription aeron, AribtingBufferConsumer listeners, FragmentHandler assembler) {
        this(aeron, listeners);
    }

    public AeronArbiterImpl(Subscription aeron, AribtingBufferConsumer listeners) {
        this.aeron = aeron;
        this.listeners = listeners;
        this.header = listeners.header();
        this.assembler = new FragmentAssembler(listeners, 8192, true);
    }

    @Override
    public boolean doWork(long nowNs) {
        listeners.setNowNs(nowNs);
        return aeron.poll(assembler, AERON_POLL_BATCH_SIZE) > 0 && listeners.dispatched();
    }

    @Override
    public <MSG extends SerializableMsg> void subscribe(Class<MSG> clazz, MsgHandler<? super MSG> handler, Consumer<? super MSG> configurator) {
        listeners.subscribe(clazz, handler, configurator);
    }

    @Override
    public MsgHeader getHeader() {
        return header;
    }

    @Override
    public void arbit(DirectBuffer buffer, int offset, int length) {
        listeners.accpet(buffer, offset, length);
    }

    @Override
    public void formatTo(StringBuilder buffer) {
        buffer.append("Arbiter(");
        buffer.append(aeron.channel());
        buffer.append(":");
        buffer.append(aeron.streamId());
        buffer.append(")");
    }

    @Override
    public String toString() {
        final var sb = new StringBuilder(32);
        formatTo(sb);
        return sb.toString();
    }
}
