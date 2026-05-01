package handball;
import domain.PlayStyle;
import sport.*;
import java.util.Random;

public class HandballSport implements ISport {
    private final String sportName;
    private final MatchFlow matchFlow;
    private final MatchSimulator matchSimulator;
    private final RosterRule rosterRule;
    private final HandballScoringRule scoringRule;
    private final TieBreakerRule tieBreakerRule;

    public HandballSport() {
        Random random = new Random();
        this.sportName = "Handball";
        this.matchFlow = new HandballMatchFlow();
        this.rosterRule = new HandballRosterRule();
        this.scoringRule = new HandballScoringRule(random);
        this.tieBreakerRule = new HandballTieBreakerRule();
        this.matchSimulator = new HandballMatchSimulator(matchFlow,rosterRule,scoringRule,random);
    }

    @Override
    public String getSportName() { return sportName;
    }

    @Override
    public MatchFlow getMatchFlow() { return matchFlow;
    }

    @Override
    public MatchSimulator getMatchSimulator() { return matchSimulator;
    }

    @Override
    public RosterRule getRosterRule() { return rosterRule;
    }

    @Override
    public ScoringRule getScoringRule() { return scoringRule;
    }

    @Override
    public TieBreakerRule getTieBreakerRule() { return tieBreakerRule;
    }

    @Override
    public ITactic getDefaultTactic() { return new HandballTactic(PlayStyle.BALANCED); }
}
