package application;

import domain.Gender;
import domain.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 PlayerGenerator is responsible for creating Player objects with randomized attributes.
 It uses predefined ranges for age, energy, condition, and injury risk.
 It supports:
 - Fully random player generation
 - Semi-random generation (custom name, gender, etc.)
 - Fully custom player creation
 NOTE:
 This class ONLY creates players.
 It does NOT store them. (PlayerManager handles storage)
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

    public PlayerGenerator(Random random) {
        this.random = random;
    }
    /**
     Generates a list of Player objects.
     @param count Number of players to generate
     @return List of randomly generated players
     */
    public List<Player> generatePlayersByGender(int count, Gender gender) {

        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");

        List<Player> players = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            players.add(generatePlayerByGender(gender));
        }

        return players;
    }

    /**
     Creates a single Player object with randomized attributes.
     @return A fully initialized Player instance
     */
    private Player createSinglePlayer() {
        boolean isMale = random.nextBoolean();
        Gender gender = isMale ? Gender.MALE : Gender.FEMALE;
        List<String> pool = isMale ? NamePool.MALE_NAMES : NamePool.FEMALE_NAMES;
        String randomName = pool.get(random.nextInt(pool.size()));

        int age = randomBetween(MIN_AGE, MAX_AGE);

        Player newPlayer = new Player(0, randomName, age, gender);

        newPlayer.setEnergy(randomBetween(MIN_ENERGY, MAX_ENERGY));
        newPlayer.setCondition(randomBetween(MIN_CONDITION, MAX_CONDITION));
        newPlayer.setInjuryRisk(randomBetween(MIN_INJURY, MAX_INJURY));

        return newPlayer;
    }

    /**
     Generates a completely random single player.
     */
    public Player generatePlayer() {
        return createSinglePlayer();
    }

    /**
     Generates a player with given name, age, and gender,
     while other attributes are randomized.
     */
    public Player generatePlayerWithName(String name, int age, Gender gender) {
        Player player = new Player(0, name, age, gender);

        player.setEnergy(randomBetween(MIN_ENERGY, MAX_ENERGY));
        player.setCondition(randomBetween(MIN_CONDITION, MAX_CONDITION));
        player.setInjuryRisk(randomBetween(MIN_INJURY, MAX_INJURY));

        return player;
    }

    /**
     Generates a random player based on a specified gender.
     Name is selected from the corresponding name pool.
     */
    public Player generatePlayerByGender(Gender gender) {
        boolean isMale = gender == Gender.MALE;
        List<String> pool = isMale ? NamePool.MALE_NAMES : NamePool.FEMALE_NAMES;

        String name = pool.get(random.nextInt(pool.size()));
        int age = randomBetween(MIN_AGE, MAX_AGE);

        Player player = new Player(0, name, age, gender);

        player.setEnergy(randomBetween(MIN_ENERGY, MAX_ENERGY));
        player.setCondition(randomBetween(MIN_CONDITION, MAX_CONDITION));
        player.setInjuryRisk(randomBetween(MIN_INJURY, MAX_INJURY));

        return player;
    }

    /**
     Generates a player with given name, age, and gender,
     while other attributes are randomized.
     */
    public Player createCustomPlayer(String name, int age, Gender gender,
                                     int energy, int condition, int injuryRisk) {

        Player player = new Player(0, name, age, gender);

        player.setEnergy(energy);
        player.setCondition(condition);
        player.setInjuryRisk(injuryRisk);

        return player;
    }

    private int randomBetween(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}