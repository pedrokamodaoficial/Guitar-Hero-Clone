package Main.Java.Notes;

public class Note {

    public static final long HIT_LINE_TIME = 2000; // ms
    public static final float SPEED = 0.2f;

    private int lane;
    private float y;
    private long hitTime;
    private long spawnTime;

    public Note(int lane, long hitTime) {
        this.lane = lane;
        this.hitTime = hitTime;
        this.spawnTime = hitTime - HIT_LINE_TIME;
        this.y = -30; // 🔑 nasce fora da tela
    }

    public void update(long songTime) {

        // 🔒 ANTES DO SPAWN → NÃO MOVE
        if (songTime < spawnTime) {
            return;
        }

        float progress = (float)(songTime - spawnTime) / HIT_LINE_TIME;

        // progress vai de 0 → 1
        y = progress * 500; //distância até a linha de hit
    }

    public int getLane() {
        return lane;
    }

    public float getY() {
        return y;
    }

    public boolean isMissed(long songTime) {
        return songTime > hitTime + 300;
    }
}


