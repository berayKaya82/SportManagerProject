package ui.controller;

import application.GameFacade;
import domain.Coach;
import domain.ManagerProfile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.SceneManager;
import ui.SoundManager;

import java.util.List;

public class CoachController {

    private final GameFacade facade;

    public CoachController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0e1a;");
        root.setTop(buildHeader());
        root.setCenter(buildCenter());
        return root;
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #052e16, #0a1a0f, #0a0e1a);" +
            "-fx-padding: 20 28 16 28;"
        );

        VBox titleBlock = new VBox(4);
        Label title = new Label("COACH MANAGEMENT");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);
        ManagerProfile profile = facade.getManagerProfile();
        Label subtitle = new Label("SEASON " + profile.getCurrentSeason()
                + "  ·  REPUTATION " + profile.getReputation());
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        subtitle.setTextFill(Color.web("#4ade80"));
        titleBlock.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("← Dashboard");
        backBtn.getStyleClass().addAll("btn", "btn-secondary");
        backBtn.setOnAction(e -> SceneManager.getInstance().switchTo("dashboard", facade));
        SoundManager.getInstance().wire(backBtn);

        header.getChildren().addAll(titleBlock, spacer, backBtn);
        return header;
    }

    private VBox buildCenter() {
        VBox center = new VBox(20);
        center.setPadding(new Insets(24, 28, 28, 28));
        center.getChildren().addAll(buildCurrentCoachCard(), buildCoachTableCard());
        return center;
    }

    private VBox buildCurrentCoachCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(cardStyle());

        Label sectionTitle = new Label("CURRENT COACH");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        sectionTitle.setTextFill(Color.web("#6b7280"));
        card.getChildren().add(sectionTitle);

        Coach coach = facade.getUserTeam().getCoach();
        if (coach == null) {
            Label noCoach = new Label("No coach assigned");
            noCoach.setFont(Font.font("Arial", 14));
            noCoach.setTextFill(Color.web("#ef4444"));
            card.getChildren().add(noCoach);
        } else {
            double relationship = facade.getUserTeam().getCoachRelationship();

            Label nameLabel = new Label(coach.getName());
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            nameLabel.setTextFill(Color.WHITE);

            Label levelLabel = new Label("Level " + coach.getCoachLevel() + levelStars(coach.getCoachLevel()));
            levelLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            levelLabel.setTextFill(Color.web("#4ade80"));

            HBox statsRow = new HBox(24);
            statsRow.setPadding(new Insets(8, 0, 0, 0));
            statsRow.getChildren().addAll(
                statBox("Training Boost",
                    String.format("%.0f%%", (coach.getTrainingMultiplier(relationship) - 1) * 100)),
                statBox("Energy Save",
                    String.format("%.0f%%", coach.getMatchEnergyReduction(relationship) * 100)),
                statBox("Match Bonus",
                    coach.getMatchBonus(relationship) > 0 ? "+1" : "0"),
                statBox("Relationship",
                    String.format("%.0f", relationship))
            );

            card.getChildren().addAll(nameLabel, levelLabel, statsRow);
        }

        return card;
    }

    private VBox buildCoachTableCard() {
        VBox card = new VBox(0);
        card.setStyle(cardStyle());

        Label sectionTitle = new Label("HIRE A NEW COACH");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        sectionTitle.setTextFill(Color.web("#6b7280"));
        sectionTitle.setPadding(new Insets(20, 18, 12, 18));

        VBox tableBox = new VBox(0);
        tableBox.getChildren().add(buildTableHeader());

        List<Coach> allCoaches = facade.getAllCoaches();
        ManagerProfile profile = facade.getManagerProfile();
        Coach currentCoach = facade.getUserTeam().getCoach();

        for (int i = 0; i < allCoaches.size(); i++) {
            Coach coach = allCoaches.get(i);
            boolean available = coach.isAvailable(profile);
            boolean isCurrent = currentCoach != null && currentCoach.equals(coach);
            boolean isEven = i % 2 == 0;
            tableBox.getChildren().add(buildTableRow(coach, available, isCurrent, isEven));
        }

        ScrollPane scroll = new ScrollPane(tableBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(220);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        card.getChildren().addAll(sectionTitle, scroll);
        return card;
    }

    private HBox buildTableHeader() {
        HBox row = new HBox();
        row.setPadding(new Insets(6, 18, 6, 18));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #1f2937; -fx-border-color: #374151; -fx-border-width: 0 0 1 0;");

        Label nameCol  = colLabel("COACH",       200);
        Label levelCol = colLabel("LEVEL",        80);
        Label reqCol   = colLabel("REQUIREMENT", 180);
        Label statCol  = colLabel("STATS",       160);
        Label actionCol = colLabel("",            80);

        row.getChildren().addAll(nameCol, levelCol, reqCol, statCol, actionCol);
        return row;
    }

    private HBox buildTableRow(Coach coach, boolean available, boolean isCurrent, boolean isEven) {
        HBox row = new HBox();
        row.setPadding(new Insets(10, 18, 10, 18));
        row.setAlignment(Pos.CENTER_LEFT);

        String bg = isCurrent ? "#0d2b0d" : (isEven ? "#111827" : "#0f1724");
        String border = isCurrent ? "#22c55e" : "transparent";
        row.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-border-color: " + border + ";" +
            "-fx-border-width: 0 0 0 3;"
        );

        // Coach name
        VBox nameBox = new VBox(2);
        nameBox.setMinWidth(200);
        Label nameLabel = new Label(coach.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameLabel.setTextFill(available ? Color.WHITE : Color.web("#4b5563"));
        nameBox.getChildren().add(nameLabel);

        // Level
        Label levelLabel = new Label(levelStars(coach.getCoachLevel()));
        levelLabel.setFont(Font.font("Arial", 13));
        levelLabel.setTextFill(available ? Color.web("#4ade80") : Color.web("#374151"));
        levelLabel.setMinWidth(80);

        // Requirement
        String reqText = available
                ? "Unlocked"
                : "S" + coach.getRequiredSeason() + "  ·  " + coach.getRequiredReputation() + " REP";
        Label reqLabel = new Label(reqText);
        reqLabel.setFont(Font.font("Arial", 12));
        reqLabel.setTextFill(available ? Color.web("#6b7280") : Color.web("#ef4444"));
        reqLabel.setMinWidth(180);

        // Stats
        double rel = facade.getUserTeam().getCoachRelationship();
        String statsText = available
                ? String.format("+%.0f%% trn  ·  -%.0f%% eng",
                    (coach.getTrainingMultiplier(rel) - 1) * 100,
                    coach.getMatchEnergyReduction(rel) * 100)
                : "—";
        Label statsLabel = new Label(statsText);
        statsLabel.setFont(Font.font("Courier New", 11));
        statsLabel.setTextFill(Color.web("#6b7280"));
        statsLabel.setMinWidth(160);

        // Action
        Region actionSpacer = new Region();
        HBox.setHgrow(actionSpacer, Priority.ALWAYS);

        if (isCurrent) {
            Label activeLabel = new Label("ACTIVE");
            activeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            activeLabel.setTextFill(Color.web("#22c55e"));
            activeLabel.setStyle(
                "-fx-background-color: #052e16;" +
                "-fx-padding: 4 10;" +
                "-fx-background-radius: 4;"
            );
            row.getChildren().addAll(nameBox, levelLabel, reqLabel, statsLabel, actionSpacer, activeLabel);
        } else if (available) {
            Button hireBtn = new Button("HIRE");
            SoundManager.getInstance().wire(hireBtn);
            hireBtn.setStyle(
                "-fx-background-color: #7c3aed;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 4 16;" +
                "-fx-background-radius: 4;" +
                "-fx-cursor: hand;"
            );
            hireBtn.setOnAction(e -> {
                facade.setCoach(coach);
                SceneManager.getInstance().switchTo("coach", facade);
            });
            row.getChildren().addAll(nameBox, levelLabel, reqLabel, statsLabel, actionSpacer, hireBtn);
        } else {
            Label lockLabel = new Label("LOCKED");
            lockLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            lockLabel.setTextFill(Color.web("#4b5563"));
            lockLabel.setStyle(
                "-fx-background-color: #1f2937;" +
                "-fx-padding: 4 10;" +
                "-fx-background-radius: 4;"
            );
            row.getChildren().addAll(nameBox, levelLabel, reqLabel, statsLabel, actionSpacer, lockLabel);
        }

        return row;
    }

    private Label colLabel(String text, double width) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        l.setTextFill(Color.web("#4ade80"));
        l.setMinWidth(width);
        return l;
    }

    private VBox statBox(String label, String value) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(8, 16, 8, 16));
        box.setStyle("-fx-background-color: #0a0e1a; -fx-background-radius: 6;");

        Label valLabel = new Label(value);
        valLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        valLabel.setTextFill(Color.web("#4ade80"));

        Label nameLabel = new Label(label);
        nameLabel.setFont(Font.font("Arial", 10));
        nameLabel.setTextFill(Color.web("#6b7280"));

        box.getChildren().addAll(valLabel, nameLabel);
        return box;
    }

    private String levelStars(int level) {
        return "★".repeat(level) + "☆".repeat(5 - level);
    }

    private String cardStyle() {
        return "-fx-background-color: #111827;" +
               "-fx-background-radius: 10;" +
               "-fx-border-color: #1f2937;" +
               "-fx-border-width: 1;" +
               "-fx-border-radius: 10;";
    }
}
