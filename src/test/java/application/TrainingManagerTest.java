package application;

import domain.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrainingManagerTest {

    private final TrainingManager trainingManager = new TrainingManager();

    private Team createTeamWithPlayer() {
        Team team = new Team(1, "Test FC", Gender.MALE);
        team.addStartingPlayer(new Player(1, "Starter", 22, Gender.MALE));
        team.setCoach(new Coach("Coach", 1, 1, 0));
        return team;
    }

    @Test
    void hardTrainingIncreasesConditionDecreasesEnergy() {
        Team team = createTeamWithPlayer();
        Player p = team.getStartingPlayers().get(0);

        int energyBefore = p.getEnergy();
        int conditionBefore = p.getCondition();

        trainingManager.applyTraining(team, TrainingIntensity.HARD);

        assertTrue(p.getEnergy() < energyBefore, "Energy should decrease after hard training");
        assertTrue(p.getCondition() >= conditionBefore, "Condition should increase after hard training");
        assertTrue(p.getInjuryRisk() > 0, "Injury risk should increase after hard training");
    }

    @Test
    void trainingSkipsInjuredPlayers() {
        Team team = createTeamWithPlayer();
        Player p = team.getStartingPlayers().get(0);
        p.injure(3);

        int energyBefore = p.getEnergy();
        int conditionBefore = p.getCondition();

        trainingManager.applyTraining(team, TrainingIntensity.HARD);

        assertEquals(energyBefore, p.getEnergy(), "Injured player's energy should not change");
        assertEquals(conditionBefore, p.getCondition(), "Injured player's condition should not change");
    }

    @Test
    void weeklyRecoveryGivesSubsMoreEnergy() {
        Team team = new Team(1, "Test FC", Gender.MALE);
        Player starter = new Player(1, "Starter", 22, Gender.MALE);
        Player sub = new Player(2, "Sub", 22, Gender.MALE);

        starter.setEnergy(50);
        sub.setEnergy(50);

        team.addStartingPlayer(starter);
        team.addSubstitute(sub);

        trainingManager.applyWeeklyRecovery(team);

        assertEquals(65, starter.getEnergy(), "Starter should recover +15");
        assertEquals(75, sub.getEnergy(), "Sub should recover +25");
    }
}
