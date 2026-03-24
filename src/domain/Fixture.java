package domain;

import java.util.*;
import java.util.stream.Collectors;

public class Fixture {

    private final Map<Integer, MatchWeek> weeks;
    private int currentWeekNumber;

    public Fixture() {
        this.weeks = new LinkedHashMap<>();
        this.currentWeekNumber = 1;
    }

    // --- Fixture Generation (Round Robin, shuffled) ---
    // Call this once from Main with your list of teams.
    // Generates a full home & away schedule automatically.
    // Each team plays every other team exactly twice (home + away).

    public static Fixture generate(List<Team> teams) {
        if (teams == null || teams.size() < 2)
            throw new IllegalArgumentException("Need at least 2 teams to generate a fixture");

        Fixture fixture = new Fixture();

        // Shuffle so the schedule is different every game
        List<Team> shuffled = new ArrayList<>(teams);
        Collections.shuffle(shuffled);

        // Round-robin needs even number of teams
        // If odd, add a dummy "bye" team as placeholder
        if (shuffled.size() % 2 != 0) {
            shuffled.add(null); // null = bye week for one team
        }

        int numTeams = shuffled.size();
        int numRounds = numTeams - 1;       // rounds for single leg
        int matchesPerRound = numTeams / 2;

        // --- First leg (home fixtures) ---
        for (int round = 0; round < numRounds; round++) {
            MatchWeek week = new MatchWeek(round + 1);

            for (int match = 0; match < matchesPerRound; match++) {
                int homeIdx = (round + match) % (numTeams - 1);
                int awayIdx = (numTeams - 1 - match + round) % (numTeams - 1);

                // Last team stays fixed, rotates against all others
                if (match == 0) awayIdx = numTeams - 1;

                Team home = shuffled.get(homeIdx);
                Team away = shuffled.get(awayIdx);

                // Skip if either team is the bye placeholder
                if (home == null || away == null) continue;

                week.addMatch(new Match(home, away));
            }

            fixture.addWeek(week);
        }

        // --- Second leg (reverse fixtures, home/away swapped) ---
        // Also shuffle the order of second leg weeks for variety
        List<Integer> secondLegOrder = new ArrayList<>();
        for (int i = 0; i < numRounds; i++) secondLegOrder.add(i);
        Collections.shuffle(secondLegOrder);

        for (int i = 0; i < numRounds; i++) {
            int sourceRound = secondLegOrder.get(i);
            MatchWeek firstLegWeek = fixture.getWeek(sourceRound + 1);
            MatchWeek week = new MatchWeek(numRounds + i + 1);

            // Reverse home/away from the corresponding first leg week
            for (Match m : firstLegWeek.getMatches()) {
                week.addMatch(new Match(m.getAwayTeam(), m.getHomeTeam()));
            }

            fixture.addWeek(week);
        }

        return fixture;
    }

    // --- Building the fixture manually (still kept for flexibility) ---

    public void addWeek(MatchWeek week) {
        if (week == null) throw new IllegalArgumentException("Week cannot be null");
        if (weeks.containsKey(week.getWeekNumber()))
            throw new IllegalStateException("Week " + week.getWeekNumber() + " already exists");
        weeks.put(week.getWeekNumber(), week);
    }

    public void addMatchToWeek(int weekNumber, Match match) {
        getWeekOrThrow(weekNumber).addMatch(match);
    }

    // --- Week navigation ---

    public MatchWeek getCurrentWeek() {
        return getWeekOrThrow(currentWeekNumber);
    }

    public boolean advanceWeek() {
        if (!getCurrentWeek().isFullyPlayed())
            throw new IllegalStateException(
                    "Week " + currentWeekNumber + " is not fully played yet");

        int next = currentWeekNumber + 1;
        if (!weeks.containsKey(next)) return false;

        currentWeekNumber = next;
        return true;
    }

    public boolean isSeasonComplete() {
        return weeks.values().stream().allMatch(MatchWeek::isFullyPlayed);
    }

    // --- Queries ---

    public MatchWeek getWeek(int weekNumber) {
        return getWeekOrThrow(weekNumber);
    }

    public Optional<MatchWeek> findWeek(int weekNumber) {
        return Optional.ofNullable(weeks.get(weekNumber));
    }

    public List<MatchWeek> getAllWeeks() {
        return Collections.unmodifiableList(new ArrayList<>(weeks.values()));
    }

    public List<MatchWeek> getPlayedWeeks() {
        return weeks.values().stream()
                .filter(MatchWeek::isFullyPlayed)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<MatchWeek> getRemainingWeeks() {
        return weeks.values().stream()
                .filter(w -> !w.isFullyPlayed())
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Match> getMatchesForTeam(Team team) {
        if (team == null) throw new IllegalArgumentException("Team cannot be null");
        return weeks.values().stream()
                .flatMap(w -> w.getMatchesForTeam(team).stream())
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Match> getRemainingMatchesForTeam(Team team) {
        return getMatchesForTeam(team).stream()
                .filter(m -> m.getStatus() == MatchStatus.SCHEDULED)
                .collect(Collectors.toUnmodifiableList());
    }

    public Optional<MatchWeek> findWeekForMatch(Match match) {
        return weeks.values().stream()
                .filter(w -> w.hasMatch(match))
                .findFirst();
    }

    // --- Validation ---

    // Useful to call after generate() to double check correctness
    public boolean validate(List<Team> teams) {
        for (Team t1 : teams) {
            for (Team t2 : teams) {
                if (t1.equals(t2)) continue;

                long count = weeks.values().stream()
                        .flatMap(w -> w.getMatches().stream())
                        .filter(m -> m.getHomeTeam().equals(t1)
                                && m.getAwayTeam().equals(t2))
                        .count();

                if (count != 1) {
                    System.err.println("VALIDATION FAIL: "
                            + t1.getName() + " vs " + t2.getName()
                            + " appears " + count + " times (expected 1)");
                    return false;
                }
            }
        }
        return true;
    }

    // --- Getters ---

    public int getCurrentWeekNumber() { return currentWeekNumber; }
    public int getTotalWeeks()        { return weeks.size(); }

    // --- Private helper ---

    private MatchWeek getWeekOrThrow(int weekNumber) {
        MatchWeek w = weeks.get(weekNumber);
        if (w == null) throw new NoSuchElementException("Week not found: " + weekNumber);
        return w;
    }

    // --- Display ---

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Full Fixture ===\n");
        sb.append(String.format("Week %d of %d | Season complete: %s%n",
                currentWeekNumber, getTotalWeeks(), isSeasonComplete()));
        sb.append("-".repeat(40)).append("\n");
        weeks.values().forEach(w -> sb.append(w).append("\n"));
        return sb.toString();
    }
}