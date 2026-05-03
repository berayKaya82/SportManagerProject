package ui.controller;

import application.GameFacade;
import domain.Match;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import ui.SceneManager;

public class VsScreenController {

    private final GameFacade facade;

    public VsScreenController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        Match match = facade.getUserMatch();
        String home = match.getHomeTeam().getName().toUpperCase();
        String away = match.getAwayTeam().getName().toUpperCase();

        StackPane root = new StackPane();

        // ───────── BACKGROUND ─────────
        ImageView bg = new ImageView(
                new Image(getClass().getResource("/images/vs.jpg").toExternalForm())
        );
        bg.setPreserveRatio(false);
        bg.setSmooth(true);
        bg.fitWidthProperty().bind(root.widthProperty());
        bg.fitHeightProperty().bind(root.heightProperty());

        // ───────── WINGS ─────────
        StackPane leftWing  = createWing(home, true,  Color.web("#003366"));
        StackPane rightWing = createWing(away, false, Color.web("#8E1616"));
        leftWing.setOpacity(0);
        rightWing.setOpacity(0);

        // ───────── BUTTON ─────────
        Button kick = new Button("KICK OFF →");
        kick.getStyleClass().add("btn-primary");
        kick.setOpacity(0);
        kick.setOnAction(e -> SceneManager.getInstance().switchTo("pre-match", facade));
        StackPane.setAlignment(kick, Pos.BOTTOM_CENTER);
        StackPane.setMargin(kick, new Insets(0, 0, 50, 0));

        root.getChildren().addAll(bg, leftWing, rightWing, kick);

        // ───────── ANIMATION ─────────
        TranslateTransition leftMove = new TranslateTransition(Duration.millis(650), leftWing);
        leftMove.setToX(-280);
        leftMove.setInterpolator(Interpolator.SPLINE(0.1, 0.9, 0.2, 1));

        TranslateTransition rightMove = new TranslateTransition(Duration.millis(650), rightWing);
        rightMove.setToX(280);
        rightMove.setInterpolator(Interpolator.SPLINE(0.1, 0.9, 0.2, 1));

        FadeTransition leftFade  = new FadeTransition(Duration.millis(350), leftWing);
        leftFade.setToValue(1);
        FadeTransition rightFade = new FadeTransition(Duration.millis(350), rightWing);
        rightFade.setToValue(1);

        ParallelTransition wingsIn = new ParallelTransition(leftMove, rightMove, leftFade, rightFade);

        FadeTransition kickFade = new FadeTransition(Duration.millis(450), kick);
        kickFade.setToValue(1);

        SequentialTransition full = new SequentialTransition(wingsIn, kickFade);

        final boolean[] played = {false};
        root.layoutBoundsProperty().addListener((obs, oldVal, bounds) -> {
            if (!played[0] && bounds.getWidth() > 0) {
                played[0] = true;
                full.play();
            }
        });

        return root;
    }

    private StackPane createWing(String name, boolean isLeft, Color color) {
        StackPane wing = new StackPane();

        Polygon poly = new Polygon();
        double w = 300, h = 75, s = 28;
        if (isLeft)
            poly.getPoints().addAll(0.0, 0.0, w, s, w, h - s, 0.0, h);
        else
            poly.getPoints().addAll(0.0, s, w, 0.0, w, h, 0.0, h - s);

        poly.setFill(color);
        poly.setStroke(Color.WHITE);

        Label lbl = new Label(name);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lbl.setTextFill(Color.WHITE);

        wing.getChildren().addAll(poly, lbl);
        wing.setMaxSize(w, h);
        return wing;
    }
}
