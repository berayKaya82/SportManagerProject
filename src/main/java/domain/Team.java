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
 * managed by a coach and playing according to a main.java.sport-specific tactic.
 * The relationship between the team and its coach influences training outcomes.</p>
 *
 * <p>Player lists are exposed as unmodifiable views to enforce
 * all mutations through the team's own methods.</p>
 */
public class Team {
    private final int id;
    private String name;
    private Gender gender;
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

    public Team(int id, String name, Gender gender) {
        if (id <= 0)
            throw new IllegalArgumentException("ID must be positive");

        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty");

        if (gender == null)
            throw new IllegalArgumentException("Gender cannot be null");

        this.id = id;
        this.name = name;
        this.gender = gender;

        this.startingPlayers = new ArrayList<>();
        this.substitutes = new ArrayList<>();
        this.coachRelationship = 50;
    }
    public Gender getGender() {
        return gender;
    }
    public int getId() {
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
    public void setCoachRelationship(double coachRelationship) {
        if (coachRelationship < 0 || coachRelationship > 100)
            throw new IllegalArgumentException("Coach relationship must be between 0 and 100");

        this.coachRelationship = coachRelationship;
    }

    public void addStartingPlayer(Player player) {
        if(player==null)
            throw new IllegalArgumentException("Player cannot be null");

        if (player.getGender() != this.gender)
            throw new IllegalArgumentException("Player gender mismatch with team");

        if (startingPlayers.contains(player) || substitutes.contains(player))
            throw new IllegalStateException("Player already in team");

        startingPlayers.add(player);
    }
    public void addSubstitute(Player player) {
        if (player == null)
            throw new IllegalArgumentException("Player cannot be null");

        if (player.getGender() != this.gender)
            throw new IllegalArgumentException("Player gender mismatch with team");

        if (startingPlayers.contains(player) || substitutes.contains(player))
            throw new IllegalStateException("Player already in team");

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

    /**
     * Swaps a starting player with a substitute.
     * The outgoing player moves to the bench, incoming player joins the lineup.
     */
    public void substitutePlayer(Player out, Player in) {
        if (out == null || in == null)
            throw new IllegalArgumentException("Players cannot be null");
        if (!startingPlayers.contains(out))
            throw new IllegalStateException("Player to remove is not in starting lineup");
        if (!substitutes.contains(in))
            throw new IllegalStateException("Player to add is not on the bench");

        startingPlayers.remove(out);
        substitutes.remove(in);
        startingPlayers.add(in);
        substitutes.add(out);
    }

    @Override
    public String toString() {
        return name;
    }
    public String getRosterStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(name).append(" - Player Status ===\n");
        sb.append(String.format("%-20s %-8s %-10s %-12s %-10s%n",
                "Name", "Energy", "Condition", "InjuryRisk", "Status"));
        sb.append("-".repeat(62)).append("\n");

        for (Player p : startingPlayers) {
            sb.append(formatPlayerRow(p));
        }
        if (!substitutes.isEmpty()) {
            sb.append("-- Substitutes --\n");
            for (Player p : substitutes) {
                sb.append(formatPlayerRow(p));
            }
        }
        return sb.toString();
    }

    private String formatPlayerRow(Player p) {
        return String.format("%-20s %-8d %-10d %-12d %-10s%n",
                p.getName(),
                p.getEnergy(),
                p.getCondition(),
                p.getInjuryRisk(),
                p.getInjuryStatus()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        return id == ((Team) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
