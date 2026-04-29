package handball;

import domain.Player;
import sport.RosterRule;
import domain.Team;

public class HandballRosterRule implements RosterRule {
    @Override
    public boolean isValidRoster(Team team){
        if(team == null)return false;
        if(team.getStartingPlayers().size() != 7)return false;
        if(team.getCoach() == null)return false;
        if(team.getTactic() == null)return false;

        for(Player player : team.getStartingPlayers()){//player can be null we should check that
            if(player == null)return false;
        }
        return true;
    }
    @Override
    public int getStartingPlayerCount() {
        return 7;
    }

}