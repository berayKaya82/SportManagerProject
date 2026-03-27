package football;

import sport.RosterRule;
import domain.Team;

public class FootballRosterRule implements RosterRule {
    @Override
    public boolean isValidRoster(Team team){
        if(team == null)return false;
        if(team.getStartingPlayers().size() != 11)return false;
        if(team.getCoach() == null)return false;
        if(team.getTactic() == null)return false;
        return true;
    }
    @Override
    public int getStartingPlayerCount() {
        return 11;
    }

}
