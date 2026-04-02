package sport;

import domain.Match;
import domain.MatchResult;

/**
 * Defines how a match is simulated for any sport.
 *
 * <p>Each sport can have a different number of periods
 * (e.g. football=2 halves, basketball=4 quarters).
 * The UI loops through periods, allowing the user to
 * intervene (tactics, substitutions) between each one.</p>
 */
public interface MatchSimulator {

    /** Simulates the entire match at once (used for AI matches). */
    MatchResult simulateMatch(Match match);

    /** Returns the number of periods for this sport (football=2, basketball=4, etc.). */
    int getNumberOfPeriods();

    /**
     * Plays a single period and returns the cumulative result.
     *
     * @param match         the match being played
     * @param periodNumber  which period to play (1-based)
     * @param currentResult accumulated result from previous periods, null for the first period
     * @return cumulative result including this period
     */
    MatchResult playPeriod(Match match, int periodNumber, MatchResult currentResult);
}
