package application;

import domain.*;
import football.FootballSport;
import handball.HandballSport;
import sport.ISport;
import sport.ITactic;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private LeagueManager leagueManager;
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
    public Match getUpcomingMatch() {
        ensureGameStarted();
        if (seasonCycleManager.isSeasonComplete()) return null;
        Season season = seasonCycleManager.getCurrentSeason();
        MatchWeek week = season.getFixture().getCurrentWeek();
        return week.getMatchesForTeam(userTeam).stream()
                .filter(m -> !m.isFinished())
                .findFirst().orElse(null);
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

        // Apply post-match effects (condition, energy loss, injuries, recovery)
        matchManager.applyPostMatchEffects(userTeam, currentUserMatch, userMatchResult);

        // Award reputation based on match result
        applyReputationGain(userMatchResult);

        // Update coach-team relationship based on match result
        applyRelationshipChange(userMatchResult);

        // Reset temporary state for next week
        this.currentUserMatch = null;
        this.currentPeriodResult = null;
        this.pendingAiResults = null;
    }

    /**
     * Debug/test only — simulates N weeks automatically with MEDIUM training.
     * Not exposed in GameFacade interface, not used in UI.
     */
    public void debugSimulateWeeks(int count) {
        ensureGameStarted();
        for (int i = 0; i < count; i++) {
            if (isSeasonComplete()) break;
            trainingManager.applyWeeklyRecovery(userTeam);
            trainingManager.applyTraining(userTeam, TrainingIntensity.MEDIUM);
            startWeek();
            MatchResult result = simulateUserMatch();
            submitWeekResults(result);
        }
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
        seasonCycleManager.advanceToNextSeason();
        managerProfile.advanceSeason();
        applyOffSeasonRecovery();
    }

    private void applyOffSeasonRecovery() {
        for (Player p : userTeam.getStartingPlayers()) {
            p.setEnergy(100);
            p.setCondition(Math.min(100, p.getCondition() + 30));
            p.clearInjury();
        }
        for (Player p : userTeam.getSubstitutes()) {
            p.setEnergy(100);
            p.setCondition(Math.min(100, p.getCondition() + 30));
            p.clearInjury();
        }
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
        userTeam.setCoachRelationship(50);
    }

    @Override
    public List<Coach> getAvailableCoaches() {
        return getCoachPool().stream()
                .filter(c -> c.isAvailable(managerProfile))
                .toList();
    }

    @Override
    public List<Coach> getAllCoaches() {
        return getCoachPool();
    }

    private List<Coach> getCoachPool() {
        return List.of(
            new Coach("Ali Yilmaz",      1, 1, 0),
            new Coach("Mehmet Demir",     2, 1, 35),
            new Coach("Ayse Kara",        3, 2, 75),
            new Coach("Fatma Celik",      4, 3, 130),
            new Coach("Kemal Ozturk",     5, 4, 220)
        );
    }

    // --- Save / Load ---

    @Override
    public void saveGame(int slotId) {
        ensureGameStarted();
        GameState state = buildGameState();
        SaveLoadManager saveLoadManager = new SaveLoadManager();
        try {
            saveLoadManager.saveGame(state, slotId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save game: " + e.getMessage(), e);
        }
    }

    @Override
    public void loadGame(int slotId) {
        SaveLoadManager saveLoadManager = new SaveLoadManager();
        GameState state;
        try {
            state = saveLoadManager.loadGame(slotId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load game: " + e.getMessage(), e);
        }
        restoreFromGameState(state);
    }

    @Override
    public List<String> getSaveSlotInfo() {
        return new SaveLoadManager().getSaveSlotInfo();
    }

    private GameState buildGameState() {
        GameState state = new GameState();

        state.setManagerName(managerProfile.getManagerName());
        state.setReputation(managerProfile.getReputation());
        state.setSeasonNumber(managerProfile.getCurrentSeason());

        state.setSportName(sport.getSportName());
        state.setGenderName(userTeam.getGender().name());
        state.setTeamName(userTeam.getName());
        state.setCoachRelationship(userTeam.getCoachRelationship());

        if (userTeam.getCoach() != null) {
            Coach c = userTeam.getCoach();
            state.setCoachName(c.getName());
            state.setCoachLevel(c.getCoachLevel());
            state.setCoachRequiredSeason(c.getRequiredSeason());
            state.setCoachRequiredReputation(c.getRequiredReputation());
        }

        if (userTeam.getTactic() != null) {
            state.setTacticStyle(userTeam.getTactic().getPlayStyle().name());
        }

        state.setStarters(toPlayerDataList(userTeam.getStartingPlayers()));
        state.setSubstitutes(toPlayerDataList(userTeam.getSubstitutes()));

        List<String> aiNames = new ArrayList<>();
        List<Team> allTeams = seasonCycleManager.getCurrentSeason().getLeague().getTeams();
        for (Team t : allTeams) {
            if (!t.getName().equals(userTeam.getName())) {
                aiNames.add(t.getName());
            }
        }
        state.setAiTeamNames(aiNames);

        state.setCurrentWeek(seasonCycleManager.getCurrentSeason().getFixture().getCurrentWeekNumber());
        state.setTotalWeeks(seasonCycleManager.getTotalWeeks());

        List<GameState.StandingData> standingDataList = new ArrayList<>();
        for (StandingEntry entry : seasonCycleManager.getCurrentStandings()) {
            GameState.StandingData sd = new GameState.StandingData();
            sd.teamName = entry.getTeam().getName();
            sd.points = entry.getPoints();
            sd.played = entry.getPlayed();
            sd.won = entry.getWins();
            sd.drawn = entry.getDraws();
            sd.lost = entry.getLosses();
            sd.goalsFor = entry.getGoalsFor();
            sd.goalsAgainst = entry.getGoalsAgainst();
            standingDataList.add(sd);
        }
        state.setStandings(standingDataList);

        state.setSeasonComplete(seasonCycleManager.isSeasonComplete());

        state.setSaveDate(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        return state;
    }

    private void restoreFromGameState(GameState state) {
        this.managerProfile = new ManagerProfile(
                state.getManagerName(), state.getReputation(), state.getSeasonNumber());

        ISport loadedSport = "Handball".equalsIgnoreCase(state.getSportName())
                ? new HandballSport() : new FootballSport();
        this.sport = loadedSport;

        Gender gender = Gender.valueOf(state.getGenderName());

        this.leagueManager = new LeagueManager(teamGenerator, playerGenerator);
        League league;
        if (state.getAiTeamNames() != null && state.getAiTeamNames().size() == loadedSport.getTeamCount() - 1) {
            league = leagueManager.createLeagueWithNamedTeams(
                    state.getTeamName(), gender, loadedSport, state.getAiTeamNames());
        } else {
            league = leagueManager.createLeagueWithUserTeam(
                    state.getTeamName(), gender, loadedSport);
        }

        this.userTeam = league.getTeams().get(0);
        this.teamManager = new TeamManager(userTeam, playerManager);
        this.matchManager = new MatchManager(loadedSport.getMatchSimulator(), new Random());
        this.seasonCycleManager = new SeasonCycleManager(
                league.getName(), loadedSport, gender, league.getTeams());
        seasonCycleManager.startNewSeason();

        restoreStandings(state.getStandings(), league.getTeams());

        if (state.isSeasonComplete()) {
            // Sezon bitmişken kaydedilmiş: 1. sezonu tamamlanmış olarak işaretle,
            // hemen 2. sezonu başlat. Kullanıcı Dashboard'da Season 2 Week 1'den devam eder.
            Team champion = seasonCycleManager.getCurrentSeason()
                    .getLeagueTable().getSortedStandings().get(0).getTeam();
            seasonCycleManager.forceCompleteAndAdvance(champion);
            managerProfile.advanceSeason();
        } else {
            restoreWeekNumber(state.getCurrentWeek());
        }

        restorePlayers(userTeam, state.getStarters(), state.getSubstitutes());

        if (state.getCoachName() != null) {
            userTeam.setCoach(new Coach(state.getCoachName(), state.getCoachLevel(),
                    state.getCoachRequiredSeason(), state.getCoachRequiredReputation()));
        }
        userTeam.setCoachRelationship(state.getCoachRelationship());

        if (state.getTacticStyle() != null) {
            PlayStyle style = PlayStyle.valueOf(state.getTacticStyle());
            userTeam.setTactic(() -> style);
        }
    }

    private void restoreWeekNumber(int savedWeek) {
        Season season = seasonCycleManager.getCurrentSeason();
        Fixture fixture = season.getFixture();
        if (savedWeek >= 1 && savedWeek <= fixture.getTotalWeeks()) {
            fixture.setCurrentWeekNumber(savedWeek);
        }
    }

    private void restoreStandings(List<GameState.StandingData> standingsData, List<Team> teams) {
        if (standingsData == null || standingsData.isEmpty()) return;

        LeagueTable table = seasonCycleManager.getCurrentSeason().getLeagueTable();
        for (GameState.StandingData sd : standingsData) {
            Team team = teams.stream()
                    .filter(t -> t.getName().equals(sd.teamName))
                    .findFirst().orElse(null);
            if (team == null) continue;

            StandingEntry entry = table.getEntry(team);
            entry.setPlayed(sd.played);
            entry.setWins(sd.won);
            entry.setDraws(sd.drawn);
            entry.setLosses(sd.lost);
            entry.setGoalsFor(sd.goalsFor);
            entry.setGoalsAgainst(sd.goalsAgainst);
        }
    }

    private void restorePlayers(Team team, List<GameState.PlayerData> starterData,
                                List<GameState.PlayerData> subData) {
        List<Player> currentStarters = new ArrayList<>(team.getStartingPlayers());
        List<Player> currentSubs = new ArrayList<>(team.getSubstitutes());

        for (int i = 0; i < Math.min(starterData.size(), currentStarters.size()); i++) {
            applyPlayerData(currentStarters.get(i), starterData.get(i));
        }
        for (int i = 0; i < Math.min(subData.size(), currentSubs.size()); i++) {
            applyPlayerData(currentSubs.get(i), subData.get(i));
        }
    }

    private void applyPlayerData(Player player, GameState.PlayerData data) {
        player.setName(data.name);
        player.setAge(data.age);
        player.setEnergy(Math.max(0, Math.min(100, data.energy)));
        player.setCondition(Math.max(0, Math.min(100, data.condition)));
        player.setInjuryRisk(Math.max(0, Math.min(100, data.injuryRisk)));
        player.setInjuryStatus(InjuryStatus.valueOf(data.injuryStatus));
        if (data.injuredGamesRemaining > 0) {
            player.injure(data.injuredGamesRemaining);
        }
    }

    private List<GameState.PlayerData> toPlayerDataList(List<Player> players) {
        List<GameState.PlayerData> list = new ArrayList<>();
        for (Player p : players) {
            GameState.PlayerData pd = new GameState.PlayerData();
            pd.id = p.getId();
            pd.name = p.getName();
            pd.age = p.getAge();
            pd.genderName = p.getGender().name();
            pd.energy = p.getEnergy();
            pd.condition = p.getCondition();
            pd.injuryRisk = p.getInjuryRisk();
            pd.injuryStatus = p.getInjuryStatus().name();
            pd.injuredGamesRemaining = p.getInjuredGamesRemaining();
            list.add(pd);
        }
        return list;
    }

    private void applyReputationGain(MatchResult result) {
        boolean isHome = currentUserMatch.getHomeTeam().equals(userTeam);
        int teamGoals = isHome ? result.getHomeGoals() : result.getAwayGoals();
        int opponentGoals = isHome ? result.getAwayGoals() : result.getHomeGoals();

        if (teamGoals > opponentGoals) {
            managerProfile.addReputation(4);
        } else if (teamGoals == opponentGoals) {
            managerProfile.addReputation(1);
        }
        // No rep for loss — keeps coach unlock pace reasonable over a season
    }

    private void applyRelationshipChange(MatchResult result) {
        if (userTeam.getCoach() == null) return;

        boolean isHome = currentUserMatch.getHomeTeam().equals(userTeam);
        int teamGoals = isHome ? result.getHomeGoals() : result.getAwayGoals();
        int opponentGoals = isHome ? result.getAwayGoals() : result.getHomeGoals();

        double current = userTeam.getCoachRelationship();
        if (teamGoals > opponentGoals) {
            current += 5;
        } else if (teamGoals == opponentGoals) {
            current += 2;
        } else {
            current -= 3;
        }
        userTeam.setCoachRelationship(Math.max(0, Math.min(100, current)));
    }

    // --- Guards ---

    private void ensureGameStarted() {
        if (userTeam == null)
            throw new IllegalStateException("Game not started. Call startNewGame() first.");
    }

    private void ensureUserMatchExists() {
        if (currentUserMatch == null)
            throw new IllegalStateException("No active user match. Call startWeek() first.");
    }
}
