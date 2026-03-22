package football;
import java.util.Random;
import sport.ScoringRule;
import domain.Match;
import domain.MatchResult;

public class FootballScoringRule implements ScoringRule {
    private final Random random = new Random();

    @Override
    public MatchResult generateResult(Match match){
        int homeGoals = random.nextInt(8);
        int awayGoals = random.nextInt(8);
        return new MatchResult(homeGoals,awayGoals);
    }
}
