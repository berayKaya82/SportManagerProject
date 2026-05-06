package ui.controller;

import application.GameFacade;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

import java.util.List;

public final class SaveSlotDialogs {

    private SaveSlotDialogs() {}

    public static void promptDeleteSave(GameFacade facade) {
        List<String> slots = facade.getSaveSlotInfo();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(slots.get(0), slots);
        dialog.setTitle("Delete Save");
        dialog.setHeaderText("Select a slot to clear (cannot be undone)");
        dialog.setContentText("Slot:");
        dialog.showAndWait().ifPresent(chosen -> {
            if (chosen.contains(": Empty")) {
                new Alert(Alert.AlertType.INFORMATION, "This slot is already empty.").showAndWait();
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm delete");
            confirm.setHeaderText("Delete this save permanently?");
            confirm.setContentText(chosen);
            confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(btn -> {
                if (btn != ButtonType.YES) {
                    return;
                }
                int slotId = slots.indexOf(chosen) + 1;
                try {
                    facade.deleteSaveGame(slotId);
                    new Alert(Alert.AlertType.INFORMATION, "Slot " + slotId + " cleared.").showAndWait();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Delete failed: " + ex.getMessage()).showAndWait();
                }
            });
        });
    }
}
