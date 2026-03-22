package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import sport.ITactic;
/**
 * Represents a sports team in the game.
 *
 * <p>A team consists of a starting lineup and a substitute bench,
 * managed by a coach and playing according to a sport-specific tactic.
 * The relationship between the team and its coach influences training outcomes.</p>
 *
 * <p>Player lists are exposed as unmodifiable views to enforce
 * all mutations through the team's own methods.</p>
 */
public class Team {
    private String id;
    private String name;
    private List<Player> startingPlayers;
    private List<Player> substitutes;
    private Coach coach;
    private ITactic tactic;
    /**
     * Relationship score between the team and its coach, ranging from 0 to 100.
     * A higher value positively affects the training multiplier.
     * Defaults to 50 (neutral) on team creation.
     */
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
