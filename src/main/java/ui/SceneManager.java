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
            case "vs-screen":
                root = new VsScreenController(facade).getRoot();
                break;
            case "pre-match":
                root = new PreMatchController(facade).getRoot();
                break;
            case "match":
                root = new MatchController(facade).getRoot();
                break;
            case "half-time":
                root = new HalfTimeController(facade).getRoot();
                break;
            case "post-match":
                root = new PostMatchController(facade).getRoot();
                break;
            case "season-end":
                root = new SeasonEndController(facade).getRoot();
                break;
            default:
                throw new IllegalArgumentException("Unknown screen: " + screenName);
        }

        if (stage.getScene() == null) {
            Scene scene = new Scene(root, 960, 660);
            scene.getStylesheets().add(
                    getClass().getResource("/style.css").toExternalForm()
            );
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
        }
        stage.show();
    }

    private Parent buildPlaceholder(String message, GameFacade facade) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));

        // Use CSS classes instead of inline styles
        root.getStyleClass().add("screen-root");

        Label label = new Label(message);
        label.getStyleClass().add("text-gray");

        boolean gameStarted = facade.getUserTeam() != null;
        Button back = new Button(gameStarted ? "Back to Dashboard" : "Back to Main Menu");
        back.getStyleClass().addAll("btn", "btn-primary");
        back.setOnAction(e -> switchTo(gameStarted ? "dashboard" : "main-menu", facade));

        root.getChildren().addAll(label, back);
        return root;
    }

}
