package application;

import domain.Gender;
import domain.Player;

import java.util.*;

/**
 * Manages Player objects.
 * Handles creation, storage, retrieval, and removal of players.
 */
public class PlayerManager {

    // Injury risk limit to consider a player healthy
    private static final int HEALTHY_THRESHOLD = 25;

    // Stores players by their unique ID
    private final Map<Integer, Player> playerMap;

    // Auto-increment ID generator
    private int nextId = 1;

    /**
     * Initializes player storage.
     */
    public PlayerManager() {
        this.playerMap = new HashMap<>();
    }

    public Player createPlayer(String name, int age, Gender gender,
                               int energy, int condition, int injuryRisk) {

        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty");

        if (age <= 0)
            throw new IllegalArgumentException("Age must be positive");

        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");

        int id = nextId++;

        Player player = new Player(id, name, age, gender);

        player.setEnergy(energy);
        player.setCondition(condition);
        player.setInjuryRisk(injuryRisk);

        playerMap.put(id, player);

        return player;
    }
    /**
     * Returns player by ID.
     * @param id player ID
     * @return Player object
     * @throws NoSuchElementException if player not found
     */
    public Player getPlayerById(int id) {
        Player player = playerMap.get(id);

        if (player == null) {
            throw new NoSuchElementException("Player not found! ID: " + id);
        }

        return player;
    }

    /**
     * Checks if a player exists.
     * @param id player ID
     * @return true if exists, false otherwise
     */
    public boolean exists(int id) {
        return playerMap.containsKey(id);
    }

    /**
     * Returns all players.
     * @return list of players
     */
    public List<Player> getAllPlayers() {
        return new ArrayList<>(playerMap.values());
    }

    /**
     * Returns players with low injury risk.
     * @return list of healthy players
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
     * Removes a player by ID.
     * @param id player ID
     * @throws NoSuchElementException if player does not exist
     */
    public void removePlayer(int id) {
        if (playerMap.remove(id) == null) {
            throw new NoSuchElementException("Player does not exist! ID: " + id);
        }
    }

    /**
     * Returns total player count.
     * @return number of players
     */
    public int getPlayerCount() {
        return playerMap.size();
    }
}