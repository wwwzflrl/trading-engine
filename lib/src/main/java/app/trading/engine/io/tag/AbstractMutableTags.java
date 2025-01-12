package app.trading.engine.io.tag;

import app.trading.engine.core.api.function.BufferConsumer;
import app.trading.engine.core.api.messaging.serialization.BufferEncoder;
import app.trading.engine.core.api.messaging.serialization.SerializablePayload;
import app.trading.engine.core.api.tag.NoSuchTag;
import app.trading.engine.core.api.tag.TagId;
import app.trading.engine.core.api.tag.Tags;
import app.trading.engine.core.noop.Noops;
import app.trading.engine.io.messaging.codec.Serializer;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.collections.Int2ObjectHashMap;
import org.agrona.concurrent.UnsafeBuffer;


import java.util.function.IntFunction;

import static app.trading.engine.core.api.tag.TagId.TagType.UUID;
import static org.agrona.BitUtil.SIZE_OF_SHORT;

abstract class AbstractMutableTags extends AbstractTags implements MutableTags, SerializablePayload {
    final Int2ObjectHashMap<UnsafeBuffer> binaries = new Int2ObjectHashMap<>();
    private final BinaryTagEncoder binaryTagEncoder = new BinaryTagEncoder();
    private final BinaryTagCloner binaryTagCloner;

    AbstractMutableTags(final AbstractUuidTags uuidTags, final IntFunction<UnsafeBuffer> allocator) {
        super(uuidTags);
        this.binaryTagCloner = new BinaryTagCloner(binaries, allocator);
    }

    @Override
    public int encode(MutableDirectBuffer buffer, int offset) {
        buffer.putShort(offset, (short) (ints.size() + longs.size() + uuids.size() + binaryTagsCount()));
        offset += SIZE_OF_SHORT;
        offset = ints.encode(buffer, offset);
        offset = longs.encode(buffer, offset);
        offset = uuids.encode(buffer, offset);
        return encodeBinaryTags(buffer, offset);
    }
    int encodeBinaryTags(final MutableDirectBuffer buffer, final int offset) {
        forEachBinaryTag(binaryTagEncoder.with(buffer, offset));
        return binaryTagEncoder.cursor;
    }

    @Override
    public int calcNeededBytes() {
        return SIZE_OF_SHORT + ints.calcNeededBytes() + longs.calcNeededBytes() + uuids.calcNeededBytes() + calcNeededBytesForBinaryTags();
    }
    int calcNeededBytesForBinaryTags() {
        int needed =0;
        for (final UnsafeBuffer unsafeBuffer : binaries.values()) {
            needed += SIZE_OF_SHORT + 1 + unsafeBuffer.capacity();
        }
        return needed;
    }

    @Override
    void getBinaryTag(int id, BufferConsumer consumer) {
        final var buffer = binaries.get(id);
        if (buffer == null) {
            throw NoSuchTag.NO_SUCH_TAG;
        }
        consumer.accpet(buffer, 0, buffer.capacity());
    }

    @Override
    void recordBinaryTagBuffer(int id, DirectBuffer buffer, int offset, int length) {
        final UnsafeBuffer ub = getWrappedBuffer(id, length);
        ub.putBytes(0, buffer, offset, length);
    }

    abstract UnsafeBuffer getWrappedBuffer(int id, int length);

    @Override
    int binaryTagsCount() {
        return binaries.size();
    }

