package ui.controller;

import application.GameFacade;
import domain.StandingEntry;
import domain.Team;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

import java.util.function.Function;

public class StandingsController {

    private final GameFacade facade;

    public StandingsController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #0a0e1a;");

        VBox body = new VBox(14);
        body.setPadding(new Insets(20, 28, 24, 28));

        Team userTeam = facade.getUserTeam();
        int pos = facade.getUserTeamPosition();

        // Position badge
        HBox posRow = new HBox(10);
        posRow.setAlignment(Pos.CENTER_LEFT);

        Label posLabel = new Label("POSITION  " + pos);
        posLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        posLabel.setStyle(
            "-fx-background-color: #052e16;" +
            "-fx-text-fill: #4ade80;" +
            "-fx-padding: 5 12;" +
            "-fx-background-radius: 6;"
        );

        Label seasonLabel = new Label("SEASON " + facade.getCurrentSeasonNumber());
        seasonLabel.setFont(Font.font("Arial", 13));
        seasonLabel.setTextFill(Color.web("#6b7280"));

        posRow.getChildren().addAll(posLabel, seasonLabel);

        TableView<StandingEntry> table = buildTable(userTeam);
        table.getItems().addAll(facade.getStandings());
        VBox.setVgrow(table, Priority.ALWAYS);

        Button backBtn = new Button("← BACK TO DASHBOARD");
        backBtn.getStyleClass().add("btn-secondary");
        SoundManager.getInstance().wire(backBtn);
        backBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        backBtn.setOnAction(e -> SceneManager.getInstance().switchTo("dashboard", facade));

        body.getChildren().addAll(posRow, table, backBtn);
        root.getChildren().addAll(buildHeader(), body);
        VBox.setVgrow(body, Priority.ALWAYS);
        return root;
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #052e16, #0a1a0f, #0a0e1a);" +
            "-fx-padding: 20 28 16 28;"
        );

        Label title = new Label("STANDINGS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label teamLabel = new Label(facade.getUserTeam().getName().toUpperCase());
        teamLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        teamLabel.setTextFill(Color.web("#22c55e"));

        header.getChildren().addAll(title, spacer, teamLabel);
        return header;
    }

    private TableView<StandingEntry> buildTable(Team userTeam) {
        TableView<StandingEntry> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No standings yet."));

        TableColumn<StandingEntry, Integer> posCol = new TableColumn<>("POS");
        posCol.setCellValueFactory(cell -> {
            int idx = table.getItems().indexOf(cell.getValue()) + 1;
            return new SimpleIntegerProperty(idx).asObject();
        });
        posCol.setCellFactory(col -> colorIntCell(userTeam));
        posCol.setPrefWidth(45);
        posCol.setSortable(false);

        TableColumn<StandingEntry, String> teamCol = new TableColumn<>("TEAM");
        teamCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTeam().getName()));
        teamCol.setCellFactory(col -> colorStringCell(userTeam));
        teamCol.setPrefWidth(180);
        teamCol.setSortable(false);

        TableColumn<StandingEntry, Integer> playedCol = intCol("P",   StandingEntry::getPlayed,   userTeam);
        TableColumn<StandingEntry, Integer> winsCol   = intCol("W",   StandingEntry::getWins,     userTeam);
        TableColumn<StandingEntry, Integer> drawsCol  = intCol("D",   StandingEntry::getDraws,    userTeam);
        TableColumn<StandingEntry, Integer> lossesCol = intCol("L",   StandingEntry::getLosses,   userTeam);
        TableColumn<StandingEntry, Integer> gfCol     = intCol("GF",  StandingEntry::getGoalsFor,     userTeam);
        TableColumn<StandingEntry, Integer> gaCol     = intCol("GA",  StandingEntry::getGoalsAgainst, userTeam);
        TableColumn<StandingEntry, Integer> gdCol     = intCol("GD",  StandingEntry::getGoalDifference, userTeam);
        TableColumn<StandingEntry, Integer> ptsCol    = intCol("PTS", StandingEntry::getPoints,   userTeam);

        table.getColumns().addAll(posCol, teamCol, playedCol, winsCol, drawsCol, lossesCol, gfCol, gaCol, gdCol, ptsCol);

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(StandingEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (!empty && entry != null && entry.getTeam().equals(userTeam)) {
                    setStyle("-fx-background-color: #052e16; -fx-border-color: #16a34a; -fx-border-width: 0 0 1 0;");
                } else if (!empty) {
                    setStyle("");
                } else {
                    setStyle("");
                }
            }
        });

        return table;
    }

    private TableColumn<StandingEntry, Integer> intCol(String header,
                                                        Function<StandingEntry, Integer> getter,
                                                        Team userTeam) {
        TableColumn<StandingEntry, Integer> col = new TableColumn<>(header);
        col.setCellValueFactory(cell ->
                new SimpleIntegerProperty(getter.apply(cell.getValue())).asObject());
        col.setCellFactory(c -> colorIntCell(userTeam));
        col.setPrefWidth(44);
        col.setSortable(false);
        return col;
    }

    private TableCell<StandingEntry, Integer> colorIntCell(Team userTeam) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(value));
                    setAlignment(Pos.CENTER);
                    TableRow<?> row = getTableRow();
                    if (row != null && row.getItem() instanceof StandingEntry) {
                        StandingEntry entry = (StandingEntry) row.getItem();
                        setTextFill(entry.getTeam().equals(userTeam)
                                ? Color.web("#22c55e") : Color.web("#e5e7eb"));
                    } else {
                        setTextFill(Color.web("#e5e7eb"));
                    }
                }
            }
        };
    }

    private TableCell<StandingEntry, String> colorStringCell(Team userTeam) {
        return new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value);
                    TableRow<?> row = getTableRow();
                    if (row != null && row.getItem() instanceof StandingEntry) {
                        StandingEntry entry = (StandingEntry) row.getItem();
                        setTextFill(entry.getTeam().equals(userTeam)
                                ? Color.web("#22c55e") : Color.web("#e5e7eb"));
                        if (entry.getTeam().equals(userTeam))
                            setStyle("-fx-font-weight: bold;");
                        else
                            setStyle("");
                    }
                }
            }
        };
    }
}
