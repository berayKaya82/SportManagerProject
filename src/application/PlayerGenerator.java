package application;

import domain.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerGenerator {
    private int nextId = 1;

    private Random random = new Random();

    public List<Player> generatePlayers(int count) {
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Player player = createSinglePlayer();
            players.add(player);
        }

        return players;
    }

    private Player createSinglePlayer() {
        boolean isMale = random.nextBoolean();
        String gender = isMale ? "Male" : "Female";

        List<String> pool = isMale ? NamePool.MALE_NAMES : NamePool.FEMALE_NAMES;
        String randomName = pool.get(random.nextInt(pool.size()));

        int age = 18 + random.nextInt(33); // 18-50

        Player newPlayer = new Player(nextId++, randomName, age, gender);

        newPlayer.setEnergy(60 + random.nextInt(41));      // 60-100
        newPlayer.setCondition(50 + random.nextInt(51));   // 50-100
        newPlayer.setInjuryRisk(random.nextInt(31));       // 0-30

        return newPlayer;
    }
}