package ui.controller;

import application.GameFacade;
import domain.StandingEntry;
import domain.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.SceneManager;

import java.util.List;

public class SeasonEndController {

    private final GameFacade facade;

    public SeasonEndController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #0a0e1a;");

        VBox body = new VBox(14);
        body.setPadding(new Insets(20, 28, 24, 28));
        body.getChildren().addAll(
                buildUserPositionCard(),
                buildStandingsCard(),
                buildButtons()
        );

        root.getChildren().addAll(buildChampionHeader(), body);
        return root;
    }

    private VBox buildChampionHeader() {
        Team champion = facade.getSeasonChampion();
        String championName = champion != null ? champion.getName().toUpperCase() : "UNKNOWN";

        VBox header = new VBox(6);
        header.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #1a1000, #0f0900, #0a0e1a);" +
                        "-fx-padding: 22 28 18 28;"
        );

        Label seasonLabel = new Label("SEASON " + facade.getCurrentSeasonNumber() + " COMPLETE");
        seasonLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        seasonLabel.setTextFill(Color.web("#6b7280"));

        Label trophyLabel = new Label("CHAMPION");
        trophyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        trophyLabel.setStyle(
                "-fx-background-color: #78350f; -fx-text-fill: #fbbf24;" +
                        "-fx-padding: 3 10; -fx-background-radius: 4;"
        );

        Label championLabel = new Label(championName);
        championLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        championLabel.setTextFill(Color.web("#fbbf24"));

        header.getChildren().addAll(seasonLabel, trophyLabel, championLabel);
        return header;
    }

    private HBox buildUserPositionCard() {
        Team userTeam = facade.getUserTeam();
        int pos = facade.getUserTeamPosition();

        HBox card = new HBox(16);
        card.setStyle(
                "-fx-background-color: #111827; -fx-background-radius: 12;" +
                        "-fx-border-color: #1f2937; -fx-border-radius: 12;" +
                        "-fx-border-width: 1; -fx-padding: 14 18;"
        );
        card.setAlignment(Pos.CENTER_LEFT);

        Label posLabel = new Label("#" + pos);
        posLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        posLabel.setTextFill(Color.web("#22c55e"));
        posLabel.setPrefWidth(60);

        VBox info = new VBox(3);
        Label teamLabel = new Label(userTeam.getName().toUpperCase());
        teamLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        teamLabel.setTextFill(Color.WHITE);

        Label subLabel = new Label("Final position this season");
        subLabel.setFont(Font.font("Arial", 12));
        subLabel.setTextFill(Color.web("#6b7280"));

        info.getChildren().addAll(teamLabel, subLabel);
        card.getChildren().addAll(posLabel, info);
        return card;
    }

    private VBox buildStandingsCard() {
        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color: #111827; -fx-background-radius: 12;" +
                        "-fx-border-color: #1f2937; -fx-border-radius: 12;" +
                        "-fx-border-width: 1; -fx-padding: 14 18;"
        );

        Label title = new Label("FINAL STANDINGS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        title.setTextFill(Color.web("#6b7280"));
        card.getChildren().add(title);

        Team userTeam = facade.getUserTeam();
        Team champion = facade.getSeasonChampion();
        List<StandingEntry> standings = facade.getStandings();

        VBox list = new VBox(3);

        Label header = new Label(String.format("  %-3s  %-22s  %3s  %3s  %3s  %3s  %4s",
                "POS", "TEAM", "PTS", "W", "D", "L", "GD"));
        header.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        header.setTextFill(Color.web("#4ade80"));
        list.getChildren().add(header);

        for (int i = 0; i < standings.size(); i++) {
            StandingEntry entry = standings.get(i);
            boolean isUser     = entry.getTeam().equals(userTeam);
            boolean isChampion = champion != null && entry.getTeam().equals(champion);

            Label row = new Label(String.format("  %-3d  %-22s  %3d  %3d  %3d  %3d  %4d",
                    i + 1,
                    entry.getTeam().getName(),
                    entry.getPoints(),
                    entry.getWins(),
                    entry.getDraws(),
                    entry.getLosses(),
                    entry.getGoalDifference()));
            row.setFont(Font.font("Courier New", 12));

            if (isChampion) {
                row.setTextFill(Color.web("#fbbf24"));
            } else if (isUser) {
                row.setTextFill(Color.web("#22c55e"));
            } else {
                row.setTextFill(Color.web("#e5e7eb"));
            }

            list.getChildren().add(row);
        }

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(200);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        card.getChildren().add(scroll);
        return card;
    }

    private HBox buildButtons() {
        Button newSeasonBtn = new Button("NEW SEASON  →");
        newSeasonBtn.getStyleClass().add("btn-primary");
        newSeasonBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        newSeasonBtn.setOnAction(e -> {
            facade.startNewSeason();
            SceneManager.getInstance().switchTo("dashboard", facade);
        });

        Button saveExitBtn = new Button("SAVE & EXIT");
        saveExitBtn.getStyleClass().add("btn-purple");
        saveExitBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        saveExitBtn.setOnAction(e -> SceneManager.getInstance().switchTo("save-load", facade));

        Button exitBtn = new Button("EXIT");
        exitBtn.getStyleClass().add("btn-red");
        exitBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        exitBtn.setOnAction(e -> javafx.application.Platform.exit());

        HBox box = new HBox(12, newSeasonBtn, saveExitBtn, exitBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}

