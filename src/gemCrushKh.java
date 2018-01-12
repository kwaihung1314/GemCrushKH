/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import game.GameConsole;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;
import java.util.HashMap;
import javax.swing.ImageIcon;
/**
 *
 * @author kwaihung1314
 */
public class gemCrushKh
{
    private GameConsole gameKH = GameConsole.getInstance();
    private GameTimer time = GameTimer.getInstance();
    
    public static final int BOARD_SIZE = 8;
    // the upper-left corner of the board, reference origin point
    public static final int orgX = 240;
    public static final int orgY = 40;
    // the size of the gem
    public static final int w = 65;
    public static final int h = 65;
    
    public static final int animationTime = 200;
    public static final int animationFrequency = 20;
    
    gemKH[][] gem = new gemKH[BOARD_SIZE][BOARD_SIZE];
    gemKH[] selectedGem = new gemKH[2];
    private int Score;
    private boolean gameEnd = false;
    private boolean gamePause = false;
    private boolean gameGetready = true;
    private String remainTimeStr;
    
    private long pauseStartTime, pauseEndTime;
    
    private Sound selectSound = new Sound("/assets/select.wav");
    private Sound matchSound = new Sound("/assets/match.wav");
    private Sound fallSound = new Sound("/assets/fall.wav");
    private static Sound backgroundMusic = new Sound("/assets/music.wav");
    private Sound getReadySound = new Sound("/assets/readySetGo.wav");
    
    public gemCrushKh(){
        gameKH.setBackground("/assets/board.png");
        gameKH.show();
    }
    
    public static void main(String[] args){
        gemCrushKh Game = new gemCrushKh();
        backgroundMusic.playSound();
        while (true)
            Game.start();
        
    }
    
    public void start(){
        
        createInitGems();
        Score = 0;
        getreadyCount();
        
        time.start();
        
        while (true) {              
                // get whatever inputs
            Point point = gameKH.getClickedPoint();
            if (point != null) {
                if(!gameEnd){
                    if (point.x > orgX && point.y > orgY){
                        if(!gamePause)
                            gemClickedAction(point);
                    }
                    if (point.x > 60 && point.x < 90 && point.y > 350 && point.y < 380) {
                        pauseAction();
                    }
                }
                //restart
                if (point.x > 60 && point.x < 90 && point.y > 410 && point.y < 440) {
                    gameEnd = false;
                    gamePause = false;
                    gameGetready = true;
                    break;
                }
            }
            if(!gamePause && !gameEnd){
                if(time.getRemainTime() <= 0) {
                    gameEnd = true;
                    GamePaint();
                    gameKH.update();
                }
            }
                
                // refresh at the specific rate, default 25 fps
            if (gameKH.shouldUpdate()) {
                GamePaint();
                gameKH.update();
            }
                
            repeatMusic();
            // the idle time affects the no. of iterations per second which 
            // should be larger than the frame rate
            // for fps at 25, it should not exceed 40ms
            gameKH.idle(10);
        }        
    }
    
    public void getreadyCount(){
        getReadySound.playSound();
        //ready
        GamePaint();
        gameKH.drawImage(250, 235, new ImageIcon(this.getClass().getResource("/assets/ready.png")).getImage());
        gameKH.update();
        gameKH.idle(1000);
        //set
        GamePaint();
        gameKH.drawImage(375, 235, new ImageIcon(this.getClass().getResource("/assets/set.png")).getImage());
        gameKH.update();
        gameKH.idle(1000);
        //go
        GamePaint();
        gameKH.drawImage(380, 235, new ImageIcon(this.getClass().getResource("/assets/go.png")).getImage());
        gameKH.update();
        gameKH.idle(800);
        
        gameGetready = false;
    }
    
    public void tooglePause() {
        gamePause = !gamePause;
    }
    
    public void pauseAction() {
        long pauseDuration;
        tooglePause();
        //recording pause time and put to timer
        if(gamePause) {
            pauseStartTime = System.currentTimeMillis();
        }else {
            pauseEndTime = System.currentTimeMillis();
            pauseDuration = pauseEndTime - pauseStartTime;
            time.setPauseTime(pauseDuration);
        }
        
    }
    
    public static void playMusic() {
        backgroundMusic.playSound();
    }
    
