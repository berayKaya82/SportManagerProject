package application;

import domain.*;
import sport.ISport;
import java.util.*;


public class SeasonCycleManager {
    private final String leagueName;
    private final ISport sport;
    private final Gender gender;
    private final List<Team> teams;
    private final List<Season> allSeasons;

    private Season currentSeason;
    private int seasonNumber;

    public SeasonCycleManager(String leagueName, ISport sport, Gender gender, List<Team> teams) {
        if (leagueName == null || leagueName.isBlank())
            throw new IllegalArgumentException("League name cannot be empty");
        if (sport == null)
            throw new IllegalArgumentException("Sport cannot be null");
        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");
        if (teams == null || teams.size() < 2)
            throw new IllegalArgumentException("Need at least 2 teams");

        this.leagueName  = leagueName;
        this.sport       = sport;
        this.gender      = gender;
        this.teams       = Collections.unmodifiableList(new ArrayList<>(teams));
        this.allSeasons  = new ArrayList<>();
        this.seasonNumber = 0;
        this.currentSeason = null;
    }

    //---Setup---
    public void startNewSeason() {
        if (currentSeason != null && !currentSeason.isCompleted())
            throw new IllegalArgumentException("Current season is not completed yet");

        seasonNumber++;
        League league = new League(leagueName,gender, teams,sport);
        currentSeason = new Season(seasonNumber, league);
        currentSeason.initialize();
        allSeasons.add(currentSeason);
    }
    //----Game flow----
    public void playCurrentWeek(Map<Match,MatchResult> results){
        ensureSeasonActive();
        currentSeason.startCurrentWeek();
        currentSeason.recordWeekResults(results);
        currentSeason.advanceWeek();
    }
    // --- Queries: Season ---
        public Season getCurrentSeason(){return currentSeason;}
        public int getSeasonNumber()       { return seasonNumber; }
        public List<Season> getAllSeasons() { return Collections.unmodifiableList(allSeasons); }

    public boolean isSeasonComplete(){
        ensureSeasonExists();
        return currentSeason.isCompleted();
    }
    public Team getChampionOfSeason(int seasonNumber){
        return allSeasons.stream()
                .filter(s->s.getSeasonNumber()==seasonNumber)
                .findFirst().orElseThrow(()->new NoSuchElementException(
                        "Season not found :" + seasonNumber)).getChampion();
    }

    // --- Queries: League & Table ---
    public League getCurrentLeague(){
       ensureSeasonExists();
       return currentSeason.getLeague();
    }
    public List<StandingEntry>getCurrentStandings(){
        ensureSeasonExists();
        return currentSeason.getLeagueTable().getSortedStandings();
    }
    public int getTeamPosition(Team team){
        ensureSeasonExists();
        return currentSeason.getLeagueTable().getPosition(team);
    }
    public StandingEntry getTeamEntry(Team team){
        ensureSeasonExists();
        return currentSeason.getLeagueTable().getEntry(team);
    }

    //---Queries: Fixture & Week---
    public MatchWeek getCurrentWeek(){
        ensureSeasonActive();
        return currentSeason.getCurrentWeek();
    }
    public int getCurrentWeekNumber() {
        ensureSeasonActive();
        return currentSeason.getCurrentWeek().getWeekNumber();
    }
    public int getTotalWeeks() {
        ensureSeasonExists();
        return currentSeason.getFixture().getTotalWeeks();
    }
    public List<MatchWeek> getRemainingWeeks() {
        ensureSeasonExists();
        return currentSeason.getFixture().getRemainingWeeks();
    }
    public List<Match> getMatchesForTeam(Team team) {
        ensureSeasonExists();
        return currentSeason.getFixture().getMatchesForTeam(team);
    }
    public List<Match> getRemainingMatchesForTeam(Team team) {
        ensureSeasonExists();
        return currentSeason.getLeague().getRemainMatchesForTeam(team);
    }

    //---Private Helpers---
    private void ensureSeasonExists() {
        if (currentSeason == null)
            throw new IllegalStateException(
                    "No season started yet. Call startNewSeason() first.");
    }
    private void ensureSeasonActive() {
        ensureSeasonExists();
        if (!currentSeason.isActive())
            throw new IllegalStateException(
                    "No active season. Current status: " + currentSeason.getStatus());
    }

    // --- Display ---

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SeasonCycleManager ===\n");
        sb.append("League : ").append(leagueName).append("\n");
        sb.append("Sport  : ").append(sport.getSportName()).append("\n");
        sb.append("Gender : ").append(gender).append("\n");
        sb.append("Teams  : ").append(teams.size()).append("\n");
        sb.append("Seasons played: ").append(allSeasons.size()).append("\n");
        if (currentSeason != null) {
            sb.append("\n").append(currentSeason);
        }
        return sb.toString();
    }

}
