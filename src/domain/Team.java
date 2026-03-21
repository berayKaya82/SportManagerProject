package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import sport.ITactic;

public class Team {
    private String id;
    private String name;
    private List<Player> startingPlayers;
    private List<Player> substitutes;
    private Coach coach;
    private ITactic tactic;
    private double coachRelationship;

    public Team(String id,String name) {
        this.id = id;
        this.name = name;
        this.startingPlayers = new ArrayList<>();
        this.substitutes = new ArrayList<>();
        this.coachRelationship = 50;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<Player> getStartingPlayers() {return Collections.unmodifiableList(startingPlayers);}
    public List<Player> getSubstitutes() {return Collections.unmodifiableList(substitutes);}
    public Coach getCoach() {return coach;}
    public void setCoach(Coach coach) {this.coach = coach;}
    public ITactic getTactic() {return tactic;}
    public void setTactic(ITactic tactic) {this.tactic = tactic;}
    public double getCoachRelationship() {return coachRelationship;}
    public void setCoachRelationship(double coachRelationship) {this.coachRelationship = coachRelationship;}

    public void addStartingPlayer(Player player) {
        if(player==null) throw new IllegalArgumentException("Player cannot be null");
        startingPlayers.add(player);
    }
    public void addSubstitute(Player player) {
        if(player==null) throw new IllegalArgumentException("Player cannot be null");
        substitutes.add(player);
    }
    public void removeStartingPlayer(Player player) {
        if (!startingPlayers.remove(player))
            throw new NoSuchElementException("Player not in starting list");
    }
    public void removeSubstitute(Player player) {
        if(!substitutes.remove(player))
            throw new NoSuchElementException("Player not in substitute list");
    }
    @Override
    public String toString() {
        return name;
    }

}
