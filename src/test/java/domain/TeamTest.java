package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    private Team createTeam() {
        return new Team(1, "Test FC", Gender.MALE);
    }

    private Player createMalePlayer(int id) {
        return new Player(id, "Player" + id, 22, Gender.MALE);
    }

    @Test
    void addStartingPlayerAppearsInList() {
        Team team = createTeam();
        Player p = createMalePlayer(1);
        team.addStartingPlayer(p);

        assertEquals(1, team.getStartingPlayers().size());
        assertTrue(team.getStartingPlayers().contains(p));
    }

    @Test
    void addStartingPlayerRejectsGenderMismatch() {
        Team team = createTeam();
        Player female = new Player(1, "Jane", 22, Gender.FEMALE);

        assertThrows(IllegalArgumentException.class, () -> team.addStartingPlayer(female));
    }

    @Test
    void substitutePlayerSwapsCorrectly() {
        Team team = createTeam();
        Player starter = createMalePlayer(1);
        Player sub = createMalePlayer(2);

        team.addStartingPlayer(starter);
        team.addSubstitute(sub);
        team.substitutePlayer(starter, sub);

        assertTrue(team.getStartingPlayers().contains(sub));
        assertTrue(team.getSubstitutes().contains(starter));
        assertFalse(team.getStartingPlayers().contains(starter));
        assertFalse(team.getSubstitutes().contains(sub));
    }

    @Test
    void addDuplicatePlayerThrowsException() {
        Team team = createTeam();
        Player p = createMalePlayer(1);
        team.addStartingPlayer(p);

        assertThrows(IllegalStateException.class, () -> team.addStartingPlayer(p));
        assertThrows(IllegalStateException.class, () -> team.addSubstitute(p));
    }
}
