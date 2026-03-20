package application;

import domain.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 PlayerGenerator is responsible for creating Player objects with randomized attributes.
 It uses predefined ranges for age, energy, condition, and injury risk.
 */
public class PlayerGenerator {
    // Unique ID counter for each generated player
    private int nextId = 1;

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
    public List<Player> generatePlayers(int count) {
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Player player = createSinglePlayer();
            players.add(player);
        }

        return players;
    }

    /**
     Creates a single Player object with randomized attributes.
     @return A fully initialized Player instance
     */
    private Player createSinglePlayer() {
        int id = nextId++;
        boolean isMale = random.nextBoolean();
        String gender = isMale ? "MALE" : "FEMALE";
        List<String> pool = isMale ? NamePool.MALE_NAMES : NamePool.FEMALE_NAMES;
        String randomName = pool.get(random.nextInt(pool.size()));

        int age = randomBetween(MIN_AGE, MAX_AGE);

        Player newPlayer = new Player(id, randomName, age, gender);

        newPlayer.setEnergy(randomBetween(MIN_ENERGY, MAX_ENERGY));
        newPlayer.setCondition(randomBetween(MIN_CONDITION, MAX_CONDITION));
        newPlayer.setInjuryRisk(randomBetween(MIN_INJURY, MAX_INJURY));

        return newPlayer;
    }

    private int randomBetween(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}