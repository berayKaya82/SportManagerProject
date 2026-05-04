package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CoachTest {

    @Test
    void constructorSetsFieldsCorrectly() {
        Coach c = new Coach("Ali", 3, 2, 50);
        assertEquals("Ali", c.getName());
        assertEquals(3, c.getCoachLevel());
        assertEquals(2, c.getRequiredSeason());
        assertEquals(50, c.getRequiredReputation());
    }

    @Test
    void trainingMultiplierScalesWithLevelAndRelationship() {
        Coach c = new Coach("Test", 1, 1, 0);
        // Level 1, relationship 50 → levelBonus=1.1, relationBonus=1.0 → 1.1
        assertEquals(1.1, c.getTrainingMultiplier(50), 0.01);

        Coach c2 = new Coach("Test", 3, 1, 0);
        // Level 3, relationship 100 → levelBonus=1.3, relationBonus=1.5 → 1.95
        assertEquals(1.95, c2.getTrainingMultiplier(100), 0.01);
    }

    @Test
    void matchEnergyReductionCappedAtMax() {
        Coach highLevel = new Coach("Pro", 5, 1, 0);
        double reduction = highLevel.getMatchEnergyReduction(100);
        assertTrue(reduction <= 0.40, "Reduction should not exceed 0.40");
        assertTrue(reduction > 0, "High-level coach should provide some reduction");
    }

    @Test
    void isAvailableChecksSeasonAndReputation() {
        Coach c = new Coach("Coach", 2, 3, 40);
        ManagerProfile notReady = new ManagerProfile("Manager", 30, 2);
        ManagerProfile ready = new ManagerProfile("Manager", 50, 3);

        assertFalse(c.isAvailable(notReady));
        assertTrue(c.isAvailable(ready));
    }
}
