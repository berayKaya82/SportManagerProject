package sport;

public interface MatchFlow {
    boolean allowsDraw();

    boolean hasExtraTime();

    boolean hasPenaltyShootout();
}
