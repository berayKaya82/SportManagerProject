package domain;

import sport.TieBreakerRule;

import java.util.*;

public class LeagueTable {
    private final Map<Team, StandingEntry> entries;
    private final List<Match> playedMatches;
    private final TieBreakerRule tieBreakerRule;

    public LeagueTable(TieBreakerRule tieBreakerRule) {
        if (tieBreakerRule == null)
            throw new IllegalArgumentException("TieBreakerRule cannot be null");
        this.entries = new LinkedHashMap<>();
        this.playedMatches = new ArrayList<>();
        this.tieBreakerRule = tieBreakerRule;
    }

    // --- Registration ---
    public void registerTeam(Team team) {
        if (team == null) throw new IllegalArgumentException("Team cannot be null");
        entries.putIfAbsent(team, new StandingEntry(team));//if there is no team add,
        // it prevents to add a team more than 1 time
    }
    // --- Update after a match ---

    public void recordMatch(Match match) {
        if (match == null || !match.isFinished()) {
            throw new IllegalArgumentException("Match must be finished to record");
        }
        if (playedMatches.contains(match)) {
            throw new IllegalStateException("Match already recorded");
        }

        StandingEntry home = getOrThrow(match.getHomeTeam());//if team is not registered gives error
        StandingEntry away = getOrThrow(match.getAwayTeam());

        home.recordResult(match.getResult(), true);
        away.recordResult(match.getResult(), false);

        playedMatches.add(match);
    }

    // --- Sorted standings ---
    public List<StandingEntry> getSortedStandings() {
        List<StandingEntry> sorted = new ArrayList<>(entries.values());
        sorted.sort((a, b) -> tieBreakerRule.compare(a, b, playedMatches));
        return Collections.unmodifiableList(sorted);
    }
    // --- Queries ---

    public StandingEntry getEntry(Team team) {
        return getOrThrow(team);
    }

    public int getPosition(Team team) {
        List<StandingEntry> sorted = getSortedStandings();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getTeam().equals(team)) return i + 1;
        }
        throw new NoSuchElementException("Team not in table: " + team);
    }

    public Team getLeader() {
        return getSortedStandings().get(0).getTeam();
    }

    public boolean isRegistered(Team team) {
        return entries.containsKey(team);
    }

    public int size() {
        return entries.size();
    }
    // --- Private helper ---

    private StandingEntry getOrThrow(Team team) {
        StandingEntry e = entries.get(team);
        if (e == null) throw new NoSuchElementException(
                "Team not registered: " + team
        );
        return e;
    }
    // --- Display ---
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s %-20s %-4s %-4s %-4s %-4s %-4s %-4s%n",
                "Pos", "Team", "Pts", "P", "W", "D", "L", "GD"));
        sb.append("-".repeat(52)).append("\n");

        List<StandingEntry> sorted = getSortedStandings();
        for (int i = 0; i < sorted.size(); i++) {
            StandingEntry e = sorted.get(i);
            sb.append(String.format("%-4d %-20s %-4d %-4d %-4d %-4d %-4d %-4d%n",
                    i + 1,
                    e.getTeam().getName(),
                    e.getPoints(),
                    e.getPlayed(),
                    e.getWins(),
                    e.getDraws(),
                    e.getLosses(),
                    e.getGoalDifference()
            ));
        }
        return sb.toString();
    }
}


