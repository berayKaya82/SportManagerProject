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
            "-fx-background-color: linear-gradient(to bottom, #1a0a2e, #0f0a1a, #0a0e1a);" +
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
        subtitle.setTextFill(Color.web("#a78bfa"));
        titleBlock.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("← Dashboard");
        backBtn.getStyleClass().addAll("btn", "btn-secondary");
        backBtn.setOnAction(e -> SceneManager.getInstance().switchTo("dashboard", facade));

        header.getChildren().addAll(titleBlock, spacer, backBtn);
        return header;
    }

    private VBox buildCenter() {
        VBox center = new VBox(20);
        center.setPadding(new Insets(24, 28, 28, 28));
        center.getChildren().addAll(buildCurrentCoachCard(), buildAvailableCoachesCard());
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
            levelLabel.setTextFill(Color.web("#a78bfa"));

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

    private VBox buildAvailableCoachesCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(cardStyle());

        Label sectionTitle = new Label("HIRE A NEW COACH");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        sectionTitle.setTextFill(Color.web("#6b7280"));
        card.getChildren().add(sectionTitle);

        List<Coach> allCoaches = facade.getAllCoaches();
        ManagerProfile profile = facade.getManagerProfile();
        Coach currentCoach = facade.getUserTeam().getCoach();

        for (Coach coach : allCoaches) {
            boolean available = coach.isAvailable(profile);
            boolean isCurrent = currentCoach != null && currentCoach.equals(coach);

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 14, 10, 14));
            row.setStyle(
                "-fx-background-color:" + (isCurrent ? "#1a2e1a" : "#111827") + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color:" + (isCurrent ? "#22c55e" : "#1f2937") + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;"
            );

            VBox info = new VBox(2);
            Label nameLabel = new Label(coach.getName() + "  " + levelStars(coach.getCoachLevel()));
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            nameLabel.setTextFill(available ? Color.WHITE : Color.web("#4b5563"));

            String reqText = "Season " + coach.getRequiredSeason()
                    + " · Reputation " + coach.getRequiredReputation();
            Label reqLabel = new Label(available ? "Level " + coach.getCoachLevel() + " Coach" : reqText);
            reqLabel.setFont(Font.font("Arial", 11));
            reqLabel.setTextFill(available ? Color.web("#9ca3af") : Color.web("#ef4444"));
            info.getChildren().addAll(nameLabel, reqLabel);

            HBox.setHgrow(info, Priority.ALWAYS);
            row.getChildren().add(info);

            if (isCurrent) {
                Label currentLabel = new Label("ACTIVE");
                currentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
                currentLabel.setTextFill(Color.web("#22c55e"));
                currentLabel.setStyle(
                    "-fx-background-color: #052e16;" +
                    "-fx-padding: 4 10;" +
                    "-fx-background-radius: 4;"
                );
                row.getChildren().add(currentLabel);
            } else if (available) {
                Button hireBtn = new Button("HIRE");
                hireBtn.getStyleClass().addAll("btn", "btn-primary");
                hireBtn.setStyle(
                    "-fx-background-color: #7c3aed;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 4 16;" +
                    "-fx-background-radius: 4;"
                );
                hireBtn.setOnAction(e -> {
                    facade.setCoach(coach);
                    SceneManager.getInstance().switchTo("coach", facade);
                });
                row.getChildren().add(hireBtn);
            } else {
                Label lockLabel = new Label("LOCKED");
                lockLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
                lockLabel.setTextFill(Color.web("#4b5563"));
                lockLabel.setStyle(
                    "-fx-background-color: #1f2937;" +
                    "-fx-padding: 4 10;" +
                    "-fx-background-radius: 4;"
                );
                row.getChildren().add(lockLabel);
            }

            card.getChildren().add(row);
        }

        return card;
    }

    private VBox statBox(String label, String value) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(8, 16, 8, 16));
        box.setStyle(
            "-fx-background-color: #0a0e1a;" +
            "-fx-background-radius: 6;"
        );

        Label valLabel = new Label(value);
        valLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        valLabel.setTextFill(Color.web("#a78bfa"));

        Label nameLabel = new Label(label);
        nameLabel.setFont(Font.font("Arial", 10));
        nameLabel.setTextFill(Color.web("#6b7280"));

        box.getChildren().addAll(valLabel, nameLabel);
        return box;
    }

    private String levelStars(int level) {
        return " " + "★".repeat(level) + "☆".repeat(5 - level);
    }

    private String cardStyle() {
        return "-fx-background-color: #111827;" +
               "-fx-background-radius: 10;" +
               "-fx-border-color: #1f2937;" +
               "-fx-border-width: 1;" +
               "-fx-border-radius: 10;";
    }
}
