package ui.controller;

import application.GameFacade;
import domain.Match;
import domain.StandingEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import ui.SceneManager;

public class DashboardController {

    private final GameFacade facade;

    public DashboardController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        // --- TOP BAR ---
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: #16213e;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(20);

        Label teamName = new Label(facade.getUserTeam().getName());
        teamName.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label weekInfo = new Label("Week " + facade.getCurrentWeekNumber() + " / " + facade.getTotalWeeks());
        weekInfo.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 14px;");

        Label position = new Label("Position: " + facade.getUserTeamPosition());
        position.setStyle("-fx-text-fill: #3498db; -fx-font-size: 14px;");

        topBar.getChildren().addAll(teamName, weekInfo, position);
        root.setTop(topBar);

        // --- CENTER ---
        VBox center = new VBox(20);
        center.setPadding(new Insets(30));

        // Match info card
        Match match = facade.getUserMatch();
        VBox matchCard = new VBox(8);
        matchCard.setPadding(new Insets(20));
        matchCard.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");

        Label matchTitle = new Label("This Week's Match");
        matchTitle.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");

        Label matchLabel = new Label(
                match.getHomeTeam().getName() + "  vs  " + match.getAwayTeam().getName()
        );
        matchLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        matchCard.getChildren().addAll(matchTitle, matchLabel);

        // Standings preview
        VBox standingsCard = new VBox(8);
        standingsCard.setPadding(new Insets(20));
        standingsCard.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");

        Label standingsTitle = new Label("Standings");
        standingsTitle.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");
        standingsCard.getChildren().add(standingsTitle);

        int rank = 1;
        for (StandingEntry entry : facade.getStandings()) {
            HBox row = new HBox(10);
            Label rankLabel = new Label(rank + ".");
            rankLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-min-width: 25;");
            Label nameLabel = new Label(entry.getTeam().getName());
            boolean isUser = entry.getTeam().getName().equals(facade.getUserTeam().getName());
            nameLabel.setStyle("-fx-text-fill: " + (isUser ? "#3498db" : "white") + "; -fx-font-weight: " + (isUser ? "bold" : "normal") + ";");
            Label ptsLabel = new Label(entry.getPoints() + " pts");
            ptsLabel.setStyle("-fx-text-fill: #aaaaaa;");
            row.getChildren().addAll(rankLabel, nameLabel, ptsLabel);
            standingsCard.getChildren().add(row);
            rank++;
            if (rank > 5) break; // show top 5 only
        }

        center.getChildren().addAll(matchCard, standingsCard);
        root.setCenter(center);

        // --- RIGHT PANEL (buttons) ---
        VBox rightPanel = new VBox(12);
        rightPanel.setPadding(new Insets(30, 20, 30, 20));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setStyle("-fx-background-color: #16213e;");
        rightPanel.setPrefWidth(180);

        Button trainingBtn = new Button("Training");
        Button rosterBtn = new Button("Squad");
        Button standingsBtn = new Button("Standings");
        Button saveBtn = new Button("Save Game");

        for (Button btn : new Button[]{trainingBtn, rosterBtn, standingsBtn, saveBtn}) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10;");
        }

        trainingBtn.setOnAction(e ->
                SceneManager.getInstance().switchTo("training", facade));
        rosterBtn.setOnAction(e ->
                SceneManager.getInstance().switchTo("roster", facade));
        standingsBtn.setOnAction(e ->
                SceneManager.getInstance().switchTo("standings", facade));
        saveBtn.setOnAction(e ->
                SceneManager.getInstance().switchTo("save-load", facade));

        rightPanel.getChildren().addAll(trainingBtn, rosterBtn, standingsBtn, saveBtn);
        root.setRight(rightPanel);

        return root;
    }
}