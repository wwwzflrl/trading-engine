package app.trading.engine.io.tag;

import app.trading.engine.core.api.expection.Failure;
import app.trading.engine.util.io.FastBufferConsumer;
import org.agrona.DirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_LONG;

class BinaryTagEqualityCheck implements BinaryTagConsumer {

    private final BinaryTagVerifier verifier = new BinaryTagVerifier();

    AbstractTags other;

    BinaryTagConsumer with(final AbstractTags other) {
        this.other = other;
        return this;
    }

    @Override
    public void accept(final int id, DirectBuffer directBuffer, int offset, int length) {
        other.getBinaryTag(id, verifier.with(directBuffer, offset, length));
    }

    static class BinaryTagVerifier extends FastBufferConsumer {
        DirectBuffer buffer;
        int offset;
        int length;

        BinaryTagVerifier with(final DirectBuffer buffer, final int offset, final int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
            return this;
        }

        @Override
        public void accpet(DirectBuffer buffer, int offset, int length) {
            if (length == this.length) {
                super.accpet(buffer, offset, length);
                return;
            }
            throw Failure.FAILURE;
        }

        @Override
        protected void consume(byte b) {
            if (b != buffer.getByte(offset++)) {
                throw Failure.FAILURE;
            }
        }

        @Override
        protected void consumeEightBytes(long eightBytes) {
           if (eightBytes == buffer.getLong(offset)) {
               offset += SIZE_OF_LONG;
               return;
           }
           throw Failure.FAILURE;
        }
    }
}