    @Override
    void forEachBinaryTag(BinaryTagConsumer consumer) {
        final var iterator = binaries.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            final int key = iterator.getIntKey();
            final UnsafeBuffer value =  iterator.getValue();
            consumer.accept(key, value, 0, value.capacity());
        }
    }

    @Override
    public void setIntTag(TagId id, int value) {
        assert id.type() == TagId.TagType.INT : "INT TAG MISMATCH";
        ints.set(TagIdTranslator.translate(id), value);
    }

    @Override
    public void setLongTag(TagId id, long value) {
        assert id.type() == TagId.TagType.LONG : "LONG TAG MISMATCH";
        longs.set(TagIdTranslator.translate(id), value);
    }

    @Override
    public void setUuidTag(TagId id, long msb, long lsb) {
        assert id.type() == UUID : "UUID TAG MISMATCH";
        uuids.set(TagIdTranslator.translate(id), msb, lsb);
    }

    @Override
    public void setBinaryTag(TagId id, BufferEncoder encoder) {
        final var buffer = getTagBuffer(id, encoder.calcNeededBytes());
        encoder.encode(buffer, 0);
    }

    private UnsafeBuffer getTagBuffer(final TagId id, final int len) {
        assert id.type() == TagId.TagType.BINARY : "BINARY TAG MISMATCH";
        final int translated = TagIdTranslator.translate(id);
        return getWrappedBuffer(translated, len);
    }


    @Override
    public void setBinaryTag(TagId id, byte[] array, int offset, int length) {
        getTagBuffer(id, length).putBytes(0, array, offset, length);
    }

    @Override
    public void setBinaryTag(TagId id, DirectBuffer buffer, int offset, int length) {
        getTagBuffer(id, length).putBytes(0, buffer, offset, length);
    }

    @Override
    public void unset(TagId id) {
        switch (id.type()) {
            case INT -> unsetIntTag(id);
            case LONG -> unsetLongTag(id);
            case UUID -> unsetUuidTag(id);
            case BINARY -> unsetBinaryTag(id);
        }
    };

    private void unsetIntTag(final TagId id) { ints.unset(TagIdTranslator.translate(id));}
    private void unsetLongTag(final TagId id) { longs.unset(TagIdTranslator.translate(id));}
    private void unsetUuidTag(final TagId id) { uuids.unset(TagIdTranslator.translate(id));}

    abstract void unsetBinaryTag(final TagId id);


    @Override
    public void clear() {
        ints.clear();
        longs.clear();
        uuids.clear();
        clearBinaryTags();
    }

    abstract void clearBinaryTags();


    @Override
    public void clone(Tags tags) {
        if (tags == Noops.NO_TAGS) {
            clear();
            return;
        }
        final var src = (AbstractTags) tags;
        ints.clone(src.ints);
        longs.clone(src.longs);
        uuids.clone(src.uuids);
        clearBinaryTags();
        src.forEachBinaryTag(binaryTagCloner);
    }

    private static class BinaryTagEncoder implements BinaryTagConsumer {
        private MutableDirectBuffer dest;
        int cursor;
        BinaryTagConsumer with(final MutableDirectBuffer dest, final int offset) {
            this.dest = dest;
            this.cursor = offset;
            return this;
        }

        @Override
        public void accept(int id, DirectBuffer directBuffer, int offset, int length) {
            dest.putShort(cursor, (short) id);
            cursor += SIZE_OF_SHORT;
            dest.putByte(cursor++, (byte) length);
            dest.putBytes(cursor, directBuffer, offset, length);
            cursor += length;
        }
    }

    private static class BinaryTagCloner implements BinaryTagConsumer {
        private final Int2ObjectHashMap<UnsafeBuffer> dest;
        private final IntFunction<UnsafeBuffer> allocator;

        private BinaryTagCloner(Int2ObjectHashMap<UnsafeBuffer> dest, IntFunction<UnsafeBuffer> allocator) {
            this.dest = dest;
            this.allocator = allocator;
        }

        @Override
        public void accept(int id, DirectBuffer directBuffer, int offset, int length) {
            final var copy = allocator.apply(length);
            copy.putBytes(0, directBuffer, offset, length);
            dest.put(id, copy);
        }
    }

    static class StringTagSerializer implements Serializer<CharSequence>, BufferEncoder {
        private CharSequence source;


        @Override
        public BufferEncoder withSource(CharSequence source) {
            this.source = source;
            return this;
        }

        @Override
        public int encode(MutableDirectBuffer buffer, int offset) {
            buffer.putStringWithoutLengthAscii(offset, source);
            return offset + source.length();
        }

        @Override
        public int calcNeededBytes() {
            return source.length();
        }
    }
}
