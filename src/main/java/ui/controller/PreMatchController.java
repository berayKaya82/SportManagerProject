package ui.controller;

import application.GameFacade;
import domain.InjuryStatus;
import domain.Match;
import domain.PlayStyle;
import domain.Player;
import domain.Team;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import sport.ITactic;
import ui.SceneManager;

public class PreMatchController {

    private final GameFacade facade;
    private VBox root;
    private ComboBox<String> tacticComboBox;

    public PreMatchController(GameFacade facade) {
        this.facade = facade;
        buildUI();
    }

    public Parent getRoot() { return root; }

    private void buildUI() {
        root = new VBox();
        root.setStyle("-fx-background-color: #0a0e1a;");
        root.getChildren().addAll(buildMatchupHeader(), buildBody());
    }

    private VBox buildMatchupHeader() {
        VBox header = new VBox(6);
        header.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #052e16, #0a0e1a);" +
                        "-fx-padding: 22 28 18 28;"
        );

        Label pre = new Label("PRE-MATCH");
        pre.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        pre.setTextFill(Color.web("#4ade80"));

        try {
            Match match    = facade.getUserMatch();
            Team userTeam  = facade.getUserTeam();
            boolean isHome = match.getHomeTeam().equals(userTeam);
            String venue   = isHome ? "HOME" : "AWAY";

            Label venueLabel = new Label(venue);
            venueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            venueLabel.setStyle(
                    "-fx-background-color: " + (isHome ? "#16a34a" : "#2563eb") + ";" +
                            "-fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 4;"
            );

            HBox topRow = new HBox(10, pre, venueLabel);
            topRow.setAlignment(Pos.CENTER_LEFT);

            Label homeLabel = new Label(match.getHomeTeam().getName().toUpperCase());
            homeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
            homeLabel.setTextFill(Color.WHITE);

            Label vsLabel = new Label("VS");
            vsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            vsLabel.setTextFill(Color.web("#22c55e"));
            vsLabel.setPadding(new Insets(0, 20, 0, 20));

            Label awayLabel = new Label(match.getAwayTeam().getName().toUpperCase());
            awayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
            awayLabel.setTextFill(Color.web("#9ca3af"));

            HBox matchRow = new HBox(homeLabel, vsLabel, awayLabel);
            matchRow.setAlignment(Pos.CENTER_LEFT);

            header.getChildren().addAll(topRow, matchRow);
        } catch (IllegalStateException e) {
            Label err = new Label("Match not scheduled yet.");
            err.setTextFill(Color.web("#9ca3af"));
            header.getChildren().addAll(pre, err);
        }

        return header;
    }

    private VBox buildBody() {
        VBox body = new VBox(16);
        body.setPadding(new Insets(20, 28, 24, 28));
        body.getChildren().addAll(buildTacticCard(), buildSquadCard(), buildStartButton());
        return body;
    }

    private VBox buildTacticCard() {
        VBox card = card();
        card.getChildren().add(sectionLabel("TACTIC"));

        tacticComboBox = new ComboBox<>();
        tacticComboBox.getItems().addAll("DEFENSIVE", "BALANCED", "OFFENSIVE");
        tacticComboBox.setValue(getCurrentTacticName());
        tacticComboBox.setPrefWidth(200);

        card.getChildren().add(tacticComboBox);
        return card;
    }

    private VBox buildSquadCard() {
        VBox card = card();
        card.getChildren().add(sectionLabel("SQUAD"));

        VBox playerList = new VBox(5);

        Label startersHeader = new Label("STARTING XI");
        startersHeader.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        startersHeader.setTextFill(Color.web("#4ade80"));
        playerList.getChildren().add(startersHeader);

        for (Player p : facade.getUserTeam().getStartingPlayers()) {
            playerList.getChildren().add(buildPlayerRow(p));
        }

        Label subHeader = new Label("BENCH");
        subHeader.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        subHeader.setTextFill(Color.web("#fbbf24"));
        subHeader.setPadding(new Insets(6, 0, 0, 0));
        playerList.getChildren().add(subHeader);

        for (Player p : facade.getUserTeam().getSubstitutes()) {
            playerList.getChildren().add(buildPlayerRow(p));
        }

        ScrollPane scroll = new ScrollPane(playerList);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(230);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        card.getChildren().add(scroll);
        return card;
    }

    private HBox buildPlayerRow(Player p) {
        boolean injured = p.getInjuryStatus() == InjuryStatus.INJURED;

        String tag = injured ? " (I)" : " (H)";
        Label name = new Label(p.getName() + tag);
        name.setPrefWidth(160);
        name.setFont(Font.font("Arial", 13));
        name.setTextFill(injured ? Color.web("#ef4444") : Color.web("#4ade80"));

        Label eLabel = new Label("E");
        eLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        eLabel.setTextFill(Color.web("#4ade80"));

        ProgressBar energyBar = new ProgressBar(p.getEnergy() / 100.0);
        energyBar.setPrefWidth(75);
        energyBar.setPrefHeight(7);
        String eColor = p.getEnergy() > 60 ? "#22c55e" : (p.getEnergy() > 30 ? "#f97316" : "#ef4444");
        energyBar.setStyle("-fx-accent: " + eColor + ";");

        Label cLabel = new Label("C");
        cLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        cLabel.setTextFill(Color.web("#60a5fa"));

        ProgressBar condBar = new ProgressBar(p.getCondition() / 100.0);
        condBar.setPrefWidth(75);
        condBar.setPrefHeight(7);
        condBar.setStyle("-fx-accent: #3b82f6;");

        HBox row = new HBox(8, name, eLabel, energyBar, cLabel, condBar);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Button buildStartButton() {
        Button btn = new Button("START MATCH  →");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("btn-primary");
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        btn.setOnAction(e -> handleStartMatch());
        return btn;
    }

    private String getCurrentTacticName() {
        try {
            return facade.getUserTeam().getTactic().getPlayStyle().name();
        } catch (Exception e) {
            return "BALANCED";
        }
    }

    private void handleStartMatch() {
        PlayStyle selectedStyle = PlayStyle.valueOf(tacticComboBox.getValue());
        ITactic tactic = new ITactic() {
            @Override public PlayStyle getPlayStyle() { return selectedStyle; }
        };
        facade.setTactic(tactic);
        facade.playPeriod(1);
        SceneManager.getInstance().switchTo("match", facade);
    }

    private VBox card() {
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: #111827; -fx-background-radius: 12;" +
                        "-fx-border-color: #1f2937; -fx-border-radius: 12;" +
                        "-fx-border-width: 1; -fx-padding: 16 18;"
        );
        return card;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setTextFill(Color.web("#6b7280"));
        return l;
    }
}

