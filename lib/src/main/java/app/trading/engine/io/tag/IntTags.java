package app.trading.engine.io.tag;

import app.trading.engine.core.api.expection.Failure;
import app.trading.engine.core.api.tag.NoSuchTag;
import org.agrona.MutableDirectBuffer;
import org.agrona.collections.Int2IntHashMap;
import org.agrona.collections.IntIntConsumer;

import static org.agrona.BitUtil.SIZE_OF_INT;
import static org.agrona.BitUtil.SIZE_OF_SHORT;

public class IntTags extends AbstractPrimitiveTags<Int2IntHashMap> {
    private final Equals equals = new Equals(tags);

    IntTags() { super(new Int2IntHashMap(Integer.MIN_VALUE)); }

    int get(final int id) {
        final int value = tags.get(id);
        if (value == Integer.MIN_VALUE) {
            throw NoSuchTag.NO_SUCH_TAG;
        }
        return value;
    }

    void set(final int id, final int value) { tags.put(id, value); }

    void unset(final int id) { tags.remove(id); }

    void clear() { tags.clear(); }

    void clone(final IntTags src) {
        tags.clear();
        tags.putAll(src.tags);
    }

    @Override
    public int encode(MutableDirectBuffer buffer, int offset) {
        final Int2IntHashMap.EntryIterator iterator = tags.entrySet().iterator();
        while (iterator.hasNext()) {
           iterator.next();
           final int key = iterator.getIntKey();
           final int value = iterator.getIntValue();
           buffer.putShort(offset, (short) key);
           offset += SIZE_OF_SHORT;
           buffer.putInt(offset, value);
           offset += SIZE_OF_INT;
        }
        return offset;
    }

    @Override
    public int calcNeededBytes() {
        return size() * (SIZE_OF_SHORT + SIZE_OF_INT);
    }

    @Override
    Class<?> getClassForEqualityCheck() {
        return IntTags.class;
    }

    @Override
    void assertEntriesAreEqual(Int2IntHashMap other) {
        other.forEachInt(equals);
    }

    static class Equals implements IntIntConsumer {
        private final Int2IntHashMap tags;
        Equals(final Int2IntHashMap tags) { this.tags = tags; }

        @Override
        public void accept(int key, int value) {
            if (tags.get(key) != value) {
                throw Failure.FAILURE;
            }
        }
    }
}
