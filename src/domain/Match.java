package domain;

import java.util.Objects;

public class Match {
    private final Team homeTeam;
    private final Team awayTeam;
    private MatchResult result;
    private MatchStatus status;

    public Team getHomeTeam()    { return homeTeam; }
    public Team getAwayTeam()    { return awayTeam; }
    public MatchResult getResult() { return result; }
    public MatchStatus getStatus() { return status; }

    public void setResult(MatchResult result) {
        this.result = result;
    }

    public Match(Team homeTeam, Team awayTeam) {
        if (homeTeam == null || awayTeam == null) {
            throw new IllegalArgumentException("Teams cannot be null");
        }
        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("A team cannot play against itself");
        }
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.setResult(null);
        this.status = MatchStatus.SCHEDULED;
    }

    // --- Actions ---
    public void startMatch() {
        if (status != MatchStatus.SCHEDULED) {
            throw new IllegalStateException("Match already started or finished");
        }
        this.status = MatchStatus.IN_PROGRESS;
    }

    public void finishMatch(MatchResult result) {
        if (status != MatchStatus.IN_PROGRESS) {
            throw new IllegalStateException("Match must be in progress to finish");
        }
        if (result == null) {
            throw new IllegalArgumentException("Result cannot be null");
        }
        this.setResult(result);
        this.status = MatchStatus.FINISHED;
    }

    // --- Queries ---
    public boolean isFinished() {
        return status == MatchStatus.FINISHED;
    }

    public Team getWinner() {
        if (!isFinished()) return null;
        int diff = result.getGoalDifference();
        if (diff > 0) return homeTeam;
        if (diff < 0) return awayTeam;
        return null; // draw
    }

    public boolean isDraw() {
        return isFinished() && result.getGoalDifference() == 0;
    }

    public boolean involvedTeam(Team team) {
        return homeTeam.equals(team) || awayTeam.equals(team);
    }

    // --- Object overrides ---

    @Override
    public String toString() {
        String score = (result != null)
                ? result.getHomeGoals() + " - " + result.getAwayGoals()
                : "vs";
        return homeTeam.getName() + " " + score + " " + awayTeam.getName()
                + " [" + status + "]";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        Match m = (Match) o;
        return homeTeam.equals(m.homeTeam) && awayTeam.equals(m.awayTeam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeTeam, awayTeam);
    }
}
