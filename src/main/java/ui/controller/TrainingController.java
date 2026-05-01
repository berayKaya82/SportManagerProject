package ui.controller;

import application.GameFacade;
import domain.InjuryStatus;
import domain.Player;
import domain.TrainingIntensity;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.SceneManager;

public class TrainingController {

    private final GameFacade facade;
    private TrainingIntensity selectedIntensity = TrainingIntensity.MEDIUM;

    public TrainingController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("Training");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Choose this week's training intensity");
        subtitle.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 14px;");

        VBox squadStatus = buildSquadStatus();

        ToggleGroup group = new ToggleGroup();
        ToggleButton lightBtn  = buildIntensityButton("LIGHT",  "Low fatigue, small gains",       TrainingIntensity.LIGHT,  group);
        ToggleButton mediumBtn = buildIntensityButton("MEDIUM", "Balanced effort and improvement", TrainingIntensity.MEDIUM, group);
        ToggleButton hardBtn   = buildIntensityButton("HARD",   "High gains, high fatigue risk",   TrainingIntensity.HARD,   group);
        mediumBtn.setSelected(true);

        group.selectedToggleProperty().addListener((obs, old, now) -> {
            if (now == lightBtn)       selectedIntensity = TrainingIntensity.LIGHT;
            else if (now == mediumBtn) selectedIntensity = TrainingIntensity.MEDIUM;
            else if (now == hardBtn)   selectedIntensity = TrainingIntensity.HARD;
        });

        HBox intensityRow = new HBox(12, lightBtn, mediumBtn, hardBtn);
        intensityRow.setAlignment(Pos.CENTER);

        Button doneBtn = new Button("Done — Start Week");
        doneBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 24;");
        doneBtn.setOnAction(e -> {
            facade.applyTraining(selectedIntensity);
            facade.startWeek();
            SceneManager.getInstance().switchTo("pre-match", facade);
        });

        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-padding: 8 20;");
        backBtn.setOnAction(e -> SceneManager.getInstance().switchTo("dashboard", facade));

        HBox btnRow = new HBox(12, backBtn, doneBtn);
        btnRow.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, subtitle, squadStatus, intensityRow, btnRow);
        return root;
    }

    private ToggleButton buildIntensityButton(String label, String desc, TrainingIntensity intensity, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(label + "\n" + desc);
        btn.setToggleGroup(group);
        btn.setPrefSize(200, 70);
        btn.setWrapText(true);
        btn.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; -fx-font-size: 12px; -fx-text-alignment: center;");
        btn.selectedProperty().addListener((obs, old, selected) ->
                btn.setStyle(selected
                        ? "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px;"
                        : "-fx-background-color: #16213e; -fx-text-fill: white; -fx-font-size: 12px;")
        );
        return btn;
    }

    private VBox buildSquadStatus() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");
        box.setMaxWidth(560);

        Label header = new Label("Starting XI — Current Status");
        header.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");
        box.getChildren().add(header);

        for (Player p : facade.getUserTeam().getStartingPlayers()) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);

            Label name = new Label(p.getName());
            name.setStyle("-fx-text-fill: white; -fx-min-width: 150;");

            Label energy = new Label("Energy: " + p.getEnergy());
            energy.setStyle("-fx-text-fill: " + energyColor(p.getEnergy()) + "; -fx-min-width: 90;");

            Label condition = new Label("Cond: " + p.getCondition());
            condition.setStyle("-fx-text-fill: #aaaaaa; -fx-min-width: 70;");

            row.getChildren().addAll(name, energy, condition);

            if (p.getInjuryStatus() == InjuryStatus.INJURED) {
                Label injLabel = new Label("INJURED");
                injLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                row.getChildren().add(injLabel);
            }

            box.getChildren().add(row);
        }
        return box;
    }

    private String energyColor(int energy) {
        if (energy >= 70) return "#2ecc71";
        if (energy >= 40) return "#f39c12";
        return "#e74c3c";
    }
}
