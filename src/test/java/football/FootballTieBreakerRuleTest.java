package football;

import domain.Gender;
import domain.StandingEntry;
import domain.Team;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;


class FootballTieBreakerRuleTest {

    private final FootballTieBreakerRule rule = new FootballTieBreakerRule();

    private StandingEntry entry(int id, String name,
                                int wins, int draws, int losses,
                                int goalsFor, int goalsAgainst) {
        StandingEntry e = new StandingEntry(new Team(id, name, Gender.MALE));
        e.setWins(wins);
        e.setDraws(draws);
        e.setLosses(losses);
        e.setGoalsFor(goalsFor);
        e.setGoalsAgainst(goalsAgainst);
        e.setPlayed(wins + draws + losses);
        return e;
    }

    @Test
    void higherPointsRanksAbove() {
        // 6 pts vs 3 pts — points alone settle it.
        StandingEntry top = entry(1, "Top", 2, 0, 0, 4, 1);
        StandingEntry bottom = entry(2, "Bot", 1, 0, 1, 9, 1);

        assertTrue(rule.compare(top, bottom, Collections.emptyList()) < 0,
                "Team with more points must rank above");
        assertTrue(rule.compare(bottom, top, Collections.emptyList()) > 0,
                "Comparator must be antisymmetric");
    }

    @Test
    void equalPointsHigherGoalDifferenceRanksAbove() {
        // Both 3 pts, but A has +5, B has +1 → A above B.
        StandingEntry a = entry(1, "A", 1, 0, 1, 7, 2);   // GD = +5
        StandingEntry b = entry(2, "B", 1, 0, 1, 3, 2);   // GD = +1

        assertEquals(a.getPoints(), b.getPoints());
        assertTrue(rule.compare(a, b, Collections.emptyList()) < 0,
                "On equal points, higher goal difference must rank above");
    }

    @Test
    void equalPointsAndGoalDifferenceHigherGoalsForRanksAbove() {
        // Both 3 pts and both GD=+1, but A scored 6 vs B scored 2 → A above B.
        StandingEntry a = entry(1, "A", 1, 0, 1, 6, 5);   // GD = +1, GF = 6
        StandingEntry b = entry(2, "B", 1, 0, 1, 2, 1);   // GD = +1, GF = 2

        assertEquals(a.getPoints(), b.getPoints());
        assertEquals(a.getGoalDifference(), b.getGoalDifference());
        assertTrue(rule.compare(a, b, Collections.emptyList()) < 0,
                "On equal points and goal difference, higher goals scored must rank above");
    }

    @Test
    void nullArgumentsRejected() {
        StandingEntry e = entry(1, "X", 0, 0, 0, 0, 0);

        assertThrows(IllegalArgumentException.class,
                () -> rule.compare(null, e, Collections.emptyList()));
        assertThrows(IllegalArgumentException.class,
                () -> rule.compare(e, null, Collections.emptyList()));
        assertThrows(IllegalArgumentException.class,
                () -> rule.compare(e, e, null));
    }
}
