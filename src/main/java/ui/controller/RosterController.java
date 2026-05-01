package ui.controller;
import application.GameFacade;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RosterController {
    private final GameFacade facade;

        public RosterController(GameFacade facade) {
            this.facade = facade;
        }

        public Parent getRoot() {
            VBox root = new VBox(15);
            Label title = new Label("Dashboard");
            root.getChildren().add(title);
            return root;
        }
    }

