package ui.controller;

import application.GameFacade;
import domain.InjuryStatus;
import domain.Match;
import domain.MatchResult;
import domain.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.SceneManager;
import ui.SoundManager;

import java.util.List;

public class MatchController {

    private final GameFacade facade;
    private final Match match;

    public MatchController(GameFacade facade) {
        this.facade = facade;
        this.match  = facade.getUserMatch();
    }

    public Parent getRoot() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #0a0e1a;");

        VBox squadContent = buildSquadSection();
        ScrollPane squadScroll = new ScrollPane(squadContent);
        squadScroll.setFitToWidth(true);
        squadScroll.setPrefHeight(230);
        squadScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox body = new VBox(16);
        body.setPadding(new Insets(20, 28, 24, 28));
        body.getChildren().addAll(squadScroll, buildActionSection());

        root.getChildren().addAll(buildScoreHeader(), body);
        return root;
    }

    private VBox buildScoreHeader() {
        VBox header = new VBox(8);
        header.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #052e16, #081a0f, #0a0e1a);" +
                        "-fx-padding: 22 28 20 28;"
        );

        MatchResult r = facade.getCurrentPeriodResult();

        Label live = new Label("  LIVE  ");
        live.setStyle(
                "-fx-background-color: #ef4444; -fx-text-fill: white;" +
                        "-fx-font-size: 11px; -fx-font-weight: bold;" +
                        "-fx-padding: 3 10; -fx-background-radius: 4;"
        );

        Label period = new Label("HALF TIME");
        period.setFont(Font.font("Arial", 12));
        period.setTextFill(Color.web("#6b7280"));

        HBox topRow = new HBox(10, live, period);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label homeLabel = new Label(match.getHomeTeam().getName().toUpperCase());
        homeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        homeLabel.setTextFill(Color.WHITE);

        Label scoreLabel = new Label(r.getHomeGoals() + "  —  " + r.getAwayGoals());
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 46));
        scoreLabel.setTextFill(Color.web("#fbbf24"));
        scoreLabel.setPadding(new Insets(0, 24, 0, 24));

        Label awayLabel = new Label(match.getAwayTeam().getName().toUpperCase());
        awayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        awayLabel.setTextFill(Color.web("#9ca3af"));

        HBox scoreRow = new HBox(homeLabel, scoreLabel, awayLabel);
        scoreRow.setAlignment(Pos.CENTER_LEFT);

        header.getChildren().addAll(topRow, scoreRow);
        return header;
    }

    private VBox buildSquadSection() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(0, 0, 4, 0));

        Label title = new Label("SQUAD STATUS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        title.setTextFill(Color.web("#6b7280"));
        box.getChildren().add(title);

        addPlayerGroup(box, "STARTERS", facade.getUserTeam().getStartingPlayers(), "#4ade80");
        addPlayerGroup(box, "BENCH",    facade.getUserTeam().getSubstitutes(),     "#fbbf24");

        return box;
    }

    private void addPlayerGroup(VBox container, String header, List<Player> players, String headerColor) {
        Label headerLabel = new Label(header);
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        headerLabel.setTextFill(Color.web(headerColor));
        headerLabel.setPadding(new Insets(6, 0, 2, 0));
        container.getChildren().add(headerLabel);

        for (Player p : players) {
            container.getChildren().add(buildPlayerRow(p));
        }
    }

    private HBox buildPlayerRow(Player p) {
        boolean injured = p.getInjuryStatus() == InjuryStatus.INJURED;

        Label name = new Label(p.getName());
        name.setPrefWidth(160);
        name.setFont(Font.font("Arial", 13));
        name.setTextFill(injured ? Color.web("#ef4444") : Color.WHITE);

        Label eLabel = new Label("E");
        eLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        eLabel.setTextFill(Color.web("#4ade80"));

        ProgressBar energyBar = new ProgressBar(p.getEnergy() / 100.0);
        energyBar.setPrefWidth(75);
        energyBar.setPrefHeight(7);
        String eColor = p.getEnergy() > 60 ? "#22c55e" : (p.getEnergy() > 30 ? "#f97316" : "#ef4444");
        energyBar.setStyle("-fx-accent: " + eColor + ";");

        Label eVal = new Label(p.getEnergy() + "");
        eVal.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        eVal.setTextFill(Color.web(eColor));
        eVal.setPrefWidth(26);

        Label cLabel = new Label("C");
        cLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        cLabel.setTextFill(Color.web("#60a5fa"));

        ProgressBar condBar = new ProgressBar(p.getCondition() / 100.0);
        condBar.setPrefWidth(75);
        condBar.setPrefHeight(7);
        condBar.setStyle("-fx-accent: #3b82f6;");

        Label cVal = new Label(p.getCondition() + "");
        cVal.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        cVal.setTextFill(Color.web("#60a5fa"));
        cVal.setPrefWidth(26);

        HBox row = new HBox(8, name, eLabel, energyBar, eVal, cLabel, condBar, cVal);
        row.setAlignment(Pos.CENTER_LEFT);

        if (injured) {
            Label badge = new Label("INJURED");
            badge.setStyle(
                    "-fx-background-color: #ef4444; -fx-text-fill: white;" +
                            "-fx-font-size: 10px; -fx-font-weight: bold;" +
                            "-fx-padding: 2 6; -fx-background-radius: 4;"
            );
            row.getChildren().add(badge);
        }

        return row;
    }

    private VBox buildActionSection() {
        VBox section = new VBox(10);

        Button halftimeBtn = new Button("HALF TIME  →  SUBSTITUTION / TACTIC");
        halftimeBtn.setMaxWidth(Double.MAX_VALUE);
        halftimeBtn.getStyleClass().add("btn-orange");
        SoundManager.getInstance().wire(halftimeBtn);
        halftimeBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        halftimeBtn.setOnAction(e -> SceneManager.getInstance().switchTo("half-time", facade));

        Button skipBtn = new Button("▶  PLAY 2ND HALF DIRECTLY");
        skipBtn.setMaxWidth(Double.MAX_VALUE);
        skipBtn.getStyleClass().add("btn-primary");
        SoundManager.getInstance().wire(skipBtn);
        skipBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        skipBtn.setOnAction(e -> {
            facade.playPeriod(2);
            halftimeBtn.setDisable(true);
            skipBtn.setDisable(true);
            SceneManager.getInstance().switchTo("post-match", facade);
        });

        section.getChildren().addAll(halftimeBtn, skipBtn);
        return section;
    }
}

