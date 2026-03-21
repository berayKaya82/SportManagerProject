package application;

import domain.Player;
//import domain.Tactic;
import domain.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 TeamManager is responsible for managing Team objects and their interactions
 with players in the system.
 Responsibilities:
 - Create and store teams
 - Retrieve teams
 - Manage players inside teams (add/remove/transfer)
 - Maintain consistency between PlayerManager and Team objects
 Teams are stored using a unique teamId as the key.
 */
public class TeamManager {
    private final Map<String, Team> teams = new HashMap<>();
    private final PlayerManager playerManager;
    public TeamManager(PlayerManager playerManager) {
        if(playerManager == null) {
            throw new IllegalArgumentException("PlayerManager cannot be null");
        }
        this.playerManager = playerManager;
    }

    private void validateId(String teamId, String fieldName) {
        if(teamId == null || teamId.trim().isEmpty()){
            throw new IllegalArgumentException( fieldName + " cannot be null or empty");
        }
    }

    /**
     Creates a new team and stores in the system
     @param teamId unique identifier of the team
     @param name name of the team
     @throws IllegalArgumentException if inputs are invalid or team already exists
     */
    public void createTeam(String teamId, String name){
        validateId(teamId, "Team ID");
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        if (teams.containsKey(teamId)) {
            throw new IllegalArgumentException("Team already exists");
        }

        teams.put(teamId, new Team(teamId, name));
    }

    /**
     Retrieves a team by its ID.
     @param teamId the ID of the team
     @return Team object
     @throws IllegalArgumentException if team does not exist
     */
    public Team getTeam(String teamId){
        validateId(teamId, "Team ID");

        if(!teams.containsKey(teamId)){
            throw new IllegalArgumentException("Team does not exist");
        }

        return teams.get(teamId);
    }

    /**
     Returns all teams in the system.
     @return list of all teams
     */
    public List<Team> getAllTeams(){
        List<Team> teamList = new ArrayList<>();
        for (Team t : teams.values()) {
            teamList.add(t);
        }
        return teamList;
    }

    /**
     * Adds a player to a team as a starting player.
     @param teamId ID of the team
     @param playerId ID of the player
     @throws IllegalStateException if player already exists in the team
     */
    public void addPlayerToTeam(String teamId, String playerId) {
        validateId(teamId, "Team ID");
        validateId(playerId, "Player ID");

        Team team = getTeam(teamId);
        Player player = playerManager.getPlayerById(playerId);

        if (team.getStartingPlayers().contains(player) ||
                team.getSubstitutes().contains(player)) {

            throw new IllegalStateException("Player is already in the team");
        }

        team.addStartingPlayer(player);
    }

    /**
     Removes a player from a team (from either starting or substitute list).
     @param teamId ID of the team
     @param playerId ID of the player
     @throws IllegalStateException if player is not in the team
     */
    public void removePlayerFromTeam(String teamId, String playerId) {
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
     Removes a team from the system
     @param teamId ID of the team
     @throws IllegalStateException if team does not exist
     */
    public void removeTeam(String teamId) {
        validateId(teamId, "Team ID");

        if (!teams.containsKey(teamId)) {
            throw new IllegalArgumentException("Team does not exist");
        }

        teams.remove(teamId);
    }

    /**
     Transfers a player from one team to another
     @param fromTeamId ID of the team
     @param toTeamId target team ID
     @param playerId player ID
     @throws IllegalStateException if player is not in source team or already in target team
     */
    public void transferPlayer(String fromTeamId, String toTeamId, String playerId) {
        validateId(fromTeamId, "From Team ID");
        validateId(toTeamId, "To Team ID");
        validateId(playerId, "Player ID");

        if (fromTeamId.equals(toTeamId)) {
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

    /*public void setTactic(String teamId, Tactic tactic){}
     * I'll write about it later according to the design changes;
     * I left it in the comments so I don't forget.
     * */
}
