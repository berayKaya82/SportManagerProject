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
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import sport.ITactic;
import ui.SceneManager;
import ui.SoundManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
                buildInjuryCard(),
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

    private VBox buildInjuryCard() {
        VBox card = card();
        card.getChildren().add(sectionLabel("INJURY REPORT"));

        java.util.List<Player> all = new java.util.ArrayList<>();
        all.addAll(facade.getUserTeam().getStartingPlayers());
        all.addAll(facade.getUserTeam().getSubstitutes());

        java.util.List<Player> injured = all.stream()
                .filter(p -> p.getInjuryStatus() == InjuryStatus.INJURED)
                .collect(java.util.stream.Collectors.toList());

        if (injured.isEmpty()) {
            Label none = new Label("No injuries — all players fit.");
            none.setTextFill(Color.web("#4ade80"));
            none.setFont(Font.font("Arial", 13));
            card.getChildren().add(none);
        } else {
            for (Player p : injured) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);

                Label badge = new Label("I");
                badge.setStyle(
                        "-fx-background-color: #ef4444; -fx-text-fill: white;" +
                        "-fx-font-size: 11px; -fx-font-weight: bold;" +
                        "-fx-padding: 1 6; -fx-background-radius: 3;"
                );

                Label info = new Label(p.getName() + "  —  out for " + p.getInjuredGamesRemaining() + " more game(s)");
                info.setTextFill(Color.web("#f87171"));
                info.setFont(Font.font("Arial", 13));

                row.getChildren().addAll(badge, info);
                card.getChildren().add(row);
            }
        }

        return card;
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
        outComboBox.getItems().setAll(sortedStartersForOut());

        inComboBox = buildPlayerComboBox("Player In");
        inComboBox.getItems().setAll(sortedSubsForIn());

        styleInjuryAwareCombo(outComboBox);
        styleInjuryAwareCombo(inComboBox);

        Button subBtn = new Button("SUBSTITUTE");
        subBtn.getStyleClass().add("btn-orange");
        SoundManager.getInstance().wire(subBtn);
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
            outComboBox.getItems().setAll(sortedStartersForOut());
            inComboBox.getItems().setAll(sortedSubsForIn());
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
        return row;
    }

    private Button buildContinueButton() {
        Button btn = new Button("CONTINUE TO 2ND HALF  →");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("btn-primary");
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        btn.setOnAction(e -> handleContinue());
        SoundManager.getInstance().wire(btn);
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

    /** Injured starters first (easy to swap out), then alphabetical. */
    private List<Player> sortedStartersForOut() {
        List<Player> list = new ArrayList<>(facade.getUserTeam().getStartingPlayers());
        list.sort(Comparator.comparing((Player p) -> p.getInjuryStatus() != InjuryStatus.INJURED)
                .thenComparing(Player::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    /** Healthy bench first for typical subs; injured bench at bottom, still styled red. */
    private List<Player> sortedSubsForIn() {
        List<Player> list = new ArrayList<>(facade.getUserTeam().getSubstitutes());
        list.sort(Comparator.comparing((Player p) -> p.getInjuryStatus() == InjuryStatus.INJURED)
                .thenComparing(Player::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    private void styleInjuryAwareCombo(ComboBox<Player> combo) {
        javafx.util.Callback<javafx.scene.control.ListView<Player>, ListCell<Player>> factory =
                lv -> new ListCell<>() {
                    @Override protected void updateItem(Player p, boolean empty) {
                        super.updateItem(p, empty);
                        if (empty || p == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            boolean inj = p.getInjuryStatus() == InjuryStatus.INJURED;
                            setText(p.getName() + (inj ? "  ● INJ" : ""));
                            setTextFill(inj ? Color.web("#f87171") : Color.WHITE);
                        }
                    }
                };
        combo.setCellFactory(factory);
        combo.setButtonCell(factory.call(null));
    }
}
