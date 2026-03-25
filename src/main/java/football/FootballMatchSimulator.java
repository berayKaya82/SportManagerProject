package football;
import domain.Match;
import domain.MatchResult;
import sport.*;

import java.util.Random;

public class FootballMatchSimulator  implements MatchSimulator {
    final MatchFlow matchFlow;
    final RosterRule rosterRule;
    final ScoringRule scoringRule;
    final TieBreakerRule tieBreakerRule;

    public FootballMatchSimulator(MatchFlow matchFlow, RosterRule rosterRule, ScoringRule scoringRule, TieBreakerRule tieBreakerRule) {
        this.matchFlow = matchFlow;
        this.rosterRule = rosterRule;
        this.scoringRule = scoringRule;
        this.tieBreakerRule = tieBreakerRule;
    }
    private final Random random = new Random(2);

    private MatchResult applyExtraTime(MatchResult currentResult){
        int extraHomeGoals = random.nextInt(2);
        int extraAwayGoals = random.nextInt(2);
        return new MatchResult((currentResult.getHomeGoals()+extraHomeGoals ),(currentResult.getAwayGoals()+extraAwayGoals));
    }

    private MatchResult applyPenaltyShootout(MatchResult currentResult){
        boolean homeWins = random.nextBoolean();
        if(homeWins)return new MatchResult(currentResult.getHomeGoals() + 1 , currentResult.getAwayGoals());
        return new MatchResult(currentResult.getHomeGoals(),currentResult.getAwayGoals()+1);
    }
    @Override
    public MatchResult simulateMatch(Match match){
        if(match == null) throw new IllegalArgumentException("Match cannot be null");
        if(!rosterRule.isValidRoster(match.getHomeTeam() )|| !rosterRule.isValidRoster(match.getAwayTeam())){
            throw new IllegalArgumentException("One or Both teams have invalid rosters");
        }

        match.startMatch();
        MatchResult result = scoringRule.generateResult(match);
        if (result.getGoalDifference() == 0 && !matchFlow.allowsDraw()) {
            if (matchFlow.hasExtraTime()) {
                result = applyExtraTime(result);
            }
            if (result.getGoalDifference() == 0 && matchFlow.hasPenaltyShootout()) {
                result = applyPenaltyShootout(result);
            }
        }
        match.finishMatch(result);
        return result;
    }
}
