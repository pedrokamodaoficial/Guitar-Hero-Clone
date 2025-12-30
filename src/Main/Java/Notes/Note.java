package Main.Java.Notes;

public class Note {

    public static final long HIT_LINE_TIME = 2000; // ms
    public static final float SPEED = 0.2f;

    public static final long PERFECT_WINDOW = 80; // ms
    public static final long GOOD_WINDOW = 150;   // ms

    private int lane;
    private float y;
    private long hitTime;
    private long spawnTime;
    private boolean hit = false;

    public Note(int lane, long hitTime) {
        this.lane = lane;
        this.hitTime = hitTime;
        this.spawnTime = hitTime - HIT_LINE_TIME;
        this.y = -30;
    }

    public void update(long songTime) {

        if (songTime < spawnTime) return;

        float progress = (float)(songTime - spawnTime) / HIT_LINE_TIME;
        y = progress * 500;
    }

    public int getLane() {
        return lane;
    }

    public float getY() {
        return y;
    }

    public long getHitTime() {
        return hitTime;
    }

    public boolean isHit() {
        return hit;
    }

    public void markHit() {
        hit = true;
    }

    public long getDelta(long songTime) {
        return Math.abs(songTime - hitTime);
    }

    public boolean isMissed(long songTime) {
        return !hit && songTime - hitTime > GOOD_WINDOW;
    }
}


