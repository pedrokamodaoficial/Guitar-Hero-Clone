package Core;

import Notes.Note;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    private boolean running = false; //Controla se o jogo está ou não rodando
    private ArrayList<Note> notes; //Criando uma lista das notas

    public GamePanel(){
        setPreferredSize(new Dimension(800, 600)); //Define altura e largura
        setBackground(Color.BLACK); //Define a cor de fundo
        addKeyListener(this); //Diz que essa janela escuta esse teclado
        setFocusable(true); //Permite que o painel receba o foco (Fundamental para o funcionamento do teclado)

        notes = new ArrayList<>();
        notes.add(new Note(200, -30)); //Criando nota de teste
        notes.add(new Note(400, -30));
        notes.add(new Note(300, -30));
        notes.add(new Note(100, -30));
    }

    @Override //Garante foco quando a janela está na tela
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    public void startGame(){
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
        for (Note note : notes) {
            note.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Note note : notes) {
            note.draw(g);
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
