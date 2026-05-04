package ui.controller;

import application.GameFacade;
import domain.Match;
import domain.MatchResult;
import domain.Team;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import ui.SceneManager;

public class PostMatchController {

    private final GameFacade facade;
    private final MatchResult matchResult;
    private final Match userMatch;
    private final Team userTeam;

    public PostMatchController(GameFacade facade) {
        this.facade      = facade;
        this.matchResult = safeGetResult();
        this.userMatch   = safeGetMatch();
        this.userTeam    = facade.getUserTeam();
        if (matchResult != null) {
            facade.submitWeekResults(matchResult);
        }
    }

    public Parent getRoot() {
        int homeGoals = matchResult != null ? matchResult.getHomeGoals() : 0;
        int awayGoals = matchResult != null ? matchResult.getAwayGoals() : 0;
        boolean userIsHome = userMatch != null && userMatch.getHomeTeam().equals(userTeam);
        int userGoals     = userIsHome ? homeGoals : awayGoals;
        int opponentGoals = userIsHome ? awayGoals : homeGoals;

        String resultText;
        String bgGradient;
        String accentColor;

        if (userGoals > opponentGoals) {
            resultText  = "VICTORY";
            bgGradient  = "linear-gradient(to bottom, #021a07, #064e1a, #021a07)";
            accentColor = "#22c55e";
        } else if (userGoals == opponentGoals) {
            resultText  = "DRAW";
            bgGradient  = "linear-gradient(to bottom, #fb8500, #ffb703, #fb8500)";
            accentColor = "#facc15";
        } else {
            resultText  = "DEFEAT";
            bgGradient  = "linear-gradient(to bottom, #1a0202, #7f1d1d, #1a0202)";
            accentColor = "#ef4444";

        }

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + bgGradient + ";");

        Button nextBtn = buildNextWeekButton();
        VBox.setMargin(nextBtn, new Insets(20, 120, 0, 120));

        VBox center = new VBox(30);
        center.setAlignment(Pos.CENTER);
        center.getChildren().addAll(
                buildResultLabel(resultText, accentColor),
                buildScoreSection(homeGoals, awayGoals),
                nextBtn
        );

        root.getChildren().add(center);
        return root;
    }

    private Label buildResultLabel(String resultText, String accentColor) {
        Label label = new Label(resultText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 250));
        label.setTextFill(Color.web(accentColor));

        DropShadow glow = new DropShadow(80, Color.web(accentColor));
        glow.setSpread(0.7);
        label.setEffect(glow);

        label.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(1800), label);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setOnFinished(e -> {
            Timeline pulse = new Timeline(
                    new KeyFrame(Duration.ZERO,        new KeyValue(label.opacityProperty(), 1.0)),
                    new KeyFrame(Duration.millis(900),  new KeyValue(label.opacityProperty(), 0.25)),
                    new KeyFrame(Duration.millis(1800), new KeyValue(label.opacityProperty(), 1.0))
            );
            pulse.setCycleCount(Timeline.INDEFINITE);
            pulse.play();
        });
        ft.play();

        return label;
    }

    private VBox buildScoreSection(int homeGoals, int awayGoals) {
        String home = userMatch != null ? userMatch.getHomeTeam().getName().toUpperCase() : "HOME";
        String away = userMatch != null ? userMatch.getAwayTeam().getName().toUpperCase() : "AWAY";

        Label homeLabel = new Label(home);
        homeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        homeLabel.setTextFill(Color.web("#e5e7eb"));

        Label scoreLabel = new Label(homeGoals + "  —  " + awayGoals);
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 58));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setPadding(new Insets(0, 32, 0, 32));

        Label awayLabel = new Label(away);
        awayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        awayLabel.setTextFill(Color.web("#9ca3af"));

        HBox scoreRow = new HBox(homeLabel, scoreLabel, awayLabel);
        scoreRow.setAlignment(Pos.CENTER);

        VBox section = new VBox(8, scoreRow);
        section.setAlignment(Pos.CENTER);
        return section;
    }

    private Button buildNextWeekButton() {
        Button btn = new Button("NEXT WEEK  →");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("btn-primary");
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        btn.setOnAction(e -> {
            if (facade.isSeasonComplete()) {
                SceneManager.getInstance().switchTo("season-end", facade);
            } else {
                facade.applyWeeklyRecovery();
                SceneManager.getInstance().switchTo("dashboard", facade);
            }
        });
        return btn;
    }

    private MatchResult safeGetResult() {
        try { return facade.getCurrentPeriodResult(); }
        catch (IllegalStateException e) { return null; }
    }

    private Match safeGetMatch() {
        try { return facade.getUserMatch(); }
        catch (IllegalStateException e) { return null; }
    }
}
