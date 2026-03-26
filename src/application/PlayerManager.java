package application;

import domain.Gender;
import domain.Player;

import java.util.*;

/**
 The PlayerManager class is responsible for managing Player objects.
 It provides methods to add, retrieve, filter, and remove players.
 Internally, players are stored in a HashMap using their unique ID as the key.
 */
public class PlayerManager {
    // Threshold value to determine whether a player is considered "healthy"
    // If a player's injury risk is below this value, they are considered healthy
    private static final int HEALTHY_THRESHOLD = 25;


    //Stores players indexed by their unique ID.
    //The key represents the player ID, and the value is the Player object.
    private final Map<Integer, Player> playerMap;

    private int nextId = 1;

    //Constructor initializes the internal player storage.
    public PlayerManager() {
        this.playerMap = new HashMap<>();
    }

    /**
     Adds a single player to the system.
     @param player the Player object to be added
     @throws IllegalArgumentException if the player is null
     @throws IllegalStateException if a player with the same ID already exists
     */
    public void addPlayer(Player player) {
        validatePlayer(player);

        player.setId(nextId++);

        playerMap.put(player.getId(), player);
    }

    /**
     Adds multiple players to the system.
     @param players list of Player objects
     @throws IllegalArgumentException if the list is null
     */
    public void addPlayers(List<Player> players) {
        if (players == null) {
            throw new IllegalArgumentException("Player list cannot be null");
        }

        for (Player p : players) {
            addPlayer(p);
        }
    }

    /**
     * Generates a player using PlayerGenerator and adds it to the system.
     * This method connects PlayerGenerator and PlayerManager.
     */
    public Player generateAndAddPlayer(PlayerGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("Generator cannot be null");
        }

        Player player = generator.generatePlayer();
        addPlayer(player);
        return player;
    }

    /**
     * Generates multiple players using PlayerGenerator and adds them to the system.
     */
    public List<Player> generateAndAddPlayers(PlayerGenerator generator, int count, Gender gender) {

        if (generator == null)
            throw new IllegalArgumentException("Generator cannot be null");

        if (count <= 0)
            throw new IllegalArgumentException("Count must be positive");

        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");

        List<Player> players = generator.generatePlayersByGender(count, gender);

        addPlayers(players);

        return players;
    }
    /**
     Retrieves a player by their unique ID.
     @param id the ID of the player
     @return the Player object
     @throws NoSuchElementException if no player is found with the given ID
     */
    public Player getPlayerById(int id) {
        Player player = playerMap.get(id);

        if (player == null) {
            throw new NoSuchElementException("Player not found! ID: " + id);
        }

        return player;
    }

    /**
     * Checks whether a player exists in the system.
     * @param id player ID
     * @return true if player exists, false otherwise
     */
    public boolean exists(int id) {
        return playerMap.containsKey(id);
    }

    /**
     Returns a list of all players.
     @return a new List containing all players
     */
    public List<Player> getAllPlayers() {
        return new ArrayList<>(playerMap.values());
    }

    /**
     Retrieves all players considered "healthy".
     A player is healthy if their injury risk is below HEALTHY_THRESHOLD.
     @return list of healthy players
     */
    public List<Player> getHealthyPlayers() {
        List<Player> healthy = new ArrayList<>();

        for (Player p : playerMap.values()) {
            if (p.getInjuryRisk() < HEALTHY_THRESHOLD) {
                healthy.add(p);
            }
        }

        return healthy;
    }

    /**
     Removes a player from the system by ID.
     @param id the ID of the player to remove
     @throws NoSuchElementException if the player does not exist
     */
    public void removePlayer(int id) {
        if (playerMap.remove(id) == null) {
            throw new NoSuchElementException("Player does not exist! ID: " + id);
        }
    }

    /**
     Returns the total number of players currently stored.
     @return number of players
     */
    public int getPlayerCount() {
        return playerMap.size();
    }

    /**
     Validates that the player object is not null.
     @param player the Player object to validate
     @throws IllegalArgumentException if player is null
     */
    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
    }
}