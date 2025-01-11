package app.trading.engine.core.api.tag;

import app.trading.engine.core.api.TeamSpecificEnum;
import org.apache.logging.log4j.util.StringBuilderFormattable;

public interface TagId extends TeamSpecificEnum, StringBuilderFormattable {

    TagType type();

    enum TagType {
        INT,
        LONG,
        /**
         * 2 longs, 16 bytes
         */
        UUID,
        /**
         * Lesst than 255 bytes
         */
        BINARY
    }

    @Override
    default void formatTo(StringBuilder builder) {
        builder.append(team())
                .append('.')
                .append(name())
                .append('=');
    }
}
