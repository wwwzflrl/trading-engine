package app.trading.engine.core.api.messaging.serialization;

import app.trading.engine.core.api.API;
import app.trading.engine.core.api.contants.Team;

public enum LpsMsgTypes implements MsgType {
    // take by aeron
    Invalid,
    Checkpoint,
    CcyUpdate,
    InstStaticUpdate,
    CheckpointLite,
    PercentileMetricData,
    CheckpointReport,
    CheckpointOnBehalf,
    TracedScenarioDeclaration,
    Ping,
    Pong,
    TracingGroupDeclaration,
    XWanStatus,
    XWanPayloadSingleMsg,
    XWanPayloadMultipleFramesStart,
    XWanPayloadContinuation,
    XWanPayloadCompletion,
    ;

    @API
    public static final int GWS_MSG_TYPE_OFFSET = 4096; // 010000 00000000

    @API
    public static final int SEQUENCER_TYPE_OFFSET = 16384; // 1 000000 00000000

    @Override
    public Team team() {
        return Team.TEAM1;
    }
}
