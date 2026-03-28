package domain;

public class Season {
    public enum Status{
        PENDING,
        ACTIVE,
        COMPLETED
    }

    private final int seasonNumber;
    private final League league;
    private Status status;
    private Team champion;

    public Season(int seasonNumber,League league){
        if (seasonNumber<1)
            throw new IllegalArgumentException("Season number must be positive");
        if (league==null)
            throw new IllegalArgumentException("League cannot be null");

        this.seasonNumber=seasonNumber;
        this.league=league;
        this.status=Status.PENDING;
        this.champion=null;
    }

    //---Setup---

    public void initialize(){
        if (status!=Status.PENDING)
            throw new IllegalArgumentException("Season already initialized");

        league.generateFixture();
        league.registerTeamsToTable();
        this.status=Status.ACTIVE;
    }
    //---Game FLow---
    public void startCurrentWeek(){
        ensureActive();
        league.startNextWeek();
    }
    public void recordWeekResults(java.util.Map<Match,MatchResult> results){
        ensureActive();
        league.recordWeekResults(results);
    }
    public void advanceWeek(){
        ensureActive();
        boolean hasNextWeek = league.advanceToNextWeek();
        if (!hasNextWeek) {
            completeSeason();
        }
    }
    private void completeSeason() {
        this.champion = league.getChampion();
        this.status = Status.COMPLETED;
    }

    //----Queries----
    public int getSeasonNumber() {return seasonNumber;}
    public League getLeague() {return league;}
    public Status getStatus() {return status;}
    public boolean isActive() {return status==Status.ACTIVE;}
    public boolean isCompleted() {return status==Status.COMPLETED;}

    public Team getChampion(){
        if (!isCompleted())
            throw new IllegalArgumentException(
                    "Season is not completed yet. No champion determined.");
        return champion;
    }
    public MatchWeek getCurrentWeek(){
        ensureActive();
        return league.getCurrentWeek();
    }
    public LeagueTable getLeagueTable(){
        return league.getLeagueTable();
    }
    public Fixture getFixture(){
        return league.getFixture();
    }

    //----Private Helpers-----
    private void ensureActive(){
        if (status==Status.PENDING)
            throw new IllegalArgumentException(
                    "Season not started yet. Call initialize() first.");
        if (status==Status.COMPLETED)
            throw new IllegalArgumentException(
                    "Season is already completed.");
    }
    //---Display---
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Season ").append(seasonNumber).append(" ===\n");
        sb.append("League : ").append(league.getName()).append("\n");
        sb.append("Status : ").append(status).append("\n");
        if (isCompleted()) {
            sb.append("Champion: ").append(champion.getName()).append("\n");
        } else if (isActive()) {
            sb.append("Current week: ")
                    .append(league.getCurrentWeek().getWeekNumber())
                    .append(" of ")
                    .append(league.getFixture().getTotalWeeks())
                    .append("\n");
        }
        sb.append("\n").append(league.getLeagueTable());
        return sb.toString();
    }
}

