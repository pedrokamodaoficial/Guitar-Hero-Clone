package Main.Java.Audio;


import javax.sound.sampled.*;
import java.io.InputStream;

public class MusicPlayer {

    private Clip clip;

    public void load(String path) {
        try {
            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream(path);

            if (is == null) {
                throw new RuntimeException("Áudio não encontrado: " + path);
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            clip = AudioSystem.getClip();
            clip.open(ais);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public long getTimeMillis() {
        if (clip == null) return 0;
        return clip.getMicrosecondPosition() / 1000;
    }
}
