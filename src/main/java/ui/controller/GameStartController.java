package ui.controller;

import application.GameFacade;
import domain.Gender;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import sport.ISport;
import ui.SceneManager;

public class GameStartController {

    private final GameFacade facade;

    public GameStartController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0e1a;");

        root.setTop(buildHeader());
        root.setCenter(buildForm());

        return root;
    }

    // ── Header bar (same pattern as Training / Roster) ───────────────────────

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(16, 28, 16, 28));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 0 0 1 0;"
        );

        VBox titleBlock = new VBox(2);
        Label title = new Label("NEW GAME");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Set up your team and get started");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#6b7280"));
        titleBlock.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("← Main Menu");
        backBtn.getStyleClass().addAll("btn", "btn-secondary");
        backBtn.setOnAction(e -> SceneManager.getInstance().switchTo("main-menu", facade));

        header.getChildren().addAll(titleBlock, spacer, backBtn);
        return header;
    }

    // ── Two-column form filling the whole center ─────────────────────────────

    private HBox buildForm() {
        // Fields
        TextField managerField = new TextField();
        managerField.setPromptText("e.g. Alex Ferguson");
        managerField.setMaxWidth(Double.MAX_VALUE);

        TextField teamField = new TextField();
        teamField.setPromptText("e.g. Manchester United");
        teamField.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> sportBox = new ComboBox<>();
        sportBox.getItems().addAll("FOOTBALL", "HANDBALL");
        sportBox.setValue("FOOTBALL");
        sportBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Gender> genderBox = new ComboBox<>();
        genderBox.getItems().addAll(Gender.values());
        genderBox.setValue(Gender.MALE);
        genderBox.setMaxWidth(Double.MAX_VALUE);

        // Left column — identity fields
        VBox leftCard = card();
        leftCard.getChildren().addAll(
                sectionLabel("MANAGER DETAILS"),
                formRow("Manager Name", managerField),
                formRow("Team Name",    teamField)
        );

        // Right column — match settings + start button
        Button startBtn = new Button("Start Game →");
        startBtn.getStyleClass().addAll("btn", "btn-primary");
        startBtn.setMaxWidth(Double.MAX_VALUE);
        startBtn.setOnAction(e -> {
            String managerName = managerField.getText().trim();
            String teamName    = teamField.getText().trim();
            Gender gender      = genderBox.getValue();

            if (managerName.isEmpty() || teamName.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please fill in all fields.").showAndWait();
                return;
            }

            ISport sport = sportBox.getValue().equals("HANDBALL")
                    ? new handball.HandballSport()
                    : new football.FootballSport();

            facade.startNewGame(managerName, teamName, gender, sport);
            SceneManager.getInstance().switchTo("dashboard", facade);
        });

        VBox rightCard = card();
        rightCard.getChildren().addAll(
                sectionLabel("MATCH SETTINGS"),
                formRow("Sport",         sportBox),
                formRow("League Gender", genderBox),
                new Region() {{ VBox.setVgrow(this, Priority.ALWAYS); }},
                startBtn
        );

        // Outer row — two cards side by side, full width
        HBox row = new HBox(20);
        row.setPadding(new Insets(28));
        HBox.setHgrow(leftCard,  Priority.ALWAYS);
        HBox.setHgrow(rightCard, Priority.ALWAYS);
        row.getChildren().addAll(leftCard, rightCard);
        return row;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private VBox card() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );
        return card;
    }

    private Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("section-header-green");
        lbl.setPadding(new Insets(0, 0, 4, 0));
        return lbl;
    }

    private VBox formRow(String labelText, Control field) {
        VBox row = new VBox(6);
        Label lbl = new Label(labelText.toUpperCase());
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web("#6b7280"));
        row.getChildren().addAll(lbl, field);
        return row;
    }
}
