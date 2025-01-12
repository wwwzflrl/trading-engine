package app.trading.engine.io.tag;

import app.trading.engine.core.api.messaging.serialization.BufferEncoder;
import app.trading.engine.core.api.tag.TagId;
import app.trading.engine.core.api.tag.Tags;
import org.agrona.DirectBuffer;

public interface MutableTags extends Tags, BufferEncoder {
    void setIntTag(TagId id, int value);

    void setLongTag(TagId id, long value);

    void setUuidTag(TagId id, long msb, long lsb);

    void setBinaryTag(TagId id, BufferEncoder encoder);

    void setBinaryTag(TagId id, byte[] array, int offset, int length);

    void setBinaryTag(TagId id, DirectBuffer buffer, int offset, int length);

    void setBinaryTag(TagId id, CharSequence value);

    void unset(TagId id);

    void clear();

    void clone(Tags tags);


    static MutableTags newMutableTags() { return new SimpleMutableTags(); }
    static MutableTags newZgcMutableTags() { return new ZgcMutableTags(); }
}
