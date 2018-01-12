/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kwaihung1314
 */

import java.io.*;
import sun.audio.*;


    
public class Sound
{

    private String soundPath;
    
    private InputStream inputStream;
    private AudioStream audioStream;

    Sound(String soundPath) {
        this.soundPath = soundPath;
    }

    public void playSound() {
        try {
            inputStream = getClass().getResourceAsStream(soundPath);
            audioStream = new AudioStream(inputStream);
            AudioPlayer.player.start(audioStream);
        } catch (Exception e) {
            System.out.println("Sound cannot play");
        }
    }
    
    public void stopSound() {
        AudioPlayer.player.stop(audioStream);
    }
    
    public boolean isSoundEnd() {
        boolean flag = false;
        try{
            if(audioStream.available() == 0)
                flag = true;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return flag;
        
    }
    
    public void setSound(String soundPath) {
        this.soundPath = soundPath;
    }
    
    public String getSound() {
        return soundPath;
    }
 
}

