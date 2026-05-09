package ui.controller;

import application.GameFacade;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.SceneManager;
import ui.SoundManager;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class MainMenuController {

    private static final double LOGO_FIT_WIDTH = 380;   // px, target range 360–420

    private final GameFacade facade;

    public MainMenuController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0a0e1a;");

        // ─────────────────────────────────────────────────────────────
        // Two-column responsive layout: an HBox holds two equal-growing
        // VBox children. Each child centers its own content vertically
        // and horizontally within its half of the window. With both
        // children flagged Hgrow=ALWAYS, the split stays 50/50 on resize
        // without any width bindings.
        // ─────────────────────────────────────────────────────────────
        HBox columns = new HBox();
        columns.setAlignment(Pos.CENTER);

        VBox leftPanel  = buildLeftPanel();
        VBox rightPanel = buildRightPanel();

        HBox.setHgrow(leftPanel,  Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        columns.getChildren().addAll(leftPanel, rightPanel);
        root.getChildren().add(columns);
        return root;
    }

    // ───────────────────────── LEFT: logo ─────────────────────────
    private VBox buildLeftPanel() {
        VBox panel = new VBox();
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(Double.MAX_VALUE);
        panel.setPadding(new Insets(40));

        URL logoUrl = getClass().getResource("/images/sports-manager-logo.png");
        if (logoUrl != null) {
            ImageView logo = new ImageView(new Image(logoUrl.toExternalForm()));
            logo.setPreserveRatio(true);
            logo.setSmooth(true);
            logo.setFitWidth(LOGO_FIT_WIDTH);
            panel.getChildren().add(logo);
        } else {
            // Fallback if the PNG hasn't been dropped in yet — keeps the app
            // launchable until the asset is added to src/main/resources/images/.
            Label title = new Label("SPORTS MANAGER");
            title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
            title.setTextFill(Color.WHITE);

            Label subtitle = new Label("Pro Edition");
            subtitle.setFont(Font.font("Arial", 14));
            subtitle.setTextFill(Color.web("#22c55e"));

            VBox fallback = new VBox(6, title, subtitle);
            fallback.setAlignment(Pos.CENTER);
            panel.getChildren().add(fallback);
        }
        return panel;
    }

    // ─────────────── RIGHT: buttons + version ───────────────
    private VBox buildRightPanel() {
        VBox panel = new VBox();
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(Double.MAX_VALUE);
        panel.setPadding(new Insets(40));

        // Inner column caps button width so the buttons stay readable
        // instead of stretching across the full half on wide windows.
        VBox menuColumn = new VBox(12);
        menuColumn.setAlignment(Pos.CENTER);
        menuColumn.setMaxWidth(320);

        Button newGameBtn    = buildMenuButton("New Game",         "btn-primary");
        Button loadGameBtn   = buildMenuButton("Load Saved Game",  "btn-secondary");
        Button deleteSaveBtn = buildMenuButton("Delete Save Slot", "btn-secondary");
        Button exitBtn       = buildMenuButton("Exit",             "btn-red");

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
        exitBtn.setOnAction(e -> javafx.application.Platform.exit());

        Label version = new Label("v1.0  —  M3 Project");
        version.setFont(Font.font("Arial", 11));
        version.setTextFill(Color.web("#374151"));
        VBox versionBlock = new VBox(version);
        versionBlock.setAlignment(Pos.CENTER);
        versionBlock.setPadding(new Insets(40, 0, 0, 0));

        menuColumn.getChildren().addAll(
                newGameBtn, loadGameBtn, deleteSaveBtn, exitBtn, versionBlock);
        panel.getChildren().add(menuColumn);
        return panel;
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
