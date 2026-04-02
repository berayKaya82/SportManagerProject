package application;

import domain.*;
import sport.ISport;
import sport.ITactic;

import java.util.List;

public interface GameFacade {

    // --- Initialization ---
    // Starts a new game and initializes all core systems (league, team, managers)
    void startNewGame(String managerName, String teamName, Gender gender, ISport sport);

    // --- Profile & Team ---
    // Returns current manager profile
    ManagerProfile getManagerProfile();

    // Returns the user's team
    Team getUserTeam();
    // List all players in the system (for user selection)
    List<Player> getAvailablePlayers();

    // Generate a random new player and register them in the system.
    Player generatePlayer(Gender gender);

    // Check if the team is ready.
    boolean isUserTeamReady();

    // --- Season ---
    // Starts a new season (after previous one is completed)
    void startNewSeason();

    // Checks whether the current season is finished
    boolean isSeasonComplete();

    // Returns current season number
    int getCurrentSeasonNumber();

    // Returns the champion of the current season
    Team getSeasonChampion();

    // --- Weekly Cycle ---
    // Starts a new week (generates matches and prepares state)
    void startWeek();

    // Applies training to the user's team for the current week
    void applyTraining(TrainingIntensity intensity);
    void applyWeeklyRecovery();

    // Returns the user's match for the current week
    Match getUserMatch();

    // Simulates the user's match completely (first half + second half internally)
    MatchResult simulateUserMatch();

    // Plays a single period, returns cumulative result
    MatchResult playPeriod(int periodNumber);

    // Returns how many periods the current sport has
    int getNumberOfPeriods();

    // Returns the cumulative result after the last played period
    MatchResult getCurrentPeriodResult();

    // Submits all results of the week (user + AI matches) and advances the week
    void submitWeekResults(MatchResult userMatchResult);

    // Returns current week number
    int getCurrentWeekNumber();

    // Returns total number of weeks in a season
    int getTotalWeeks();

    // --- Standings ---
    // Returns sorted league standings
    List<StandingEntry> getStandings();

    // Returns the current position of the user's team in the standings
    int getUserTeamPosition();

    // --- Team Management ---
    // Adds a player to starting lineup
    void addPlayerToStarting(int playerId);

    // Removes a player from starting lineup
    void removePlayerFromStarting(int playerId);

    // Adds a player to substitutes bench
    void addPlayerToSubstitutes(int playerId);

    // Sets the team's tactic
    void setTactic(ITactic tactic);
    // Sets the team's coach
    void setCoach(Coach coach);
}