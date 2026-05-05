package ui;

import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static SoundManager instance;

    private final Map<String, Media> sounds = new HashMap<>();

    private SoundManager() {
        load("button",    "/sounds/button.m4a");
        load("victory",   "/sounds/victory.m4a");
        load("draw",      "/sounds/draw.m4a");
        load("game-over", "/sounds/game-over.m4a");
        load("champion",  "/sounds/champion.m4a");
        load("vs",        "/sounds/vs.m4a");
    }

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    public void wire(Button... buttons) {
        for (Button btn : buttons) {
            btn.setOnMouseEntered(e -> playButton());
        }
    }

    public void playButton()    { play("button"); }
    public void playVictory()   { play("victory"); }
    public void playDraw()      { play("draw"); }
    public void playGameOver()  { play("game-over"); }
    public void playChampion()  { play("champion"); }
    public void playMatchStart(){ play("vs"); }

    private void play(String key) {
        Media media = sounds.get(key);
        if (media == null) return;
        try {
            MediaPlayer player = new MediaPlayer(media);
            player.setOnEndOfMedia(player::dispose);
            player.play();
        } catch (Exception e) {
            System.err.println("[SoundManager] play error: " + key + " | " + e.getMessage());
        }
    }

    private void load(String key, String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return;
            sounds.put(key, new Media(url.toExternalForm()));
        } catch (Exception e) {
            System.err.println("[SoundManager] load failed: " + path + " | " + e.getMessage());
        }
    }
}
