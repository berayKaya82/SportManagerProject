package handball;
import domain.Match;
import domain.MatchResult;
import domain.PlayStyle;
import domain.Player;
import domain.Team;
import sport.ScoringRule;
import java.util.List;
import java.util.Random;

public class HandballScoringRule implements ScoringRule {

    private static final int HALF_BASE = 10;                    // base goals per half
    private static final int HALF_SPREAD = 4;                   // random.nextInt(4) -> 0..3 -> 10..13
    private static final int HOME_ADVANTAGE = 1;
    private static final int TACTIC_BONUS = 2;                  // scaled vs football's +/-1
    private static final int ENERGY_BONUS = 2;
    private static final int CONDITION_BONUS = 2;
    private static final double HIGH_FITNESS_THRESHOLD = 80.0;
    private static final double LOW_FITNESS_THRESHOLD = 50.0;
    private static final int MIN_HALF_GOALS = 2;                // realistic floor for a handball half

    private final Random random;

    public HandballScoringRule(Random random){
        if(random==null){
            throw new IllegalArgumentException("Random can not be null");
        }
        this.random=random;
    }

    @Override
    public MatchResult generateResult(Match match){
        // Full match = sum of two halves; consistent with HandballMatchSimulator's per-period flow.
        MatchResult firstHalf = generateHalfResult(match);
        MatchResult secondHalf = generateHalfResult(match);
        return new MatchResult(
                firstHalf.getHomeGoals() + secondHalf.getHomeGoals(),
                firstHalf.getAwayGoals() + secondHalf.getAwayGoals()
        );
    }

    public MatchResult generateHalfResult(Match match){
        return generateScore(match);
    }


    private MatchResult generateScore(Match match){
        if(match == null){
            throw new IllegalArgumentException("Match can not be null");
        }
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();

        int homeGoals = calculateGoals(homeTeam,awayTeam,true);
        int awayGoals = calculateGoals(awayTeam,homeTeam,false);

        return new MatchResult(homeGoals , awayGoals);
    }
    private int calculateGoals(Team attackingTeam,Team defendingTeam , boolean isHome){
        // half-time base score: HALF_BASE..HALF_BASE+HALF_SPREAD-1  (10..13)
        int score = HALF_BASE + random.nextInt(HALF_SPREAD);
        if(isHome){
            score += HOME_ADVANTAGE;
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
            case OFFENSIVE -> TACTIC_BONUS;
            case BALANCED -> 0;
            case DEFENSIVE -> -TACTIC_BONUS;
        };
    }
    private int getDefenseEffectFromTactic(PlayStyle style){
        return switch (style){
            case OFFENSIVE -> -TACTIC_BONUS;
            case BALANCED -> 0;
            case DEFENSIVE -> TACTIC_BONUS;
        };
    }
    private int getEnergyBonus(Team team){
        double avgEnergy = getAverageEnergy(team);
        if(avgEnergy >= HIGH_FITNESS_THRESHOLD)return ENERGY_BONUS;
        if(avgEnergy <= LOW_FITNESS_THRESHOLD)return -ENERGY_BONUS;
        return 0;
    }
    private int getConditionBonus(Team team){
        double avgCondition=getAverageCondition(team);
        if(avgCondition >= HIGH_FITNESS_THRESHOLD)return CONDITION_BONUS;
        if(avgCondition <= LOW_FITNESS_THRESHOLD)return -CONDITION_BONUS;
        return 0;
    }
    private double getAverageEnergy(Team team){
        List<Player> players = team.getStartingPlayers();
        int count = 0;
        int total = 0;

        for(Player player : players){
            if (!player.isAvailableForMatch()) continue;
            total += player.getEnergy();
            count++;
        }
        return count == 0 ? 0 : (double) total / count;
    }
    private double getAverageCondition(Team team){
        List<Player> players = team.getStartingPlayers();
        int count = 0;
        int total = 0;

        for(Player player : players){
            if (!player.isAvailableForMatch()) continue;
            total += player.getCondition();
            count++;
        }
        return count == 0 ? 0 : (double) total / count;
    }
    private int getCoachMatchBonus(Team team) {
        if (team.getCoach() == null) return 0;
        return team.getCoach().getMatchBonus(team.getCoachRelationship());
    }
    private int clampGoals(int goals){
        return Math.max(MIN_HALF_GOALS, goals);
    }
}
