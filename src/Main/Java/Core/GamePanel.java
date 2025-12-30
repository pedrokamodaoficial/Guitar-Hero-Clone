package Main.Java.Core;

import Main.Java.Audio.MusicPlayer;
import Main.Java.Notes.Note;
import Main.Java.Notes.NoteEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    private boolean running = false; //Controla se o jogo está ou não rodando
    private ArrayList<Note> notes; //Criando uma lista das notas
    private ArrayList<NoteEvent> chart;
    private int chartIndex;
    private long gameStartTime;

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

    private void loadChart() {
        chart.clear();
        chart.add(new NoteEvent(8022, 2));
        chart.add(new NoteEvent(8022, 1));
        chart.add(new NoteEvent(8430, 2));
        chart.add(new NoteEvent(15000, 1, 3));
        chart.add(new NoteEvent(18000, 4));
        chart.add(new NoteEvent(22000, 0));
        chart.add(new NoteEvent(26000, 2, 4));
    }

    @Override //Garante foco quando a janela está na tela
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    public void startGame(){
        loadChart();

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

        long songTime = music.getTimeMillis() - gameStartTime;

        while (chartIndex < chart.size() && songTime >= chart.get(chartIndex).time - Note.HIT_LINE_TIME){
            NoteEvent event = chart.get(chartIndex);

            for (int lane: event.lanes){
                notes.add(new Note(lane, event.time));
            }
            chartIndex++;
        }

        for (Note note : notes) {
            note.update(songTime);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //Limpa o desenho antes de partir para o próximo

        Graphics2D g2 = (Graphics2D) g;

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