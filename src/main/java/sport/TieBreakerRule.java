package sport;
import domain.Match;
import domain.StandingEntry;

import java.util.List;

public interface TieBreakerRule {
    int compare(StandingEntry first,StandingEntry second,List<Match>playedMatches);

}
