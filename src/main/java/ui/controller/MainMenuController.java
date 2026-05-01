package ui.controller;

import application.GameFacade;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import ui.SceneManager;

public class MainMenuController {

    private final GameFacade facade;

    public MainMenuController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label title = new Label("Sports Manager");

        Button newGameBtn = new Button("New Game");
        newGameBtn.setOnAction(e ->
                SceneManager.getInstance().switchTo("new-game", facade));

        Button loadGameBtn = new Button("Load Saved Game");
        loadGameBtn.setOnAction(e ->
                SceneManager.getInstance().switchTo("save-load", facade));

        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(e ->
                javafx.application.Platform.exit());

        root.getChildren().addAll(title, newGameBtn, loadGameBtn, exitBtn);
        return root;
    }
}