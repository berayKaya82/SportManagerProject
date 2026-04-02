package application;
import domain.*;
public class TrainingManager {
    /**
     * Applies a training session to all players in the given team.
     *
     * <p>Effects per intensity (before coach multiplier):
     * <ul>
     *   <li>LIGHT:  energy -5,  condition +3, injuryRisk -2</li>
     *   <li>MEDIUM: energy -10, condition +6, injuryRisk +0</li>
     *   <li>HARD:   energy -20, condition +10, injuryRisk +5</li>
     * </ul>
     * Coach multiplier improves condition gain and reduces energy loss.
     * </p>
     *
     * @param team      the team to train
     * @param intensity the selected training intensity
     * @throws IllegalArgumentException if team or intensity is null
     */
    public void applyTraining(Team team, TrainingIntensity intensity) {
        if (team == null) throw new IllegalArgumentException("Team cannot be null");
        if (intensity == null) throw new IllegalArgumentException("Intensity cannot be null");

        double multiplier = getCoachMultiplier(team);

        int energyLoss     = getEnergyLoss(intensity);
        int conditionGain  = getConditionGain(intensity);
        int injuryRiskDelta = getInjuryRiskDelta(intensity);

        // Coach multiplier: improves condition gain, reduces energy loss
        int adjustedEnergyLoss    = (int) Math.round(energyLoss / multiplier);
        int adjustedConditionGain = (int) Math.round(conditionGain * multiplier);

        for (Player player : team.getStartingPlayers()) {
            applyToPlayer(player, adjustedEnergyLoss, adjustedConditionGain, injuryRiskDelta);
        }
        for (Player player : team.getSubstitutes()) {
            applyToPlayer(player, adjustedEnergyLoss, adjustedConditionGain, injuryRiskDelta);
        }
    }
    private void applyToPlayer(Player player, int energyLoss, int conditionGain, int injuryRiskDelta) {
        // Skip injured players — they rest, not train
        if (player.getInjuryStatus() == InjuryStatus.INJURED) return;

        player.setEnergy(clamp(player.getEnergy() - energyLoss));
        player.setCondition(clamp(player.getCondition() + conditionGain));
        player.setInjuryRisk(clamp(player.getInjuryRisk() + injuryRiskDelta));
    }
    /**
     * Restores energy for all players at the start of each week.
     * Healthy players recover more than injured ones.
     *
     * @param team the team to recover
     * @throws IllegalArgumentException if team is null
     */
    public void applyWeeklyRecovery(Team team) {
        if (team == null) throw new IllegalArgumentException("Team cannot be null");

        for (Player player : team.getStartingPlayers()) {
            recoverPlayer(player);
        }
        for (Player player : team.getSubstitutes()) {
            recoverPlayer(player);
        }
    }

    private void recoverPlayer(Player player) {
        int recovery = (player.getInjuryStatus() == InjuryStatus.INJURED) ? 10 : 20;
        player.setEnergy(clamp(player.getEnergy() + recovery));
    }

    private double getCoachMultiplier(Team team) {
        if (team.getCoach() == null) return 1.0; // no coach = no bonus
        return team.getCoach().getTrainingMultiplier(team.getCoachRelationship());
    }

    private int getEnergyLoss(TrainingIntensity intensity) {
        return switch (intensity) {
            case LIGHT  -> 5;
            case MEDIUM -> 10;
            case HARD   -> 20;
        };
    }
    private int getConditionGain(TrainingIntensity intensity) {
        return switch (intensity) {
            case LIGHT  -> 3;
            case MEDIUM -> 6;
            case HARD   -> 10;
        };
    }

    private int getInjuryRiskDelta(TrainingIntensity intensity) {
        return switch (intensity) {
            case LIGHT  -> -2;  // light training reduces injury risk
            case MEDIUM ->  0;  // no change
            case HARD   ->  5;  // hard training increases injury risk
        };
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
