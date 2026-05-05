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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.SceneManager;
import ui.SoundManager;

public class TrainingController {

    private final GameFacade facade;
    private TrainingIntensity selectedIntensity = TrainingIntensity.MEDIUM;

    public TrainingController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0e1a;");
        root.setTop(buildHeader());
        root.setCenter(buildCenter());
        return root;
    }

    // ── Header ───────────────────────────────────────────────────────────────

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #052e16, #0a1a0f, #0a0e1a);" +
            "-fx-padding: 20 28 16 28;"
        );

        VBox titleBlock = new VBox(4);
        Label title = new Label("TRAINING");
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

        header.getChildren().addAll(titleBlock, spacer, backBtn);
        return header;
    }

    // ── Center ───────────────────────────────────────────────────────────────

    private VBox buildCenter() {
        VBox center = new VBox(18);
        center.setPadding(new Insets(24, 28, 28, 28));

        Label subtitle = new Label("Choose this week's training intensity");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#6b7280"));

        // Status label — updates live when user picks an intensity
        Label statusLabel = new Label("Balanced training — good gains with moderate fatigue");
        statusLabel.setFont(Font.font("Arial", 13));
        statusLabel.setTextFill(Color.web("#f97316"));

        // Intensity toggle buttons
        ToggleGroup group = new ToggleGroup();
        ToggleButton lightBtn  = intensityBtn("LIGHT",  "Low fatigue · small gains",         TrainingIntensity.LIGHT,  group);
        ToggleButton mediumBtn = intensityBtn("MEDIUM", "Balanced effort · good improvement", TrainingIntensity.MEDIUM, group);
        ToggleButton hardBtn   = intensityBtn("HARD",   "High gains · injury risk ↑",         TrainingIntensity.HARD,   group);
        mediumBtn.setSelected(true);

        group.selectedToggleProperty().addListener((obs, old, now) -> {
            if (now == lightBtn) {
                selectedIntensity = TrainingIntensity.LIGHT;
                statusLabel.setText("Light training — players stay fresh, small stat gains");
            } else if (now == mediumBtn) {
                selectedIntensity = TrainingIntensity.MEDIUM;
                statusLabel.setText("Balanced training — good gains with moderate fatigue");
            } else if (now == hardBtn) {
                selectedIntensity = TrainingIntensity.HARD;
                statusLabel.setText("Intense training — big gains but injury risk increases");
            }
        });

        HBox intensityRow = new HBox(12, lightBtn, mediumBtn, hardBtn);
        intensityRow.setAlignment(Pos.CENTER_LEFT);

        // Squad status card
        VBox squadCard = buildSquadCard();

        // Start week button
        Button doneBtn = new Button("Done — Start Week →");
        doneBtn.getStyleClass().addAll("btn", "btn-primary");
        SoundManager.getInstance().wire(doneBtn);
        doneBtn.setOnAction(e -> {
            facade.applyWeeklyRecovery();
            facade.applyTraining(selectedIntensity);
            facade.startWeek();
            SceneManager.getInstance().switchTo("vs-screen", facade);
        });

        center.getChildren().addAll(subtitle, intensityRow, statusLabel, squadCard, doneBtn);
        return center;
    }

    // ── Toggle button factory ─────────────────────────────────────────────────

    private ToggleButton intensityBtn(String label, String desc,
                                      TrainingIntensity intensity, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(label + "\n" + desc);
        btn.setToggleGroup(group);
        btn.setPrefSize(210, 68);
        btn.setWrapText(true);
        btn.setStyle(unselectedStyle());
        btn.selectedProperty().addListener((obs, old, selected) ->
                btn.setStyle(selected ? selectedStyle() : unselectedStyle()));
        return btn;
    }

    private String selectedStyle() {
        return "-fx-background-color: #052e16;" +
               "-fx-text-fill: #4ade80;" +
               "-fx-font-size: 12px;" +
               "-fx-text-alignment: center;" +
               "-fx-border-color: #22c55e;" +
               "-fx-border-width: 1;" +
               "-fx-border-radius: 8;" +
               "-fx-background-radius: 8;";
    }

    private String unselectedStyle() {
        return "-fx-background-color: #111827;" +
               "-fx-text-fill: #e5e7eb;" +
               "-fx-font-size: 12px;" +
               "-fx-text-alignment: center;" +
               "-fx-border-color: #1f2937;" +
               "-fx-border-width: 1;" +
               "-fx-border-radius: 8;" +
               "-fx-background-radius: 8;";
    }

    // ── Squad status card ─────────────────────────────────────────────────────

    private VBox buildSquadCard() {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );

        Label header = new Label("STARTING XI — CURRENT STATUS");
        header.getStyleClass().add("section-header-green");
        card.getChildren().add(header);

        for (Player p : facade.getUserTeam().getStartingPlayers()) {
            boolean injured = p.getInjuryStatus() == InjuryStatus.INJURED;

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(3, 0, 3, 0));

            Label name = new Label(p.getName());
            name.getStyleClass().add(injured ? "player-row-injured" : "player-row");
            name.setMinWidth(160);

            Label energyLbl = new Label("E " + p.getEnergy());
            energyLbl.setFont(Font.font("Courier New", 12));
            energyLbl.setTextFill(energyColor(p.getEnergy()));
            energyLbl.setMinWidth(52);

            Label condLbl = new Label("C " + p.getCondition());
            condLbl.setFont(Font.font("Courier New", 12));
            condLbl.setTextFill(Color.web("#6b7280"));

            row.getChildren().addAll(name, energyLbl, condLbl);

            if (injured) {
                Label injLbl = new Label("  INJURED");
                injLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
                injLbl.setTextFill(Color.web("#ef4444"));
                row.getChildren().add(injLbl);
            }

            card.getChildren().add(row);
        }
        return card;
    }

    private Color energyColor(int energy) {
        if (energy >= 70) return Color.web("#4ade80");
        if (energy >= 40) return Color.web("#fbbf24");
        return Color.web("#ef4444");
    }
}
