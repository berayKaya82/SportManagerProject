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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.SceneManager;

public class RosterController {

    private final GameFacade facade;

    public RosterController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0e1a;");
        root.setTop(buildTopBar());
        root.setCenter(buildColumns());
        root.setRight(buildRightPanel());
        return root;
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(16, 28, 16, 28));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label title = new Label(
            "SQUAD MANAGEMENT — " + facade.getUserTeam().getName().toUpperCase());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("← Dashboard");
        backBtn.getStyleClass().addAll("btn", "btn-secondary");
        backBtn.setOnAction(e -> SceneManager.getInstance().switchTo("dashboard", facade));

        bar.getChildren().addAll(title, spacer, backBtn);
        return bar;
    }

    // ── Player columns (Starting XI | Bench) ──────────────────────────────────

    private HBox buildColumns() {
        HBox columns = new HBox(16);
        columns.setPadding(new Insets(20));

        VBox startingCol = buildPlayerCard(
            "STARTING XI", facade.getUserTeam().getStartingPlayers());
        VBox benchCol = buildPlayerCard(
            "BENCH", facade.getUserTeam().getSubstitutes());

        HBox.setHgrow(startingCol, Priority.ALWAYS);
        HBox.setHgrow(benchCol,    Priority.ALWAYS);
        columns.getChildren().addAll(startingCol, benchCol);
        return columns;
    }

    private VBox buildPlayerCard(String header, java.util.List<Player> players) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );

        Label headerLabel = new Label(header + "  (" + players.size() + ")");
        headerLabel.getStyleClass().add("section-header-green");
        card.getChildren().add(headerLabel);

        for (Player p : players) {
            boolean injured = p.getInjuryStatus() == InjuryStatus.INJURED;

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(4, 0, 4, 0));

            Label name = new Label(p.getName());
            name.getStyleClass().add(injured ? "player-row-injured" : "player-row");
            name.setMinWidth(145);

            Label energyLbl = new Label("E:" + p.getEnergy());
            energyLbl.setFont(Font.font("Courier New", 12));
            energyLbl.setTextFill(energyColor(p.getEnergy()));
            energyLbl.setMinWidth(46);

            Label condLbl = new Label("C:" + p.getCondition());
            condLbl.setFont(Font.font("Courier New", 12));
            condLbl.setTextFill(Color.web("#6b7280"));

            row.getChildren().addAll(name, energyLbl, condLbl);

            if (injured) {
                Label injTag = new Label(" INJ");
                injTag.setFont(Font.font("Arial", FontWeight.BOLD, 11));
                injTag.setTextFill(Color.web("#ef4444"));
                row.getChildren().add(injTag);
            }

            card.getChildren().add(row);
        }
        return card;
    }

    // ── Right panel (Tactic + Substitution) ──────────────────────────────────

    private VBox buildRightPanel() {
        VBox panel = new VBox(14);
        panel.setPadding(new Insets(20));
        panel.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 0 0 0 1;"
        );
        panel.setPrefWidth(230);

        // ── Tactic ──
        Label tacticTitle = new Label("TACTIC");
        tacticTitle.getStyleClass().add("section-header-green");

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

        // ── Substitution ──
        Label swapTitle = new Label("SUBSTITUTION");
        swapTitle.getStyleClass().add("section-header-yellow");

        ComboBox<Player> outBox = new ComboBox<>();
        outBox.getItems().addAll(facade.getUserTeam().getStartingPlayers());
        outBox.setPromptText("Player Out (starter)");
        outBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Player> inBox = new ComboBox<>();
        inBox.getItems().addAll(facade.getUserTeam().getSubstitutes());
        inBox.setPromptText("Player In (bench)");
        inBox.setMaxWidth(Double.MAX_VALUE);

        Button swapBtn = new Button("Make Substitution");
        swapBtn.getStyleClass().addAll("btn", "btn-blue");
        swapBtn.setMaxWidth(Double.MAX_VALUE);
        swapBtn.setOnAction(e -> {
            Player out = outBox.getValue();
            Player in  = inBox.getValue();
            if (out == null || in == null) {
                new Alert(Alert.AlertType.WARNING, "Select both players first.").showAndWait();
                return;
            }
            try {
                facade.getUserTeam().substitutePlayer(out, in);
                // Reload roster so lists refresh
                SceneManager.getInstance().switchTo("roster", facade);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        panel.getChildren().addAll(
                tacticTitle, tacticBox,
                new Separator(),
                swapTitle, outBox, inBox, swapBtn
        );
        return panel;
    }

    private Color energyColor(int energy) {
        if (energy >= 70) return Color.web("#4ade80");
        if (energy >= 40) return Color.web("#fbbf24");
        return Color.web("#ef4444");
    }
}
