package app.trading.engine.io.tag;

import app.trading.engine.core.collections.MutableUuid;

public class SimpleUuidTags extends AbstractUuidTags {

    @Override
    void set(int id, long msb, long lsb) {
        final MutableUuid uuid = new MutableUuid();
        uuid.lsb = lsb;
        uuid.msb = msb;
        tags.put(id, uuid);
    }

    @Override
    void unset(int id) {
        tags.remove(id);
    }

    @Override
    void clear() {
        tags.clear();
    }
}
