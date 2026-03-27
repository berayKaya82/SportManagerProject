package application;

import domain.*;
// import football.FootballTactic; // Not used currently
import sport.ISport;
// import sport.ITactic; // Tactic system temporarily disabled

import java.util.*;

/**
 TeamGenerator is responsible for creating fully initialized AI teams.
 Responsibilities:
 - Generate unique team names
 - Create players based on main.java.sport rules
 - Ensure gender consistency
 - Assign coach and initial team attributes
 Note:
 This class ONLY creates teams, it does not store them.
 Storage responsibility belongs to TeamManager.
 */
public class TeamGenerator {

    private final PlayerGenerator playerGenerator;
    private final PlayerManager playerManager;
    private final Random random;

    // Prevents duplicate team names in a single generation session
    private final Set<String> usedNames = new HashSet<>();

    public TeamGenerator(PlayerGenerator playerGenerator,
                         PlayerManager playerManager,
                         Random random) {

        if (playerGenerator == null || playerManager == null || random == null)
            throw new IllegalArgumentException("Dependencies cannot be null");

        this.playerGenerator = playerGenerator;
        this.playerManager = playerManager;
        this.random = random;
    }

    /**
     Creates a fully initialized AI team.
     Flow:
     1. Generate unique team name
     2. Create players according to main.java.sport roster rules
     3. Assign players to team
     4. Assign coach
     5. Initialize team chemistry
     @param id      unique team id
     @param gender  team gender
     @param sport   main.java.sport rules provider
     @return fully constructed Team
     */
    public Team createRandomTeam(int id, Gender gender, ISport sport) {

        if (id <= 0)
            throw new IllegalArgumentException("Team ID must be positive");

        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");

        if (sport == null)
            throw new IllegalArgumentException("Sport cannot be null");

        //Generate team name
        String name = getUniqueTeamName();
        Team team = new Team(id, name, gender);

        //Generate players based on main.java.sport rules
        int startingCount = sport.getRosterRule().getStartingPlayerCount();

        if (startingCount <= 0)
            throw new IllegalStateException("Invalid roster size from main.java.sport");

        List<Player> starters =
                playerGenerator.generatePlayersByGender(startingCount, gender);

        //Register players globally (single source of truth)
        playerManager.addPlayers(starters);

        //Assign players to team
        for (Player p : starters) {
            team.addStartingPlayer(p);
        }

        //Assign coach
        team.setCoach(generateCoach());

        // team.setTactic(generateDefaultTactic(main.java.sport));

        //Initialize team chemistry
        team.setCoachRelationship(randomBetween(45, 85));

        return team;
    }

    /**
     Generates a unique team name.
     Falls back to random name if pool is exhausted.
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

        // fallback if all names are used
        return "Team_" + random.nextInt(1000);
    }

    /**
     Generates a randomized coach.
     Logic:
     - Most coaches are mid-level
     - Few high-level coaches exist
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

    /*
    private ITactic generateDefaultTactic(ISport main.java.sport) {
        return main.java.sport.createDefaultTactic();
    }
    */

    /**
     Utility method for generating a random integer in range [min, max]
     */
    private int randomBetween(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}
