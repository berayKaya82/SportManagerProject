package application;

import domain.*;
import sport.ISport;
import sport.ITactic;

import java.util.*;

public class DefaultGameFacade implements GameFacade {

    private ManagerProfile managerProfile;
    private ISport sport;
    private Team userTeam;
    private Match currentUserMatch;
    private MatchResult currentPeriodResult;
    private Map<Match, MatchResult> pendingAiResults;

    // Core managers responsible for different subsystems
    private final PlayerManager playerManager;
    private final PlayerGenerator playerGenerator;
    private final TeamGenerator teamGenerator;
    private final LeagueManager leagueManager;
    private final TrainingManager trainingManager;

    // Managers initialized after game setup
    private MatchManager matchManager;
    private TeamManager teamManager;
    private SeasonCycleManager seasonCycleManager;

    public DefaultGameFacade() {
        Random random = new Random();

        // Initialize base systems required before game starts
        this.playerManager = new PlayerManager();
        this.playerGenerator = new PlayerGenerator(random, playerManager);
        this.teamGenerator = new TeamGenerator(playerGenerator, random);
        this.leagueManager = new LeagueManager(teamGenerator,playerGenerator);
        this.trainingManager = new TrainingManager();

        // MatchManager depends on sport → initialized in startNewGame()
    }

    @Override
    public void startNewGame(String managerName, String teamName, Gender gender, ISport sport) {
        // Create manager profile and store selected sport
        this.managerProfile = new ManagerProfile(managerName, 0, 1);
        this.sport = sport;

        // Create league including user team and AI teams
        League league = leagueManager.createLeagueWithUserTeam(teamName, gender, sport);

        // Assign user team and initialize team manager
        this.userTeam = league.getTeams().get(0);
        this.teamManager = new TeamManager(userTeam, playerManager);

        // Initialize match manager using sport's abstract simulator
        this.matchManager = new MatchManager(sport.getMatchSimulator(), new Random());

        // Initialize season cycle manager and start first season
        this.seasonCycleManager = new SeasonCycleManager(
                league.getName(), sport, gender, league.getTeams()
        );
        seasonCycleManager.startNewSeason();
    }

    @Override
    public void startWeek() {
        ensureGameStarted();

        // Start current week in season
        Season season = seasonCycleManager.getCurrentSeason();
        season.startCurrentWeek();

        MatchWeek week = season.getCurrentWeek();

        // Simulate all AI matches and store results
        this.pendingAiResults = matchManager.simulateAIMatches(week, userTeam);

        // Retrieve the user's match for this week
        this.currentUserMatch = matchManager.findUserMatch(week, userTeam)
                .orElseThrow(() -> new IllegalStateException("No user match found this week"));
    }

    @Override
    public Match getUserMatch() {
        ensureUserMatchExists();
        return currentUserMatch;
    }

    @Override
    public MatchResult simulateUserMatch() {
        ensureUserMatchExists();
        MatchResult result = null;
        int periods = matchManager.getNumberOfPeriods();
        for (int i = 1; i <= periods; i++) {
            result = matchManager.playUserPeriod(currentUserMatch, i, result);
        }
        return result;
    }

    @Override
    public MatchResult playPeriod(int periodNumber) {
        ensureUserMatchExists();
        this.currentPeriodResult = matchManager.playUserPeriod(
                currentUserMatch, periodNumber, currentPeriodResult);
        return currentPeriodResult;
    }

    @Override
    public int getNumberOfPeriods() {
        return matchManager.getNumberOfPeriods();
    }

    @Override
    public MatchResult getCurrentPeriodResult() {
        if (currentPeriodResult == null)
            throw new IllegalStateException("No period played yet.");
        return currentPeriodResult;
    }

    @Override
    public void submitWeekResults(MatchResult userMatchResult) {
        ensureUserMatchExists();

        if (userMatchResult == null)
            throw new IllegalArgumentException("Result cannot be null");

        // Merge AI results with user result
        Map<Match, MatchResult> allResults = new LinkedHashMap<>(pendingAiResults);
        allResults.put(currentUserMatch, userMatchResult);

        // Update season state (standings + week progression)
        Season season = seasonCycleManager.getCurrentSeason();
        season.recordWeekResults(allResults);
        season.advanceWeek();

        // Apply post-match effects (energy loss, injuries, recovery)
        matchManager.applyPostMatchEffects(userTeam);

        // Reset temporary state for next week
        this.currentUserMatch = null;
        this.currentPeriodResult = null;
        this.pendingAiResults = null;
    }

    @Override
    public void applyTraining(TrainingIntensity intensity) {
        ensureGameStarted();

        // Apply training effects to user team
        trainingManager.applyTraining(userTeam, intensity);
    }

    @Override
    public void applyWeeklyRecovery() {
        ensureGameStarted();
        trainingManager.applyWeeklyRecovery(userTeam);
    }

    @Override
    public List<StandingEntry> getStandings() {
        // Retrieve current league standings
        return seasonCycleManager.getCurrentStandings();
    }

    @Override
    public int getUserTeamPosition() {
        // Retrieve user's current ranking
        return seasonCycleManager.getTeamPosition(userTeam);
    }

    @Override
    public void startNewSeason() {
        // Advance to next season and update manager profile
        seasonCycleManager.advanceToNextSeason();
        managerProfile.advanceSeason();
    }

    @Override
    public boolean isSeasonComplete() {
        return seasonCycleManager.isSeasonComplete();
    }

    @Override
    public int getCurrentSeasonNumber() {
        return seasonCycleManager.getSeasonNumber();
    }

    @Override
    public Team getSeasonChampion() {
        // Retrieve champion of the current season
        return seasonCycleManager.getChampionOfSeason(seasonCycleManager.getSeasonNumber());
    }

    @Override
    public int getCurrentWeekNumber() {
        return seasonCycleManager.getCurrentWeekNumber();
    }

    @Override
    public int getTotalWeeks() {
        return seasonCycleManager.getTotalWeeks();
    }

    @Override
    public ManagerProfile getManagerProfile() {
        return managerProfile;
    }

    @Override
    public Team getUserTeam() {
        return userTeam;
    }

    @Override
    public void addPlayerToStarting(int playerId) {
        teamManager.addPlayerToStarting(playerId);
    }

    @Override
    public void removePlayerFromStarting(int playerId) {
        teamManager.removePlayerFromStarting(playerId);
    }

    @Override
    public void addPlayerToSubstitutes(int playerId) {
        teamManager.addPlayerToSubstitutes(playerId);
    }

    @Override
    public List<Player> getAvailablePlayers() {
        return playerManager.getAllPlayers();
    }

    @Override
    public Player generatePlayer(Gender gender) {
        return playerGenerator.generatePlayerByGender(gender);
    }

    @Override
    public boolean isUserTeamReady() {
        return sport.getRosterRule().isValidRoster(userTeam);
    }

    @Override
    public void setTactic(ITactic tactic) {
        teamManager.setTactic(tactic);
    }

    @Override
    public void setCoach(Coach coach) {
        teamManager.setCoach(coach);
    }

    // --- Guards ---

    // Ensures game is initialized before performing operations
    private void ensureGameStarted() {
        if (userTeam == null)
            throw new IllegalStateException("Game not started. Call startNewGame() first.");
    }

    // Ensures a user match is available in the current week
    private void ensureUserMatchExists() {
        if (currentUserMatch == null)
            throw new IllegalStateException("No active user match. Call startWeek() first.");
    }
}