    public void createInitGems() {
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                gem[j][i]=new gemKH(j,i);                                
            }
        }
        while(checkElimination(false)) {
            replaceGem(false);
        }
    }
    
    public void gemClickedAction(Point point) {
        int ptX = ((point.x - orgX) / w);
        int ptY = ((point.y - orgY) / h);
        
        //first select
        if (selectedGem[0] == null) {
            selectedGem[0] = gem[ptX][ptY];
            selectedGem[0].toggleFocus();
            selectSound.playSound();
            System.out.printf("gem(%d)(%d) is %s %n", ptX, ptY, selectedGem[0].selectStatus());
        }else { 
            if (selectedGem[0] == gem[ptX][ptY]) {
                selectedGem[0].toggleFocus();
                System.out.printf("gem(%d)(%d) is %s %n", ptX, ptY, selectedGem[0].selectStatus());
                selectedGem[0] = null;
            }else {
                //set another gem to be selected
                selectedGem[1] = gem[ptX][ptY];
                selectedGem[1].toggleFocus();
                selectSound.playSound();
                //check if two gems ajacent
                if (checkTwoSelectedGemAdj()) {
                    System.out.printf("gem(%d)(%d) is %s %n", ptX, ptY, selectedGem[1].selectStatus());
                    selectedGem[0].toggleFocus();
                    selectedGem[1].toggleFocus();
                    //swap two gems
                    swapGems();

                    selectedGem[0] = null;
                    selectedGem[1] = null;
                    //perform gem elimation and replace gems
                    while(checkElimination(true)) {
                        replaceGem(true);
                    }
                    
                }else {
                    System.out.printf("gem(%d)(%d) and gem(%d)(%d) cannot swap %n",selectedGem[0].getPosX(),
                    selectedGem[0].getPosY(),selectedGem[1].getPosX(),selectedGem[1].getPosY());
                    selectedGem[0].toggleFocus();
                    selectedGem[0] = selectedGem[1];
                    selectedGem[1] = null;
                }
            }
            
        }
        
    }
    
    public void displayGame(){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                gem[i][j].display();
            }
        }
    }

    public boolean checkTwoSelectedGemAdj(){
        
        boolean result = false;
        int x0 = selectedGem[0].getPosX();
        int y0 = selectedGem[0].getPosY();
        int x1 = selectedGem[1].getPosX();
        int y1 = selectedGem[1].getPosY();
        
        if((x0 == x1 + 1 || x0 == x1 - 1) && y0 == y1){
            result = true;
        }else if((y0 == y1 + 1 || y0 == y1 - 1) && x0 == x1){
            result = true;
        }
        
        return result;
    }
    public void swapGems() {
        int x0 = selectedGem[0].getPosX();
        int y0 = selectedGem[0].getPosY();
        int x1 = selectedGem[1].getPosX();
        int y1 = selectedGem[1].getPosY();
                   
        gemKH temp = selectedGem[0];
        
        //swap animation
        float moveAmt = w / animationFrequency;
        float count = 0;
        for(int a = 1; a <= animationFrequency; a++){
            count += moveAmt;
            gameKH.clear();
            if(x0 == x1){
                if(y0 > y1){
                    selectedGem[0].setMoveY(count * -1);
                    selectedGem[1].setMoveY(count);
                }
                if(y0 < y1){
                    selectedGem[0].setMoveY(count);
                    selectedGem[1].setMoveY(count * -1);
                }
            }
            if(y0 == y1){
                if(x0 > x1){
                    selectedGem[0].setMoveX(count * -1);
                    selectedGem[1].setMoveX(count);            
                }
                if(x0 < x1){
                    selectedGem[0].setMoveX(count);
                    selectedGem[1].setMoveX(count * -1);           
                }
            }
        
            GamePaint();
            gameKH.update();
            gameKH.idle(animationTime / animationFrequency);  
        }
        selectedGem[0].setMoveY(0);
        selectedGem[1].setMoveY(0);
        selectedGem[0].setMoveX(0);
        selectedGem[1].setMoveX(0);
        
        //exchange x,y pos
        selectedGem[0].setPosX(x1);
        selectedGem[0].setPosY(y1);
        selectedGem[1].setPosX(x0);
        selectedGem[1].setPosY(y0);
        //exchange ref
        gem[x0][y0] = gem[x1][y1];
        gem[x1][y1] = temp;
        
        System.out.printf("gem(%d)(%d) is swapped with gem(%d)(%d) %n",x0,y0,x1,y1);       
    }
    
    public void eliminateGem(gemKH gemToCheck, boolean playSound){
        int gemX=gemToCheck.getPosX();
        int gemY=gemToCheck.getPosY();
        //check right side
        if(gemX < 7) {
            int count = 1;
            for(int i = 1; i < BOARD_SIZE - gemX; i++) {
                if(gemToCheck.compareGem(gem[gemX + i][gemY])) {
                    count++;
                }else {
                    break;
                }
            }
            if(count >= 3) {
                for(int i = 0; i < count; i++) {
                    gem[gemX + i][gemY].setEliminate(true);
                    Score += gem[gemX + i][gemY].getScore();
                }
                if(playSound)
                matchSound.playSound();
            }
        }
            
        //check downside
        if(gemY < 7) {
            int count = 1;
            for(int i = 1; i < BOARD_SIZE - gemY; i++) {
                if(gemToCheck.compareGem(gem[gemX][gemY + i])) {
                    count++;
                }else {
                    break;
                }
            }
            if(count >= 3) {
                for(int i = 0; i < count; i++) {
                    gem[gemX][gemY + i].setEliminate(true);
                    Score += gem[gemX][gemY + i].getScore();
                }
                if(playSound)
                matchSound.playSound();
            }
        }
    }
    
    public boolean checkElimination(boolean playSound) {
        boolean Done = false;
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j = 0; j < BOARD_SIZE; j++) {
                eliminateGem(gem[j][i], playSound);
                if(gem[j][i].shouldEliminate()) {
                    System.out.print(gem[j][i].getColor()+"\t");
                    Done = true;
                }else {
                    System.out.print("O"+"\t");
                }
            }
            System.out.println("");
        }
        System.out.println("");
        return Done;
    }
