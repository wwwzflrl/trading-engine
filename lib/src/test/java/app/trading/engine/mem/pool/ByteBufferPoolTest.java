package app.trading.engine.mem.pool;

import org.junit.jupiter.api.Test;

public class ByteBufferPoolTest {
    @Test
    void testByteBufferPool() {
        final ByteBufferPool pool = ByteBufferPool.newByteBufferPool(256);
    }
}
