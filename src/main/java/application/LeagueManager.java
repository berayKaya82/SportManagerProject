package application;

import domain.*;
import sport.ISport;

import java.util.*;

/**
 * Manages leagues.
 * Handles creation and retrieval of leagues by gender and sport.
 */
public class LeagueManager {

    // Stores leagues by gender and sport name
    private final Map<Gender, Map<String, League>> leagues = new HashMap<>();

    private final TeamGenerator teamGenerator;

    // Auto-increment team ID
    private int nextTeamId = 1;

    // Total teams in a league
    private static final int TOTAL_TEAMS = 18;

    /**
     * Initializes LeagueManager with required dependencies.
     * @param teamGenerator team generator instance
     * @throws IllegalArgumentException if teamGenerator is null
     */
    public LeagueManager(TeamGenerator teamGenerator) {
        if (teamGenerator == null)
            throw new IllegalArgumentException("TeamGenerator cannot be null");

        this.teamGenerator = teamGenerator;
    }

    public League createLeagueWithUserTeam(String teamName, Gender gender, ISport sport) {

        if (teamName == null || teamName.isBlank())
            throw new IllegalArgumentException("Team name cannot be empty");
        if (gender == null || sport == null)
            throw new IllegalArgumentException("Gender and sport cannot be null");

        String sportKey = sport.getSportName();
        if (leagues.containsKey(gender) && leagues.get(gender).containsKey(sportKey))
            throw new IllegalStateException("League already exists for this gender and sport");

        int userTeamId = nextTeamId++;
        Team userTeam = new Team(userTeamId, teamName, gender);

        List<Team> teams = new ArrayList<>();
        teams.add(userTeam);

        for (int i = 0; i < TOTAL_TEAMS - 1; i++) {
            Team aiTeam = teamGenerator.createRandomTeam(nextTeamId++, gender, sport);
            teams.add(aiTeam);
        }

        String leagueName = sport.getSportName() + " League";
        League league = new League(leagueName, gender, teams, sport);

        leagues.computeIfAbsent(gender, g -> new HashMap<>())
                .put(sportKey, league);

        return league;
    }

    /**
     * Returns the league for given gender and sport.
     */
    public League getLeague(Gender gender, ISport sport) {
        Map<String, League> sportMap = leagues.get(gender);

        if (sportMap == null)
            throw new IllegalStateException("No leagues found for gender: " + gender);

        League league = sportMap.get(sport.getSportName());

        if (league == null)
            throw new IllegalStateException("League not found for given gender and sport");

        return league;
    }

    /**
     * Returns fixture of a league.
     */
    public Fixture getFixture(Gender gender, ISport sport) {
        return getLeague(gender, sport).getFixture();
    }
}