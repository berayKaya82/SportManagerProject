package ui.controller;

import application.GameFacade;
import domain.StandingEntry;
import domain.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import ui.SceneManager;

public class SeasonEndController {

    private final GameFacade facade;

    public SeasonEndController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label seasonLabel = new Label("SEASON " + facade.getCurrentSeasonNumber() + " COMPLETE");
        seasonLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 14px; -fx-font-weight: bold;");

        Team champion = facade.getSeasonChampion();
        boolean userIsChampion = champion != null && champion.equals(facade.getUserTeam());

        Label champSubtitle = new Label("Champion");
        champSubtitle.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 13px;");

        Label champName = new Label(champion != null ? champion.getName() : "Unknown");
        champName.setStyle(
                "-fx-text-fill: " + (userIsChampion ? "#f1c40f" : "white") +
                "; -fx-font-size: 28px; -fx-font-weight: bold;"
        );

        root.getChildren().addAll(seasonLabel, champSubtitle, champName);

        if (userIsChampion) {
            Label congrats = new Label("You are the champion!");
            congrats.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 16px;");
            root.getChildren().add(congrats);
        } else {
            Label finishLabel = new Label(
                    "Your finish: " + facade.getUserTeamPosition() +
                    (facade.getUserTeamPosition() == 1 ? "st" :
                     facade.getUserTeamPosition() == 2 ? "nd" :
                     facade.getUserTeamPosition() == 3 ? "rd" : "th")
            );
            finishLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 14px;");
            root.getChildren().add(finishLabel);
        }

        root.getChildren().add(buildStandings());

        int nextSeason = facade.getCurrentSeasonNumber() + 1;
        Button nextBtn = new Button("Start Season " + nextSeason);
        nextBtn.setStyle(
                "-fx-background-color: #3498db; -fx-text-fill: white;" +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 36;"
        );
        nextBtn.setOnAction(e -> {
            facade.startNewSeason();
            SceneManager.getInstance().switchTo("dashboard", facade);
        });

        root.getChildren().add(nextBtn);
        return root;
    }

    private VBox buildStandings() {
        VBox box = new VBox(4);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");
        box.setMaxWidth(460);

        Label header = new Label("Final Standings");
        header.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");
        box.getChildren().add(header);

        int rank = 1;
        for (StandingEntry entry : facade.getStandings()) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            boolean isUser = entry.getTeam().equals(facade.getUserTeam());

            Label rankLabel = new Label(rank + ".");
            rankLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-min-width: 25;");

            Label nameLabel = new Label(entry.getTeam().getName());
            nameLabel.setStyle(
                    "-fx-text-fill: " + (isUser ? "#3498db" : "white") +
                    "; -fx-font-weight: " + (isUser ? "bold" : "normal") + ";"
            );
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            Label ptsLabel = new Label(entry.getPoints() + " pts");
            ptsLabel.setStyle("-fx-text-fill: #aaaaaa;");

            row.getChildren().addAll(rankLabel, nameLabel, ptsLabel);
            box.getChildren().add(row);
            rank++;
        }
        return box;
    }
}
