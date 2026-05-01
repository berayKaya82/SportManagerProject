package ui.controller;

import application.GameFacade;
import domain.InjuryStatus;
import domain.PlayStyle;
import domain.Player;
import sport.ITactic;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.SceneManager;

public class RosterController {

    private final GameFacade facade;

    public RosterController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        // Top bar
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: #16213e;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Squad Management — " + facade.getUserTeam().getName());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        topBar.getChildren().add(title);
        root.setTop(topBar);

        // Center: starting XI and bench side by side
        HBox columns = new HBox(20);
        columns.setPadding(new Insets(20));

        VBox startingCol = buildPlayerList("Starting XI", facade.getUserTeam().getStartingPlayers());
        VBox benchCol    = buildPlayerList("Bench",       facade.getUserTeam().getSubstitutes());

        HBox.setHgrow(startingCol, Priority.ALWAYS);
        HBox.setHgrow(benchCol,    Priority.ALWAYS);
        columns.getChildren().addAll(startingCol, benchCol);
        root.setCenter(columns);

        // Right panel: tactic + substitution + back
        VBox rightPanel = new VBox(16);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setStyle("-fx-background-color: #16213e;");
        rightPanel.setPrefWidth(220);

        // Tactic
        Label tacticLabel = new Label("Tactic");
        tacticLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");

        ComboBox<PlayStyle> tacticBox = new ComboBox<>();
        tacticBox.getItems().addAll(PlayStyle.values());
        PlayStyle current = facade.getUserTeam().getTactic() != null
                ? facade.getUserTeam().getTactic().getPlayStyle()
                : PlayStyle.BALANCED;
        tacticBox.setValue(current);
        tacticBox.setMaxWidth(Double.MAX_VALUE);
        tacticBox.setOnAction(e -> {
            PlayStyle selected = tacticBox.getValue();
            facade.setTactic(new ITactic() {
                @Override public PlayStyle getPlayStyle() { return selected; }
                @Override public ITactic getDefaultTactic() { return this; }
            });
        });

        // Substitution
        Label swapLabel = new Label("Substitution");
        swapLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");

        ComboBox<Player> outBox = new ComboBox<>();
        outBox.getItems().addAll(facade.getUserTeam().getStartingPlayers());
        outBox.setPromptText("Player Out (starter)");
        outBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Player> inBox = new ComboBox<>();
        inBox.getItems().addAll(facade.getUserTeam().getSubstitutes());
        inBox.setPromptText("Player In (bench)");
        inBox.setMaxWidth(Double.MAX_VALUE);

        Button swapBtn = new Button("Make Substitution");
        swapBtn.setMaxWidth(Double.MAX_VALUE);
        swapBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8;");
        swapBtn.setOnAction(e -> {
            Player out = outBox.getValue();
            Player in  = inBox.getValue();
            if (out == null || in == null) {
                new Alert(Alert.AlertType.WARNING, "Select both players first.").showAndWait();
                return;
            }
            try {
                facade.getUserTeam().substitutePlayer(out, in);
                SceneManager.getInstance().switchTo("roster", facade);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-padding: 8;");
        backBtn.setOnAction(e -> SceneManager.getInstance().switchTo("dashboard", facade));

        rightPanel.getChildren().addAll(
                tacticLabel, tacticBox,
                new Separator(),
                swapLabel, outBox, inBox, swapBtn,
                new Separator(),
                backBtn
        );
        root.setRight(rightPanel);

        return root;
    }

    private VBox buildPlayerList(String header, java.util.List<Player> players) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");

        Label headerLabel = new Label(header + " (" + players.size() + ")");
        headerLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");
        box.getChildren().add(headerLabel);

        for (Player p : players) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(3, 0, 3, 0));

            Label name = new Label(p.getName());
            name.setStyle("-fx-text-fill: white; -fx-min-width: 140;");

            Label energy = new Label("E:" + p.getEnergy());
            energy.setStyle("-fx-text-fill: " + energyColor(p.getEnergy()) + "; -fx-min-width: 45;");

            Label cond = new Label("C:" + p.getCondition());
            cond.setStyle("-fx-text-fill: #aaaaaa; -fx-min-width: 45;");

            row.getChildren().addAll(name, energy, cond);

            if (p.getInjuryStatus() == InjuryStatus.INJURED) {
                Label injLabel = new Label("INJ");
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
