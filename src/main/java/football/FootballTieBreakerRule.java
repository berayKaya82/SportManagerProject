package football;

import domain.Match;
import domain.StandingEntry;
import sport.TieBreakerRule;
import java.util.List;


public class  FootballTieBreakerRule implements TieBreakerRule {
    @Override
    public int compare(StandingEntry first, StandingEntry second , List<Match>playedMatches){
        if(first == null || second == null){
            throw new IllegalArgumentException("first or second standing entry cannot be null !");
        }
        if(first.getPoints() != second.getPoints()){
            return Integer.compare(second.getPoints(),first.getPoints() );//the team that has more point should be upper level
        }
        if(first.getGoalDifference() != second.getGoalDifference()){
            return Integer.compare(second.getGoalDifference(), first.getGoalDifference());// if the teams has equal points we will look average(average is goalsfor-goalsagainst)
        }
        if(first.getGoalsFor() != second.getGoalsFor()){
            return Integer.compare(second.getGoalsFor(), first.getGoalsFor());// if average is equal than we look for the team has most goal
        }
        return first.getTeam().getName().compareToIgnoreCase(second.getTeam().getName());// everything can be equal so we don't do anything we maybe can toss a coin but there may be errors in test case so we leave it like that
        //while comparing I always put secondteam first so after comparing if first team is more succesfull it can be go up in the table
    }
}
