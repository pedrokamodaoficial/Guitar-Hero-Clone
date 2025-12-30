package Notes;

import java.awt.*;

public class Note {

    private int x;
    private int y;
    private final int SPEED = 3;

    public Note(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y += SPEED;
    }

    public void draw(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillOval(x, y, 30, 30);
    }
}

