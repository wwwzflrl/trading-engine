package app.trading.engine.core.api.contants;

/**
 * Cannot exceed 16 values.
 * Ordinal matters
 * If a value is to be removed, rebane it to {RECYCLED_<NAME>}
 */
public enum Team {
    TEAM1,
    TEAM2,
    TEAM3;

    public static final Team[] TEAMS = values();
}
