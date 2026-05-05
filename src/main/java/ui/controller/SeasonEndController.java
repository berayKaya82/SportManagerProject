package ui.controller;

import application.GameFacade;
import domain.StandingEntry;
import domain.Team;
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
import ui.SoundManager;

import java.util.List;

public class SeasonEndController {

    private final GameFacade facade;

    public SeasonEndController(GameFacade facade) {
        this.facade = facade;
    }

    public Parent getRoot() {
        SoundManager.getInstance().playChampion();
        VBox root = new VBox();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #ff7b00, #ffe97f);");

        VBox body = new VBox(20);
        body.setPadding(new Insets(24, 32, 28, 32));
        body.setAlignment(Pos.CENTER);
        body.getChildren().addAll(
                buildTop5Card(),
                buildButtons()
        );

        root.getChildren().addAll(buildChampionHeader(), body);
        return root;
    }

    private VBox buildChampionHeader() {
        Team champion = facade.getSeasonChampion();
        String championName = champion != null ? champion.getName().toUpperCase() : "UNKNOWN";

        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 36 28 28 28;" +
                "-fx-border-color: rgba(255,255,255,0.4); -fx-border-width: 0 0 2 0;"
        );

        Label seasonLabel = new Label("SEASON " + facade.getCurrentSeasonNumber() + " COMPLETE");
        seasonLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        seasonLabel.setTextFill(Color.web("#d00000"));

        Label trophyLabel = new Label("★  CHAMPION  ★");
        trophyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        trophyLabel.setStyle(
                "-fx-background-color: rgba(255,255,255,0.85); -fx-text-fill: #d00000;" +
                "-fx-padding: 5 18; -fx-background-radius: 20;"
        );

        Label championLabel = new Label(championName);
        championLabel.setFont(Font.font("Arial", FontWeight.BOLD, 54));
        championLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-effect: dropshadow(gaussian, #c2410c, 28, 0.8, 0, 0);"
        );

