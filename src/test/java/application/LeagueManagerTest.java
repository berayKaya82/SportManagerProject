package application;

import domain.Gender;
import domain.League;
import football.FootballSport;
import handball.HandballSport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeagueManagerTest {

    private LeagueManager leagueManager;

    @BeforeEach
    void setUp() {
        // Deterministic seed so AI team / player generation is reproducible.
        Random random = new Random(42);
        PlayerManager playerManager = new PlayerManager();
        PlayerGenerator playerGenerator = new PlayerGenerator(random, playerManager);
        TeamGenerator teamGenerator = new TeamGenerator(playerGenerator, random);
        leagueManager = new LeagueManager(teamGenerator, playerGenerator);
    }

    @Test
    void footballLeagueHas18Teams() {
        League league = leagueManager.createLeagueWithUserTeam(
                "User FC", Gender.MALE, new FootballSport());

        assertEquals(18, league.getTeams().size(),
                "FootballSport.getTeamCount()=18 must drive league size");
    }

    @Test
    void handballLeagueHas12Teams() {
        League league = leagueManager.createLeagueWithUserTeam(
                "User HC", Gender.MALE, new HandballSport());

        assertEquals(12, league.getTeams().size(),
                "HandballSport.getTeamCount()=12 must drive league size");
    }
}
