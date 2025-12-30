package Main.Java.Core;

import Main.Java.Audio.MusicPlayer;
import Main.Java.Notes.Note;
import Main.Java.Notes.NoteEvent;
import Main.Java.Utils.Constants;

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
        chart.add(new NoteEvent(1000, 2));
        chart.add(new NoteEvent(1500, 1, 3));
        chart.add(new NoteEvent(1800, 4));
        chart.add(new NoteEvent(2200, 0));
        chart.add(new NoteEvent(2600, 2, 4));
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

        long songTime = music.getTimeMillis();

        while (chartIndex < chart.size() && songTime >= chart.get(chartIndex).time){
            NoteEvent event = chart.get(chartIndex);

            for (int lane: event.lanes){
                notes.add(new Note(lane));
            }
            chartIndex++;
        }

        for (Note note : notes) {
            note.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //Limpa o desenho antes de partir para o próximo

        g.setColor(Color.GRAY);
        g.fillRect(100, Constants.HIT_ZONE_Y, 400, 5);

        for (Note note : notes) {
            note.draw(g); //Desenha as notas na tela
        }
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
