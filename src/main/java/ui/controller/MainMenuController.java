package ui.controller;

import application.GameFacade;
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

import java.util.List;
import java.util.stream.Collectors;

public class MainMenuController {

    private final GameFacade facade;

    public MainMenuController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0a0e1a;");

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(320);

        // Title block
        VBox titleBlock = new VBox(6);
        titleBlock.setAlignment(Pos.CENTER);
        titleBlock.setPadding(new Insets(0, 0, 32, 0));

        Label title = new Label("SPORTS MANAGER");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Pro Edition");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#22c55e"));

        titleBlock.getChildren().addAll(title, subtitle);

        // Buttons
        Button newGameBtn     = buildMenuButton("New Game",         "btn-primary");
        Button loadGameBtn    = buildMenuButton("Load Saved Game",  "btn-secondary");
        Button deleteSaveBtn  = buildMenuButton("Delete Save Slot", "btn-secondary");
        Button exitBtn        = buildMenuButton("Exit",             "btn-red");

        newGameBtn.setOnAction(e ->
                SceneManager.getInstance().switchTo("new-game", facade));
        loadGameBtn.setOnAction(e -> {
            List<String> slots = facade.getSaveSlotInfo();
            List<String> existing = slots.stream()
                    .filter(s -> !s.contains("Empty"))
                    .collect(Collectors.toList());
            if (existing.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "No saved games found.").showAndWait();
                return;
            }
            ChoiceDialog<String> dialog = new ChoiceDialog<>(existing.get(0), existing);
            dialog.setTitle("Load Game");
            dialog.setHeaderText("Choose a save to load");
            dialog.setContentText("Save:");
            dialog.showAndWait().ifPresent(chosen -> {
                int slotId = slots.indexOf(chosen) + 1;
                try {
                    facade.loadGame(slotId);
                    SceneManager.getInstance().switchTo("dashboard", facade);
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Load failed: " + ex.getMessage()).showAndWait();
                }
            });
        });
        deleteSaveBtn.setOnAction(e -> SaveSlotDialogs.promptDeleteSave(facade));
        exitBtn.setOnAction(e ->
                javafx.application.Platform.exit());

        // Version label at bottom
        Label version = new Label("v1.0  —  M3 Project");
        version.setFont(Font.font("Arial", 11));
        version.setTextFill(Color.web("#374151"));

        VBox versionBlock = new VBox(version);
        versionBlock.setAlignment(Pos.CENTER);
        versionBlock.setPadding(new Insets(40, 0, 0, 0));

        content.getChildren().addAll(titleBlock, newGameBtn, loadGameBtn, deleteSaveBtn, exitBtn, versionBlock);
        root.getChildren().add(content);
        return root;
    }

    private Button buildMenuButton(String text, String styleClass) {
        Button btn = new Button(text);
        btn.getStyleClass().addAll("btn", styleClass);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        SoundManager.getInstance().wire(btn);
        return btn;
    }
}