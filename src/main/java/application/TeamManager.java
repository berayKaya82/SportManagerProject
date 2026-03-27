package application;

import domain.Gender;
import domain.Player;
import domain.Team;
// import sport.ITactic; // Tactic feature is temporarily disabled

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TeamManager handles all operations related to Team entities.
 *
 * Responsibilities:
 * - Creating and storing teams
 * - Retrieving teams
 * - Managing player-team relationships (add/remove/transfer)
 * - Ensuring consistency between PlayerManager and Team
 *
 * Note:
 * TeamManager does NOT generate players, it only manages existing ones.
 */
public class TeamManager {

    private final Map<Integer, Team> teams = new HashMap<>();
    private final PlayerManager playerManager;

    public TeamManager(PlayerManager playerManager) {
        if (playerManager == null) {
            throw new IllegalArgumentException("PlayerManager cannot be null");
        }
        this.playerManager = playerManager;
    }

    /**
     * Validates that an ID is positive.
     */
    private void validateId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

    /**
     * Creates and registers a new team in the system.
     *
     * Rules:
     * - teamId must be unique
     * - name cannot be empty
     * - gender must be defined
     */
    public void createTeam(int teamId, String name, Gender gender) {
        validateId(teamId, "Team ID");

        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty");

        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");

        if (teams.containsKey(teamId))
            throw new IllegalArgumentException("Team already exists");

        teams.put(teamId, new Team(teamId, name, gender));
    }

    /**
     * Retrieves a team by ID.
     *
     * Throws exception if team does not exist → fail-fast design
     */
    public Team getTeam(int teamId) {
        validateId(teamId, "Team ID");

        if (!teams.containsKey(teamId)) {
            throw new IllegalArgumentException("Team does not exist");
        }

        return teams.get(teamId);
    }

    /**
     * Returns all teams (defensive copy).
     */
    public List<Team> getAllTeams() {
        return new ArrayList<>(teams.values());
    }

    /**
     * Adds a player to a team's starting lineup.
     *
     * Important rule:
     * Player gender must match team gender.
     */
    public void addPlayerToTeam(int teamId, int playerId) {
        validateId(teamId, "Team ID");
        validateId(playerId, "Player ID");

        Team team = getTeam(teamId);
        Player player = playerManager.getPlayerById(playerId);

        if (player.getGender() != team.getGender()) {
            throw new IllegalArgumentException("Player gender does not match team");
        }

        team.addStartingPlayer(player);
    }

    /**
     * Removes a player from the team (starting or substitute).
     *
     * Throws exception if player is not part of the team.
     */
    public void removePlayerFromTeam(int teamId, int playerId) {
        validateId(teamId, "Team ID");
        validateId(playerId, "Player ID");

        Team team = getTeam(teamId);
        Player player = playerManager.getPlayerById(playerId);

        if (team.getStartingPlayers().contains(player)) {
            team.removeStartingPlayer(player);
            return;
        }

        if (team.getSubstitutes().contains(player)) {
            team.removeSubstitute(player);
            return;
        }

        throw new IllegalStateException("Player is not in the team");
    }

    /**
     * Removes a team from the system.
     */
    public void removeTeam(int teamId) {
        validateId(teamId, "Team ID");

        if (!teams.containsKey(teamId)) {
            throw new IllegalArgumentException("Team does not exist");
        }

        teams.remove(teamId);
    }

    /**
     * Transfers a player between teams.
     *
     * Rules:
     * - Cannot transfer within same team
     * - Player must exist in source team
     * - Player must NOT exist in target team
     *
     * Preserves role:
     * starting → starting
     * substitute → substitute
     */
    public void transferPlayer(int fromTeamId, int toTeamId, int playerId) {
        validateId(fromTeamId, "From Team ID");
        validateId(toTeamId, "To Team ID");
        validateId(playerId, "Player ID");

        if (fromTeamId == toTeamId) {
            throw new IllegalArgumentException("Cannot transfer within the same team");
        }

        Team fromTeam = getTeam(fromTeamId);
        Team toTeam = getTeam(toTeamId);

        Player player = playerManager.getPlayerById(playerId);

        boolean isInStarting = fromTeam.getStartingPlayers().contains(player);
        boolean isInSubstitute = fromTeam.getSubstitutes().contains(player);

        if (!isInStarting && !isInSubstitute) {
            throw new IllegalStateException("Player is not in the source team");
        }

        if (toTeam.getStartingPlayers().contains(player) ||
                toTeam.getSubstitutes().contains(player)) {

            throw new IllegalStateException("Player already exists in target team");
        }

        if (isInStarting) {
            fromTeam.removeStartingPlayer(player);
            toTeam.addStartingPlayer(player);
        } else {
            fromTeam.removeSubstitute(player);
            toTeam.addSubstitute(player);
        }
    }

    /*
    public void setTactic(int teamId, ITactic tactic) {
        validateId(teamId, "Team ID");

        if (tactic == null)
            throw new IllegalArgumentException("Tactic cannot be null");

        Team team = getTeam(teamId);
        team.setTactic(tactic);
    }
    */
}
