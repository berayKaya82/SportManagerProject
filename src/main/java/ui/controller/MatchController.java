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

public class MatchController {

    private final GameFacade facade;
    private VBox root;

    public MatchController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #1a1a2e;");

        showPrePlayState(detectCurrentPeriod());
        return root;
    }

    private int detectCurrentPeriod() {
        try {
            facade.getCurrentPeriodResult();
            return 2;
        } catch (IllegalStateException e) {
            return 1;
        }
    }

    private void showPrePlayState(int period) {
        Match match = facade.getUserMatch();

        Label periodLabel = new Label(period == 1 ? "FIRST HALF" : "SECOND HALF");
        periodLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label matchLabel = new Label(
                match.getHomeTeam().getName() + "  vs  " + match.getAwayTeam().getName()
        );
        matchLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        String scoreText = period == 1 ? "0  —  0" : formatScore(facade.getCurrentPeriodResult());
        Label scoreLabel = new Label(scoreText);
        scoreLabel.setStyle("-fx-text-fill: #3498db; -fx-font-size: 38px; -fx-font-weight: bold;");

        Button playBtn = new Button("Play " + (period == 1 ? "First" : "Second") + " Half");
        playBtn.setStyle(
                "-fx-background-color: #2ecc71; -fx-text-fill: white;" +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 36;"
        );
        playBtn.setOnAction(e -> {
            MatchResult result = facade.playPeriod(period);
            showPostPlayState(period, result);
        });

        root.getChildren().setAll(periodLabel, matchLabel, scoreLabel, playBtn);
    }

    private void showPostPlayState(int period, MatchResult result) {
        Match match = facade.getUserMatch();
        int totalPeriods = facade.getNumberOfPeriods();
        boolean isLastPeriod = period >= totalPeriods;

        Label periodLabel = new Label(isLastPeriod ? "FULL TIME" : "HALF TIME");
        periodLabel.setStyle(
                "-fx-text-fill: " + (isLastPeriod ? "#2ecc71" : "#f39c12") +
                "; -fx-font-size: 20px; -fx-font-weight: bold;"
        );

        Label matchLabel = new Label(
                match.getHomeTeam().getName() + "  vs  " + match.getAwayTeam().getName()
        );
        matchLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label scoreLabel = new Label(formatScore(result));
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 44px; -fx-font-weight: bold;");

        Label descLabel = new Label(isLastPeriod ? "Final score" : "Half-time score");
        descLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 13px;");

        Button continueBtn;
        if (isLastPeriod) {
            continueBtn = new Button("See Result");
            continueBtn.setStyle(
                    "-fx-background-color: #3498db; -fx-text-fill: white;" +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 36;"
            );
            continueBtn.setOnAction(e -> SceneManager.getInstance().switchTo("post-match", facade));
        } else {
            continueBtn = new Button("Half Time");
            continueBtn.setStyle(
                    "-fx-background-color: #f39c12; -fx-text-fill: white;" +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 36;"
            );
            continueBtn.setOnAction(e -> SceneManager.getInstance().switchTo("half-time", facade));
        }

        root.getChildren().setAll(periodLabel, matchLabel, scoreLabel, descLabel, continueBtn);
    }

    private String formatScore(MatchResult r) {
        return r.getHomeGoals() + "  —  " + r.getAwayGoals();
    }
}
