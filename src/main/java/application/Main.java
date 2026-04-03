package application;

import domain.*;
import football.FootballSport;
import football.FootballTactic;
import sport.ISport;

import java.util.List;

public class Main {

    private static final String DIVIDER = "=".repeat(60);
    private static final String THIN_DIVIDER = "-".repeat(60);

    public static void main(String[] args) {

        GameFacade game = new DefaultGameFacade();
        ISport football = new FootballSport();

        // --- 1. Start a new game ---
        System.out.println(DIVIDER);
        System.out.println("       SPORTS MANAGER - SEASON SIMULATION");
        System.out.println(DIVIDER);

        game.startNewGame("Ahmet", "Eagles FC", Gender.MALE, football);

        ManagerProfile manager = game.getManagerProfile();
        Team userTeam = game.getUserTeam();

        System.out.println("Manager    : " + manager.getManagerName());
        System.out.println("Team       : " + userTeam.getName());
        System.out.println("Coach      : " + userTeam.getCoach());
        System.out.println("Tactic     : " + userTeam.getTactic().getPlayStyle());
        System.out.println("Season     : " + game.getCurrentSeasonNumber());
        System.out.println("Total Weeks: " + game.getTotalWeeks());
        System.out.println();

        // --- 2. Show initial roster ---
        printRoster(userTeam);

        // --- 3. Play the full season ---
        int totalWeeks = game.getTotalWeeks();

        for (int week = 1; week <= totalWeeks; week++) {

            System.out.println(DIVIDER);
            System.out.printf("  WEEK %d / %d%n", week, totalWeeks);
            System.out.println(DIVIDER);

            // a) Weekly energy recovery
            game.applyWeeklyRecovery();

            // b) Training — vary intensity across the season
            TrainingIntensity intensity = pickTrainingIntensity(week);
            game.applyTraining(intensity);
            System.out.println("Training: " + intensity);

            // c) Start the week (simulates AI matches, finds user match)
            game.startWeek();

            Match userMatch = game.getUserMatch();
            System.out.println("Match: " + userMatch.getHomeTeam().getName()
                    + " vs " + userMatch.getAwayTeam().getName());

            // d) Swap injured starters with healthy subs before kick-off
            for (int s = 0; s < userTeam.getStartingPlayers().size(); s++) {
                Player starter = userTeam.getStartingPlayers().get(s);
                if (starter.getInjuryStatus() == InjuryStatus.INJURED) {
                    Player healthySub = findHealthySub(userTeam);
                    if (healthySub != null) {
                        userTeam.substitutePlayer(starter, healthySub);
                        System.out.println("  [SWAP] " + starter.getName()
                                + " (INJURED) -> " + healthySub.getName() + " (from bench)");
                    }
                }
            }

            // Swap low-energy starters with higher-energy subs
            for (int s = 0; s < userTeam.getStartingPlayers().size(); s++) {
                Player starter = userTeam.getStartingPlayers().get(s);
                if (starter.getInjuryStatus() != InjuryStatus.INJURED && starter.getEnergy() < 30) {
                    Player bestSub = findBestEnergySub(userTeam, starter.getEnergy());
                    if (bestSub != null) {
                        userTeam.substitutePlayer(starter, bestSub);
                        System.out.println("  [SWAP] " + starter.getName()
                                + " (Energy:" + starter.getEnergy() + ") -> "
                                + bestSub.getName() + " (Energy:" + bestSub.getEnergy() + ")");
                    }
                }
            }

            long availableCount = userTeam.getStartingPlayers().stream()
                    .filter(Player::isAvailableForMatch).count();
            System.out.println("Available starters: " + availableCount
                    + " / " + userTeam.getStartingPlayers().size());

            // e) Play user match using period system
            MatchResult result;

            if (week <= 3) {
                // First 3 weeks: demonstrate period-by-period play
                System.out.println(THIN_DIVIDER);

                MatchResult halfTime = game.playPeriod(1);
                System.out.printf("  Half-time: %d - %d%n",
                        halfTime.getHomeGoals(), halfTime.getAwayGoals());

                // Week 2: demonstrate substitution at half-time
                if (week == 2 && userTeam.getSubstitutes().size() > 0) {
                    Player out = userTeam.getStartingPlayers().get(0);
                    Player in = userTeam.getSubstitutes().get(0);
                    userTeam.substitutePlayer(out, in);
                    System.out.println("  -> Substitution: " + out.getName()
                            + " OUT, " + in.getName() + " IN");
                }

                // Tactical change at half-time when losing
                if (halfTime.getGoalDifference() < 0) {
                    game.setTactic(new FootballTactic(PlayStyle.OFFENSIVE));
                    System.out.println("  -> Tactic changed to OFFENSIVE");
                }

                result = game.playPeriod(2);
                System.out.printf("  Full-time: %d - %d%n",
                        result.getHomeGoals(), result.getAwayGoals());

            } else {
                // Remaining weeks: simulate in one go
                result = game.simulateUserMatch();
                System.out.printf("  Result: %d - %d%n",
                        result.getHomeGoals(), result.getAwayGoals());
            }

            // e) Submit results and apply post-match effects
            game.submitWeekResults(result);

            System.out.println("Position: " + game.getUserTeamPosition()
                    + " / " + game.getStandings().size());

            // f) Log injured/recovered players (starters + subs)
            for (Player p : userTeam.getStartingPlayers()) {
                if (p.getInjuryStatus() == InjuryStatus.INJURED && p.getInjuredGamesRemaining() > 0) {
                    System.out.println("  [!] " + p.getName() + " (START) INJURED — "
                            + p.getInjuredGamesRemaining() + " game(s) remaining");
                }
            }
            for (Player p : userTeam.getSubstitutes()) {
                if (p.getInjuryStatus() == InjuryStatus.INJURED && p.getInjuredGamesRemaining() > 0) {
                    System.out.println("  [!] " + p.getName() + " (SUB) INJURED — "
                            + p.getInjuredGamesRemaining() + " game(s) remaining");
                }
            }

            // g) Mid-season: upgrade coach
            if (week == totalWeeks / 2) {
                Coach betterCoach = new Coach("Experienced Coach", 3, 1, 0);
                game.setCoach(betterCoach);
                System.out.println();
                System.out.println(">>> Coach upgraded to: " + betterCoach
                        + " (better training & match bonuses)");
            }

            // h) Show standings at key moments
            if (week == 1 || week == totalWeeks / 2 || week == totalWeeks) {
                System.out.println();
                printStandings(game.getStandings());
            }

            // i) Show roster status every 10 weeks
            if (week % 10 == 0) {
                System.out.println();
                printRoster(userTeam);
            }
        }

        // --- 4. Season complete ---
        System.out.println();
        System.out.println(DIVIDER);
        System.out.println("       SEASON COMPLETE");
        System.out.println(DIVIDER);

        Team champion = game.getSeasonChampion();
        System.out.println("Champion: " + champion.getName());
        System.out.println();

        if (champion.equals(userTeam)) {
            System.out.println("Congratulations! You won the league!");
        } else {
            System.out.println("Better luck next season.");
        }

        System.out.println();
        System.out.println("Final Standings:");
        printStandings(game.getStandings());

        System.out.println();
        System.out.println("Final Roster:");
        printRoster(userTeam);

        System.out.println(DIVIDER);
        System.out.println("Manager: " + game.getManagerProfile());
        System.out.println(DIVIDER);
    }

