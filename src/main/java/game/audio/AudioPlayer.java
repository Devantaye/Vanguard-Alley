package game.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {
    private Clip clip;

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

// 🔉 Volume control (e.g., -10.0f is about 70% volume)
FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
volumeControl.setValue(-10.0f); // You can tweak this to -6.0f or -15.0f depending on your preference

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
}



