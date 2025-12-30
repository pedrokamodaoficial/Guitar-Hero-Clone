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

    private static final int LANE_COUNT = 4;
    private static final int LANE_WIDTH = 80;
    private static final int LANE_START_X = 100;

    private static final int HIT_LINE_Y = 500;
    private static final int BUTTON_HEIGTH = 20;

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

        notes.removeIf(note ->
                !note.isHit() &&
                        songTime - note.getHitTime() > Note.GOOD_WINDOW
        );


        for (Note note : notes) {
            note.update(songTime);
        }
    }

    private int[] generateRandomLanes() {

        double roll = random.nextDouble();
        int laneCount;

        if (roll < 0.65) {
            laneCount = 1;      // 65%
        } else if (roll < 0.95) {
            laneCount = 2;      // 30%
        } else {
            laneCount = 3;      // 5%
        }

        Set<Integer> lanesSet = new HashSet<>();

        while (lanesSet.size() < laneCount) {
            int lane = random.nextInt(4);

            // evita repetir lane sozinho
            if (laneCount == 1 && lane == lastLane) continue;

            lanesSet.add(lane);
        }

        lastLane = lanesSet.iterator().next();
        return lanesSet.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //Limpa o desenho antes de partir para o próximo

        Graphics2D g2 = (Graphics2D) g;
        drawLanes(g2);
        drawHitLine(g2);
        drawButtons(g2);

        for (Note note : notes) {
            if (note.getY() > -50 && note.getY() < getHeight()) {
                drawNote(g2, note);
            }
        }
    }

    private void drawLanes(Graphics2D g){
        g.setColor(Color.DARK_GRAY);

        for (int i = 0; i <= LANE_COUNT; i++){
            int x = LANE_START_X + i *LANE_WIDTH;
            g.drawLine(x, 0, x, getHeight());
        }
    }

    private void drawButtons(Graphics2D g){

        for (int lane = 0; lane < LANE_COUNT; lane++){
            int x = LANE_START_X + lane * LANE_WIDTH;
            int y = HIT_LINE_Y;

            switch (lane){
                case 0 -> g.setColor(Color.GREEN);
                case 1 -> g.setColor(Color.RED);
                case 2 -> g.setColor(Color.YELLOW);
                case 3 -> g.setColor(Color.BLUE);
            }

            g.fillRoundRect(
                    x + 10,
                    y,
                    LANE_WIDTH - 20,
                    BUTTON_HEIGTH,
                    10,
                    10
            );
        }
    }

    private void drawHitLine(Graphics2D g){
        g.setColor(Color.WHITE);
        g.drawLine(0, HIT_LINE_Y, getWidth(), HIT_LINE_Y);
    }

    private void drawNote(Graphics2D g, Note note) {
        int x = LANE_START_X + note.getLane() * LANE_WIDTH + 10;

        switch (note.getLane()) {
            case 0 -> g.setColor(Color.GREEN);
            case 1 -> g.setColor(Color.RED);
            case 2 -> g.setColor(Color.YELLOW);
            case 3 -> g.setColor(Color.BLUE);
        }

        g.fillRoundRect(x, (int) note.getY(), 60, 20, 10, 10);
    }

    private int keyToLane(int keyCode){
        return switch (keyCode) {
            case KeyEvent.VK_A -> 0;
            case KeyEvent.VK_S -> 1;
            case KeyEvent.VK_D -> 2;
            case KeyEvent.VK_F -> 3;
            default -> -1;
        };
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int lane = keyToLane(e.getKeyCode());
        if (lane == -1) return;

        long songTime = (music.getTimeMillis() - gameStartTime) - INTRO_DELAY;

        Note closestNote = null;
        long smallestDelta = Long.MAX_VALUE;

        for (Note note : notes){
            if (note.isHit()) continue;
            if (note.getLane() != lane) continue;

            long delta = Math.abs(songTime - note.getHitTime());

            if (delta < smallestDelta){
                smallestDelta = delta;
                closestNote = note;
            }
        }

        if (closestNote == null) return;

        if (smallestDelta <= Note.PERFECT_WINDOW) {
            registerHit("PERFECT", closestNote);
        } else if (smallestDelta <= Note.GOOD_WINDOW) {
            registerHit("GOOD", closestNote);
        } else {
            // fora da janela → ignora
        }
    }

    private void registerHit(String result, Note note) {
        note.markHit();
        notes.remove(note);

        System.out.println(result);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}