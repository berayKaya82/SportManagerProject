package ui;

import application.DefaultGameFacade;
import application.GameFacade;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private static final boolean DEBUG_SKIP_TO_WEEK = true;
    private static final int DEBUG_TARGET_WEEK = 33;

    private final GameFacade facade = new DefaultGameFacade();

    @Override
    public void start(Stage stage) {
        SceneManager.getInstance().setStage(stage);
        stage.setTitle("Sports Manager");

        if (DEBUG_SKIP_TO_WEEK) {
            DefaultGameFacade df = (DefaultGameFacade) facade;
            df.startNewGame("Debug", "TestFC", domain.Gender.MALE, new football.FootballSport());
            df.debugSimulateWeeks(DEBUG_TARGET_WEEK - 1);
            SceneManager.getInstance().switchTo("dashboard", facade);
        } else {
            SceneManager.getInstance().switchTo("main-menu", facade);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}