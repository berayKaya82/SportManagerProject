package football;

import sport.*;

import java.util.Random;

public class FootballSport implements ISport {
    private final String sportName;
    private final MatchFlow matchFlow;
    private final MatchSimulator matchSimulator;
    private final RosterRule rosterRule;
    private final FootballScoringRule scoringRule;
    private final TieBreakerRule tieBreakerRule;

    public FootballSport() {
        Random random =new Random();
        this.sportName = "Football";
        this.matchFlow = new FootballMatchFlow();
        this.rosterRule = new FootballRosterRule();
        this.scoringRule = new FootballScoringRule(random);
        this.tieBreakerRule = new FootballTieBreakerRule();
        this.matchSimulator = new FootballMatchSimulator(matchFlow,rosterRule, scoringRule,random);
    }

    @Override
    public String getSportName() {
        return sportName;
    }

    @Override
    public MatchFlow getMatchFlow() {
        return matchFlow;
    }

    @Override
    public MatchSimulator getMatchSimulator() {
        return matchSimulator;
    }

    @Override
    public RosterRule getRosterRule() {
        return rosterRule;
    }

    @Override
    public ScoringRule getScoringRule() {
        return scoringRule;
    }

    @Override
    public TieBreakerRule getTieBreakerRule() {
        return tieBreakerRule;
    }
}
