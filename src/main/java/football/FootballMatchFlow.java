package football;
import sport.MatchFlow;


public class FootballMatchFlow  implements MatchFlow {
    private final boolean allowsDraw;
    private final boolean hasExtraTime;
    private final boolean hasPenaltyShootout;

    public FootballMatchFlow(boolean allowsDraw, boolean hasExtraTime, boolean hasPenaltyShootout) {
        this.allowsDraw=allowsDraw;
        this.hasExtraTime=hasExtraTime;
        this.hasPenaltyShootout =hasPenaltyShootout;
    }

    public FootballMatchFlow() {
        this.allowsDraw =true;
        this.hasExtraTime = false;
        this.hasPenaltyShootout = false;

    }

    @Override
    public boolean allowsDraw() {
        return allowsDraw;
    }

    @Override
    public boolean hasExtraTime() {
        return hasExtraTime;
    }

    @Override
    public boolean hasPenaltyShootout() {
        return hasPenaltyShootout;
    }
}

