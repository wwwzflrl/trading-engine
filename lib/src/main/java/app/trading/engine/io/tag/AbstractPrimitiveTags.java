package app.trading.engine.io.tag;

import app.trading.engine.core.api.expection.Failure;
import app.trading.engine.core.api.messaging.serialization.BufferEncoder;
import org.apache.logging.log4j.util.StringBuilderFormattable;

import java.util.Map;

abstract class AbstractPrimitiveTags<T extends Map<?, ?>> implements BufferEncoder, StringBuilderFormattable {
    final T tags;

    AbstractPrimitiveTags(final T tags) { this.tags = tags; }
    int size() { return tags.size(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return getClassForEqualityCheck().isInstance(obj) && tagsAreEqualTo(((AbstractPrimitiveTags<T>) obj).tags);
    }

    abstract Class<?> getClassForEqualityCheck();

    final boolean tagsAreEqualTo(final T other) {
        if (other.size() == tags.size()) {
            return tagEntriesAreEqualTo(other);
        }
        return false;
    }

    final boolean tagEntriesAreEqualTo(final T other) {
        try {
            assertEntriesAreEqual(other);
            return true;
        } catch (final Failure fail) {
            return false;
        }
    }

    abstract void assertEntriesAreEqual(T other);

    @Override
    public int hashCode() {
        return tags.hashCode();
    }

    @Override
    public void formatTo(StringBuilder buffer) {
        // TODO addString;
    }
}
