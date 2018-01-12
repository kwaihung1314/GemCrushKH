/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kwaihung1314
 */
public class GameTimer
{
    private long initTime;
    private static GameTimer GameTime = new GameTimer();
    private long pauseTime = 0;
    
    public void GameTimer() {}
    
    public static GameTimer getInstance() {
        return GameTime;
    }

    public void start() {
        initTime = System.currentTimeMillis() + 30000;
    }

    public String getTimeString() {

        int time = (int) (initTime + pauseTime - System.currentTimeMillis());
        int hour = (time / 1000) / 3600;
        int minute = (time / 1000) % 3600 / 60;
        int second = (time / 1000) % 3600 % 60;
        int milSec =  time % 1000 / 10;
        
        if(time > 0) {
            return String.format("%02d:%02d:%02d", minute, second, milSec);
        }else {
            return "00:00:00";
        }
    }
    
    public int getRemainTime() {
        int time = (int) (initTime + pauseTime - System.currentTimeMillis());
        return time;
    }
    
    public void setPauseTime(long lastPauseTime) {
        pauseTime += lastPauseTime;
    }
}
