package football;
import domain.Match;
import domain.MatchResult;
import sport.MatchFlow;
import sport.MatchSimulator;
import sport.RosterRule;
import java.util.Random;

public class FootballMatchSimulator  implements MatchSimulator {
    private final MatchFlow matchFlow;
    private final RosterRule rosterRule;
    private final FootballScoringRule scoringRule;
    private final Random random;


    public FootballMatchSimulator(MatchFlow matchFlow, RosterRule rosterRule, FootballScoringRule scoringRule,Random random) {
        if(matchFlow == null)throw new IllegalArgumentException("MatchFlow can not be null");
        if(rosterRule == null)throw new IllegalArgumentException("RosterRule can not be null");
        if(scoringRule == null)throw new IllegalArgumentException("ScoringRule can not be null");
        if(random == null)throw new IllegalArgumentException("Random can not be null");

        this.matchFlow = matchFlow;
        this.rosterRule = rosterRule;
        this.scoringRule = scoringRule;
        this.random=random;
    }
    public MatchResult playFirstHalf(Match match){
        validateMatch(match);
        validateRosters(match);

        return scoringRule.generateHalfResult(match);
    }
    public MatchResult playSecondHalf(Match match,MatchResult firstHalfResult){
        validateMatch(match);
        if(firstHalfResult == null){
            throw new IllegalArgumentException("First Half Result can not be null");
        }
        MatchResult secondHalfResult =scoringRule.generateHalfResult(match);
        int finalHomeGoals = firstHalfResult.getHomeGoals()+ secondHalfResult.getHomeGoals();
        int finalAwayGoals = firstHalfResult.getAwayGoals()+ secondHalfResult.getAwayGoals();

        MatchResult finalResult = new MatchResult(finalHomeGoals,finalAwayGoals);

        if(!matchFlow.allowsDraw() && finalHomeGoals == finalAwayGoals){
            finalResult = resolveDraw(finalResult);
        }
        match.finishMatch(finalResult);
        return finalResult;
    }
    @Override
    public MatchResult simulateMatch(Match match){
        MatchResult firstHalfResult = playFirstHalf(match);
        return playSecondHalf(match,firstHalfResult);
    }
    private void validateMatch(Match match){
        if(match == null)throw new IllegalArgumentException("Match can not be null");
    }
    private void validateRosters(Match match){
        if(!rosterRule.isValidRoster(match.getHomeTeam())){
            throw new IllegalStateException("The home team lineup is not suitable");
        }
        if(!rosterRule.isValidRoster(match.getAwayTeam())){
            throw new IllegalStateException("The away team lineup is not suitable");
        }
    }
    private MatchResult resolveDraw(MatchResult currentResult){
        int homeGoals = currentResult.getHomeGoals();
        int awayGoals = currentResult.getAwayGoals();

        if(matchFlow.hasExtraTime()){
            homeGoals += random.nextInt(3);
            awayGoals += random.nextInt(3);
        }
        if(homeGoals == awayGoals && matchFlow.hasPenaltyShootout()){
            if(random.nextBoolean()){
                homeGoals +=1;
            }else{
                awayGoals +=1;
            }
        }
        return new MatchResult(homeGoals,awayGoals);
    }
}
