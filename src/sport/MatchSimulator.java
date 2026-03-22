package sport;
import domain.Match;
import domain.MatchResult;

public interface MatchSimulator {
    MatchResult simulateMatch(Match match);
}
