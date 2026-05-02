package ui.controller;

import application.GameFacade;
import domain.Match;
import domain.MatchResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ui.SceneManager;

public class PostMatchController {

    private final GameFacade facade;

    public PostMatchController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        VBox root = new VBox(22);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #1a1a2e;");

        // Capture everything before submitting (submit resets currentUserMatch)
        Match match = facade.getUserMatch();
        MatchResult result = facade.getCurrentPeriodResult();

        boolean isHome = match.getHomeTeam().equals(facade.getUserTeam());
        int userGoals     = isHome ? result.getHomeGoals() : result.getAwayGoals();
        int opponentGoals = isHome ? result.getAwayGoals() : result.getHomeGoals();

        String outcome;
        String outcomeColor;
        if (userGoals > opponentGoals) {
            outcome = "VICTORY";
            outcomeColor = "#2ecc71";
        } else if (userGoals == opponentGoals) {
            outcome = "DRAW";
            outcomeColor = "#f39c12";
        } else {
            outcome = "DEFEAT";
            outcomeColor = "#e74c3c";
        }

        // Submit week results — updates standings, advances week, resets match state
        facade.submitWeekResults(result);

        Label outcomeLabel = new Label(outcome);
        outcomeLabel.setStyle(
                "-fx-text-fill: " + outcomeColor +
                "; -fx-font-size: 36px; -fx-font-weight: bold;"
        );

        Label vsLabel = new Label(
                match.getHomeTeam().getName() + "  vs  " + match.getAwayTeam().getName()
        );
        vsLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 14px;");

        Label scoreLabel = new Label(result.getHomeGoals() + "  —  " + result.getAwayGoals());
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 44px; -fx-font-weight: bold;");

        Label positionLabel = new Label("League position: " + facade.getUserTeamPosition());
        positionLabel.setStyle("-fx-text-fill: #3498db; -fx-font-size: 14px;");

        Button continueBtn;
        if (facade.isSeasonComplete()) {
            continueBtn = new Button("Season Summary");
            continueBtn.setStyle(
                    "-fx-background-color: #9b59b6; -fx-text-fill: white;" +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 36;"
            );
            continueBtn.setOnAction(e -> SceneManager.getInstance().switchTo("season-end", facade));
        } else {
            continueBtn = new Button("Continue");
            continueBtn.setStyle(
                    "-fx-background-color: #3498db; -fx-text-fill: white;" +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 36;"
            );
            continueBtn.setOnAction(e -> SceneManager.getInstance().switchTo("dashboard", facade));
        }

        root.getChildren().addAll(outcomeLabel, vsLabel, scoreLabel, positionLabel, continueBtn);
        return root;
    }
}
