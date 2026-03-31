package application;

import domain.*;
import football.FootballMatchSimulator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Orchestrates the match phase of each game week.
 *
 * Responsibilities:
 *
 * Simulate AI matches using the sport's MatchSimulator</li>
 *  Handle the user's match in two halves (first + second)</li>
 *  Apply post-match energy loss to players</li>
 *  Collect and return results for SeasonCycleManager</li>
 *
 * Tactic changes between halves are handled by the caller (Main),
 * not by this class.
 *
 */
public class MatchManager {
    private static final int MATCH_ENERGY_LOSS = 15;
    private static final int SUB_ENERGY_LOSS = 5;
    private final FootballMatchSimulator matchSimulator;

    public MatchManager(FootballMatchSimulator matchSimulator) {
        if (matchSimulator == null)
            throw new IllegalArgumentException("MatchSimulator cannot be null");
        this.matchSimulator = matchSimulator;
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
    public void applyPostMatchEnergyLoss(Team team) {
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null");

        for (Player player : team.getStartingPlayers()) {
            applyEnergyLoss(player, MATCH_ENERGY_LOSS);
        }
        for (Player player : team.getSubstitutes()) {
            applyEnergyLoss(player, SUB_ENERGY_LOSS);
        }
    }
    //Check All Matches
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
}
