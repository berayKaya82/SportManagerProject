package application;

import java.util.List;

public class GameState {

    private String managerName;
    private int reputation;
    private int seasonNumber;

    private String sportName;
    private String genderName;
    private String teamName;
    private double coachRelationship;

    private List<PlayerData> starters;
    private List<PlayerData> substitutes;

    private String coachName;
    private int coachLevel;
    private int coachRequiredSeason;
    private int coachRequiredReputation;

    private String tacticStyle;

    private int currentWeek;
    private int totalWeeks;

    private List<StandingData> standings;

    private String saveDate;

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }

    public int getSeasonNumber() { return seasonNumber; }
    public void setSeasonNumber(int seasonNumber) { this.seasonNumber = seasonNumber; }

    public String getSportName() { return sportName; }
    public void setSportName(String sportName) { this.sportName = sportName; }

    public String getGenderName() { return genderName; }
    public void setGenderName(String genderName) { this.genderName = genderName; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public double getCoachRelationship() { return coachRelationship; }
    public void setCoachRelationship(double coachRelationship) { this.coachRelationship = coachRelationship; }

    public List<PlayerData> getStarters() { return starters; }
    public void setStarters(List<PlayerData> starters) { this.starters = starters; }

    public List<PlayerData> getSubstitutes() { return substitutes; }
    public void setSubstitutes(List<PlayerData> substitutes) { this.substitutes = substitutes; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public int getCoachLevel() { return coachLevel; }
    public void setCoachLevel(int coachLevel) { this.coachLevel = coachLevel; }

    public int getCoachRequiredSeason() { return coachRequiredSeason; }
    public void setCoachRequiredSeason(int coachRequiredSeason) { this.coachRequiredSeason = coachRequiredSeason; }

    public int getCoachRequiredReputation() { return coachRequiredReputation; }
    public void setCoachRequiredReputation(int coachRequiredReputation) { this.coachRequiredReputation = coachRequiredReputation; }

    public String getTacticStyle() { return tacticStyle; }
    public void setTacticStyle(String tacticStyle) { this.tacticStyle = tacticStyle; }

    public int getCurrentWeek() { return currentWeek; }
    public void setCurrentWeek(int currentWeek) { this.currentWeek = currentWeek; }

    public int getTotalWeeks() { return totalWeeks; }
    public void setTotalWeeks(int totalWeeks) { this.totalWeeks = totalWeeks; }

    public List<StandingData> getStandings() { return standings; }
    public void setStandings(List<StandingData> standings) { this.standings = standings; }

    public String getSaveDate() { return saveDate; }
    public void setSaveDate(String saveDate) { this.saveDate = saveDate; }

    public static class PlayerData {
        public int id;
        public String name;
        public int age;
        public String genderName;
        public int energy;
        public int condition;
        public int injuryRisk;
        public String injuryStatus;
        public int injuredGamesRemaining;
    }

    public static class StandingData {
        public String teamName;
        public int points;
        public int played;
        public int won;
        public int drawn;
        public int lost;
        public int goalsFor;
        public int goalsAgainst;
    }
}
