package sport;

public interface ISport {
    String getSportName();
    MatchFlow getMatchFlow();
    MatchSimulator getMatchSimulator();
    RosterRule getRosterRule();
    ScoringRule getScoringRule();
    TieBreakerRule getTieBreakerRule();
    ITactic getDefaultTactic();
    int getTeamCount();
    boolean isDoubleRoundRobin();
}
