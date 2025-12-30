package Main.Java.Notes;

import Main.Java.Utils.Constants;

import java.awt.*;

public class Note {

    private int lane;
    private int x;
    private int y;
    private final int SPEED = 3;

    public Note(int x) {
        this.lane = lane;
        this.x = Constants.LANE_X[lane];
        this.y = Constants.NOTE_Y_START;
    }

    public void update() {
        y += SPEED;
    }

    public void draw(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillOval(x, y, 30, 30);
    }

    public int getY(){
        return y;
    }

    public int getX(){
        return lane;
    }
}

