package app.trading.engine.core.api.messaging.arbiter;

import app.trading.engine.core.api.messaging.MsgHeader;

public interface MsgHandler<MSG> {
    /**
     * Implementation should never block.
     * Both header and payload are re-used upon returning. Do safe-copy if needed
     * @param msgHeader
     * @param message
     * @param nowNs
     */
    void handle(MsgHeader msgHeader, MSG message, long nowNs);
}
