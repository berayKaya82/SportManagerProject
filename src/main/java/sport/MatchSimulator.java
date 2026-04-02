package sport;

import domain.Match;
import domain.MatchResult;

/**
 * Defines how a match is simulated for any sport.
 * Supports both full simulation and split-half control
 * so the user can intervene between halves/periods.
 */
public interface MatchSimulator {

    /** Simulates the entire match at once (used for AI matches). */
    MatchResult simulateMatch(Match match);

    /** Simulates only the first half/period and returns that partial result. */
    MatchResult playFirstHalf(Match match);

    /** Simulates the second half/period, combining with the first half result. */
    MatchResult playSecondHalf(Match match, MatchResult firstHalfResult);
}
