package Main.Java.UI;

import java.awt.*;

public class HitFeedback {
    private String text;
    private Color color;
    private long startTime;
    private static final long DURATION = 600; //ms

    public HitFeedback(String text, Color color, long startTime) {
        this.text = text;
        this.color = color;
        this.startTime = startTime;
    }

    public boolean isAlive(long currentTime){
        return currentTime - startTime < DURATION;
    }

    public void draw(Graphics2D g, int x, int y, long currentTime){

        float alpha = 1f - (float)(currentTime - startTime) / DURATION;
        alpha = Math.max(0f, alpha);

        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(text, x, y);

        g.setComposite(old);
    }
}
