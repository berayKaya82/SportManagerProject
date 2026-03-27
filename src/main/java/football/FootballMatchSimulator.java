package football;
import domain.Match;
import domain.MatchResult;
import sport.*;

public class FootballMatchSimulator  implements MatchSimulator {
    private MatchFlow matchFlow;
    private RosterRule rosterRule;
    private ScoringRule scoringRule;
    private TieBreakerRule tieBreakerRule;

    public FootballMatchSimulator(MatchFlow matchFlow, RosterRule rosterRule, ScoringRule scoringRule, TieBreakerRule tieBreakerRule) {
        this.matchFlow = matchFlow;
        this.rosterRule = rosterRule;
        this.scoringRule = scoringRule;
        this.tieBreakerRule = tieBreakerRule;
    }
    @Override
    public MatchResult simulateMatch(Match match){
        if(match == null) throw new IllegalArgumentException("Match cannot be null");
        if(!rosterRule.isValidRoster(match.getHomeTeam() )|| !rosterRule.isValidRoster(match.getAwayTeam())){
            throw new IllegalArgumentException("One or Both teams have invalid rosters");
        }

        match.startMatch();
        MatchResult result = scoringRule.generateResult(match);
        match.finishMatch(result);
        return result;
    }
}
