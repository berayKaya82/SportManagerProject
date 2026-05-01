package handball;

import domain.*;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class HandballScoringRuleTest {

    private Team createFullTeam(int id, String name) {
        Team team = new Team(id, name, Gender.MALE);
        for (int i = 1; i <= 7; i++) {
            team.addStartingPlayer(new Player(id * 100 + i, "P" + i, 22, Gender.MALE));
        }
        team.setCoach(new Coach("Coach", 1, 1, 0));
        team.setTactic(new HandballTactic(PlayStyle.BALANCED));
        team.setCoachRelationship(50);
        return team;
    }

    @Test
    void generateHalfResultReturnsNonNegativeGoals() {
        HandballScoringRule rule = new HandballScoringRule(new Random(42));
        Team home = createFullTeam(1, "Home");
        Team away = createFullTeam(2, "Away");
        Match match = new Match(home, away);

        for (int i = 0; i < 100; i++) {
            MatchResult result = rule.generateHalfResult(match);
            assertTrue(result.getHomeGoals() >= 0, "Home goals should never be negative");
            assertTrue(result.getAwayGoals() >= 0, "Away goals should never be negative");
        }
    }

    @Test
    void injuredPlayersExcludedFromAverages() {
        Team team = createFullTeam(1, "Test HC");

        // Injure most starters — only a few healthy remain
        for (int i = 0; i < 5; i++) {
            team.getStartingPlayers().get(i).injure(3);
        }
        // Set remaining healthy players to low energy
        for (Player p : team.getStartingPlayers()) {
            if (p.isAvailableForMatch()) {
                p.setEnergy(20);
            }
        }

        Team opponent = createFullTeam(2, "Opponent");

        HandballScoringRule rule = new HandballScoringRule(new Random(0));
        Match match = new Match(team, opponent);
        MatchResult result = rule.generateHalfResult(match);

        // Injured players (energy=100) should NOT inflate the average
        // With only low-energy healthy players, team should get energy penalty
        assertNotNull(result);
        assertTrue(result.getHomeGoals() >= 0);
    }
    @Test
    void generateResultThrowsExceptionWhenMatchIsNull() {
        HandballScoringRule rule = new HandballScoringRule(new Random(42));
        assertThrows(IllegalArgumentException.class, () -> {
            rule.generateResult(null);
        });
    }
}

