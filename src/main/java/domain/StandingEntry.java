package domain;

public class StandingEntry {
    private Team team;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;

    public StandingEntry(Team team) {
        this.team = team;
        this.played = 0;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
    }

    public Team getTeam() {return team;}
    public int getPlayed() {return played;}
    public int getWins() {return wins;}
    public int getDraws() {return draws;}
    public int getLosses() {return losses;}
    public int getGoalsFor() {return goalsFor;}
    public int getGoalsAgainst() {return goalsAgainst;}


    public void recordResult(MatchResult result, boolean isHomeTeam) {
        int scored = isHomeTeam ? result.getHomeGoals() : result.getAwayGoals();
        int conceded = isHomeTeam ? result.getAwayGoals() : result.getHomeGoals();

        played++;
        goalsFor += scored;
        goalsAgainst += conceded;

        if (scored > conceded) {
            wins++;
        } else if (scored == conceded) {
            draws++;
        } else {
            losses++;
        }
    }

    public int getPoints() {
        // From the PDF: win = 2pts, draw = 1pt, loss = 0pts
        return (wins * 2) + (draws * 1);
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

}
