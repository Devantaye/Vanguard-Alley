/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.audio;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MusicPlayer {
    private Clip clip;
    private FloatControl volumeControl;

    /** Load and play a sound resource from src/main/resources */
    public void initResource(String resourcePath) {
        stop(); // stop any previous clip

        // Look inside classpath (src/main/resources)
        URL url = MusicPlayer.class.getResource(resourcePath);
        if (url == null) {
            System.err.println("Could not find resource: " + resourcePath);
            return;
        }

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(url)) {
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            stop();
        }
    }

    /** Set volume between 0.0 (mute) and 1.0 (max) */
    public void setVolume(float level) {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum(); // usually -80.0
            float max = volumeControl.getMaximum(); // usually +6.0
            float gain = min + (max - min) * Math.max(0f, Math.min(1f, level));
            volumeControl.setValue(gain);
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
            volumeControl = null;
        }
    }
}
