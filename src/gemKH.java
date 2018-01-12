
import java.awt.Image;
import java.util.Random;
import javax.swing.ImageIcon;
import game.GameConsole;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kwaihung1314
 */
public class gemKH
{
    // the upper-left corner of the board, reference origin point
    public static final int orgX = 240;
    public static final int orgY = 40;
    // the size of the gem
    public static final int w = 65;
    public static final int h = 65;
    
    public static final int extraW = 10;
    public static final int extraH = 10;
    // default position in 8x8 grid    
    private int posX = 0;
    private int posY = 0;
    private float moveX = 0;
    private float moveY = 0;
    private boolean selected = false;
            
    private Image pic;
    private Image focus;
    private Image bonus;
    private String color;
    private int gemScore = 10;
    
    private boolean eliminate = false;
    
    private boolean plus = false;
    
    public gemKH(int x,int y){
        this.posX = x;
        this.posY = y;
        Random rdn1 = new Random();
        Random rdn2 = new Random();
        //assign gem color
        switch(rdn1.nextInt(7)){
            case 0: 
                this.pic = new ImageIcon(this.getClass().getResource("/assets/gemBlue.png")).getImage();
                this.color = "Blue";
                break;
            case 1:
                this.pic = new ImageIcon(this.getClass().getResource("/assets/gemGreen.png")).getImage();
                this.color = "Green";
                break;
            case 2:
                this.pic = new ImageIcon(this.getClass().getResource("/assets/gemOrange.png")).getImage();
                this.color = "Orange";
                break;
            case 3:
                this.pic = new ImageIcon(this.getClass().getResource("/assets/gemPurple.png")).getImage();
                this.color = "Purple";
                break;
            case 4:
                this.pic = new ImageIcon(this.getClass().getResource("/assets/gemRed.png")).getImage();
                this.color = "Red";
                break;
            case 5:
                this.pic = new ImageIcon(this.getClass().getResource("/assets/gemWhite.png")).getImage();
                this.color = "White";
                break;
            case 6:
                this.pic = new ImageIcon(this.getClass().getResource("/assets/gemYellow.png")).getImage();
                this.color = "Yellow";
                break;
        }
        //assign gem plus
        int plusRam = rdn2.nextInt(8);
        if(plusRam == 0) {
            this.plus = true;
            this.gemScore = 30;
        }
        this.focus = new ImageIcon(this.getClass().getResource("/assets/focus.png")).getImage();
        this.bonus = new ImageIcon(this.getClass().getResource("/assets/gemSp.png")).getImage();
    }
    
    public void display() {
        if(eliminate == false){
            GameConsole.getInstance().drawImage((int)(posX * w + orgX + moveX), (int)(posY * h + orgY + moveY), pic);
            if(selected)
                GameConsole.getInstance().drawImage((int)(posX * w + orgX), (int)(posY * h + orgY), focus);
            if(plus)
                GameConsole.getInstance().drawImage((int)(posX * w + orgX + extraW + moveX), (int)(posY * h + orgY + extraH + moveY), bonus);
        }
    }
    
    public String getColor(){
        return color;
    }
    
    public void toggleFocus() {
        selected = !selected;
    }
    
    public boolean shouldEliminate(){
        return eliminate;
    }
    
    public void setEliminate(boolean Eliminate){
        this.eliminate = Eliminate;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
    
    public void setMoveX(float x) {
        this.moveX = x;
    }
    
    public float getMoveX() {
        return moveX;
    }
    
    public void setMoveY(float y) {
        this.moveY = y;
    }
    
    public float getMoveY() {
        return moveY;
    }
    
    public Image getPic() {
        return pic;
    }

    public void setPic(String file) {
        this.pic = new ImageIcon(this.getClass().getResource(file)).getImage();
    }
    
    public boolean compareGem(gemKH g){
        boolean sameGem = false;
        if(this.color.equals(g.color)){
            sameGem = true;
        }
        return sameGem;
    }
    
    public String selectStatus(){
        String Status = "";
        if(selected)
            Status = "selected";
        if(!selected)
            Status = "unselected";
        return Status;
    }
    
    public int getScore() {
        return gemScore;
    }
    
}
