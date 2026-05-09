package ui;

import application.DefaultGameFacade;
import application.GameFacade;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    // TO START FROM WEEK 33 WITH MEDIUM TRAININGS AND BALANCED TACTICS - SET THAT TRUE
    private static final boolean DEBUG_SKIP_TO_WEEK = false;
    private static final int DEBUG_TARGET_WEEK = 33;

    private final GameFacade facade = new DefaultGameFacade();

    @Override
    public void start(Stage stage) {
        SceneManager.getInstance().setStage(stage);
        stage.setTitle("Sports Manager");

        if (DEBUG_SKIP_TO_WEEK) {
            DefaultGameFacade df = (DefaultGameFacade) facade;
            df.startNewGame("Debug", "TestFC", domain.Gender.MALE, "FOOTBALL");
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