package domain;

import java.util.Objects;

/**
 * Represents a coach that can be hired to manage a team's training.
 *
 * <p>Coaches are tiered by level. Higher-level coaches provide better
 * training multipliers but require the manager to reach a minimum season
 * and reputation threshold before becoming available for hire.</p>
 *
 * <p>The training multiplier is influenced by both the coach's level
 * and the current coach-team relationship score stored in {@link Team}.</p>
 */
public class Coach {
    private String name;
    private int coachLevel;
    private int requiredSeason;
    private int requiredReputation;

    public Coach(String name, int coachLevel, int requiredSeason, int requiredReputation) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Coach name cannot be null or empty");
        if (coachLevel < 1)
            throw new IllegalArgumentException("Coach level must be at least 1");
        if (requiredSeason < 1)
            throw new IllegalArgumentException("Required season must be at least 1");
        if (requiredReputation < 0)
            throw new IllegalArgumentException("Required reputation cannot be negative");
        this.name = name;
        this.coachLevel = coachLevel;
        this.requiredSeason = requiredSeason;
        this.requiredReputation = requiredReputation;
    }

    public String getName() { return name; }
    public int getCoachLevel() { return coachLevel; }
    public int getRequiredSeason() { return requiredSeason; }
    public int getRequiredReputation() { return requiredReputation; }
    /**
     * Calculates the training multiplier based on coach level and
     * the current relationship between the coach and the team.
     *
     * <p>Formula:
     * <ul>
     *   <li>Level bonus: {@code 1.0 + (coachLevel - 1) * 0.1} → Level 1 = 1.0x, Level 2 = 1.1x, ...</li>
     *   <li>Relation bonus: {@code 0.5 + (coachRelationship / 100.0)} → 0 = 0.5x, 50 = 1.0x, 100 = 1.5x</li>
     *   <li>Final multiplier: {@code levelBonus * relationBonus}</li>
     * </ul>
     * </p>
     *
     * @param coachRelationship the current coach-team relationship score (0–100),
     *                          retrieved from {@link Team#getCoachRelationship()}
     * @return the combined training multiplier
     */
    public double getTrainingMultiplier(double coachRelationship) {
        double levelBonus = 1.0 + (coachLevel - 1) * 0.1;        // Level 1=1.0x, Level 2=1.1x ...
        double relationBonus = 0.5 + (coachRelationship / 100.0); // 0=0.5x, 50=1.0x, 100=1.5x
        return levelBonus * relationBonus;
    }
    /**
     * Checks whether this coach is available for hire
     * given the manager's current season and reputation.
     *
     * @param profile the manager's current profile
     * @return true if both season and reputation requirements are met
     */
    public boolean isAvailable(ManagerProfile profile) {
        return profile.getCurrentSeason() >= requiredSeason
                && profile.getReputation() >= requiredReputation;
    }

    @Override
    public String toString() {
        return name + " (Level " + coachLevel + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coach)) return false;
        Coach c = (Coach) o;
        return name.equals(c.name) && coachLevel == c.coachLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, coachLevel);
    }
}
