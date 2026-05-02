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
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0a0e1a;");

        VBox card = new VBox(0);
        card.setMaxWidth(420);
        card.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );

        // Header
        VBox header = new VBox(4);
        header.setPadding(new Insets(24, 28, 20, 28));
        header.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #052e16, #111827);" +
            "-fx-background-radius: 12 12 0 0;"
        );

        Label title = new Label("NEW GAME");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Set up your team and get started");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#6b7280"));

        header.getChildren().addAll(title, subtitle);

        // Form body
        VBox form = new VBox(16);
        form.setPadding(new Insets(24, 28, 28, 28));

        TextField managerField = new TextField();
        managerField.setPromptText("e.g. Alex Ferguson");
        managerField.getStyleClass().add("text-field");
        managerField.setMaxWidth(Double.MAX_VALUE);

        TextField teamField = new TextField();
        teamField.setPromptText("e.g. Manchester United");
        teamField.getStyleClass().add("text-field");
        teamField.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> sportBox = new ComboBox<>();
        sportBox.getItems().addAll("FOOTBALL", "HANDBALL");
        sportBox.setValue("FOOTBALL");
        sportBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Gender> genderBox = new ComboBox<>();
        genderBox.getItems().addAll(Gender.values());
        genderBox.setValue(Gender.MALE);
        genderBox.setMaxWidth(Double.MAX_VALUE);

        form.getChildren().addAll(
                formRow("Manager Name", managerField),
                formRow("Team Name",    teamField),
                formRow("Sport",        sportBox),
                formRow("League Gender", genderBox)
        );

        // Buttons
        HBox btnRow = new HBox(12);
        btnRow.setPadding(new Insets(4, 28, 24, 28));
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button backBtn = new Button("← Back");
        backBtn.getStyleClass().addAll("btn", "btn-secondary");
        backBtn.setOnAction(e ->
                SceneManager.getInstance().switchTo("main-menu", facade));

        Button startBtn = new Button("Start Game →");
        startBtn.getStyleClass().addAll("btn", "btn-primary");
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

        btnRow.getChildren().addAll(backBtn, startBtn);

        card.getChildren().addAll(header, form, btnRow);
        StackPane.setAlignment(card, Pos.CENTER);
        root.getChildren().add(card);
        return root;
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