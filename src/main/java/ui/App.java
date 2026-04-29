package ui;

import application.DefaultGameFacade;
import application.GameFacade;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private final GameFacade facade = new DefaultGameFacade();

    @Override
    public void start(Stage stage) {
        SceneManager.getInstance().setStage(stage);
        SceneManager.getInstance().switchTo("main-menu", facade);

        stage.setTitle("Sports Manager");
    }

    public static void main(String[] args) {
        launch(args);
    }
}