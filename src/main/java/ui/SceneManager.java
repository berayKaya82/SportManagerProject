package ui;
import application.GameFacade;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.controller.*;


public class SceneManager {

    private static SceneManager instance;

    private Stage stage;

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
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

            default:
                throw new IllegalArgumentException("Unknown screen: " + screenName);
        }

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }
}
