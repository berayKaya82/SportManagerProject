package ui.controller;

import application.GameFacade;
import domain.Match;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
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
        bg.fitWidthProperty().bind(root.widthProperty());
        bg.fitHeightProperty().bind(root.heightProperty());

        // ───────── CENTER GLOW ─────────
        Region glow = new Region();
        glow.setPrefWidth(120);
        glow.setMaxHeight(Double.MAX_VALUE);

        glow.setBackground(new Background(new BackgroundFill(
                new LinearGradient(
                        0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.TRANSPARENT),
                        new Stop(0.5, Color.web("#ffffff22")),
                        new Stop(1, Color.TRANSPARENT)
                ), null, null
        )));

        glow.setEffect(new GaussianBlur(25));
        glow.setOpacity(0.6);

        // ───────── TEAM TEXT ─────────
        Text homeText = new Text(home);
        homeText.setFill(Color.WHITE);
        homeText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        homeText.setTextAlignment(TextAlignment.RIGHT);

        Text awayText = new Text(away);
        awayText.setFill(Color.WHITE);
        awayText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        awayText.setTextAlignment(TextAlignment.LEFT);

        // Limit each side to 40% of screen width, leaving room for the VS graphic in the center
        homeText.wrappingWidthProperty().bind(root.widthProperty().multiply(0.40));
        awayText.wrappingWidthProperty().bind(root.widthProperty().multiply(0.40));

        // Home box: align content to the right edge
        StackPane homeBox = new StackPane(homeText);
        homeBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(homeBox, Priority.ALWAYS);

        // Away box: align content to the left edge
        StackPane awayBox = new StackPane(awayText);
        awayBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(awayBox, Priority.ALWAYS);

        // Center gap matches the width of the VS graphic
        HBox row = new HBox(homeBox, awayBox);
        row.setAlignment(Pos.CENTER);
        row.setSpacing(120);
        row.setMouseTransparent(true);

        // ───────── BUTTON ─────────
        Button kick = new Button("KICK OFF →");
        kick.getStyleClass().addAll("btn-primary");

        kick.setOnAction(e ->
                SceneManager.getInstance().switchTo("pre-match", facade)
        );

        StackPane.setAlignment(kick, Pos.BOTTOM_CENTER);
        StackPane.setMargin(kick, new javafx.geometry.Insets(0,0,50,0));

        root.getChildren().addAll(bg, glow, row, kick);

        // ───────── RESPONSIVE FONT ─────────
        root.widthProperty().addListener((obs, oldV, newV) -> {
            double size = Math.min(42, newV.doubleValue() / 22);
            homeText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, size));
            awayText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, size));
        });

        // ───────── ANIMATION ─────────
        root.layoutBoundsProperty().addListener((obs, oldVal, bounds) -> {

            double width = bounds.getWidth();
            double startOffset = width * 0.15; // proportional to screen width

            homeBox.setTranslateX(startOffset);
            awayBox.setTranslateX(-startOffset);

            homeBox.setOpacity(0);
            awayBox.setOpacity(0);

            TranslateTransition moveHome = new TranslateTransition(Duration.millis(700), homeBox);
            moveHome.setFromX(startOffset);
            moveHome.setToX(0);

            TranslateTransition moveAway = new TranslateTransition(Duration.millis(700), awayBox);
            moveAway.setFromX(-startOffset);
            moveAway.setToX(0);

            moveHome.setInterpolator(Interpolator.SPLINE(0.2, 0.8, 0.2, 1));
            moveAway.setInterpolator(Interpolator.SPLINE(0.2, 0.8, 0.2, 1));

            FadeTransition fadeL = new FadeTransition(Duration.millis(500), homeBox);
            fadeL.setFromValue(0);
            fadeL.setToValue(1);

            FadeTransition fadeR = new FadeTransition(Duration.millis(500), awayBox);
            fadeR.setFromValue(0);
            fadeR.setToValue(1);

            // light sweep
            TranslateTransition light = new TranslateTransition(Duration.millis(600), glow);
            light.setFromX(-60);
            light.setToX(60);
            light.setAutoReverse(true);
            light.setCycleCount(2);

            new ParallelTransition(
                    moveHome, moveAway,
                    fadeL, fadeR,
                    light
            ).play();
        });

        return root;
    }
}

