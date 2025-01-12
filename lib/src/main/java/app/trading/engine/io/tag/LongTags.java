package app.trading.engine.io.tag;

import app.trading.engine.core.api.expection.Failure;
import app.trading.engine.core.api.tag.NoSuchTag;
import org.agrona.MutableDirectBuffer;
import org.agrona.collections.Long2LongHashMap;
import org.agrona.collections.LongLongConsumer;

import static org.agrona.BitUtil.*;

public class LongTags extends AbstractPrimitiveTags<Long2LongHashMap> {
    private final Equals equals = new Equals(tags);

    LongTags() { super(new Long2LongHashMap(Long.MIN_VALUE)); }

    long get(final int id) {
        final long value = tags.get(id);
        if (value == Long.MIN_VALUE) {
            throw NoSuchTag.NO_SUCH_TAG;
        }
        return value;
    }

    void set(final int id, final long value) { tags.put(id, value); }

    void unset(final int id) { tags.remove(id); }

    void clear() { tags.clear(); }

    void clone(final LongTags src) {
        tags.clear();
        tags.putAll(src.tags);
    }

    @Override
    public int encode(MutableDirectBuffer buffer, int offset) {
        final Long2LongHashMap.EntryIterator iterator = tags.entrySet().iterator();
        while (iterator.hasNext()) {
           iterator.next();
           final short key = (short) iterator.getLongKey();
           final long value = iterator.getLongValue();
           buffer.putShort(offset, key);
           offset += SIZE_OF_SHORT;
           buffer.putLong(offset, value);
           offset += SIZE_OF_LONG;
        }
        return offset;
    }

    @Override
    public int calcNeededBytes() {
        return size() * (SIZE_OF_SHORT + SIZE_OF_LONG);
    }

    @Override
    Class<?> getClassForEqualityCheck() {
        return LongTags.class;
    }

    @Override
    void assertEntriesAreEqual(Long2LongHashMap other) {
        other.forEachLong(equals);
    }

    static class Equals implements LongLongConsumer {
        private final Long2LongHashMap tags;
        Equals(final Long2LongHashMap tags) { this.tags = tags; }

        @Override
        public void accept(long key, long value) {
            if (tags.get(key) != value) {
                throw Failure.FAILURE;
            }
        }
    }
}
