package game.audio;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import game.app.GameConfig;

public class ClickSound {
    /** Plays click.wav from resources/audio/click.wav */
    public static void play() {
        URL url = ClickSound.class.getResource(GameConfig.CLICK_SFX_PATH);
        if (url == null) {
            System.err.println("Click sound not found: /audio/click.wav");
            return;
        }

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(url)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}

