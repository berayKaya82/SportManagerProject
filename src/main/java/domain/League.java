package domain;

import sport.ISport;

import java.util.*;
import java.util.stream.Collectors;


public class League {
    private final String name;
    private final Gender gender;
    private final ISport sport;
    private final List<Team> teams;
    private final LeagueTable leagueTable;
    private Fixture fixture;

    public League(String name, Gender gender, List<Team> teams, ISport sport) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("League name cannot be empty");
        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");
        if (sport == null)
            throw new IllegalArgumentException("Sport cannot be null");
        if (teams == null || teams.size() < 2)
            throw new IllegalArgumentException("Need at least 2 teams");
        if (!areTeamsEligible(teams, gender))
            throw new IllegalArgumentException(
                    "All teams must match the league gender:" + gender);

        this.name = name;
        this.gender = gender;
        this.sport = sport;
        this.teams = Collections.unmodifiableList(new ArrayList<>(teams));
        this.leagueTable = new LeagueTable();
        this.fixture = null;
    }

    // --- Setup ---

    public void generateFixture() {
        checkFixtureReady(false);
        this.fixture = Fixture.generate(teams);
    }

    public void registerTeamsToTable() {
        if (leagueTable.size() > 0)
            throw new IllegalStateException("Teams already registered to table");
        teams.forEach(leagueTable::registerTeam);
    }

    // --- Game flow ---

    // Call this before simulating the current week's matches
    public MatchWeek startNextWeek() {
        checkFixtureReady(true);
        MatchWeek current = fixture.getCurrentWeek();
        if (current.hasStarted())
            throw new IllegalStateException("Week" + current.getWeekNumber() +
                    "already started");
        current.getMatches().forEach(Match::startMatch);
        return current;
    }

    // Call this after all matches in the current week are finished
    // Records every finished match into the league table
    public void recordWeekResults(Map<Match, MatchResult> results) {
        checkFixtureReady(true);
        if (results == null || results.isEmpty())
            throw new IllegalArgumentException("Result cannot be null or empty");
        MatchWeek current = fixture.getCurrentWeek();

        for (Map.Entry<Match, MatchResult> entry : results.entrySet()) {
            Match match = entry.getKey();
            MatchResult result = entry.getValue();

            if (!current.hasMatch(match))
                throw new IllegalArgumentException(
                        "Match does not belong to current week");
            leagueTable.recordMatch(match);
        }
    }

    // Call this after recordWeekResults() to move to the next week
    // Returns true if there is a next week, false if season is over
    public boolean advanceToNextWeek() {
        checkFixtureReady(true);
        return fixture.advanceWeek();
    }

    public boolean isSeasonComplete() {
        checkFixtureReady(true);
        return fixture.isSeasonComplete();
    }

    // --- Queries ---

    public String getName() {return name;}
    public Gender getGender() {return gender;}
    public ISport getSport() {return sport;}
    public List<Team> getTeams() {return teams;}
    public LeagueTable getLeagueTable() {return leagueTable;}
    public Fixture getFixture() {return fixture;}

    public MatchWeek getCurrentWeek() {
        checkFixtureReady(true);
        return fixture.getCurrentWeek();
    }

    public Team getChampion() {
        if (!isSeasonComplete())
            throw new IllegalStateException(
                    "Season is not complete yet ");
        return leagueTable.getLeader();
    }

    public List<Team> getStandingsAsTeams() {
        return leagueTable.getSortedStandings()
                .stream().map(StandingEntry::getTeam)
                .collect(Collectors.toList());
    }

    public List<Match> getRemainMatchesForTeam(Team team) {
        checkFixtureReady(true);
        return fixture.getRemainingMatchesForTeam(team);
    }

    //-----Validation------
    public boolean validateFixture() {
        checkFixtureReady(true);
        return fixture.validate(teams);
    }

    //-----Private Helpers-----
    private void checkFixtureReady(boolean expectGenerated) {
        if (expectGenerated && fixture==null)
            throw new IllegalStateException(
                    "Fixture not generated yet.Call generateFixture() first.");
        if (!expectGenerated && fixture !=null)
            throw new IllegalStateException("Fixture already generated.");
    }
    //Gender check - evert teams gender must match the league gender
    private boolean areTeamsEligible(List<Team> teams,Gender gender) {
        return teams.stream().allMatch(t -> t.getGender() == gender);
    }
    public int getTotalWeeks() {
        checkFixtureReady(true);
        return fixture.getTotalWeeks();
    }

    public boolean containsTeam(Team team) {
        return teams.contains(team);
    }
    // --- Display ---

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== League: ").append(name).append(" ===\n");
        sb.append("Sport: ").append(sport.getSportName()).append("\n");
        sb.append("Gender: ").append(gender).append("\n");
        sb.append("Teams: ").append(teams.size()).append("\n");
        if (fixture != null) {
            sb.append("Week: ").append(fixture.getCurrentWeekNumber())
                    .append(" of ").append(fixture.getTotalWeeks()).append("\n");
            sb.append("Season complete: ").append(isSeasonComplete()).append("\n");
        } else {
            sb.append("Fixture: not generated yet\n");
        }
        sb.append("\n").append(leagueTable);
        return sb.toString();
    }



    }

