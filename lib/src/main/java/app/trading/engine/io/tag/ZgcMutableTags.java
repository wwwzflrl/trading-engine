package app.trading.engine.io.tag;

import app.trading.engine.core.api.tag.TagId;
import app.trading.engine.io.messaging.codec.Serializer;
import app.trading.engine.mem.pool.ByteBufferPool;
import app.trading.engine.mem.pool.ObjPool;
import org.agrona.concurrent.UnsafeBuffer;

import static app.trading.engine.core.noop.Noops.NO_CONSUMER;

public class ZgcMutableTags extends AbstractMutableTags {
    private static final ByteBufferPool BYTE_BUFFER_POOL = ByteBufferPool.newByteBufferPool(1 << 8); // 256BYTES
    private static final ObjPool<UnsafeBuffer> UNSAFE_BUFFER_OBJ_POOL = ObjPool.newPool(UnsafeBuffer::new, NO_CONSUMER);
    private static final Serializer<CharSequence> csSerializer = new StringTagSerializer();

    ZgcMutableTags() {
        super(new ZgcUuidTags(), ZgcMutableTags::allocateAndWrapBuffer);
    }

    @Override
    UnsafeBuffer getWrappedBuffer(int id, int length) {
        final UnsafeBuffer existing = binaries.get(id);
        if (existing == null) {
            final var allocated = allocateAndWrapBuffer(length);
            binaries.put(id, allocated);
            return allocated;
        }
        BYTE_BUFFER_POOL.release(existing.byteBuffer());
        existing.wrap(BYTE_BUFFER_POOL.get(length), 0, length);
        return existing;
    }

    static UnsafeBuffer allocateAndWrapBuffer(final int needed) {
        final var ub = UNSAFE_BUFFER_OBJ_POOL.get();
        final var bb = BYTE_BUFFER_POOL.get(needed);
        ub.wrap(bb);
        return ub;
    }

    @Override
    void unsetBinaryTag(TagId id) {
        final var removed = binaries.remove(id);
        if (removed != null) {
            recycle(removed);
        }
    }

    @Override
    void clearBinaryTags() {
        binaries.values().forEach(ZgcMutableTags::recycle);
        binaries.clear();
    }

    @Override
    public void setBinaryTag(TagId id, CharSequence value) {
        setBinaryTag(id, csSerializer.withSource(value));
    }

    private static void recycle(final UnsafeBuffer recycled) {
        BYTE_BUFFER_POOL.release(recycled.byteBuffer());
        UNSAFE_BUFFER_OBJ_POOL.release(recycled);
    }
}
