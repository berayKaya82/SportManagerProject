package application;

import domain.*;
import sport.ISport;

import java.util.*;

/**
 * LeagueManager:
 * Responsible for creating and managing leagues.
 */
public class LeagueManager {

    private final Map<Gender, League> leagues = new HashMap<>();
    private final TeamGenerator teamGenerator;

    private static final int TOTAL_TEAMS = 18;

    public LeagueManager(TeamGenerator teamGenerator) {
        if (teamGenerator == null)
            throw new IllegalArgumentException("TeamGenerator cannot be null");

        this.teamGenerator = teamGenerator;
    }

    public League createLeague(Gender gender, Team userTeam, ISport sport) {

        if (gender == null || userTeam == null || sport == null)
            throw new IllegalArgumentException("Inputs cannot be null");

        if (!gender.equals(userTeam.getGender()))
            throw new IllegalArgumentException("User team gender mismatch");

        if (leagues.containsKey(gender))
            throw new IllegalStateException("League already exists for gender: " + gender);

        List<Team> teams = new ArrayList<>();
        teams.add(userTeam);

        //Safe ID generation
        int nextId = 1;

        for (int i = 0; i < TOTAL_TEAMS - 1; i++) {

            while (containsId(teams, nextId)) {
                nextId++;
            }

            Team aiTeam = teamGenerator.createRandomTeam(nextId, gender, sport);
            teams.add(aiTeam);
            nextId++;
        }

        //League name
        String leagueName = sport.getClass().getSimpleName() + " League";

        League league = new League(leagueName, gender, teams, sport);

        leagues.put(gender, league);

        return league;
    }

    public League getLeague(Gender gender) {
        League league = leagues.get(gender);

        if (league == null)
            throw new IllegalStateException("League not found for gender: " + gender);

        return league;
    }

    public Fixture getFixture(Gender gender) {
        return getLeague(gender).getFixture();
    }
    /*
    public List<StandingEntry> getStandings(Gender gender) {
        return getLeague(gender).getTable().getSortedStandings();
    }

    public void updateStandings(Gender gender, Match match) {

        if (match == null)
            throw new IllegalArgumentException("Match cannot be null");

        League league = getLeague(gender);

        league.getTable().recordMatch(match);
    }
    */
    private boolean containsId(List<Team> teams, int id) {
        return teams.stream().anyMatch(t -> t.getId() == id);
    }
}
