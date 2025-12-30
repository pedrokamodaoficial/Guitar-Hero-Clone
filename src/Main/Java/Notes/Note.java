package Main.Java.Notes;

public class Note {

    private int lane;
    private long hitTime;
    private float y;

    public static final long HIT_LINE_TIME = 1500;
    public static final float SPEED = 0.3f;

    public Note(int lane, long hitTime) {
        this.lane = lane;
        this.hitTime = hitTime;
        this.y = -50;
    }

    public void update(long songTime) {
        long spawnTime = hitTime - HIT_LINE_TIME;
        long diff = songTime - spawnTime;
        y = diff * SPEED;
    }

    public int getLane() {
        return lane;
    }

    public float getY() {
        return y;
    }
}

