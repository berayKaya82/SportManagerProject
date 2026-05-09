package domain;

import football.FootballTieBreakerRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class LeagueTableTest {

    private Team newTeam(int id, String name) {
        return new Team(id, name, Gender.MALE);
    }

    private Match playedMatch(Team home, Team away, int homeGoals, int awayGoals) {
        Match match = new Match(home, away);
        match.startMatch();
        match.finishMatch(new MatchResult(homeGoals, awayGoals));
        return match;
    }

    @Test
    void recordingThreeOneUpdatesBothEntriesCorrectly() {
        LeagueTable table = new LeagueTable(new FootballTieBreakerRule());
        Team home = newTeam(1, "Home FC");
        Team away = newTeam(2, "Away FC");
        table.registerTeam(home);
        table.registerTeam(away);

        table.recordMatch(playedMatch(home, away, 3, 1));

        StandingEntry homeEntry = table.getEntry(home);
        assertEquals(1, homeEntry.getPlayed());
        assertEquals(1, homeEntry.getWins());
        assertEquals(0, homeEntry.getDraws());
        assertEquals(0, homeEntry.getLosses());
        assertEquals(3, homeEntry.getGoalsFor());
        assertEquals(1, homeEntry.getGoalsAgainst());
        assertEquals(2, homeEntry.getGoalDifference());
        assertEquals(3, homeEntry.getPoints());

        StandingEntry awayEntry = table.getEntry(away);
        assertEquals(1, awayEntry.getPlayed());
        assertEquals(0, awayEntry.getWins());
        assertEquals(0, awayEntry.getDraws());
        assertEquals(1, awayEntry.getLosses());
        assertEquals(1, awayEntry.getGoalsFor());
        assertEquals(3, awayEntry.getGoalsAgainst());
        assertEquals(-2, awayEntry.getGoalDifference());
        assertEquals(0, awayEntry.getPoints());
    }

    @Test
    void recordingDrawGivesOnePointToBothTeams() {
        LeagueTable table = new LeagueTable(new FootballTieBreakerRule());
        Team home = newTeam(1, "Home FC");
        Team away = newTeam(2, "Away FC");
        table.registerTeam(home);
        table.registerTeam(away);

        table.recordMatch(playedMatch(home, away, 2, 2));

        assertEquals(1, table.getEntry(home).getDraws());
        assertEquals(1, table.getEntry(away).getDraws());
        assertEquals(1, table.getEntry(home).getPoints());
        assertEquals(1, table.getEntry(away).getPoints());
        assertEquals(0, table.getEntry(home).getGoalDifference());
        assertEquals(0, table.getEntry(away).getGoalDifference());
    }
}
