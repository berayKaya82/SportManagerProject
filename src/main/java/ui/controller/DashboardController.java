package ui.controller;

import application.GameFacade;
import domain.Match;
import domain.StandingEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.SceneManager;

public class DashboardController {

    private final GameFacade facade;

    public DashboardController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0e1a;");
        root.setTop(buildTopBar());
        root.setCenter(buildCenter());
        root.setRight(buildNavPanel());
        return root;
    }

    // ── Top bar ─────────────────────────────────────────────────────────────

    private HBox buildTopBar() {
        HBox bar = new HBox(20);
        bar.setPadding(new Insets(16, 28, 16, 28));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label teamLabel = new Label(facade.getUserTeam().getName().toUpperCase());
        teamLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        teamLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label weekLabel = new Label(
            "WEEK " + facade.getCurrentWeekNumber() + " / " + facade.getTotalWeeks());
        weekLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        weekLabel.setTextFill(Color.web("#6b7280"));

        Label posLabel = new Label("  #" + facade.getUserTeamPosition() + "  ");
        posLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        posLabel.setTextFill(Color.web("#22c55e"));
        posLabel.setStyle(
            "-fx-background-color: #052e16;" +
            "-fx-padding: 4 10;" +
            "-fx-background-radius: 6;"
        );

        bar.getChildren().addAll(teamLabel, spacer, weekLabel, posLabel);
        return bar;
    }

    // ── Center ───────────────────────────────────────────────────────────────

    private VBox buildCenter() {
        VBox center = new VBox(16);
        center.setPadding(new Insets(24, 28, 24, 28));
        center.getChildren().addAll(buildMatchCard(), buildStandingsCard());
        return center;
    }

    private VBox buildMatchCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(card());

        Label sectionTitle = new Label("THIS WEEK'S MATCH");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        sectionTitle.setTextFill(Color.web("#6b7280"));
        card.getChildren().add(sectionTitle);

        // getUserMatch() throws IllegalStateException until startWeek() is called
        Match match = null;
        try { match = facade.getUserMatch(); } catch (IllegalStateException ignored) {}

        if (match != null) {
            Label vsLabel = new Label(
                match.getHomeTeam().getName() + "  vs  " + match.getAwayTeam().getName());
            vsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            vsLabel.setTextFill(Color.WHITE);

            boolean isHome = match.getHomeTeam().getName().equals(facade.getUserTeam().getName());
            Label venue = new Label(isHome ? "Home fixture" : "Away fixture");
            venue.setFont(Font.font("Arial", 12));
            venue.setTextFill(Color.web("#9ca3af"));

            card.getChildren().addAll(vsLabel, venue);
        } else {
            Label hint = new Label("Train your squad to begin Week " + facade.getCurrentWeekNumber());
            hint.setFont(Font.font("Arial", 14));
            hint.setTextFill(Color.web("#f97316"));

            Label arrow = new Label("→ Click Training on the right to start the week");
            arrow.setFont(Font.font("Arial", 12));
            arrow.setTextFill(Color.web("#6b7280"));

            card.getChildren().addAll(hint, arrow);
        }

        return card;
    }

    private VBox buildStandingsCard() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle(card());

        Label sectionTitle = new Label("STANDINGS");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        sectionTitle.setTextFill(Color.web("#6b7280"));
        card.getChildren().add(sectionTitle);

        int rank = 1;
        for (StandingEntry entry : facade.getStandings()) {
            boolean isUser = entry.getTeam().equals(facade.getUserTeam());

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(3, isUser ? 8 : 2, 3, isUser ? 8 : 2));
            if (isUser) {
                row.setStyle("-fx-background-color: #052e16; -fx-background-radius: 4;");
            }

            Label rankLabel = new Label(rank + ".");
            rankLabel.setFont(Font.font("Arial", 12));
            rankLabel.setTextFill(Color.web("#6b7280"));
            rankLabel.setMinWidth(22);

            Label nameLabel = new Label(entry.getTeam().getName());
            nameLabel.setFont(Font.font("Arial",
                isUser ? FontWeight.BOLD : FontWeight.NORMAL, 13));
            nameLabel.setTextFill(isUser ? Color.web("#22c55e") : Color.web("#e5e7eb"));
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            Label ptsLabel = new Label(entry.getPoints() + " pts");
            ptsLabel.setFont(Font.font("Arial", 12));
            ptsLabel.setTextFill(Color.web("#6b7280"));

            row.getChildren().addAll(rankLabel, nameLabel, ptsLabel);
            card.getChildren().add(row);
            rank++;
            if (rank > 6) break;
        }

        return card;
    }

    // ── Right nav panel ───────────────────────────────────────────────────────

    private VBox buildNavPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(24, 16, 24, 16));
        panel.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 0 0 0 1;"
        );
        panel.setPrefWidth(170);
        panel.setAlignment(Pos.TOP_CENTER);

        Label navTitle = new Label("MENU");
        navTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        navTitle.setTextFill(Color.web("#374151"));
        navTitle.setPadding(new Insets(0, 0, 8, 0));

        Button trainingBtn = navBtn("Training",  "nav-btn-green");
        Button rosterBtn   = navBtn("Squad",     "nav-btn-blue");
        Button standBtn    = navBtn("Standings", "nav-btn-purple");
        Button saveBtn     = navBtn("Save Game", "nav-btn-gray");

        trainingBtn.setOnAction(e -> SceneManager.getInstance().switchTo("training",  facade));
        rosterBtn.setOnAction(e   -> SceneManager.getInstance().switchTo("roster",    facade));
        standBtn.setOnAction(e    -> SceneManager.getInstance().switchTo("standings", facade));
        saveBtn.setOnAction(e     -> SceneManager.getInstance().switchTo("save-load", facade));

        panel.getChildren().addAll(navTitle, trainingBtn, rosterBtn, standBtn, saveBtn);
        return panel;
    }

    private Button navBtn(String text, String colorClass) {
        Button btn = new Button(text);
        btn.getStyleClass().addAll("nav-btn", colorClass);
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private String card() {
        return "-fx-background-color: #111827;" +
               "-fx-background-radius: 10;" +
               "-fx-border-color: #1f2937;" +
               "-fx-border-width: 1;" +
               "-fx-border-radius: 10;";
    }
}
