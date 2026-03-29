package application;

import domain.Gender;
import domain.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Responsible for creating Player objects with random or custom values.
 * Does NOT store players, only creates them.
 */
public class PlayerGenerator {

    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 50;

    private static final int MIN_ENERGY = 60;
    private static final int MAX_ENERGY = 100;

    private static final int MIN_CONDITION = 50;
    private static final int MAX_CONDITION = 100;

    private static final int MIN_INJURY = 0;
    private static final int MAX_INJURY = 30;

    private final Random random;
    private final PlayerManager playerManager;

    public PlayerGenerator(Random random, PlayerManager playerManager) {
        if (random == null)
            throw new IllegalArgumentException("Random cannot be null");
        if (playerManager == null)
            throw new IllegalArgumentException("PlayerManager cannot be null");

        this.random = random;
        this.playerManager = playerManager;
    }

    /**
     * Generates multiple players with the given gender.
     */
    public List<Player> generatePlayersByGender(int count, Gender gender) {
        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");

        if (count <= 0)
            throw new IllegalArgumentException("Count must be positive");

        List<Player> players = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            players.add(generatePlayerByGender(gender));
        }

        return players;
    }

    /**
     * Generates a fully random player.
     */
    public Player generatePlayer() {
        boolean isMale = random.nextBoolean();
        Gender gender = isMale ? Gender.MALE : Gender.FEMALE;

        return generatePlayerByGender(gender);
    }

    /**
     * Generates a player with given name, age, and gender.
     * Other attributes are random.
     */
    public Player generatePlayerWithName(String name, int age, Gender gender) {

        validateBasicInputs(name, age, gender);

        return playerManager.createPlayer(
                name,
                age,
                gender,
                randomBetween(MIN_ENERGY, MAX_ENERGY),
                randomBetween(MIN_CONDITION, MAX_CONDITION),
                randomBetween(MIN_INJURY, MAX_INJURY)
        );
    }

    /**
     * Generates a random player for a specific gender.
     * Name is selected from a predefined name pool.
     */
    public Player generatePlayerByGender(Gender gender) {

        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");

        List<String> pool =
                gender == Gender.MALE ? NamePool.MALE_NAMES : NamePool.FEMALE_NAMES;

        String name = pool.get(random.nextInt(pool.size()));
        int age = randomBetween(MIN_AGE, MAX_AGE);

        return playerManager.createPlayer(
                name,
                age,
                gender,
                randomBetween(MIN_ENERGY, MAX_ENERGY),
                randomBetween(MIN_CONDITION, MAX_CONDITION),
                randomBetween(MIN_INJURY, MAX_INJURY)
        );
    }

    /**
     * Creates a fully custom player with all values provided.
     */
    public Player createCustomPlayer(String name, int age, Gender gender,
                                     int energy, int condition, int injuryRisk) {

        validateBasicInputs(name, age, gender);

        return playerManager.createPlayer(
                name,
                age,
                gender,
                energy,
                condition,
                injuryRisk
        );
    }

    private void validateBasicInputs(String name, int age, Gender gender) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty");

        if (age <= 0)
            throw new IllegalArgumentException("Age must be positive");

        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");
    }

    private int randomBetween(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}