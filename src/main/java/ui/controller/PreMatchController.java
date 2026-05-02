package ui.controller;

import application.GameFacade;
import domain.InjuryStatus;
import domain.Match;
import domain.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import ui.SceneManager;

public class PreMatchController {

    private final GameFacade facade;

    public PreMatchController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Match match = facade.getUserMatch();

        Label subtitle = new Label("Week " + facade.getCurrentWeekNumber() + " of " + facade.getTotalWeeks());
        subtitle.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 13px;");

        Label matchLabel = new Label(
                match.getHomeTeam().getName() + "  vs  " + match.getAwayTeam().getName()
        );
        matchLabel.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;");

        VBox squadBox = buildSquadStatus();

        Button rosterBtn = new Button("Manage Squad");
        rosterBtn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-padding: 8 20;");
        rosterBtn.setOnAction(e -> SceneManager.getInstance().switchTo("roster", facade));

        Button kickOffBtn = new Button("Kick Off!");
        kickOffBtn.setStyle(
                "-fx-background-color: #2ecc71; -fx-text-fill: white;" +
                "-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12 36;"
        );
        kickOffBtn.setOnAction(e -> SceneManager.getInstance().switchTo("match", facade));

        HBox btnRow = new HBox(12, rosterBtn, kickOffBtn);
        btnRow.setAlignment(Pos.CENTER);

        root.getChildren().addAll(subtitle, matchLabel, squadBox, btnRow);
        return root;
    }

    private VBox buildSquadStatus() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");
        box.setMaxWidth(560);

        Label header = new Label("Starting XI");
        header.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");
        box.getChildren().add(header);

        for (Player p : facade.getUserTeam().getStartingPlayers()) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);

            Label name = new Label(p.getName());
            name.setStyle("-fx-text-fill: white; -fx-min-width: 150;");

            Label energy = new Label("Energy: " + p.getEnergy());
            energy.setStyle("-fx-text-fill: " + energyColor(p.getEnergy()) + "; -fx-min-width: 90;");

            row.getChildren().addAll(name, energy);

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