//    
    public void replaceGem(boolean Animation) {
        int[] ColNewGemNum = new int[BOARD_SIZE];
        ArrayList<ArrayList<gemKH>> ColNewGem = new ArrayList<>(BOARD_SIZE);
        ArrayList<ArrayList<Integer>> replaceGem = new ArrayList<>(BOARD_SIZE);
        
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(gem[j][i].getColor() + "\t");
            }
            System.out.println();
        }
        
        for(int i = 0; i < BOARD_SIZE; i++) {
            int count = 0;
            ArrayList<Integer> replaceGemCor = new ArrayList<>();
            for(int j = 0; j < BOARD_SIZE; j++) {
                if(gem[i][j].shouldEliminate()) {
                    count++;
                    replaceGemCor.add(j);
                }
            }
            ColNewGemNum[i] = count;
            ColNewGem.add(new ArrayList<>());
            for(int k = 1; k <= count; k++) {
                ColNewGem.get(i).add(new gemKH(i,k*-1));
            }
            replaceGem.add(replaceGemCor);
        }
        //display 
        System.out.println("ColNewGemNum:");
        for(int i = 0; i < BOARD_SIZE; i++) {
            System.out.print(ColNewGemNum[i] + "\t");
        }
        System.out.println();
        System.out.println("ColNewGem:");
        for(int i = 0; i < BOARD_SIZE; i++) {
            System.out.print(ColNewGem.get(i).size() + "\t");
        }
        System.out.println();
        System.out.println("ReplaceGem Cor:");
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(Integer Cor : replaceGem.get(i)) {
                System.out.print(Cor + ","); 
            }
            System.out.print("\t");
        }
        System.out.println();
        
        
        ArrayList<HashMap<Integer, Integer>> dropList = new ArrayList<>();
        for(int i = 0; i < BOARD_SIZE; i++) {
            HashMap<Integer, Integer> Map = new HashMap<Integer, Integer>();
            
            for(int j = 0 - ColNewGemNum[i]; j < BOARD_SIZE; j++) {
                int dropValue;
                
                if(j < 0) {
                    dropValue = replaceGem.get(i).size();
                }else {
                    int index = -1;
                    for(int k = replaceGem.get(i).size() - 1; k >= 0; k--) {
                        if(j == replaceGem.get(i).get(k)) {
                            index = -1;
                            break;
                        }
                        if(j < replaceGem.get(i).get(k)) {
                            index = k;
                        }
                    }
                    if(index < 0) {
                        dropValue = 0;
                    }else {
                        dropValue = replaceGem.get(i).size() - index;
                    }
                }
                
                Map.put(j, dropValue);
            }
            
            dropList.add(Map);
        }
        
        for(int i = 0; i < BOARD_SIZE; i++) {
            System.out.println("droplist"+i);
            for(HashMap.Entry<Integer, Integer> map : dropList.get(i).entrySet()) {
                System.out.println(map); 
            }
        }
        System.out.println();
        System.out.println();
        
        if(Animation) {
            for(int a = 1; a <= animationFrequency; a++) {
                gameKH.clear();
                for(int i = 0; i < BOARD_SIZE; i++) {
                    for(int j = 0 - ColNewGemNum[i]; j < BOARD_SIZE; j++) {
                        float moveAmt = dropList.get(i).get(j) * w / animationFrequency;
                        if(j < 0) {
                            ColNewGem.get(i).get(j*-1 - 1).setMoveY(moveAmt * a);
                        }else {
                            gem[i][j].setMoveY(moveAmt * a);
                        }
                    }
                }
                GamePaint();
                for(int i = 0; i < BOARD_SIZE; i++) {
                    for (gemKH get : ColNewGem.get(i)) {
                        get.display();
                    }
                }
                gameKH.update();
                gameKH.idle(animationTime / animationFrequency);
            }
            fallSound.playSound();
            for(int i = 0; i < BOARD_SIZE; i++) {
                for(int j = 0; j < BOARD_SIZE; j++) {
                    gem[i][j].setMoveY(0);
                }
                for (gemKH get : ColNewGem.get(i)) {
                    get.setMoveY(0);
                }
            }
        }
        
        //exchange position and ref
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j = 0 - ColNewGemNum[i]; j < BOARD_SIZE; j++) {
                if(j < 0) {
                    ColNewGem.get(i).get(j*-1 - 1).setPosY(j + dropList.get(i).get(j));
                }else {
                    gem[i][j].setPosY(j + dropList.get(i).get(j));
                }
            }
        }
        
        for(int i = 0; i < BOARD_SIZE; i++) {
            
            for(int j = BOARD_SIZE - 1; j >= 0; j--) {
                if(dropList.get(i).get(j) > 0) {
                    gem[i][j + dropList.get(i).get(j)] = gem[i][j];
                }
            }
            for(int j = 0; j < ColNewGemNum[i]; j++) {
                gem[i][j] = ColNewGem.get(i).get(ColNewGemNum[i] - 1 - j);
            }
        }
        
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(gem[j][i].getColor() + "\t");
            }
            System.out.println();
        }
        
    }
    
    public void GamePaint() {
        gameKH.clear();
        
        displayGame();
        
        gameKH.drawText(60, 150, "[TIME]", new Font("Helvetica", Font.BOLD, 20), Color.white);
        
        if(gameEnd) {
            gameKH.drawText(60, 180, "00:00:00", new Font("Helvetica", Font.PLAIN, 20), Color.white);
            gameKH.drawImage(220, 20, new ImageIcon(this.getClass().getResource("/assets/gemBlur.png")).getImage());
            gameKH.drawImage(250, 235, new ImageIcon(this.getClass().getResource("/assets/timeup.png")).getImage());
        }else {
            if(!gamePause) {
                if(gameGetready) {
                    gameKH.drawText(60, 180, "00:30:00", new Font("Helvetica", Font.PLAIN, 20), Color.white);
                    gameKH.drawImage(220, 20, new ImageIcon(this.getClass().getResource("/assets/gemBlur.png")).getImage());
                }else {
                    remainTimeStr = time.getTimeString();
                    gameKH.drawText(60, 180, remainTimeStr, new Font("Helvetica", Font.PLAIN, 20), Color.white);
                }
            }else {
                gameKH.drawText(60, 180, remainTimeStr, new Font("Helvetica", Font.PLAIN, 20), Color.white);
                gameKH.drawImage(220, 20, new ImageIcon(this.getClass().getResource("/assets/gemBlur.png")).getImage());
                gameKH.drawImage(250, 235, new ImageIcon(this.getClass().getResource("/assets/paused.png")).getImage());
            }
        }     
        gameKH.drawText(60, 250, "[SCORE]", new Font("Helvetica", Font.BOLD, 20), Color.white);
        gameKH.drawText(60, 280, String.valueOf(Score), new Font("Helvetica", Font.PLAIN, 20), Color.white);
        
        gameKH.drawImage(60, 350, new ImageIcon(this.getClass().getResource("/assets/pause.png")).getImage());
        
        gameKH.drawImage(60, 410, new ImageIcon(this.getClass().getResource("/assets/restart.png")).getImage());
        
        
        
    }
    
    public void repeatMusic() {
        if(backgroundMusic.isSoundEnd())
            backgroundMusic.playSound();
    }
    
}
