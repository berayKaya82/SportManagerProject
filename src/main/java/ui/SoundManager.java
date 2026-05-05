package ui;

import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static SoundManager instance;

    private final Map<String, AudioClip> clips = new HashMap<>();
    private String matchStartUrl;

    private SoundManager() {
        load("button",    "/sounds/button.wav");
        load("victory",   "/sounds/victory.wav");
        load("draw",      "/sounds/draw.wav");
        load("game-over", "/sounds/game-over.wav");
        load("champion",  "/sounds/champion.wav");

        URL url = getClass().getResource("/sounds/vs.wav");
        if (url != null) matchStartUrl = url.toExternalForm();
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

    public void playButton()  { play("button"); }
    public void playVictory() { play("victory"); }
    public void playDraw()    { play("draw"); }
    public void playGameOver(){ play("game-over"); }
    public void playChampion(){ play("champion"); }

    public void playMatchStart() {
        if (matchStartUrl == null) return;
        try {
            MediaPlayer player = new MediaPlayer(new Media(matchStartUrl));
            player.setOnEndOfMedia(player::dispose);
            player.play();
        } catch (Exception e) {
            System.err.println("[SoundManager] playMatchStart error: " + e.getMessage());
        }
    }

    private void play(String key) {
        AudioClip clip = clips.get(key);
        if (clip != null) clip.play();
    }

    private void load(String key, String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return;
            clips.put(key, new AudioClip(url.toExternalForm()));
        } catch (Exception e) {
            System.err.println("[SoundManager] load failed: " + path + " | " + e.getMessage());
        }
    }

}


