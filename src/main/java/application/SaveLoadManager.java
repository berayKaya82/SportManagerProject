package application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SaveLoadManager {

    private static final int MAX_SLOTS = 3;
    private final Gson gson;
    private final Path saveDirectory;

    public SaveLoadManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.saveDirectory = Path.of(System.getProperty("user.home"), "SportManager", "saves");
    }

    public void saveGame(GameState state, int slotId) throws IOException {
        validateSlotId(slotId);
        Files.createDirectories(saveDirectory);
        Path file = saveDirectory.resolve("slot_" + slotId + ".json");
        String json = gson.toJson(state);
        Files.writeString(file, json);
    }

    public GameState loadGame(int slotId) throws IOException {
        validateSlotId(slotId);
        Path file = saveDirectory.resolve("slot_" + slotId + ".json");
        if (!Files.exists(file)) {
            throw new IllegalStateException("No save found in slot " + slotId);
        }
        String json = Files.readString(file);
        return gson.fromJson(json, GameState.class);
    }

    public boolean slotExists(int slotId) {
        if (slotId < 1 || slotId > MAX_SLOTS) return false;
        return Files.exists(saveDirectory.resolve("slot_" + slotId + ".json"));
    }

    public List<String> getSaveSlotInfo() {
        List<String> info = new ArrayList<>();
        for (int i = 1; i <= MAX_SLOTS; i++) {
            if (slotExists(i)) {
                try {
                    GameState state = loadGame(i);
                    info.add("Slot " + i + ": " + state.getTeamName()
                            + " | " + state.getSportName()
                            + " | Season " + state.getSeasonNumber()
                            + " | Week " + state.getCurrentWeek()
                            + " | " + state.getSaveDate());
                } catch (IOException e) {
                    info.add("Slot " + i + ": [Error reading save]");
                }
            } else {
                info.add("Slot " + i + ": Empty");
            }
        }
        return info;
    }

    private void validateSlotId(int slotId) {
        if (slotId < 1 || slotId > MAX_SLOTS) {
            throw new IllegalArgumentException("Slot ID must be between 1 and " + MAX_SLOTS);
        }
    }
}
