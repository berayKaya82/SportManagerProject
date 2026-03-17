package domain;

public class MatchResult {
    private final int homeGoals;
    private final int awayGoals;

    public MatchResult(int homeGoals, int awayGoals) {
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }

    public int getHomeGoals() { return homeGoals; }
    public int getAwayGoals() { return awayGoals; }

    public int getGoalDifference() { return homeGoals - awayGoals; }
}

