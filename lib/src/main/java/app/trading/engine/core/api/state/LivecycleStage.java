package app.trading.engine.core.api.state;

public enum LivecycleStage {
    INIT {
        @Override
        public boolean canShiftTo(LivecycleStage stage) {
            return stage == RECOVERY;
        }
    },

    RECOVERY {
        @Override
        public boolean canShiftTo(LivecycleStage stage) {
            switch (stage) {
                case STANDBY:
                case LIVE:
                    return true;
                default:
                    return false;
            }
        }
    },

    STANDBY {
        @Override
        public boolean canShiftTo(LivecycleStage stage) {
            return stage == LIVE;
        }
    },

    LIVE {
        @Override
        public boolean canShiftTo(LivecycleStage stage) {
            return stage == STANDBY;
        }
    };

    public abstract boolean canShiftTo(LivecycleStage stage);
}
