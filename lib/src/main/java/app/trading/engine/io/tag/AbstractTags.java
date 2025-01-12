package app.trading.engine.io.tag;

import app.trading.engine.core.api.expection.Failure;
import app.trading.engine.core.api.function.BufferConsumer;
import app.trading.engine.core.api.tag.TagId;
import app.trading.engine.core.api.tag.Tags;
import app.trading.engine.core.collections.MutableUuid;
import app.trading.engine.core.math.HashCodes;
import org.agrona.DirectBuffer;

import static org.agrona.BitUtil.*;

abstract class AbstractTags implements Tags {
    final BinaryTagEqualityCheck binaryTagEqualityCheck = new BinaryTagEqualityCheck();
    final BinaryTagHashcode binaryTagHashcode = new BinaryTagHashcode();
    final IntTags ints = new IntTags();
    final LongTags longs = new LongTags();
    final AbstractUuidTags uuids;

    AbstractTags(AbstractUuidTags uuids) {
        this.uuids = uuids;
    }

    @Override
    public int getIntTag(TagId id) {
        return ints.get(TagIdTranslator.translate(id));
    }

    @Override
    public long getLongTag(TagId id) {
        return longs.get(TagIdTranslator.translate(id));
    }

    @Override
    public MutableUuid getUuidTag(TagId id) {
        return uuids.get(TagIdTranslator.translate(id));
    }

    @Override
    public void getBinaryTag(TagId id, BufferConsumer consumer) {
        getBinaryTag(TagIdTranslator.translate(id), consumer);
    }

    abstract void getBinaryTag(int id, BufferConsumer consumer);

    @Override
    public int decode(DirectBuffer buffer, int offset) {
        final int remaining = Short.toUnsignedInt(buffer.getShort(offset));
        offset += SIZE_OF_SHORT;
        for (int i = 0; i < remaining; i++) {
            offset = decodeNextTag(buffer, offset);
        }
        return offset;
    }

    private int decodeNextTag(final DirectBuffer buffer, int offset) {
        final int id = Short.toUnsignedInt(buffer.getShort(offset));
        offset += SIZE_OF_SHORT;
        switch (id & 3) {
            case 0:
                return decodeIntTag(id, buffer, offset);
            case 1:
                return decodeLongTag(id, buffer, offset);
            case 2:
                return decodeUuidTag(id, buffer, offset);
            case 3:
                return decodeBinaryTag(id, buffer, offset);
            default:
                throw new AssertionError("Will never reach here");
        }
    }

    private int decodeIntTag(final int id, final DirectBuffer buffer, final int offset) {
        ints.set(id, buffer.getInt(offset));
        return offset + SIZE_OF_INT;
    }

    private int decodeLongTag(final int id, final DirectBuffer buffer, final int offset) {
        longs.set(id, buffer.getLong(offset));
        return offset + SIZE_OF_LONG;
    }

    private int decodeUuidTag(final int id, final DirectBuffer buffer, final int offset) {
        uuids.set(id, buffer.getLong(offset), buffer.getLong(offset + SIZE_OF_LONG));
        return offset + MutableUuid.ENCODE_LEN;
    }

    private int decodeBinaryTag(final int id, final DirectBuffer buffer, int offset) {
        final int len = Short.toUnsignedInt(buffer.getByte(offset++));
        recordBinaryTagBuffer(id, buffer, offset, len);
        return offset + len;
    }

    abstract void recordBinaryTagBuffer(final int id, final DirectBuffer buffer, final int offset, final int length);
    abstract int binaryTagsCount();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof AbstractTags) {
            final var other = (AbstractTags) obj;
            return ints.equals(other.ints) && longs.equals(other.longs) && uuids.equals(other.uuids) && areBinaryTagsEqual(other);
        }
        return false;
    }

    boolean areBinaryTagsEqual(final AbstractTags other) {
        if (binaryTagsCount() == other.binaryTagsCount()) {
            return isEachBinaryTagEqual(other);
        }
        return false;
    }

    boolean isEachBinaryTagEqual(final AbstractTags other) {
        try {
            forEachBinaryTag(binaryTagEqualityCheck.with(other));
            return true;
        } catch (final Failure failure) {
            return false;
        }
    }

    abstract void forEachBinaryTag(final BinaryTagConsumer consumer);

    @Override
    public int hashCode() {
        return HashCodes.hash(HashCodes.hash(ints.hashCode(), HashCodes.hash(longs.hashCode(), uuids.hashCode())), binaryTagsHashCode());
    }

    int binaryTagsHashCode() {
        forEachBinaryTag(binaryTagHashcode.init(binaryTagsCount()));
        return binaryTagHashcode.result;
    }

    void formatBinaryTags(final StringBuilder builder) {
        builder.append("Binary tags TODO in the future");
    }

    @Override
    public void formatTo(StringBuilder buffer) {
        buffer.append("TAGS to do in the future");
    }
}
