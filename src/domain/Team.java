package domain;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private List<Player> players;
    private Coach coach;
    private Tactic tactic;
    private double coachRelationship;

    public Team(String name) {
        this.name = name;
        this.players = new ArrayList<Player>();
        this.coachRelationship = 50;
    }


}
