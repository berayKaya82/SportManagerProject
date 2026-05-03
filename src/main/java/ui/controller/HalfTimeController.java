package ui.controller;

import application.GameFacade;
import domain.InjuryStatus;
import domain.Match;
import domain.MatchResult;
import domain.PlayStyle;
import domain.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import sport.ITactic;
import ui.SceneManager;

public class HalfTimeController {

    private final GameFacade facade;
    private ToggleGroup tacticGroup;
    private ComboBox<Player> outComboBox;
    private ComboBox<Player> inComboBox;
    private VBox squadList;

    public HalfTimeController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #0a0e1a;");

        VBox body = new VBox(14);
        body.setPadding(new Insets(20, 28, 24, 28));
        body.getChildren().addAll(
                buildTacticCard(),
                buildSubstitutionCard(),
                buildSquadCard(),
                buildContinueButton()
        );

        root.getChildren().addAll(buildHalfTimeHeader(), body);
        return root;
    }

    private VBox buildHalfTimeHeader() {
        VBox header = new VBox(8);
        header.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #052e16, #081a0f, #0a0e1a);" +
                        "-fx-padding: 20 28 16 28;"
        );

        Label periodLabel = new Label("HALF TIME");
        periodLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        periodLabel.setStyle(
                "-fx-background-color: #1f2937; -fx-text-fill: #9ca3af;" +
                        "-fx-padding: 3 10; -fx-background-radius: 4;"
        );

        Match match  = facade.getUserMatch();
        MatchResult r = facade.getCurrentPeriodResult();

        Label homeLabel = new Label(match.getHomeTeam().getName().toUpperCase());
        homeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        homeLabel.setTextFill(Color.WHITE);

        Label scoreLabel = new Label(r.getHomeGoals() + "  —  " + r.getAwayGoals());
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 46));
        scoreLabel.setTextFill(Color.web("#fbbf24"));
        scoreLabel.setPadding(new Insets(0, 24, 0, 24));

        Label awayLabel = new Label(match.getAwayTeam().getName().toUpperCase());
        awayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        awayLabel.setTextFill(Color.web("#9ca3af"));

        HBox scoreRow = new HBox(homeLabel, scoreLabel, awayLabel);
        scoreRow.setAlignment(Pos.CENTER_LEFT);

        header.getChildren().addAll(periodLabel, scoreRow);
        return header;
    }

    private VBox buildTacticCard() {
        VBox card = card();
        card.getChildren().add(sectionLabel("CHANGE TACTIC"));

        tacticGroup = new ToggleGroup();
        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER_LEFT);

        String current = getCurrentTacticName();
        for (PlayStyle style : PlayStyle.values()) {
            RadioButton rb = new RadioButton(style.name());
            rb.setToggleGroup(tacticGroup);
            if (style.name().equals(current)) rb.setSelected(true);
            radioBox.getChildren().add(rb);
        }

        card.getChildren().add(radioBox);
        return card;
    }

    private String getCurrentTacticName() {
        try {
            return facade.getUserTeam().getTactic().getPlayStyle().name();
        } catch (Exception e) {
            return "BALANCED";
        }
    }

    private VBox buildSubstitutionCard() {
        VBox card = card();
        card.getChildren().add(sectionLabel("SUBSTITUTION"));

        outComboBox = buildPlayerComboBox("Player Out");
        outComboBox.getItems().addAll(facade.getUserTeam().getStartingPlayers());

        inComboBox = buildPlayerComboBox("Player In");
        inComboBox.getItems().addAll(facade.getUserTeam().getSubstitutes());

        Button subBtn = new Button("SUBSTITUTE");
        subBtn.getStyleClass().add("btn-orange");
        subBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        subBtn.setOnAction(e -> handleSubstitution());

        Label arrow = new Label("→");
        arrow.setTextFill(Color.web("#22c55e"));
        arrow.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        HBox row = new HBox(12, outComboBox, arrow, inComboBox, subBtn);
        row.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().add(row);
        return card;
    }

    private ComboBox<Player> buildPlayerComboBox(String prompt) {
        ComboBox<Player> box = new ComboBox<>();
        box.setPromptText(prompt);
        box.setPrefWidth(190);
        box.setConverter(new StringConverter<>() {
            @Override public String toString(Player p) { return p == null ? "" : p.getName(); }
            @Override public Player fromString(String s) { return null; }
        });
        return box;
    }

    private void handleSubstitution() {
        Player out = outComboBox.getValue();
        Player in  = inComboBox.getValue();

        if (out == null || in == null) {
            new Alert(Alert.AlertType.WARNING, "Select both players to substitute.").showAndWait();
            return;
        }

        try {
            facade.getUserTeam().substitutePlayer(out, in);
            outComboBox.getItems().setAll(facade.getUserTeam().getStartingPlayers());
            inComboBox.getItems().setAll(facade.getUserTeam().getSubstitutes());
            outComboBox.setValue(null);
            inComboBox.setValue(null);
            refreshSquadList();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Substitution failed: " + e.getMessage()).showAndWait();
        }
    }

    private VBox buildSquadCard() {
        VBox card = card();
        card.getChildren().add(sectionLabel("SQUAD STATUS"));

        squadList = new VBox(5);
        squadList.setStyle("-fx-background-color: transparent;");
        refreshSquadList();

        ScrollPane scroll = new ScrollPane(squadList);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(160);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        card.getChildren().add(scroll);
        return card;
    }

    private void refreshSquadList() {
        squadList.getChildren().clear();
        addPlayerRows(squadList, "STARTERS", facade.getUserTeam().getStartingPlayers(), "#4ade80");
        addPlayerRows(squadList, "BENCH",    facade.getUserTeam().getSubstitutes(),     "#fbbf24");
    }

    private void addPlayerRows(VBox container, String header, java.util.List<Player> players, String color) {
        Label headerLabel = new Label(header);
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        headerLabel.setTextFill(Color.web(color));
        headerLabel.setPadding(new Insets(4, 0, 2, 0));
        container.getChildren().add(headerLabel);

        for (Player p : players) {
            container.getChildren().add(buildPlayerRow(p));
        }
    }

    private HBox buildPlayerRow(Player p) {
        boolean injured = p.getInjuryStatus() == InjuryStatus.INJURED;

        Label name = new Label(p.getName());
        name.setPrefWidth(160);
        name.setFont(Font.font("Arial", 13));
        name.setTextFill(injured ? Color.web("#ef4444") : Color.WHITE);

        Label eLabel = new Label("E");
        eLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        eLabel.setTextFill(Color.web("#4ade80"));

        ProgressBar energyBar = new ProgressBar(p.getEnergy() / 100.0);
        energyBar.setPrefWidth(75);
        energyBar.setPrefHeight(7);
        String eColor = p.getEnergy() > 60 ? "#22c55e" : (p.getEnergy() > 30 ? "#f97316" : "#ef4444");
        energyBar.setStyle("-fx-accent: " + eColor + ";");

        Label eVal = new Label(p.getEnergy() + "");
        eVal.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        eVal.setTextFill(Color.web(eColor));
        eVal.setPrefWidth(26);

        Label cLabel = new Label("C");
        cLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        cLabel.setTextFill(Color.web("#60a5fa"));

        ProgressBar condBar = new ProgressBar(p.getCondition() / 100.0);
        condBar.setPrefWidth(75);
        condBar.setPrefHeight(7);
        condBar.setStyle("-fx-accent: #3b82f6;");

        Label cVal = new Label(p.getCondition() + "");
        cVal.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        cVal.setTextFill(Color.web("#60a5fa"));
        cVal.setPrefWidth(26);

        HBox row = new HBox(8, name, eLabel, energyBar, eVal, cLabel, condBar, cVal);
        row.setAlignment(Pos.CENTER_LEFT);

        if (injured) {
            Label badge = new Label("INJURED");
            badge.setStyle(
                    "-fx-background-color: #ef4444; -fx-text-fill: white;" +
                            "-fx-font-size: 10px; -fx-font-weight: bold;" +
                            "-fx-padding: 2 6; -fx-background-radius: 4;"
            );
            row.getChildren().add(badge);
        }

        return row;
    }

    private Button buildContinueButton() {
        Button btn = new Button("CONTINUE TO 2ND HALF  →");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("btn-primary");
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        btn.setOnAction(e -> handleContinue());
        return btn;
    }

    private void handleContinue() {
        if (tacticGroup.getSelectedToggle() != null) {
            RadioButton selected = (RadioButton) tacticGroup.getSelectedToggle();
            PlayStyle style = PlayStyle.valueOf(selected.getText());
            ITactic tactic = new ITactic() {
                @Override public PlayStyle getPlayStyle() { return style; }
            };
            facade.setTactic(tactic);
        }
        facade.playPeriod(2);
        SceneManager.getInstance().switchTo("post-match", facade);
    }

    private VBox card() {
        VBox card = new VBox(10);
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
}
