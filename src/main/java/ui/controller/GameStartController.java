package ui.controller;

import application.GameFacade;
import domain.Gender;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import sport.ISport;
import ui.SceneManager;

public class GameStartController {

    private final GameFacade facade;

    public GameStartController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0e1a;");

        root.setTop(buildHeader());
        root.setCenter(buildForm());

        return root;
    }

    // ── Two-column form filling the whole center ─────────────────────────────
    //
    // The form lives in a 2-column GridPane where each column is locked to
    // exactly 50% of the available width via ColumnConstraints#setPercentWidth.
    // We deliberately do NOT use HBox + Hgrow here: with HBox the cards are
    // sized as (preferredWidth + share of leftover space), and because the two
    // cards have different preferred widths (left: name fields; right: combos +
    // spacer + button), opening the Sport ComboBox triggers a layout pass that
    // re-evaluates preferred sizes and visibly shifts the cards. With percent
    // columns the cards' widths are independent of any child's preferred size,
    // so dropdown opening cannot reshape the layout.

    // ── Header bar (same pattern as Training / Roster) ───────────────────────

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #052e16, #0a1a0f, #0a0e1a);" +
            "-fx-padding: 20 28 16 28;"
        );

        VBox titleBlock = new VBox(4);
        Label title = new Label("NEW GAME");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);
        Label subtitle = new Label("Set up your team and get started");
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        subtitle.setTextFill(Color.web("#4ade80"));
        titleBlock.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("← Main Menu");
        backBtn.getStyleClass().addAll("btn", "btn-secondary");
        backBtn.setOnAction(e -> SceneManager.getInstance().switchTo("main-menu", facade));

        header.getChildren().addAll(titleBlock, spacer, backBtn);
        return header;
    }

    private GridPane buildForm() {
        // Fields
        TextField managerField = new TextField();
        managerField.setPromptText("e.g. Alex Ferguson");
        managerField.setMaxWidth(Double.MAX_VALUE);

        TextField teamField = new TextField();
        teamField.setPromptText("e.g. Manchester United");
        teamField.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> sportBox = new ComboBox<>();
        sportBox.getItems().addAll("FOOTBALL", "HANDBALL");
        sportBox.setValue("FOOTBALL");
        sportBox.setMaxWidth(Double.MAX_VALUE);
        // Don't let the popup's content widen the control's preferred size.
        sportBox.setMinWidth(0);

        ComboBox<Gender> genderBox = new ComboBox<>();
        genderBox.getItems().addAll(Gender.values());
        genderBox.setValue(Gender.MALE);
        genderBox.setMaxWidth(Double.MAX_VALUE);
        genderBox.setMinWidth(0);

        // Left column — identity fields
        VBox leftCard = card();
        leftCard.setMaxWidth(Double.MAX_VALUE);
        leftCard.getChildren().addAll(
                sectionLabel("MANAGER DETAILS"),
                formRow("Manager Name", managerField),
                formRow("Team Name",    teamField)
        );

        // Right column — match settings + start button
        Button startBtn = new Button("Start Game →");
        startBtn.getStyleClass().addAll("btn", "btn-primary");
        startBtn.setMaxWidth(Double.MAX_VALUE);
        startBtn.setOnAction(e -> {
            String managerName = managerField.getText().trim();
            String teamName    = teamField.getText().trim();
            Gender gender      = genderBox.getValue();

            if (managerName.isEmpty() || teamName.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please fill in all fields.").showAndWait();
                return;
            }

            ISport sport = sportBox.getValue().equals("HANDBALL")
                    ? new handball.HandballSport()
                    : new football.FootballSport();

            facade.startNewGame(managerName, teamName, gender, sport);
            SceneManager.getInstance().switchTo("dashboard", facade);
        });

        VBox rightCard = card();
        rightCard.setMaxWidth(Double.MAX_VALUE);
        Region rightSpacer = new Region();
        VBox.setVgrow(rightSpacer, Priority.ALWAYS);
        rightCard.getChildren().addAll(
                sectionLabel("MATCH SETTINGS"),
                formRow("Sport",         sportBox),
                formRow("League Gender", genderBox),
                rightSpacer,
                startBtn
        );

        // Outer grid — two columns, each pinned to 50% of the available width.
        // GridPane percent columns ignore children's preferred widths, which is
        // what prevents the ComboBox dropdown from re-shuffling the cards.
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setPadding(new Insets(28));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);
        col2.setFillWidth(true);

        grid.getColumnConstraints().addAll(col1, col2);

        // Single row that fills any remaining vertical space so the right
        // card's spacer can push Start Game to the bottom.
        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        row.setFillHeight(true);
        grid.getRowConstraints().add(row);

        // Make sure each card stretches to fill its column / row cell.
        GridPane.setHgrow(leftCard,  Priority.ALWAYS);
        GridPane.setHgrow(rightCard, Priority.ALWAYS);
        GridPane.setVgrow(leftCard,  Priority.ALWAYS);
        GridPane.setVgrow(rightCard, Priority.ALWAYS);
        GridPane.setFillWidth(leftCard,  true);
        GridPane.setFillWidth(rightCard, true);
        GridPane.setFillHeight(leftCard,  true);
        GridPane.setFillHeight(rightCard, true);

        grid.add(leftCard,  0, 0);
        grid.add(rightCard, 1, 0);
        return grid;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private VBox card() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(24));
        card.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #1f2937;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );
        return card;
    }

    private Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("section-header-green");
        lbl.setPadding(new Insets(0, 0, 4, 0));
        return lbl;
    }

    private VBox formRow(String labelText, Control field) {
        VBox row = new VBox(6);
        Label lbl = new Label(labelText.toUpperCase());
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web("#6b7280"));
        row.getChildren().addAll(lbl, field);
        return row;
    }
}
