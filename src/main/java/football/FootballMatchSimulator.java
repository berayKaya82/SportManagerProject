package football;
import domain.Match;
import domain.MatchResult;
import sport.MatchFlow;
import sport.MatchSimulator;
import sport.RosterRule;
import java.util.Random;

public class FootballMatchSimulator implements MatchSimulator {

    private static final int FOOTBALL_PERIODS = 2;

    private final MatchFlow matchFlow;
    private final RosterRule rosterRule;
    private final FootballScoringRule scoringRule;
    private final Random random;

    public FootballMatchSimulator(MatchFlow matchFlow, RosterRule rosterRule,
                                  FootballScoringRule scoringRule, Random random) {
        if (matchFlow == null) throw new IllegalArgumentException("MatchFlow can not be null");
        if (rosterRule == null) throw new IllegalArgumentException("RosterRule can not be null");
        if (scoringRule == null) throw new IllegalArgumentException("ScoringRule can not be null");
        if (random == null) throw new IllegalArgumentException("Random can not be null");

        this.matchFlow = matchFlow;
        this.rosterRule = rosterRule;
        this.scoringRule = scoringRule;
        this.random = random;
    }

    @Override
    public int getNumberOfPeriods() {
        return FOOTBALL_PERIODS;
    }

    @Override
    public MatchResult playPeriod(Match match, int periodNumber, MatchResult currentResult) {
        validateMatch(match);

        if (periodNumber == 1) {
            validateRosters(match);
            return scoringRule.generateHalfResult(match);
        }

        if (currentResult == null)
            throw new IllegalArgumentException("Previous period result cannot be null");

        MatchResult periodResult = scoringRule.generateHalfResult(match);
        int totalHome = currentResult.getHomeGoals() + periodResult.getHomeGoals();
        int totalAway = currentResult.getAwayGoals() + periodResult.getAwayGoals();

        MatchResult finalResult = new MatchResult(totalHome, totalAway);

        if (periodNumber == FOOTBALL_PERIODS) {
            if (!matchFlow.allowsDraw() && totalHome == totalAway) {
                finalResult = resolveDraw(finalResult);
            }
            match.finishMatch(finalResult);
        }

        return finalResult;
    }

    @Override
    public MatchResult simulateMatch(Match match) {
        MatchResult result = null;
        for (int i = 1; i <= FOOTBALL_PERIODS; i++) {
            result = playPeriod(match, i, result);
        }
        return result;
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
