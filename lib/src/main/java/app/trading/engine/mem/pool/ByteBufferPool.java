package app.trading.engine.mem.pool;

import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;

import static app.trading.engine.mem.pool.ByteBufferPoolImpl.newByteBufferPoolImpl;
import static org.agrona.BitUtil.CACHE_LINE_LENGTH;

public interface ByteBufferPool {
    int MAX_CAPACITY = 1 << 30;

    ByteBuffer get(@Range(from = 0, to = MAX_CAPACITY) int size);

    void release(ByteBuffer buffer);
    int maxBufSize();
    static ByteBufferPool newByteBufferPool(@Range(from = CACHE_LINE_LENGTH, to = MAX_CAPACITY) final int maxBufSize) {
        return newByteBufferPoolImpl(maxBufSize);
    }
}
