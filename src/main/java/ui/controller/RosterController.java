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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.util.StringConverter;
import ui.SoundManager;

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
        bar.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #052e16, #0a1a0f, #0a0e1a);" +
            "-fx-padding: 20 28 16 28;"
        );

        VBox titleBlock = new VBox(4);
        Label title = new Label("SQUAD MANAGEMENT");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);
        Label subtitle = new Label(facade.getUserTeam().getName().toUpperCase());
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        subtitle.setTextFill(Color.web("#4ade80"));
        titleBlock.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("← Dashboard");
        backBtn.getStyleClass().addAll("btn", "btn-secondary");
        backBtn.setOnAction(e -> SceneManager.getInstance().switchTo("dashboard", facade));
        SoundManager.getInstance().wire(backBtn);

        bar.getChildren().addAll(titleBlock, spacer, backBtn);
        return bar;
    }

    // ── Player columns (Starting XI | Bench) ──────────────────────────────────

    private HBox buildColumns() {
        HBox columns = new HBox(16);
        columns.setPadding(new Insets(20));

        VBox startingCol = buildPlayerCard(
            true, facade.getUserTeam().getStartingPlayers());
        VBox benchCol = buildPlayerCard(
            false, facade.getUserTeam().getSubstitutes());

        HBox.setHgrow(startingCol, Priority.ALWAYS);
        HBox.setHgrow(benchCol,    Priority.ALWAYS);
        columns.getChildren().addAll(startingCol, benchCol);
        return columns;
    }

    private VBox buildPlayerCard(boolean isStartersColumn, List<Player> players) {
        String header = isStartersColumn ? "STARTING XI" : "BENCH";
        List<Player> sorted = sortedForColumn(players, isStartersColumn);

        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );

        Label headerLabel = new Label(header + "  (" + sorted.size() + ")");
        headerLabel.getStyleClass().add("section-header-green");
        card.getChildren().add(headerLabel);

        for (Player p : sorted) {
            boolean injured = p.getInjuryStatus() == InjuryStatus.INJURED;

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(4, 0, 4, 0));

            Label name = new Label(p.getName() + (injured ? "  ● INJ" : ""));
            name.setFont(Font.font("Arial", 13));
            name.setTextFill(injured ? Color.web("#f87171") : Color.WHITE);
            name.setMinWidth(155);

            Label energyLbl = new Label("E:" + p.getEnergy());
            energyLbl.setFont(Font.font("Courier New", 12));
            energyLbl.setTextFill(energyColor(p.getEnergy()));
            energyLbl.setMinWidth(46);

            Label condLbl = new Label("C:" + p.getCondition());
            condLbl.setFont(Font.font("Courier New", 12));
            condLbl.setTextFill(Color.web("#6b7280"));

            row.getChildren().addAll(name, energyLbl, condLbl);
            card.getChildren().add(row);
        }
        return card;
    }

    /** Starters: injured first. Bench: healthy first (same as half-time substitution). */
    private List<Player> sortedForColumn(List<Player> players, boolean startersColumn) {
        List<Player> copy = new ArrayList<>(players);
        if (startersColumn) {
            copy.sort(Comparator.comparing((Player p) -> p.getInjuryStatus() != InjuryStatus.INJURED)
                    .thenComparing(Player::getName, String.CASE_INSENSITIVE_ORDER));
        } else {
            copy.sort(Comparator.comparing((Player p) -> p.getInjuryStatus() == InjuryStatus.INJURED)
                    .thenComparing(Player::getName, String.CASE_INSENSITIVE_ORDER));
        }
        return copy;
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
            });
        });

        // ── Substitution ──
        Label swapTitle = new Label("SUBSTITUTION");
        swapTitle.getStyleClass().add("section-header-yellow");

        ComboBox<Player> outBox = new ComboBox<>();
        outBox.getItems().setAll(sortedStartersForSubstitutionOut());
        outBox.setPromptText("Player Out (starter)");
        outBox.setMaxWidth(Double.MAX_VALUE);
        applyPlayerComboConverter(outBox);
        styleInjuryAwareCombo(outBox);

        ComboBox<Player> inBox = new ComboBox<>();
        inBox.getItems().setAll(sortedSubsForSubstitutionIn());
        inBox.setPromptText("Player In (bench)");
        inBox.setMaxWidth(Double.MAX_VALUE);
        applyPlayerComboConverter(inBox);
        styleInjuryAwareCombo(inBox);

        Button swapBtn = new Button("Make Substitution");
        swapBtn.getStyleClass().addAll("btn", "btn-blue");
        swapBtn.setMaxWidth(Double.MAX_VALUE);
        SoundManager.getInstance().wire(swapBtn);
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

    private List<Player> sortedStartersForSubstitutionOut() {
        List<Player> list = new ArrayList<>(facade.getUserTeam().getStartingPlayers());
        list.sort(Comparator.comparing((Player p) -> p.getInjuryStatus() != InjuryStatus.INJURED)
                .thenComparing(Player::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    private List<Player> sortedSubsForSubstitutionIn() {
        List<Player> list = new ArrayList<>(facade.getUserTeam().getSubstitutes());
        list.sort(Comparator.comparing((Player p) -> p.getInjuryStatus() == InjuryStatus.INJURED)
                .thenComparing(Player::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    private void applyPlayerComboConverter(ComboBox<Player> combo) {
        combo.setConverter(new StringConverter<>() {
            @Override public String toString(Player p) {
                if (p == null) return "";
                boolean inj = p.getInjuryStatus() == InjuryStatus.INJURED;
                return p.getName() + (inj ? "  ● INJ" : "");
            }
            @Override public Player fromString(String s) { return null; }
        });
    }

    private void styleInjuryAwareCombo(ComboBox<Player> combo) {
        javafx.util.Callback<ListView<Player>, ListCell<Player>> factory =
                lv -> new ListCell<>() {
                    @Override protected void updateItem(Player p, boolean empty) {
                        super.updateItem(p, empty);
                        if (empty || p == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            boolean inj = p.getInjuryStatus() == InjuryStatus.INJURED;
                            setText(p.getName() + (inj ? "  ● INJ" : ""));
                            setTextFill(inj ? Color.web("#f87171") : Color.WHITE);
                        }
                    }
                };
        combo.setCellFactory(factory);
        combo.setButtonCell(factory.call(null));
    }
}