        header.getChildren().addAll(seasonLabel, trophyLabel, championLabel);
        return header;
    }

    private VBox buildTop5Card() {
        List<StandingEntry> standings = facade.getStandings();
        Team userTeam = facade.getUserTeam();
        Team champion = facade.getSeasonChampion();

        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.25);" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: rgba(255,255,255,0.6);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 16;" +
                "-fx-padding: 20 24;"
        );

        Label title = new Label("TOP 5 FINAL STANDINGS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        title.setTextFill(Color.web("#d00000"));
        title.setPadding(new Insets(0, 0, 8, 0));
        card.getChildren().add(title);

        int limit = Math.min(5, standings.size());
        boolean userInTop5 = false;

        for (int i = 0; i < limit; i++) {
            StandingEntry entry = standings.get(i);
            boolean isUser  = entry.getTeam().equals(userTeam);
            boolean isChamp = champion != null && entry.getTeam().equals(champion);
            if (isUser) userInTop5 = true;
            card.getChildren().add(buildRankRow(i + 1, entry, isUser, isChamp));
        }

        if (!userInTop5) {
            int userPos = facade.getUserTeamPosition();
            if (userPos > 5 && userPos <= standings.size()) {
                Label divider = new Label("· · ·");
                divider.setFont(Font.font("Arial", 13));
                divider.setTextFill(Color.web("#d00000"));
                divider.setPadding(new Insets(4, 0, 4, 0));
                divider.setMaxWidth(Double.MAX_VALUE);
                divider.setAlignment(Pos.CENTER);
                card.getChildren().add(divider);

                StandingEntry userEntry = standings.get(userPos - 1);
                card.getChildren().add(buildRankRow(userPos, userEntry, true, false));
            }
        }

        return card;
    }

    private HBox buildRankRow(int pos, StandingEntry entry, boolean isUser, boolean isChampion) {
        String[] palette = rankPalette(pos, isUser, isChampion);
        String numColor  = palette[0];
        String nameColor = palette[1];
        String bgColor   = palette[2];
        String border    = palette[3];

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + border + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;"
        );

        double rankFontSize = (pos == 1) ? 26 : 20;
        Label rankLbl = new Label(rankPrefix(pos));
        rankLbl.setFont(Font.font("Arial", FontWeight.BOLD, rankFontSize));
        rankLbl.setTextFill(Color.web(numColor));
        rankLbl.setMinWidth(54);
        rankLbl.setAlignment(Pos.CENTER);

        Label nameLbl = new Label(entry.getTeam().getName().toUpperCase());
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, (pos == 1) ? 18 : 14));
        nameLbl.setTextFill(Color.web(nameColor));
        HBox.setHgrow(nameLbl, Priority.ALWAYS);
        nameLbl.setMaxWidth(Double.MAX_VALUE);

        Label ptsLbl = new Label(entry.getPoints() + " pts");
        ptsLbl.setFont(Font.font("Arial", FontWeight.BOLD, (pos == 1) ? 16 : 13));
        ptsLbl.setTextFill(Color.web(numColor));

        row.getChildren().addAll(rankLbl, nameLbl, ptsLbl);
        return row;
    }

    private String rankPrefix(int pos) {
        return switch (pos) {
            case 1 -> "1st";
            case 2 -> "2nd";
            case 3 -> "3rd";
            default -> pos + "th";
        };
    }

    // Returns: [numColor, nameColor, bgColor, borderColor]
    private String[] rankPalette(int pos, boolean isUser, boolean isChampion) {
        if (isChampion || pos == 1)
            return new String[]{"#92400e", "#3d1a00", "#f9844a", "#ff6b6b"};
        if (pos == 2)
            return new String[]{"#78350f", "#3d1a00", "#f9844a", "#f9844a"};
        if (pos == 3)
            return new String[]{"#92400e", "#3d1a00", "#f9844a", "#f9844a"};
        if (isUser)
            return new String[]{"#15803d", "#166534", "#f9844a", "#86efac"};
        return new String[]{"#7c2d12", "#5c2a00", "#f9844a", "#f9844a"};
    }

    private HBox buildButtons() {
        Button newSeasonBtn = new Button("NEW SEASON  →");
        newSeasonBtn.getStyleClass().add("btn-primary");
        newSeasonBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        newSeasonBtn.setOnAction(e -> {
            facade.startNewSeason();
            SceneManager.getInstance().switchTo("dashboard", facade);
        });

        Button saveExitBtn = new Button("SAVE & EXIT");
        saveExitBtn.getStyleClass().add("btn-purple");
        saveExitBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        saveExitBtn.setOnAction(e -> {
            List<String> slots = facade.getSaveSlotInfo();
            javafx.scene.control.ChoiceDialog<String> dialog =
                    new javafx.scene.control.ChoiceDialog<>(slots.get(0), slots);
            dialog.setTitle("Save Game");
            dialog.setHeaderText("Choose a slot to save");
            dialog.setContentText("Slot:");
            dialog.showAndWait().ifPresent(chosen -> {
                int slotId = slots.indexOf(chosen) + 1;
                try {
                    facade.saveGame(slotId);
                    SceneManager.getInstance().switchTo("main-menu", facade);
                } catch (Exception ex) {
                    new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.ERROR,
                            "Save failed: " + ex.getMessage()
                    ).showAndWait();
                }
            });
        });

        Button exitBtn = new Button("EXIT");
        exitBtn.getStyleClass().add("btn-red");
        exitBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        exitBtn.setOnAction(e -> javafx.application.Platform.exit());

        SoundManager.getInstance().wire(newSeasonBtn, saveExitBtn, exitBtn);

        HBox box = new HBox(14, newSeasonBtn, saveExitBtn, exitBtn);
        box.setAlignment(Pos.CENTER);
        return box;
    }
}