    private static TrainingIntensity pickTrainingIntensity(int week) {
        int mod = week % 3;
        if (mod == 1) return TrainingIntensity.HARD;
        if (mod == 2) return TrainingIntensity.MEDIUM;
        return TrainingIntensity.LIGHT;
    }

    private static void printStandings(List<StandingEntry> standings) {
        System.out.printf("%-4s %-22s %-4s %-4s %-4s %-4s %-4s %-4s%n",
                "Pos", "Team", "Pts", "P", "W", "D", "L", "GD");
        System.out.println(THIN_DIVIDER);
        for (int i = 0; i < standings.size(); i++) {
            StandingEntry e = standings.get(i);
            System.out.printf("%-4d %-22s %-4d %-4d %-4d %-4d %-4d %-4d%n",
                    i + 1,
                    e.getTeam().getName(),
                    e.getPoints(),
                    e.getPlayed(),
                    e.getWins(),
                    e.getDraws(),
                    e.getLosses(),
                    e.getGoalDifference());
        }
    }

    private static Player findHealthySub(Team team) {
        for (Player p : team.getSubstitutes()) {
            if (p.isAvailableForMatch()) return p;
        }
        return null;
    }

    private static Player findBestEnergySub(Team team, int currentStarterEnergy) {
        Player best = null;
        for (Player p : team.getSubstitutes()) {
            if (!p.isAvailableForMatch()) continue;
            if (p.getEnergy() <= currentStarterEnergy) continue;
            if (best == null || p.getEnergy() > best.getEnergy()) {
                best = p;
            }
        }
        return best;
    }

    private static void printRoster(Team team) {
        System.out.println("--- " + team.getName() + " Roster ---");
        System.out.printf("%-22s %-6s %-8s %-10s %-12s %-10s%n",
                "Name", "Role", "Energy", "Condition", "InjuryRisk", "Status");
        System.out.println(THIN_DIVIDER);
        for (Player p : team.getStartingPlayers()) {
            printPlayerRow(p, "START");
        }
        for (Player p : team.getSubstitutes()) {
            printPlayerRow(p, "SUB");
        }
        System.out.println();
    }

    private static void printPlayerRow(Player p, String role) {
        System.out.printf("%-22s %-6s %-8d %-10d %-12d %-10s%n",
                p.getName(),
                role,
                p.getEnergy(),
                p.getCondition(),
                p.getInjuryRisk(),
                p.getInjuryStatus());
    }
}
