package football;

import sport.*;

public class FootballSport implements ISport {
    private String sportName;
    private MatchFlow matchFlow;
    private MatchSimulator matchSimulator;
    private RosterRule rosterRule;
    private ScoringRule scoringRule;
    private TieBreakerRule tieBreakerRule;

    public FootballSport() {
        this.sportName = "Football";
        this.matchFlow = new FootballMatchFlow();
        this.rosterRule = new FootballRosterRule();
        this.scoringRule = new FootballScoringRule();
        this.tieBreakerRule = new FootballTieBreakerRule();
        this.matchSimulator = new FootballMatchSimulator(matchFlow,rosterRule,scoringRule,tieBreakerRule);
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
