package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player createPlayer() {
        return new Player(1, "Test Player", 25, Gender.MALE);
    }

    @Test
    void constructorSetsFieldsCorrectly() {
        Player p = createPlayer();
        assertEquals(1, p.getId());
        assertEquals("Test Player", p.getName());
        assertEquals(25, p.getAge());
        assertEquals(Gender.MALE, p.getGender());
        assertEquals(100, p.getEnergy());
        assertEquals(100, p.getCondition());
        assertEquals(0, p.getInjuryRisk());
        assertEquals(InjuryStatus.HEALTHY, p.getInjuryStatus());
    }

    @Test
    void constructorRejectsInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> new Player(0, "Name", 25, Gender.MALE));
        assertThrows(IllegalArgumentException.class, () -> new Player(1, null, 25, Gender.MALE));
        assertThrows(IllegalArgumentException.class, () -> new Player(1, "  ", 25, Gender.MALE));
        assertThrows(IllegalArgumentException.class, () -> new Player(1, "Name", -1, Gender.MALE));
        assertThrows(IllegalArgumentException.class, () -> new Player(1, "Name", 25, null));
    }

    @Test
    void setEnergyEnforcesBounds() {
        Player p = createPlayer();
        p.setEnergy(0);
        assertEquals(0, p.getEnergy());
        p.setEnergy(100);
        assertEquals(100, p.getEnergy());
        assertThrows(IllegalArgumentException.class, () -> p.setEnergy(-1));
        assertThrows(IllegalArgumentException.class, () -> p.setEnergy(101));
    }

    @Test
    void injureSetsStatusAndCountdown() {
        Player p = createPlayer();
        p.injure(3);
        assertEquals(InjuryStatus.INJURED, p.getInjuryStatus());
        assertEquals(3, p.getInjuredGamesRemaining());
        assertThrows(IllegalArgumentException.class, () -> p.injure(0));
    }

    @Test
    void recoverOneGameDecrementsAndHeals() {
        Player p = createPlayer();
        p.injure(2);

        p.recoverOneGame();
        assertEquals(InjuryStatus.INJURED, p.getInjuryStatus());
        assertEquals(1, p.getInjuredGamesRemaining());

        p.recoverOneGame();
        assertEquals(InjuryStatus.HEALTHY, p.getInjuryStatus());
        assertEquals(0, p.getInjuredGamesRemaining());
    }

    @Test
    void isAvailableForMatchWhenHealthyWithEnergy() {
        Player p = createPlayer();
        assertTrue(p.isAvailableForMatch());

        p.setEnergy(0);
        assertFalse(p.isAvailableForMatch());
    }

    @Test
    void isAvailableForMatchReturnsFalseWhenInjured() {
        Player p = createPlayer();
        p.injure(1);
        assertFalse(p.isAvailableForMatch());
    }
}
