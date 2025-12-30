package Main.Java.Core;

import Main.Chart.FCPChartLoader;
import Main.Java.Audio.MusicPlayer;
import Main.Java.Notes.Note;
import Main.Java.Notes.NoteEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    private boolean running = false; //Controla se o jogo está ou não rodando
    private ArrayList<Note> notes; //Criando uma lista das notas
    private ArrayList<NoteEvent> chart;
    private int chartIndex;
    private long gameStartTime;
    private Random random = new Random();
    private int lastLane = -1;

    private static final long INTRO_DELAY = 7800;

    private MusicPlayer music;

    public GamePanel(){
        setPreferredSize(new Dimension(800, 600)); //Define altura e largura
        setBackground(Color.BLACK); //Define a cor de fundo
        addKeyListener(this); //Diz que essa janela escuta esse teclado
        setFocusable(true); //Permite que o painel receba o foco (Fundamental para o funcionamento do teclado)

        notes = new ArrayList<>();
        chart = new ArrayList<>();
        chartIndex = 0;

        music = new MusicPlayer();
    }

    @Override //Garante foco quando a janela está na tela
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    public void startGame(){
        chart = FCPChartLoader.load(
                "src/Main/resources/Assets/FateOfOpheliaBeats.fcpxml"
        );

        chartIndex = 0;

        music.load("Assets/FateOfOphelia.wav");
        music.play();
        gameStartTime = music.getTimeMillis();

        running = true; //Marca que o jogo está ativo
        //Thread onde o jogo irá rodar
        Thread gameThread = new Thread(this); //Cria uma thread própria para rodar o jogo
        gameThread.start(); //Inicia a Thread
    }

    @Override
    public void run() { //Metodo executado pela Thread
        final int FPS = 60; //Define 60 frames por segundo
        final long FRAME_TIME = 1000 / FPS; //Calcula o tempo que cada frame deve durar

        while (running){
            update();
            repaint();

            try {
                Thread.sleep(FRAME_TIME); //Pausa o jogo e espera ~16 ms para repetir
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {

        long songTime = (music.getTimeMillis() - gameStartTime) - INTRO_DELAY;

        if (songTime < 0) {
            repaint();
            return;
        }

        while (chartIndex < chart.size()
                && songTime >= chart.get(chartIndex).time - Note.HIT_LINE_TIME) {

            NoteEvent event = chart.get(chartIndex);

            int[] lanes = generateRandomLanes();

            for (int lane : lanes) {
                notes.add(new Note(lane, event.time));
            }

            chartIndex++;
        }

        for (Note note : notes) {
            note.update(songTime);
        }
    }

    private int[] generateRandomLanes() {

        int laneCount = random.nextInt(3) + 1; // 1 a 3 notas
        Set<Integer> lanesSet = new HashSet<>();

        while (lanesSet.size() < laneCount) {
            lanesSet.add(random.nextInt(4)); // lanes 0-3
        }

        return lanesSet.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //Limpa o desenho antes de partir para o próximo

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.drawLine(0, 500, getWidth(), 500);

        for (Note note : notes) {
            if (note.getY() > -50 && note.getY() < getHeight()) {
                drawNote(g2, note);
            }
        }
    }

    private void drawNote(Graphics2D g, Note note) {
        int x = 100 + note.getLane() * 80;

        switch (note.getLane()) {
            case 0 -> g.setColor(Color.GREEN);
            case 1 -> g.setColor(Color.RED);
            case 2 -> g.setColor(Color.YELLOW);
            case 3 -> g.setColor(Color.BLUE);
        }

        g.fillRoundRect(x, (int) note.getY(), 60, 20, 10, 10);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}