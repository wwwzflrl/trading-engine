package app.trading.engine.core.api.tag;

import app.trading.engine.core.api.API;
import app.trading.engine.core.api.contants.Team;

@API
public enum LpsTags implements TagId {
    Exchange(TagType.BINARY),
    Book(TagType.BINARY),
    INDEX(TagType.BINARY),
    Account(TagType.BINARY),
    RefType(TagType.BINARY)
    ;

    private final TagType type;

    LpsTags(final TagType type) { this.type = type; }

    @Override
    public Team team() {
        return Team.TEAM1;
    }

    @Override
    public TagType type() { return type; }
}
