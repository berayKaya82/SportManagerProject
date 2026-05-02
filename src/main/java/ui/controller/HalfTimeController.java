package ui.controller;

import application.GameFacade;
import domain.InjuryStatus;
import domain.Match;
import domain.MatchResult;
import domain.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.SceneManager;

public class HalfTimeController {

    private final GameFacade facade;

    public HalfTimeController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        root.setTop(buildScoreBox());
        root.setCenter(buildSquadStatus());
        root.setRight(buildSubPanel());

        return root;
    }

    private VBox buildScoreBox() {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(28, 25, 20, 25));
        box.setStyle("-fx-background-color: #16213e;");

        Label halfLabel = new Label("HALF TIME");
        halfLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 14px; -fx-font-weight: bold;");

        Match match = facade.getUserMatch();
        MatchResult result = facade.getCurrentPeriodResult();

        Label matchLabel = new Label(
                match.getHomeTeam().getName() + "  vs  " + match.getAwayTeam().getName()
        );
        matchLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label scoreLabel = new Label(result.getHomeGoals() + "  —  " + result.getAwayGoals());
        scoreLabel.setStyle("-fx-text-fill: #3498db; -fx-font-size: 36px; -fx-font-weight: bold;");

        box.getChildren().addAll(halfLabel, matchLabel, scoreLabel);
        return box;
    }

    private VBox buildSquadStatus() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(20));

        VBox card = new VBox(6);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");

        Label header = new Label("Squad — Half Time Status");
        header.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");
        card.getChildren().add(header);

        for (Player p : facade.getUserTeam().getStartingPlayers()) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);

            Label name = new Label(p.getName());
            name.setStyle("-fx-text-fill: white; -fx-min-width: 150;");

            Label energy = new Label("E:" + p.getEnergy());
            energy.setStyle("-fx-text-fill: " + energyColor(p.getEnergy()) + "; -fx-min-width: 55;");

            Label cond = new Label("C:" + p.getCondition());
            cond.setStyle("-fx-text-fill: #aaaaaa; -fx-min-width: 55;");

            row.getChildren().addAll(name, energy, cond);

            if (p.getInjuryStatus() == InjuryStatus.INJURED) {
                Label injLabel = new Label("INJ");
                injLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                row.getChildren().add(injLabel);
            }

            card.getChildren().add(row);
        }

        box.getChildren().add(card);
        return box;
    }

    private VBox buildSubPanel() {
        VBox panel = new VBox(14);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #16213e;");
        panel.setPrefWidth(220);

        Label subTitle = new Label("Substitution");
        subTitle.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");

        ComboBox<Player> outBox = new ComboBox<>();
        outBox.getItems().addAll(facade.getUserTeam().getStartingPlayers());
        outBox.setPromptText("Player Out");
        outBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Player> inBox = new ComboBox<>();
        inBox.getItems().addAll(facade.getUserTeam().getSubstitutes());
        inBox.setPromptText("Player In");
        inBox.setMaxWidth(Double.MAX_VALUE);

        Button swapBtn = new Button("Substitute");
        swapBtn.setMaxWidth(Double.MAX_VALUE);
        swapBtn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-padding: 8;");
        swapBtn.setOnAction(e -> {
            Player out = outBox.getValue();
            Player in  = inBox.getValue();
            if (out == null || in == null) {
                new Alert(Alert.AlertType.WARNING, "Select both players.").showAndWait();
                return;
            }
            try {
                facade.getUserTeam().substitutePlayer(out, in);
                SceneManager.getInstance().switchTo("half-time", facade);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        Button continueBtn = new Button("Start Second Half");
        continueBtn.setMaxWidth(Double.MAX_VALUE);
        continueBtn.setStyle(
                "-fx-background-color: #2ecc71; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10;"
        );
        continueBtn.setOnAction(e -> SceneManager.getInstance().switchTo("match", facade));

        panel.getChildren().addAll(
                subTitle, outBox, inBox, swapBtn,
                new Separator(),
                continueBtn
        );
        return panel;
    }

    private String energyColor(int energy) {
        if (energy >= 70) return "#2ecc71";
        if (energy >= 40) return "#f39c12";
        return "#e74c3c";
    }
}
