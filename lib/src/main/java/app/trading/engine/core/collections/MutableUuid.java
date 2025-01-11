package app.trading.engine.core.collections;

import app.trading.engine.core.api.API;
import app.trading.engine.core.api.messaging.serialization.SerializablePayload;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.apache.logging.log4j.util.StringBuilderFormattable;

import static app.trading.engine.core.math.HashCodes.hashShift;

public class MutableUuid implements SerializablePayload, StringBuilderFormattable {
    public static final int ENCODE_LEN = 16;
    public long msb;
    public long lsb;

    @API
    public void clone(final MutableUuid uuid) {
        msb = uuid.msb;
        lsb = uuid.lsb;
    }

    @Override
    public int decode(DirectBuffer buffer, int offset) {
        msb = buffer.getLong(offset);
        lsb = buffer.getLong(offset + 8);
        return offset + ENCODE_LEN;
    }

    @Override
    public int encode(MutableDirectBuffer buffer, int offset) {
        buffer.putLong(offset, msb);
        buffer.putLong(offset + 8, lsb);
        return ENCODE_LEN;
    }

    @Override
    public int calcNeededBytes() {
        return ENCODE_LEN;
    }

    @Override
    public void formatTo(StringBuilder buffer) {
        buffer.append(msb).append('-').append(lsb);
    }

    @Override
    public int hashCode() { return hashShift(Long.hashCode(msb)) ^ Long.hashCode(lsb); }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MutableUuid) {
            final MutableUuid other = (MutableUuid) obj;
            return msb == other.msb && lsb == other.lsb;
        }
        return false;
    }

}
