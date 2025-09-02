/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;


import java.io.File;
import javax.sound.sampled.*;

/**
 *
 * @author rocka
 */
public class ClickSound 
{    
    public static void play() 
    {
        try 
        {
            File soundFile = new File("res/click.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}
