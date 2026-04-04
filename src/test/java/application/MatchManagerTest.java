package application;

import domain.*;
import football.*;
import sport.MatchSimulator;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MatchManagerTest {

    private Team createFullTeam(int id, String name, Gender gender) {
        Team team = new Team(id, name, gender);
        for (int i = 1; i <= 11; i++) {
            team.addStartingPlayer(new Player(id * 100 + i, "P" + i, 22, gender));
        }
        team.setCoach(new Coach("Coach", 1, 1, 0));
        team.setTactic(new FootballTactic(PlayStyle.BALANCED));
        return team;
    }

    private MatchManager createMatchManager() {
        FootballSport sport = new FootballSport();
        return new MatchManager(sport.getMatchSimulator(), new Random(42));
    }

    @Test
    void conditionChangeOnWinIsNetZero() {
        Team home = createFullTeam(1, "Home FC", Gender.MALE);
        Team away = createFullTeam(2, "Away FC", Gender.MALE);
        Match match = new Match(home, away);
        match.startMatch();
        MatchResult winResult = new MatchResult(3, 1);

        Player p = home.getStartingPlayers().get(0);
        int condBefore = p.getCondition();

        MatchManager mm = createMatchManager();
        mm.applyConditionChange(home, match, winResult);

        assertEquals(condBefore, p.getCondition(), "Win should result in net 0 condition change");
    }

    @Test
    void conditionChangeOnLossReducesCondition() {
        Team home = createFullTeam(1, "Home FC", Gender.MALE);
        Team away = createFullTeam(2, "Away FC", Gender.MALE);
        Match match = new Match(home, away);
        match.startMatch();
        MatchResult lossResult = new MatchResult(0, 3);

        Player p = home.getStartingPlayers().get(0);
        int condBefore = p.getCondition();

        MatchManager mm = createMatchManager();
        mm.applyConditionChange(home, match, lossResult);

        assertEquals(condBefore - 11, p.getCondition(), "Loss should reduce condition by 11");
    }

    @Test
    void postMatchEnergyLossReducedByCoach() {
        Team team = createFullTeam(1, "FC", Gender.MALE);
        Coach goodCoach = new Coach("Pro", 5, 1, 0);
        team.setCoach(goodCoach);
        team.setCoachRelationship(80);

        Team opponent = createFullTeam(2, "Opp FC", Gender.MALE);

        Player p = team.getStartingPlayers().get(0);
        int energyBefore = p.getEnergy();

        MatchManager mm = createMatchManager();
        mm.applyPostMatchEnergyLoss(team);

        int actualLoss = energyBefore - p.getEnergy();
        assertTrue(actualLoss < 15, "Coach bonus should reduce the default 15 energy loss");
        assertTrue(actualLoss > 0, "There should still be some energy loss");
    }
}
