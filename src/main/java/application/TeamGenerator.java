package application;

import domain.*;
import sport.ISport;
import sport.ITactic;

import java.util.*;

/**
 * Responsible for generating fully initialized AI teams.
 * Does NOT store teams, only creates them.
 */
public class TeamGenerator {

    private final PlayerGenerator playerGenerator;
    private final Random random;

    private final Set<String> usedNames = new HashSet<>();

    public TeamGenerator(PlayerGenerator playerGenerator, Random random) {
        if (playerGenerator == null || random == null)
            throw new IllegalArgumentException("Dependencies cannot be null");

        this.playerGenerator = playerGenerator;
        this.random = random;
    }

    /**
     * Creates a fully initialized AI team.
     * Includes name generation, player creation, coach assignment, and chemistry setup.
     * @param id team ID
     * @param gender team gender
     * @param sport sport rules provider
     * @return created Team
     * @throws IllegalArgumentException if inputs are invalid
     * @throws IllegalStateException if roster size is invalid
     */
    public Team createRandomTeam(int id, Gender gender, ISport sport) {

        if (id <= 0)
            throw new IllegalArgumentException("Team ID must be positive");

        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");

        if (sport == null)
            throw new IllegalArgumentException("Sport cannot be null");

        // Generate team name
        String name = getUniqueTeamName();
        Team team = new Team(id, name, gender);

        // Generate players based on sport rules
        int startingCount = sport.getRosterRule().getStartingPlayerCount();

        if (startingCount <= 0)
            throw new IllegalStateException("Invalid roster size from sport");

        List<Player> starters =
                playerGenerator.generatePlayersByGender(startingCount, gender);

        // Assign players to team
        for (Player p : starters) {
            team.addStartingPlayer(p);
        }
        // Assign coach
        team.setCoach(generateCoach());

        team.setTactic(generateDefaultTactic());

        // Initialize team chemistry
        team.setCoachRelationship(randomBetween(45, 85));

        return team;
    }

    /**
     * Generates a unique team name.
     * Falls back to a random name if pool is exhausted.
     * @return unique team name
     */
    private String getUniqueTeamName() {

        List<String> names = NamePool.TEAM_NAMES;

        if (names == null || names.isEmpty()) {
            return "Team_" + random.nextInt(1000);
        }

        for (int i = 0; i < 100; i++) {
            String name = names.get(random.nextInt(names.size()));

            if (!usedNames.contains(name)) {
                usedNames.add(name);
                return name;
            }
        }

        // Fallback if all names are used
        return "Team_" + random.nextInt(1000);
    }

    /**
     * Generates a random coach.
     * Most coaches are mid-level, few are high-level.
     * @return Coach object
     */
    private Coach generateCoach() {

        String name = "Coach " + (char)(random.nextInt(26) + 'A') + ". " + (random.nextInt(900) + 100);

        int level = random.nextInt(100) < 70
                ? randomBetween(1, 3)
                : randomBetween(4, 5);

        int requiredSeason = 1;
        int reputation = level * 10;

        return new Coach(name, level, requiredSeason, reputation);
    }

    private ITactic generateDefaultTactic() {

        int r = random.nextInt(3);

        switch (r) {
            case 0:
                return () -> PlayStyle.DEFENSIVE;
            case 1:
                return () -> PlayStyle.OFFENSIVE;
            default:
                return () -> PlayStyle.BALANCED;
        }
    }

    /**
     * Generates a random number between min and max (inclusive).
     * @param min minimum value
     * @param max maximum value
     * @return random number in range
     */
    private int randomBetween(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}