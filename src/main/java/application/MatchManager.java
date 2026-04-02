package application;

import domain.*;
import sport.MatchSimulator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Orchestrates the match phase of each game week.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Simulate AI matches using the sport's MatchSimulator</li>
 *   <li>Handle the user's match in two halves (first + second)</li>
 *   <li>Apply post-match effects: energy loss, injuries, recovery</li>
 *   <li>Collect and return results for SeasonCycleManager</li>
 * </ul>
 *
 * <p>Tactic changes between halves are handled by the caller,
 * not by this class.</p>
 */
public class MatchManager {
    private static final int MATCH_ENERGY_LOSS = 15;
    private static final int SUB_ENERGY_LOSS = 5;
    private static final int INJURY_THRESHOLD = 50;
    private static final int MAX_INJURY_GAMES = 5;

    private final MatchSimulator matchSimulator;
    private final Random random;

    public MatchManager(MatchSimulator matchSimulator, Random random) {
        if (matchSimulator == null)
            throw new IllegalArgumentException("MatchSimulator cannot be null");
        if (random == null)
            throw new IllegalArgumentException("Random cannot be null");
        this.matchSimulator = matchSimulator;
        this.random = random;
    }

    public Map<Match, MatchResult> simulateAIMatches(MatchWeek week, Team userTeam) {
        if (week == null)   throw new IllegalArgumentException("Week cannot be null");
        if (userTeam == null) throw new IllegalArgumentException("UserTeam cannot be null");

        Map<Match, MatchResult> results = new LinkedHashMap<>();

        for (Match match : week.getMatches()) {
            // Skip the user's match
            if (match.involvedTeam(userTeam)) continue;
            // Skip already finished matches
            if (match.isFinished()) continue;

            MatchResult result = matchSimulator.simulateMatch(match);
            results.put(match, result);
        }

        return results;
    }
    public Optional<Match> findUserMatch(MatchWeek week, Team userTeam) {
        if (week == null || userTeam == null)
            throw new IllegalArgumentException("Week and userTeam cannot be null");

        return week.getMatches().stream().filter(m -> m.involvedTeam(userTeam)).findFirst();
    }
    // -------------------------------------------------------------------------
    // User Match — Two Halves
    // -------------------------------------------------------------------------
    public MatchResult playUserFirstHalf(Match match) {
        if (match == null)
            throw new IllegalArgumentException("Match cannot be null");
        if (match.getStatus() != MatchStatus.IN_PROGRESS)
            throw new IllegalStateException("Match must be 'IN PROGRESS' to start first half");
        return matchSimulator.playFirstHalf(match);
    }
    public MatchResult playUserSecondHalf(Match match, MatchResult firstHalfResult) {
        if (match == null)
            throw new IllegalArgumentException("Match cannot be null");
        if (firstHalfResult == null)
            throw new IllegalArgumentException("First half result cannot be null");
        if (match.getStatus() != MatchStatus.IN_PROGRESS)
            throw new IllegalStateException("Match must be IN_PROGRESS for second half");
        return matchSimulator.playSecondHalf(match, firstHalfResult);
    }
    // -------------------------------------------------------------------------
    // Post-Match Effects
    // -------------------------------------------------------------------------

    /**
     * Applies all post-match effects in order:
     * energy loss (reduced by coach bonus), random injuries, injury recovery.
     */
    public void applyPostMatchEffects(Team team) {
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");

        applyPostMatchEnergyLoss(team);
        applyPostMatchInjuries(team);
        applyInjuryRecovery(team);
    }

    /**
     * Applies energy loss to all players. Coach reduces the loss
     * via {@link Coach#getMatchEnergyReduction(double)}.
     */
    public void applyPostMatchEnergyLoss(Team team) {
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");

        double reduction = getCoachEnergyReduction(team);

        for (Player player : team.getStartingPlayers()) {
            int adjustedLoss = (int) Math.round(MATCH_ENERGY_LOSS * (1.0 - reduction));
            applyEnergyLoss(player, adjustedLoss);
        }
        for (Player player : team.getSubstitutes()) {
            int adjustedLoss = (int) Math.round(SUB_ENERGY_LOSS * (1.0 - reduction));
            applyEnergyLoss(player, adjustedLoss);
        }
    }

    /**
     * Randomly injures starting players whose injuryRisk exceeds the threshold.
     * Injury duration is 1–{@value MAX_INJURY_GAMES} games.
     */
    public void applyPostMatchInjuries(Team team) {
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");

        for (Player player : team.getStartingPlayers()) {
            if (player.getInjuryStatus() == InjuryStatus.INJURED) continue;

            if (player.getInjuryRisk() > INJURY_THRESHOLD
                    && random.nextInt(100) < player.getInjuryRisk()) {
                int games = random.nextInt(MAX_INJURY_GAMES) + 1;
                player.injure(games);
            }
        }
    }

    /**
     * Decrements the injury counter for all injured players in the team.
     * Players whose counter reaches 0 are automatically set to HEALTHY.
     */
    public void applyInjuryRecovery(Team team) {
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");

        for (Player player : team.getStartingPlayers()) {
            player.recoverOneGame();
        }
        for (Player player : team.getSubstitutes()) {
            player.recoverOneGame();
        }
    }

    public boolean isWeekComplete(MatchWeek week) {
        if (week == null)
            throw new IllegalArgumentException("Week cannot be null");
        return week.isFullyPlayed();
    }

    // --- Private helpers ---

    private void applyEnergyLoss(Player player, int loss) {
        if (player.getInjuryStatus() == InjuryStatus.INJURED) return;
        player.setEnergy(Math.max(0, player.getEnergy() - loss));
    }

    private double getCoachEnergyReduction(Team team) {
        if (team.getCoach() == null) return 0.0;
        return team.getCoach().getMatchEnergyReduction(team.getCoachRelationship());
    }
}
