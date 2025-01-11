package app.trading.engine.core.api.messaging.serialization;

import app.trading.engine.core.api.contants.Team;
import org.jetbrains.annotations.Range;

/**
 * Order matters, do not reorder
 */
public interface MsgType {
    Team team();

    String name();

    int ordinal();

    /**0 - 1111111111111111110000000000
     * 11 -28 is for offset
     * 1-10 is for team
     */
    @Range(from = 0, to = 268434432)
    default int offset() { return 0; }

    default int id() { return id(this); }

    static int id(final MsgType type) { return (type.team().ordinal() << 28) | type.offset() | type.ordinal(); }
}
