package app.trading.engine.mem.pool;

import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;

import static java.lang.Integer.bitCount;
import static java.lang.Integer.numberOfTrailingZeros;
import static java.lang.Math.max;
import static org.agrona.BitUtil.CACHE_LINE_LENGTH;
import static org.agrona.BitUtil.findNextPositivePowerOfTwo;
import static org.agrona.BufferUtil.allocateDirectAligned;

public class ByteBufferPoolImpl implements ByteBufferPool {
    static final int OFFSET = Integer.numberOfTrailingZeros(CACHE_LINE_LENGTH);
    private final ObjPool<ByteBuffer>[] pools;

    ByteBufferPoolImpl(ObjPool<ByteBuffer>[] pools) {
        this.pools = pools;
    }

    static ByteBufferPool newByteBufferPoolImpl(final int maxBufSize) {
        if (maxBufSize >= CACHE_LINE_LENGTH && maxBufSize <= MAX_CAPACITY && bitCount(maxBufSize) == 1) {
            final int arrSize = index(maxBufSize) + 1;
            final ObjPool<ByteBuffer>[] pools = new ObjPool[arrSize];
            for (int i = 0; i < arrSize; i++) {
                final int capacity = 1 << (i + OFFSET);
                pools[i] = ObjPool.newPool(() -> allocateDirectAligned(capacity, CACHE_LINE_LENGTH), ByteBuffer::clear);
            }
            return new ByteBufferPoolImpl(pools);
        }
        throw new IllegalArgumentException("Not Right");
    }


    @Override
    public ByteBuffer get(@Range(from = 0, to = MAX_CAPACITY) int size) {
        final int index = max(index(findNextPositivePowerOfTwo(size)), 0);
        final ObjPool<ByteBuffer> pool = pools[index];
        return pool.get();
    }

    @Override
    public void release(ByteBuffer buffer) {
        pools[index(buffer.capacity())].release(buffer);
    }

    @Override
    public int maxBufSize() {
        return 1 << (pools.length + OFFSET - 1);
    }

    static int index(int size) { return numberOfTrailingZeros(size) - OFFSET; }
}
