package ui.controller;

import application.GameFacade;
import domain.InjuryStatus;
import domain.Match;
import domain.MatchResult;
import domain.Player;
import domain.StandingEntry;
import domain.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #0a0e1a;");

        VBox body = new VBox(14);
        body.setPadding(new Insets(20, 28, 24, 28));
        body.getChildren().addAll(
                buildInjuryCard(),
                buildStandingsCard(),
                buildNextWeekButton()
        );

        root.getChildren().addAll(buildResultHeader(), body);
        return root;
    }

    private VBox buildResultHeader() {
        int homeGoals = matchResult != null ? matchResult.getHomeGoals() : 0;
        int awayGoals = matchResult != null ? matchResult.getAwayGoals() : 0;

        boolean userIsHome = userMatch != null && userMatch.getHomeTeam().equals(userTeam);
        int userGoals      = userIsHome ? homeGoals : awayGoals;
        int opponentGoals  = userIsHome ? awayGoals : homeGoals;

        String resultText;
        String bgGradient;
        String resultColor;

        if (userGoals > opponentGoals) {
            resultText = "VICTORY";
            bgGradient = "linear-gradient(to bottom, #052e16, #0a1a0f, #0a0e1a)";
            resultColor = "#4ade80";
        } else if (userGoals == opponentGoals) {
            resultText = "DRAW";
            bgGradient = "linear-gradient(to bottom, #1a1400, #0f0d00, #0a0e1a)";
            resultColor = "#fbbf24";
        } else {
            resultText = "DEFEAT";
            bgGradient = "linear-gradient(to bottom, #2a0000, #1a0000, #0a0e1a)";
            resultColor = "#ef4444";
        }

        VBox header = new VBox(6);
        header.setStyle(
                "-fx-background-color: " + bgGradient + ";" +
                        "-fx-padding: 22 28 18 28;"
        );

        Label result = new Label(resultText);
        result.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        result.setTextFill(Color.web(resultColor));

        String home = userMatch != null ? userMatch.getHomeTeam().getName().toUpperCase() : "HOME";
        String away = userMatch != null ? userMatch.getAwayTeam().getName().toUpperCase() : "AWAY";

        Label homeLabel = new Label(home);
        homeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        homeLabel.setTextFill(Color.WHITE);

        Label scoreLabel = new Label(homeGoals + "  —  " + awayGoals);
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        scoreLabel.setTextFill(Color.web("#fbbf24"));
        scoreLabel.setPadding(new Insets(0, 22, 0, 22));

        Label awayLabel = new Label(away);
        awayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        awayLabel.setTextFill(Color.web("#9ca3af"));

        HBox scoreRow = new HBox(homeLabel, scoreLabel, awayLabel);
        scoreRow.setAlignment(Pos.CENTER_LEFT);

        header.getChildren().addAll(result, scoreRow);
        return header;
    }

    private VBox buildInjuryCard() {
        VBox card = card();
        card.getChildren().add(sectionLabel("INJURY REPORT"));

        List<Player> all = new ArrayList<>();
        all.addAll(userTeam.getStartingPlayers());
        all.addAll(userTeam.getSubstitutes());

        List<Player> injured = all.stream()
                .filter(p -> p.getInjuryStatus() == InjuryStatus.INJURED)
                .collect(Collectors.toList());

        if (injured.isEmpty()) {
            Label none = new Label("No injuries this match.");
            none.setTextFill(Color.web("#4ade80"));
            none.setFont(Font.font("Arial", 13));
            card.getChildren().add(none);
        } else {
            for (Player p : injured) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);

                Label badge = new Label("INJURED");
                badge.setStyle(
                        "-fx-background-color: #ef4444; -fx-text-fill: white;" +
                                "-fx-font-size: 10px; -fx-font-weight: bold;" +
                                "-fx-padding: 2 6; -fx-background-radius: 4;"
                );

                Label info = new Label(p.getName() + "  — out for " + p.getInjuredGamesRemaining() + " game(s)");
                info.setTextFill(Color.web("#f87171"));
                info.setFont(Font.font("Arial", 13));

                row.getChildren().addAll(badge, info);
                card.getChildren().add(row);
            }
        }

        return card;
    }

    private VBox buildStandingsCard() {
        VBox card = card();
        card.getChildren().add(sectionLabel("STANDINGS"));

        VBox list = new VBox(2);
        list.getChildren().add(standingRow("#6b7280", true, "POS", "TEAM", "PTS", "W", "D", "L", "GD"));

        List<StandingEntry> standings = facade.getStandings();
        for (int i = 0; i < standings.size(); i++) {
            StandingEntry entry = standings.get(i);
            boolean isUser = entry.getTeam().equals(userTeam);
            String color = isUser ? "#22c55e" : "#e5e7eb";
            list.getChildren().add(standingRow(color, false,
                    String.valueOf(i + 1),
                    entry.getTeam().getName(),
                    String.valueOf(entry.getPoints()),
                    String.valueOf(entry.getWins()),
                    String.valueOf(entry.getDraws()),
                    String.valueOf(entry.getLosses()),
                    String.valueOf(entry.getGoalDifference())));
        }

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(180);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        card.getChildren().add(scroll);
        return card;
    }

    private HBox standingRow(String color, boolean bold, String pos, String team,
                              String pts, String w, String d, String l, String gd) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        FontWeight weight = bold ? FontWeight.BOLD : FontWeight.NORMAL;
        row.getChildren().addAll(
            col(pos,  30, color, weight),
            col(team, 170, color, weight),
            col(pts,  36, color, weight),
            col(w,    28, color, weight),
            col(d,    28, color, weight),
            col(l,    28, color, weight),
            col(gd,   36, color, weight)
        );
        return row;
    }

    private Label col(String text, double width, String color, FontWeight weight) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setFont(Font.font("Arial", weight, 12));
        l.setTextFill(Color.web(color));
        return l;
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
                SceneManager.getInstance().switchTo("dashboard", facade);
            }
        });
        return btn;
    }

    private VBox card() {
        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color: #111827; -fx-background-radius: 12;" +
                        "-fx-border-color: #1f2937; -fx-border-radius: 12;" +
                        "-fx-border-width: 1; -fx-padding: 14 18;"
        );
        return card;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setTextFill(Color.web("#6b7280"));
        return l;
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

