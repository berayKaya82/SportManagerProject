package handball;

import domain.*;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class HandballMatchSimulatorTest {
    private Team createTeam(int id, String name) {
        Team team = new Team(id, name, Gender.MALE);
        for (int i = 1; i <= 7; i++) {
            team.addStartingPlayer(new Player(id * 100 + i, "P" + i, 22, Gender.MALE));
        }
        team.setCoach(new Coach("Coach", 1, 1, 0));
        team.setTactic(new HandballTactic(PlayStyle.BALANCED));
        team.setCoachRelationship(50);
        return team;
    }
    @Test
    void getNumberOfPeriodsReturnsTwo(){
        Random random =new Random(1);

        HandballMatchSimulator simulator = new HandballMatchSimulator(new HandballMatchFlow(),new HandballRosterRule(),new HandballScoringRule(random),random);
        assertEquals(2,simulator.getNumberOfPeriods());
    }
    @Test
    void simulateMatchIsValidResult(){
        Random random=new Random(1);
        HandballMatchSimulator simulator = new HandballMatchSimulator(new HandballMatchFlow(),new HandballRosterRule(),new HandballScoringRule(random),random);

        Team home = createTeam(1,"Home");
        Team away = createTeam(2,"Away");

        Match match = new Match(home,away);
        MatchResult result = simulator.simulateMatch(match);
        assertNotNull(result);
        assertTrue(result.getHomeGoals() >= 0);
        assertTrue(result.getAwayGoals() >= 0);


    }
    @Test
    void simulateMatchThrowsExceptionWhenRosterIsInvalid(){
        Random random=new Random(1);
        HandballMatchSimulator simulator = new HandballMatchSimulator(new HandballMatchFlow(),new HandballRosterRule(),new HandballScoringRule(random),random);

        Team home = createTeam(1,"Home");
        Team away = new Team(2,"Away",Gender.MALE);
        for(int i=1; i<=6; i++){
            away.addStartingPlayer(new Player(i,"P"+i,22,Gender.MALE));
        }
        away.setCoach(new Coach("Coach",1,1,0));
        away.setTactic(new HandballTactic(PlayStyle.BALANCED));
        away.setCoachRelationship(50);



        Match match = new Match(home,away);
        assertThrows(IllegalStateException.class , () ->{
            simulator.simulateMatch(match);
        });



    }

}
