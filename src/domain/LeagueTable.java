package domain;

import java.util.*;

public class LeagueTable {
    private final Map<Team, StandingEntry> entries;
    private final List<Match> playedMatches;

    public LeagueTable() {
        this.entries = new LinkedHashMap<>();
        this.playedMatches = new ArrayList<>();
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
    public List<StandingEntry> getSortedStandings(){
        List<StandingEntry>sorted = new ArrayList<>(entries.values());
        sorted.sort(this::compareEntries);
        return Collections.unmodifiableList(sorted);
    }
    private int compareEntries(StandingEntry a, StandingEntry b) {
        // 1. Points
        int cmp = Integer.compare(b.getPoints(), a.getPoints());
        if (cmp != 0) return cmp;

        // 2. Head-to-head
        cmp = compareHeadToHead(a.getTeam(), b.getTeam());
        if (cmp != 0) return cmp;

        // 3. Goal difference
        cmp = Integer.compare(b.getGoalDifference(), a.getGoalDifference());
        if (cmp != 0) return cmp;

        // 4. Coin toss — deterministic via hash, keeps sort contract valid
        return Integer.compare(
                System.identityHashCode(a.getTeam()),
                System.identityHashCode(b.getTeam())
        );
    }
    private int compareHeadToHead(Team teamA, Team teamB) {
        int pointsA = 0, pointsB = 0;

        for (Match m : playedMatches) {
            boolean aHome = m.getHomeTeam().equals(teamA) && m.getAwayTeam().equals(teamB);
            boolean bHome = m.getHomeTeam().equals(teamB) && m.getAwayTeam().equals(teamA);
            if (!aHome && !bHome) continue;

            if (m.isDraw()) {
                pointsA++;
                pointsB++;
            } else {
                if (m.getWinner().equals(teamA)) pointsA += 2;
                else                             pointsB += 2;
            }
        }

        return Integer.compare(pointsB, pointsA); // higher = ranked first
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


