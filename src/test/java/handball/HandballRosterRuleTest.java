package handball;

import domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HandballRosterRuleTest {

    private Team createTeam(int playerCount, boolean hasCoach, boolean hasTactic) {
        Team team = new Team(1, "Handball Team", Gender.MALE);
        for (int i = 1; i <= playerCount; i++) {
            team.addStartingPlayer(new Player(i, "P" + i, 22, Gender.MALE));
        }
        if (hasCoach) {
            team.setCoach(new Coach("Coach", 1, 1, 0));
        }
        if (hasTactic) {
            team.setTactic(new HandballTactic(PlayStyle.BALANCED));
        }
        return team;
    }
    @Test
    void validRosterReturnsTrue(){
        HandballRosterRule rule =new HandballRosterRule();
        Team team = createTeam(7,true ,true);
        assertTrue(rule.isValidRoster(team));
    }
    @Test
    void invalidRosterRuleWhenPlayerIsLess(){
        HandballRosterRule rule =new HandballRosterRule();
        Team team = createTeam(6,true ,true);
        assertFalse(rule.isValidRoster(team));
    }
    @Test
    void invalidRosterRuleWhenNoCoach(){
        HandballRosterRule rule =new HandballRosterRule();
        Team team = createTeam(7,false,true);
        assertFalse(rule.isValidRoster(team));
    }
    @Test
    void invalidRosterRuleWhenNoTactic(){
        HandballRosterRule rule =new HandballRosterRule();
        Team team = createTeam(7,true ,false);
        assertFalse(rule.isValidRoster(team));
    }
    @Test
    void startingPlayerCountIsSeven(){
        HandballRosterRule rule =new HandballRosterRule();
        assertEquals(7,rule.getStartingPlayerCount());
    }
}
