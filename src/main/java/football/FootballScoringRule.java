package football;
import domain.Match;
import domain.MatchResult;
import domain.PlayStyle;
import domain.Player;
import domain.Team;
import sport.ScoringRule;
import java.util.List;
import java.util.Random;

public class FootballScoringRule implements ScoringRule {
    private final Random random;

    public FootballScoringRule(Random random){
        if(random==null){
            throw new IllegalArgumentException("Random can not be null");
        }
        this.random=random;
    }
    @Override
    public MatchResult generateResult(Match match){
        return generateScore(match,false);
    }
    public MatchResult generateHalfResult(Match match){
        return generateScore(match,true);
    }


    private MatchResult generateScore(Match match,boolean isHalf){
        if(match == null){
            throw new IllegalArgumentException("Match can not be null");
        }
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();

        int homeGoals = calculateGoals(homeTeam,awayTeam,true,isHalf);
        int awayGoals = calculateGoals(awayTeam,homeTeam,false,isHalf);

        return new MatchResult(homeGoals , awayGoals);
}
 private int calculateGoals(Team attackingTeam,Team defendingTeam , boolean isHome,boolean isHalf){
       int score;
        if(isHalf){
            score =random.nextInt(3)+random.nextInt(3);
        }else{
            score=random.nextInt(6)+random.nextInt(6);
        }
        if(isHome){
            score +=1;//home advantage
        }
        PlayStyle attackingStyle = attackingTeam.getTactic().getPlayStyle();
        PlayStyle defendingStyle = defendingTeam.getTactic().getPlayStyle();

        score += getAttackBonusFromTactic(attackingStyle);
        score -= getDefenseEffectFromTactic(defendingStyle);
        score += getEnergyBonus(attackingTeam);
        score += getConditionBonus(attackingTeam);
        score += getCoachMatchBonus(attackingTeam);

        return clampGoals(score);
 }
  private int getAttackBonusFromTactic(PlayStyle style){
     return switch(style){
         case OFFENSIVE -> 1;
         case BALANCED -> 0;
         case DEFENSIVE -> -1;
     };
 }
 private int getDefenseEffectFromTactic(PlayStyle style){
        return switch (style){
            case OFFENSIVE -> -1;
            case BALANCED -> 0;
            case DEFENSIVE -> 1;
        };
 }
 private int getEnergyBonus(Team team){
        double avgEnergy = getAverageEnergy(team);
        if(avgEnergy >= 80)return 1;
        if(avgEnergy <= 50)return -1;
        return 0;
 }
 private int getConditionBonus(Team team){
        double avgCondition=getAverageCondition(team);
        if(avgCondition >= 80)return 1;
        if(avgCondition <= 50)return -1;
        return 0;
 }
 private double getAverageEnergy(Team team){
        List<Player> players = team.getStartingPlayers();
        if (players.isEmpty())return 0;
        int total = 0;

        for(Player player : players){
            total += player.getEnergy();
        }
        return (double) total / players.size();

 }
 private double getAverageCondition(Team team){
        List<Player> players = team.getStartingPlayers();
        if(players.isEmpty())return 0;
        int total=0;

        for(Player player :players){
            total += player.getCondition();
        }
        return (double) total/ players.size();
 }
 private int getCoachMatchBonus(Team team) {
        if (team.getCoach() == null) return 0;
        return team.getCoach().getMatchBonus(team.getCoachRelationship());
 }
 private int clampGoals(int goals){
        return Math.max(0,goals);
 }
}
