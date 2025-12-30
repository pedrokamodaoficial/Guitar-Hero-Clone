// java
package Main.Java.Audio;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;

public class MusicPlayer {

    private Clip clip;

    public void load(String path) {
        try {
            AudioInputStream ais;

            // Tenta carregar do classpath (por exemplo: src/Main/resources/Assets/...)
            URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource(path);
            if (resourceUrl != null) {
                ais = AudioSystem.getAudioInputStream(resourceUrl);
            } else {
                // Fallback para arquivo no sistema (caminho relativo ou absoluto)
                File file = new File(path);
                ais = AudioSystem.getAudioInputStream(file);
            }

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

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }

    public long getTimeMillis() {
        if (clip == null) return 0;
        return clip.getMicrosecondPosition() / 1000;
    }

    public long getLengthMillis() {
        if (clip == null) return 0;
        return clip.getMicrosecondLength() / 1000;
    }

    public boolean isFinished() {
        return clip != null &&
                clip.getMicrosecondPosition() >= clip.getMicrosecondLength();
    }
}