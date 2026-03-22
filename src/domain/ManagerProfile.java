package domain;

import java.util.Objects;
/**
 * Represents the human player's profile in the game.
 *
 * <p>Tracks progression across seasons through a reputation system
 * that acts similarly to an XP/level mechanic. Reputation is cumulative
 * and never resets between seasons, rewarding long-term play.</p>
 *
 * <p>Reputation and current season together determine which coaches
 * are available for hire. See {@link Coach#isAvailable(ManagerProfile)}.</p>
 */
public class ManagerProfile {
    private String managerName;
    private int reputation;
    private int currentSeason;

    public ManagerProfile(String managerName, int reputation, int currentSeason) {
        if(managerName == null || managerName.trim().isEmpty()){
            throw new IllegalArgumentException("Manager name cannot be null or empty");
        }
        this.managerName = managerName;
        this.reputation = reputation;
        this.currentSeason = currentSeason;
    }

    public String getManagerName() { return managerName; }
    public int getReputation() { return reputation; }
    public int getCurrentSeason() { return currentSeason; }
    /**
     * Adds reputation points to the manager's total.
     * Called after each match based on result and performance.
     *
     * @param amount positive amount of reputation to add
     * @throws IllegalArgumentException if amount is negative
     */
    public void addReputation(int amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Amount cannot be negative");
        this.reputation += amount;
    }
    /**
     * Advances the manager to the next season.
     * Reputation is retained across seasons.
     */
    public void advanceSeason() {
        this.currentSeason++;
    }

    @Override
    public String toString() {
        return managerName + " | Season: " + currentSeason + " | Reputation: " + reputation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ManagerProfile)) return false;
        ManagerProfile m = (ManagerProfile) o;
        return managerName.equals(m.managerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managerName);
    }
}
