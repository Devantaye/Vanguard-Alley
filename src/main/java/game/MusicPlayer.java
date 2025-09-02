/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author rocka
 */
public class MusicPlayer 
{
    private Clip clip;
    private FloatControl volumeControl;

    public void init(String filePath) 
    {
        try 
        {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // play in loop
            clip.start();
            
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) 
            {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }
        } 
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) 
        {
            e.printStackTrace();
        }
    }

    // Set volume between 0.0 (mute) and 1.0 (max)
    public void setVolume(float level) 
    {
        if (volumeControl != null) 
        {
            float min = volumeControl.getMinimum(); // usually -80.0
            float max = volumeControl.getMaximum(); // usually 6.0
            float gain = min + (max - min) * level;
            volumeControl.setValue(gain);
        }
    }
}
