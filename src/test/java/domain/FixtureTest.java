package domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FixtureTest {

    private List<Team> createTeams(int count) {
        List<Team> teams = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            teams.add(new Team(i, "T" + i, Gender.MALE));
        }
        return teams;
    }

    private int totalMatches(Fixture fixture) {
        return fixture.getAllWeeks().stream()
                .mapToInt(MatchWeek::getMatchCount)
                .sum();
    }

    @Test
    void doubleRoundRobinWith18TeamsProduces34WeeksAnd306Matches() {
        Fixture fixture = Fixture.generate(createTeams(18), true);

        assertEquals(34, fixture.getTotalWeeks(),
                "18-team double round-robin must have 2*(N-1)=34 weeks");
        assertEquals(306, totalMatches(fixture),
                "18-team double round-robin must have N*(N-1)=306 matches");
    }

    @Test
    void doubleRoundRobinWith12TeamsProduces22WeeksAnd132Matches() {
        Fixture fixture = Fixture.generate(createTeams(12), true);

        assertEquals(22, fixture.getTotalWeeks(),
                "12-team double round-robin must have 2*(N-1)=22 weeks");
        assertEquals(132, totalMatches(fixture),
                "12-team double round-robin must have N*(N-1)=132 matches");
    }

    @Test
    void noMatchHasTheSameTeamAsHomeAndAway() {
        Fixture fixture = Fixture.generate(createTeams(18), true);

        for (MatchWeek week : fixture.getAllWeeks()) {
            for (Match match : week.getMatches()) {
                assertNotEquals(match.getHomeTeam(), match.getAwayTeam(),
                        "Week " + week.getWeekNumber()
                                + " has a match with the same team on both sides");
            }
        }
    }
}
