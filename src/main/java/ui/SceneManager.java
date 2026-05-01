package ui;

import application.GameFacade;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.controller.*;

public class SceneManager {

    private static SceneManager instance;
    private Stage stage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void switchTo(String screenName, GameFacade facade) {
        Parent root;

        switch (screenName) {
            case "main-menu":
                root = new MainMenuController(facade).getRoot();
                break;
            case "new-game":
                root = new GameStartController(facade).getRoot();
                break;
            case "dashboard":
                root = new DashboardController(facade).getRoot();
                break;
            case "training":
                root = new TrainingController(facade).getRoot();
                break;
            case "roster":
                root = new RosterController(facade).getRoot();
                break;
            case "standings":
                root = new StandingsController(facade).getRoot();
                break;
            case "pre-match":
                root = buildPlaceholder("Pre-Match", facade);
                break;
            case "match":
                root = buildPlaceholder("Match", facade);
                break;
            case "half-time":
                root = buildPlaceholder("Half-Time", facade);
                break;
            case "post-match":
                root = buildPlaceholder("Post-Match ", facade);
                break;
            case "season-end":
                root = buildPlaceholder("Season End ", facade);
                break;
            case "save-load":
                root = buildPlaceholder("Save / Load — coming soon", facade);
                break;
            default:
                throw new IllegalArgumentException("Unknown screen: " + screenName);
        }

        Scene scene = new Scene(root, 960, 660);

        //CSS
        scene.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.show();
    }

    private Parent buildPlaceholder(String message, GameFacade facade) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));

        // Use CSS classes instead of inline styles
        root.getStyleClass().add("screen-root");

        Label label = new Label(message);
        label.getStyleClass().add("text-gray");

        Button back = new Button("Back to Dashboard");
        back.getStyleClass().addAll("btn", "btn-primary");
        back.setOnAction(e -> switchTo("dashboard", facade));

        root.getChildren().addAll(label, back);
        return root;
    }

}
