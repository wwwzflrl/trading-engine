package app.trading.engine.core.noop;

import app.trading.engine.core.api.function.BufferConsumer;
import app.trading.engine.core.api.tag.NoSuchTag;
import app.trading.engine.core.api.tag.TagId;
import app.trading.engine.core.api.tag.Tags;
import app.trading.engine.core.collections.MutableUuid;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_SHORT;

final class NoTags implements Tags {
    @Override
    public int decode(DirectBuffer buffer, int offset) {
        throw new UnsupportedOperationException("Unsupported for NoTags");
    }

    @Override
    public int encode(MutableDirectBuffer buffer, int offset) {
        buffer.putShort(offset, (short) 0);
        return offset + SIZE_OF_SHORT;
    }

    @Override
    public int calcNeededBytes() {
        return 2;
    }

    @Override
    public int getIntTag(TagId id) {
        throw NoSuchTag.NO_SUCH_TAG;
    }

    @Override
    public long getLongTag(TagId id) {
        throw NoSuchTag.NO_SUCH_TAG;
    }

    @Override
    public MutableUuid getUuidTag(TagId id) {
        throw NoSuchTag.NO_SUCH_TAG;
    }

    @Override
    public void getBinaryTag(TagId id, BufferConsumer decoder) {
        throw NoSuchTag.NO_SUCH_TAG;
    }

    @Override
    public void formatTo(StringBuilder buffer) {
        buffer.append("{int=[],long=[],uuid=[],binary=[]}");
    }

    @Override
    public int hashCode() { return 0; };

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj instanceof Tags) {
            final var others = (Tags) obj;
            return others.calcNeededBytes() == 2 && others.hashCode() == 0;
        }
        return false;
    }
}
