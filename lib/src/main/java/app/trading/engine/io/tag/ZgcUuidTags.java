package app.trading.engine.io.tag;

import app.trading.engine.core.api.tag.Tags;
import app.trading.engine.core.collections.MutableUuid;
import app.trading.engine.mem.pool.ObjPool;

import static app.trading.engine.core.noop.Noops.NO_CONSUMER;

public class ZgcUuidTags extends AbstractUuidTags {
    private static final ObjPool<MutableUuid> UUID_OBJ_POOL = ObjPool.newPool(MutableUuid::new, NO_CONSUMER);


    @Override
    void set(int id, long msb, long lsb) {
        final var uuid = UUID_OBJ_POOL.get();
        uuid.msb = msb;
        uuid.lsb = lsb;
        tags.put(id, uuid);
    }

    @Override
    void unset(int id) {
        final MutableUuid removed = tags.remove(id);
        if (removed != null) {
            UUID_OBJ_POOL.release(removed);
        }
    }

    @Override
    void clear() {
        tags.values().forEach(UUID_OBJ_POOL::release);
        tags.clear();
    }
}
