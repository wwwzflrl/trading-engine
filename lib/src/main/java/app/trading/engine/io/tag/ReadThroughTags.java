package app.trading.engine.io.tag;

import app.trading.engine.core.api.function.BufferConsumer;
import app.trading.engine.core.api.messaging.serialization.BufferDecoder;
import app.trading.engine.core.api.tag.Tags;
import app.trading.engine.mem.pool.ObjPool;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.collections.Int2ObjectHashMap;

import static app.trading.engine.core.api.tag.NoSuchTag.NO_SUCH_TAG;
import static app.trading.engine.core.noop.Noops.NO_CONSUMER;

public class ReadThroughTags extends AbstractTags implements Tags, BufferDecoder {
    private static final ObjPool<int[]> OBJ_POOL = ObjPool.newPool(() -> new int[2], NO_CONSUMER);
    private final Int2ObjectHashMap<int[]> descriptors = new Int2ObjectHashMap<>();
    private DirectBuffer buffer;
    private int offset;
    private int len;
    public ReadThroughTags() {
        super(new ZgcUuidTags());
    }

    @Override
    public int encode(MutableDirectBuffer buffer, int offset) {
        buffer.putBytes(offset, this.buffer, this.offset, len);
        return offset + len;
    }

    @Override
    public int decode(DirectBuffer buffer, int offset) {
        reset();
        this.buffer = buffer;
        final var end = super.decode(buffer, offset);
        this.offset = offset;
        len = end - offset;
        return end;
    }

    private void reset() {
        ints.clear();
        longs.clear();
        uuids.clear();
        for (final int[] descriptor : descriptors.values()) {
            OBJ_POOL.release(descriptor);
        }
        descriptors.clear();
    }
    @Override
    public int calcNeededBytes() {
        return len;
    }

    @Override
    void getBinaryTag(int id, BufferConsumer consumer) {
        final int[] descriptor = descriptors.get(id);
        if (descriptor == null) throw NO_SUCH_TAG;
        consumer.accpet(buffer, descriptor[0], descriptor[1]);
    }

    @Override
    void recordBinaryTagBuffer(int id, DirectBuffer buffer, int offset, int length) {
        final int[] descriptor = OBJ_POOL.get();
        descriptor[0] = offset;
        descriptor[1] = length;
        descriptors.put(id, descriptor);
    }

    @Override
    int binaryTagsCount() {
        return descriptors.size();
    }

    @Override
    void forEachBinaryTag(BinaryTagConsumer consumer) {
        final var iterator = descriptors.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            final int key = iterator.getIntKey();
            final int[] descriptor =  iterator.getValue();
            consumer.accept(key, buffer, descriptor[0], descriptor[1]);
        }
    }
}
