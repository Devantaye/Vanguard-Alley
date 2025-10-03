package game;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {
    private Clip clip;
    private float volume = 0.7f; // Add volume field, default volume is 70%

    /**
     * @param resourcePath e.g. "audio/music.wav" on your classpath
     */
    public AudioPlayer(String resourcePath) {
        try {
            // 1) Load from classpath
            URL url = getClass().getClassLoader().getResource(resourcePath);
            if (url == null) throw new IOException("Resource not found: " + resourcePath);

            // 2) Get raw AudioInputStream
            AudioInputStream in = AudioSystem.getAudioInputStream(url);

            // 3) Define a PCM‐signed format
            AudioFormat base = in.getFormat();
            AudioFormat pcm = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                base.getSampleRate(),
                16,
                base.getChannels(),
                base.getChannels() * 2,
                base.getSampleRate(),
                false
            );

            // 4) Convert to PCM
            AudioInputStream din = AudioSystem.getAudioInputStream(pcm, in);

            // 5) Read it fully into a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = din.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            byte[] audioBytes = baos.toByteArray();
            din.close();
            in.close();

            // 6) Open clip from byte[] (bypasses the <0 check)
            clip = AudioSystem.getClip();
            clip.open(pcm, audioBytes, 0, audioBytes.length);

            // Set the initial volume
            setVolume(volume);

            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    /** Start playback (if not already running) */
    public void play() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }

    /** Stop playback */
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    
    /** Set volume */
    public void setVolume(float volume) {
        this.volume = volume;
        if (clip != null) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
               // Convert linear volume to decibels
// The decibel range is typically -80.0f (silent) to 6.0f (loud)
// We map the linear volume range of 0.0-1.0 to this range
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                float dB = min + (max - min) * volume;
                gainControl.setValue(dB);
            } catch (IllegalArgumentException e) {
                
                try {
                    FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
                    float min = volumeControl.getMinimum();
                    float max = volumeControl.getMaximum();
                    float value = min + (max - min) * volume;
                    volumeControl.setValue(value);
                } catch (IllegalArgumentException ex) {
                    System.err.println("The audio system does not support volume control");
                }
            }
        }
    }
    
   /** Get the current volume */
    public float getVolume() {
        return volume;
    }
}
