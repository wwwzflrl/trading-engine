package app.trading.engine.io.tag;

import app.trading.engine.core.api.tag.TagId;
import org.agrona.concurrent.UnsafeBuffer;

public class SimpleMutableTags extends AbstractMutableTags {

    SimpleMutableTags() { super(new SimpleUuidTags(), needed -> new UnsafeBuffer(new byte[needed]));}

    @Override
    UnsafeBuffer getWrappedBuffer(int id, int length) {
        final UnsafeBuffer created = new UnsafeBuffer(new byte[length]);
        binaries.put(id, created);
        return created;
    }

    @Override
    void unsetBinaryTag(TagId id) {
        binaries.remove(TagIdTranslator.translate(id));
    }

    @Override
    void clearBinaryTags() {
        binaries.clear();
    }

    @Override
    public void setBinaryTag(TagId id, CharSequence value) {
        setBinaryTag(id, new StringTagSerializer().withSource(value));
    }
}
