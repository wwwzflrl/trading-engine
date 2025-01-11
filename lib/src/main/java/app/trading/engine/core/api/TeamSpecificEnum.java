package app.trading.engine.core.api;

import app.trading.engine.core.api.contants.Team;

public interface TeamSpecificEnum {
    Team team();

    String name();

    int ordinal();
}
