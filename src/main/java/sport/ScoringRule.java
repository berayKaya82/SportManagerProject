package sport;
import domain.Match;
import domain.MatchResult;

public interface ScoringRule {
    MatchResult generateResult(Match match);

}
