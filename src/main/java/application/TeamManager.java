package application;

import domain.Coach;
import domain.Player;
import domain.Team;
import sport.ITactic;

/**
 * TeamManager is responsible ONLY for managing the user's team.
 * IMPORTANT DESIGN CHANGE:
 * This class no longer manages multiple teams (no Map).
 * It holds a single Team reference (managedTeam).
 * This ensures that TeamManager and League use the SAME Team object.
 * WHY?
 * If TeamManager created its own Team or kept a Map,
 * it would cause synchronization issues with League.
 */
public class TeamManager {

    // The single team managed by the user (shared reference with League)
    private final Team managedTeam;

    // Used to fetch players safely from the system
    private final PlayerManager playerManager;

    /**
     * Constructor requires an existing Team.
     * This Team should come from League (NOT created here).
     */
    public TeamManager(Team userTeam, PlayerManager playerManager) {

        if (userTeam == null)
            throw new IllegalArgumentException("Team cannot be null");

        if (playerManager == null)
            throw new IllegalArgumentException("PlayerManager cannot be null");

        // Store reference (NO COPY → same object as League)
        this.managedTeam = userTeam;
        this.playerManager = playerManager;
    }

    /**
     * Returns the managed team.
     */
    public Team getManagedTeam() {
        return managedTeam;
    }

    /**
     * Adds a player to the starting lineup.
     * Player is retrieved from PlayerManager using ID.
     */
    public void addPlayerToStarting(int playerId) {

        // Get player from system (guaranteed valid & registered)
        Player player = playerManager.getPlayerById(playerId);

        // Delegate to Team
        managedTeam.addStartingPlayer(player);
    }

    /**
     * Removes a player from starting lineup.
     */
    public void removePlayerFromStarting(int playerId) {

        Player player = playerManager.getPlayerById(playerId);

        managedTeam.removeStartingPlayer(player);
    }

    /**
     * Adds a player to substitutes bench.
     */
    public void addPlayerToSubstitutes(int playerId) {

        Player player = playerManager.getPlayerById(playerId);

        managedTeam.addSubstitute(player);
    }

    /**
     * Assigns a tactic to the team.
     */
    public void setTactic(ITactic tactic) {

        if (tactic == null)
            throw new IllegalArgumentException("Tactic cannot be null");

        managedTeam.setTactic(tactic);
    }
    public void transferPlayer(int playerId, Team fromTeam) {
        Player player = playerManager.getPlayerById(playerId);

        if (!fromTeam.getStartingPlayers().contains(player) &&
                !fromTeam.getSubstitutes().contains(player))
            throw new IllegalStateException("Player not in source team");

        if (managedTeam.getStartingPlayers().contains(player) ||
                managedTeam.getSubstitutes().contains(player))
            throw new IllegalStateException("Player already in managed team");

        if (fromTeam.getStartingPlayers().contains(player)) {
            fromTeam.removeStartingPlayer(player);
            managedTeam.addStartingPlayer(player);
        } else {
            fromTeam.removeSubstitute(player);
            managedTeam.addSubstitute(player);
        }
    }

    /**
     * Assigns a coach to the team.
     */
    public void setCoach(Coach coach) {

        if (coach == null)
            throw new IllegalArgumentException("Coach cannot be null");

        managedTeam.setCoach(coach);
    }
}