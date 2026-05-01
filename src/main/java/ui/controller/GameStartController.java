package ui.controller;

import application.GameFacade;
import domain.Gender;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import sport.ISport;
import ui.SceneManager;

public class GameStartController {

    private final GameFacade facade;

    public GameStartController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label title = new Label("New Game");

        // Team name
        Label teamLabel = new Label("Team Name:");
        TextField teamField = new TextField();
        teamField.setPromptText("Enter team name");

        // Manager name
        Label managerLabel = new Label("Manager Name:");
        TextField managerField = new TextField();
        managerField.setPromptText("Enter manager name");

        // Sport selection
        Label sportLabel = new Label("Sport:");
        ComboBox<String> sportBox = new ComboBox<>();
        sportBox.getItems().addAll("FOOTBALL", "HANDBALL");
        sportBox.setValue("FOOTBALL");

        // Gender selection
        Label genderLabel = new Label("Gender:");
        ComboBox<Gender> genderBox = new ComboBox<>();
        genderBox.getItems().addAll(Gender.values());
        genderBox.setValue(Gender.MALE);

        // Buttons
        Button startBtn = new Button("Start");
        startBtn.setOnAction(e -> {
            String teamName = teamField.getText().trim();
            String managerName = managerField.getText().trim();
            Gender gender = genderBox.getValue();

            if (teamName.isEmpty() || managerName.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please fill in all fields.").showAndWait();
                return;
            }

            ISport sport = new football.FootballSport();  // handball eklenince güncellenecek

            facade.startNewGame(managerName, teamName, gender, sport);
            SceneManager.getInstance().switchTo("dashboard", facade);
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e ->
                SceneManager.getInstance().switchTo("main-menu", facade));

        HBox buttons = new HBox(10, backBtn, startBtn);
        buttons.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
                title,
                teamLabel, teamField,
                managerLabel, managerField,
                sportLabel, sportBox,
                genderLabel, genderBox,
                buttons
        );

        return root;
    }
}