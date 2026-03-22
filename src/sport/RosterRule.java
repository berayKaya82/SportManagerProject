package sport;

import domain.Team;

public interface RosterRule {
    boolean isValidRoster(Team team);
}
