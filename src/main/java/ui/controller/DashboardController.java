package ui.controller;

import application.GameFacade;
import domain.Coach;
import domain.InjuryStatus;
import domain.Match;
import domain.Player;
import domain.StandingEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.SceneManager;
import ui.SoundManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardController {

    private final GameFacade facade;

    public DashboardController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0e1a;");
        root.setTop(buildTopBar());

        // Center is wrapped in a ScrollPane so the cards (coach / injury / match /
        // standings) can grow without pushing the window: when the user shrinks
        // the stage the middle column scrolls instead of overflowing.
        ScrollPane centerScroll = new ScrollPane(buildCenter());
        centerScroll.setFitToWidth(true);
        centerScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        centerScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        centerScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        // Allow the scroll viewport itself to shrink to the available space.
        centerScroll.setMinHeight(0);
        centerScroll.setMinWidth(0);
        root.setCenter(centerScroll);

        root.setRight(buildNavPanel());
        return root;
    }

    // ── Top bar ─────────────────────────────────────────────────────────────

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #052e16, #0a1a0f, #0a0e1a);" +
            "-fx-padding: 20 28 16 28;"
        );

        VBox titleBlock = new VBox(4);
        Label teamLabel = new Label(facade.getUserTeam().getName().toUpperCase());
        teamLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        teamLabel.setTextFill(Color.WHITE);

        Label managerLabel = new Label("Manager: " + facade.getManagerProfile().getManagerName()
                + "  |  Season " + facade.getCurrentSeasonNumber()
                + "  |  Reputation: " + facade.getManagerProfile().getReputation());
        managerLabel.setTextFill(Color.web("#9ca3af"));
        managerLabel.setFont(Font.font("Arial", 12));
        Label weekLabel = new Label(
            "WEEK " + facade.getCurrentWeekNumber() + " / " + facade.getTotalWeeks());
        weekLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        weekLabel.setTextFill(Color.web("#4ade80"));
        titleBlock.getChildren().addAll(teamLabel, managerLabel, weekLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label posLabel = new Label("  #" + facade.getUserTeamPosition() + "  ");
        posLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        posLabel.setTextFill(Color.web("#22c55e"));
        posLabel.setStyle(
            "-fx-background-color: #052e16;" +
            "-fx-padding: 4 10;" +
            "-fx-background-radius: 6;"
        );

        bar.getChildren().addAll(titleBlock, spacer, posLabel);
        return bar;
    }

    // ── Center ───────────────────────────────────────────────────────────────

    private VBox buildCenter() {
        VBox center = new VBox(16);
        center.setPadding(new Insets(24, 28, 24, 28));
        center.getChildren().add(buildCoachInfoCard());
        VBox injuryCard = buildInjuryReportCardIfAny();
        if (injuryCard != null) center.getChildren().add(injuryCard);
        center.getChildren().addAll(buildMatchCard(), buildStandingsCard());
        return center;
    }

    private HBox buildCoachInfoCard() {
        HBox card = new HBox(12);
        card.setPadding(new Insets(14, 20, 14, 20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(card());

        Coach coach = facade.getUserTeam().getCoach();
        String coachText = coach != null
                ? coach.getName() + "  (Lv." + coach.getCoachLevel() + ")"
                : "No coach";
        Label coachLabel = new Label("COACH:  " + coachText);
        coachLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        coachLabel.setTextFill(Color.web("#a78bfa"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label repLabel = new Label("REP: " + facade.getManagerProfile().getReputation());
        repLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        repLabel.setTextFill(Color.web("#6b7280"));

        card.getChildren().addAll(coachLabel, spacer, repLabel);
        return card;
    }

    /**
     * Shown only when at least one squad member is injured — mirrors pre-match injury awareness.
     */
    private VBox buildInjuryReportCardIfAny() {
        List<Player> injured = new ArrayList<>();
        for (Player p : facade.getUserTeam().getStartingPlayers()) {
            if (p.getInjuryStatus() == InjuryStatus.INJURED) injured.add(p);
        }
        for (Player p : facade.getUserTeam().getSubstitutes()) {
            if (p.getInjuryStatus() == InjuryStatus.INJURED) injured.add(p);
        }
        if (injured.isEmpty()) return null;

        injured.sort(Comparator.comparing(Player::getName));

        VBox card = new VBox(8);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle(
                "-fx-background-color: #450a0a; -fx-background-radius: 10;" +
                "-fx-border-color: #7f1d1d; -fx-border-radius: 10; -fx-border-width: 1;"
        );

        Label title = new Label("INJURY REPORT — open Squad to substitute");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        title.setTextFill(Color.web("#fca5a5"));

        Label hint = new Label("Injured players do not count in match strength until replaced.");
        hint.setFont(Font.font("Arial", 11));
        hint.setTextFill(Color.web("#9ca3af"));
        hint.setWrapText(true);

        card.getChildren().addAll(title, hint);

        for (Player p : injured) {
            String line = p.getName() + "  —  " + p.getInjuredGamesRemaining() + " game(s) out";
            Label row = new Label(line);
            row.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            row.setTextFill(Color.web("#fecaca"));
            card.getChildren().add(row);
        }

        return card;
    }

    private VBox buildMatchCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(card());

        Label sectionTitle = new Label("THIS WEEK'S MATCH");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        sectionTitle.setTextFill(Color.web("#6b7280"));
        card.getChildren().add(sectionTitle);

        Match match = facade.getUpcomingMatch();

        if (match != null) {
            Label vsLabel = new Label(
                match.getHomeTeam().getName() + "  vs  " + match.getAwayTeam().getName());
            vsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            vsLabel.setTextFill(Color.WHITE);

            boolean isHome = match.getHomeTeam().getName().equals(facade.getUserTeam().getName());
            Label venue = new Label(isHome ? "Home fixture" : "Away fixture");
            venue.setFont(Font.font("Arial", 12));
            venue.setTextFill(Color.web("#9ca3af"));

            Label hint = new Label("→ Go to Training to start the week");
            hint.setFont(Font.font("Arial", 12));
            hint.setTextFill(Color.web("#6b7280"));

            card.getChildren().addAll(vsLabel, venue, hint);
        } else {
            Label hint = new Label("No match scheduled this week.");
            hint.setFont(Font.font("Arial", 14));
            hint.setTextFill(Color.web("#6b7280"));
            card.getChildren().add(hint);
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
        // Outer panel — fixed width so the right column never collapses or stretches.
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(24, 16, 24, 16));
        panel.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 0 0 0 1;"
        );
        panel.setPrefWidth(170);
        panel.setMinWidth(170);
        panel.setMaxWidth(170);
        panel.setAlignment(Pos.TOP_CENTER);

        Label navTitle = new Label("MENU");
        navTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        navTitle.setTextFill(Color.web("#374151"));
        navTitle.setPadding(new Insets(0, 0, 8, 0));

        Button trainingBtn = navBtn("Training",  "nav-btn-green");
        Button rosterBtn   = navBtn("Squad",     "nav-btn-blue");
        Button coachBtn    = navBtn("Coach",     "nav-btn-red");
        Button standBtn    = navBtn("Standings", "nav-btn-purple");
        Button saveBtn     = navBtn("Save Game",    "nav-btn-gray");
        Button deleteBtn   = navBtn("Delete Save",  "nav-btn-gray");
        Button mainMenuBtn = navBtn("Main Menu",    "nav-btn-gray");

        trainingBtn.setOnAction(e -> SceneManager.getInstance().switchTo("training",  facade));
        rosterBtn.setOnAction(e   -> SceneManager.getInstance().switchTo("roster",    facade));
        coachBtn.setOnAction(e    -> SceneManager.getInstance().switchTo("coach",     facade));
        standBtn.setOnAction(e    -> SceneManager.getInstance().switchTo("standings", facade));
        saveBtn.setOnAction(e -> {
            List<String> slots = facade.getSaveSlotInfo();
            ChoiceDialog<String> dialog = new ChoiceDialog<>(slots.get(0), slots);
            dialog.setTitle("Save Game");
            dialog.setHeaderText("Choose a save slot");
            dialog.setContentText("Slot:");
            dialog.showAndWait().ifPresent(chosen -> {
                int slotId = slots.indexOf(chosen) + 1;
                try {
                    facade.saveGame(slotId);
                    new Alert(Alert.AlertType.INFORMATION, "Game saved to Slot " + slotId + ".").showAndWait();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Save failed: " + ex.getMessage()).showAndWait();
                }
            });
        });
        deleteBtn.setOnAction(e -> SaveSlotDialogs.promptDeleteSave(facade));
        mainMenuBtn.setOnAction(e -> SceneManager.getInstance().switchTo("main-menu", facade));

        VBox upperButtons = new VBox(10);
        upperButtons.setAlignment(Pos.TOP_CENTER);
        upperButtons.getChildren().addAll(trainingBtn, rosterBtn, coachBtn, standBtn, saveBtn, deleteBtn);
        VBox.setVgrow(upperButtons, Priority.ALWAYS);

        panel.getChildren().addAll(navTitle, upperButtons, mainMenuBtn);
        return panel;
    }

    private Button navBtn(String text, String colorClass) {
        Button btn = new Button(text);
        btn.getStyleClass().addAll("nav-btn", colorClass);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnMouseEntered(e -> SoundManager.getInstance().playButton());
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
