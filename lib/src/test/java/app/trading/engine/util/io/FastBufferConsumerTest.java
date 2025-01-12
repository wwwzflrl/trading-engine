package app.trading.engine.util.io;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class FastBufferConsumerTest {
    @ParameterizedTest
    @MethodSource("suite")
    void sanity(final int len, final Class<? extends FastBufferConsumer> impl) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final var source = new byte[len];
        ThreadLocalRandom.current().nextBytes(source);
        final var dest = new byte[len];
        final var consumer = impl.getDeclaredConstructor(byte[].class).newInstance(dest);
        consumer.accpet(new UnsafeBuffer(source), 0, len);
        final var expected = negate(source);
        assertArrayEquals(expected, dest);
    }

    public static Stream<Arguments> suite() {
        final Stream.Builder<Arguments> builder = Stream.builder();
        suiteForClass(NothingOverridden.class).forEach(builder);
        suiteForClass(FourBytesOverridden.class).forEach(builder);
        suiteForClass(EightBytesOverridden.class).forEach(builder);
        suiteForClass(BothOverridden.class).forEach(builder);
        return builder.build();
    }

    private static Stream<Arguments> suiteForClass(final Class<? extends FastBufferConsumer> clazz) {
        return Stream.of(
                Arguments.arguments(0, clazz),
                Arguments.arguments(3, clazz),
                Arguments.arguments(4, clazz),
                Arguments.arguments(7, clazz),
                Arguments.arguments(8, clazz),
                Arguments.arguments(31, clazz)
                );
    }

    static byte[] negate(final byte[] src) {
        final var dest = new byte[src.length];
        for (int i = src.length - 1; i >= 0; i--) {
            dest[i] = (byte) ~src[i];
        }
        return dest;
    }

    static class NothingOverridden extends FastBufferConsumer {
        final byte[] bytes;
        int offset;
        NothingOverridden(final byte[] bytes) { this.bytes = bytes; }

        @Override
        protected void consume(byte b) {
            bytes[offset++] = (byte) ~b;
        }
    }

    static class FourBytesOverridden extends NothingOverridden {
        final MutableDirectBuffer buffer = new UnsafeBuffer();
        FourBytesOverridden(final byte[] bytes) {
            super(bytes);
            buffer.wrap(bytes);
        }

        @Override
        protected void consumeFourBytes(final int fourBytes) {
            buffer.putInt(offset, ~fourBytes);
            offset += 4;
        }
    }

    static class EightBytesOverridden extends NothingOverridden {
        final MutableDirectBuffer buffer = new UnsafeBuffer();
        EightBytesOverridden(final byte[] bytes) {
            super(bytes);
            buffer.wrap(bytes);
        }

        @Override
        protected void consumeEightBytes(final long eightBytes) {
            buffer.putLong(offset, ~eightBytes);
            offset += 8;
        }
    }

    static class BothOverridden extends FourBytesOverridden {
        BothOverridden(final byte[] bytes) { super(bytes);}

        @Override
        protected void consumeEightBytes(final long eightBytes) {
            buffer.putLong(offset, ~eightBytes);
            offset += 8;
        }
    }
}
