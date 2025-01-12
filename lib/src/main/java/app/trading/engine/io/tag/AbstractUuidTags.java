package app.trading.engine.io.tag;

import app.trading.engine.core.api.expection.Failure;
import app.trading.engine.core.api.tag.NoSuchTag;
import app.trading.engine.core.collections.MutableUuid;
import org.agrona.MutableDirectBuffer;
import org.agrona.collections.Int2ObjectHashMap;
import org.agrona.collections.IntObjConsumer;

import static org.agrona.BitUtil.*;

abstract class AbstractUuidTags extends AbstractPrimitiveTags<Int2ObjectHashMap<MutableUuid>> {
    private final Equals equals = new Equals(tags);

    AbstractUuidTags() { super(new Int2ObjectHashMap()); }

    MutableUuid get(final int id) {
        final MutableUuid value = tags.get(id);
        if (value == null) {
            throw NoSuchTag.NO_SUCH_TAG;
        }
        return value;
    }

    abstract void set(final int id, final long msb, final long lsb);

    abstract void unset(final int id);

    abstract void clear();

    void clone(final AbstractUuidTags src) {
        tags.clear();
        tags.putAll(src.tags);
    }

    @Override
    public int encode(MutableDirectBuffer buffer, int offset) {
        final Int2ObjectHashMap<MutableUuid>.EntryIterator iterator = tags.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            final int key = iterator.getIntKey();
            buffer.putShort(offset, (short) key);
            offset += SIZE_OF_SHORT;
            offset += iterator.getValue().encode(buffer, offset);
        }
        return offset;
    }

    @Override
    public int calcNeededBytes() {
        return size() * (SIZE_OF_SHORT + SIZE_OF_LONG + SIZE_OF_LONG);
    }

    @Override
    Class<?> getClassForEqualityCheck() {
        return AbstractUuidTags.class;
    }

    @Override
    void assertEntriesAreEqual(Int2ObjectHashMap<MutableUuid> other) {
        other.forEachInt(equals);
    }

    static class Equals implements IntObjConsumer<MutableUuid> {
        private final Int2ObjectHashMap<MutableUuid> tags;
        Equals(final Int2ObjectHashMap<MutableUuid> tags) { this.tags = tags; }

        @Override
        public void accept(int key, MutableUuid value) {
            if (tags.get(key) != value) {
                throw Failure.FAILURE;
            }
        }
    }
}
