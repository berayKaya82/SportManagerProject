package domain;
import java.util.*;

public class MatchWeek {
        private final int weekNumber;
        private final List<Match> matches;

        public MatchWeek(int weekNumber) {
            if (weekNumber < 1) throw new IllegalArgumentException("Week number must be positive");
            this.weekNumber = weekNumber;
            this.matches = new ArrayList<>();
        }

        // --- Match management ---

        public void addMatch(Match match) {
            if (match == null) throw new IllegalArgumentException("Match cannot be null");
            if (hasMatch(match)) throw new IllegalStateException("Match already exists in this week");
            matches.add(match);
        }

        public void removeMatch(Match match) {
            if (!matches.remove(match))
                throw new NoSuchElementException("Match not found in week " + weekNumber);
        }

        // --- Queries ---

        public boolean hasMatch(Match match) {
            return matches.contains(match);
        }

        public boolean isFullyPlayed() {
            return !matches.isEmpty() && matches.stream()
                    .allMatch(Match::isFinished);
        }

        public boolean hasStarted() {
            return matches.stream().anyMatch(m ->
                    m.getStatus() == MatchStatus.IN_PROGRESS || m.isFinished());
        }

        public List<Match> getFinishedMatches() {
            return matches.stream()
                    .filter(Match::isFinished)
                    .collect(java.util.stream.Collectors.toUnmodifiableList());
        }

        public List<Match> getPendingMatches() {
            return matches.stream()
                    .filter(m -> m.getStatus() == MatchStatus.SCHEDULED)
                    .collect(java.util.stream.Collectors.toUnmodifiableList());
        }

        public Optional<Match> findMatch(Team home, Team away) {
            return matches.stream()
                    .filter(m -> m.getHomeTeam().equals(home)
                            && m.getAwayTeam().equals(away))
                    .findFirst();
        }

        public List<Match> getMatchesForTeam(Team team) {
            return matches.stream()
                    .filter(m -> m.involvedTeam(team))
                    .collect(java.util.stream.Collectors.toUnmodifiableList());
        }

        // --- Getters ---

        public int getWeekNumber()    { return weekNumber; }
        public List<Match> getMatches() { return Collections.unmodifiableList(matches); }
        public int getMatchCount()    { return matches.size(); }

        // --- Display ---

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Week ").append(weekNumber).append(" ===\n");
            if (matches.isEmpty()) {
                sb.append("  No matches scheduled.\n");
            } else {
                matches.forEach(m -> sb.append("  ").append(m).append("\n"));
            }
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MatchWeek)) return false;
            return weekNumber == ((MatchWeek) o).weekNumber;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(weekNumber);
        }
    }

