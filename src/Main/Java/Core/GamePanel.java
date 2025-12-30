package Main.Java.Core;

import Main.Chart.FCPChartLoader;
import Main.Java.Audio.MusicPlayer;
import Main.Java.Notes.Note;
import Main.Java.Notes.NoteEvent;
import Main.Java.UI.HitFeedback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener {

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

    int hudX = LANE_START_X + LANE_COUNT * LANE_WIDTH + 40;
    int hudWidth = getWidth() - hudX - 20;
    int hudCenterX = hudX + hudWidth / 2;

    private static final int HIT_LINE_Y = 500;
    private static final int BUTTON_HEIGTH = 20;

    private static final long INTRO_DELAY = 7800;

    private boolean gameOver = false;
    private int score = 0;
    private int streak = 0;
    private int maxStreak = 0;
    private int multiplier = 1;
    private static final int MAX_MULTIPLIER = 5;

    private long[] laneGlowTime = new long[LANE_COUNT];
    private static final long GLOW_DURATION = 120; // ms

    private boolean stopSpawningNotes = false;
    private static final long END_BUFFER = 9000; // 5 segundos

    private HitFeedback feedback;

    private MusicPlayer music;

    private enum GameState{
        PLAYING,
        FINISHED
    }

    private GameState gameState = GameState.PLAYING;

    public GamePanel(){
        setPreferredSize(new Dimension(800, 600)); //Define altura e largura
        setBackground(Color.BLACK); //Define a cor de fundo
        addKeyListener(this); //Diz que essa janela escuta esse teclado
        setFocusable(true); //Permite que o painel receba o foco (Fundamental para o funcionamento do teclado)
        addMouseListener(this); //Diz que a janela escuta esse mouse

        notes = new ArrayList<>();
        chart = new ArrayList<>();
        chartIndex = 0;

        music = new MusicPlayer();
    }

    private long getChartEndTime() {
        if (chart.isEmpty()) return 0;
        return chart.get(chart.size() - 1).time;
    }

    private long getSongDuration() {
        if (chart.isEmpty()) return 0;
        return chart.get(chart.size() - 1).time + 3000; // 3s após última nota
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

        stopSpawningNotes = false; // garantir que possa spawnar notas no início
        gameOver = false;
        gameState = GameState.PLAYING;

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

        if (gameOver) return;

        long songTime =
                (music.getTimeMillis() - gameStartTime) - INTRO_DELAY;

        long songLength = music.getLengthMillis();

        if (songLength - music.getTimeMillis() <= END_BUFFER) {
            stopSpawningNotes = true;
        }

        if (!stopSpawningNotes) {
            while (chartIndex < chart.size()
                    && songTime >= chart.get(chartIndex).time - Note.HIT_LINE_TIME) {

                NoteEvent event = chart.get(chartIndex);

                int[] lanes = generateRandomLanes();
                for (int lane : lanes) {
                    notes.add(new Note(lane, event.time));
                }

                chartIndex++;
            }
        }

        // Atualiza notas
        for (Note note : notes) {
            note.update(songTime);
        }

        // Remove misses
        notes.removeIf(note -> {
            if (!note.isHit() &&
                    songTime - note.getHitTime() > Note.GOOD_WINDOW) {

                streak = 0;
                multiplier = 1;
                return true;
            }
            return false;
        });

        // Quando a música terminou e não há mais notas em cena, finalizar jogo
        if (music.isFinished() && notes.isEmpty()) {
            gameOver = true;
            stopSpawningNotes = true;
            gameState = GameState.FINISHED;
            music.stop(); // garante que o player esteja parado
        }
    }

    private void restartGame() {
        notes.clear();
        chartIndex = 0;

        score = 0;
        streak = 0;
        maxStreak = 0;
        multiplier = 1;
        feedback = null;
        gameOver = false;

        stopSpawningNotes = false; // permitir spawn novamente
        gameState = GameState.PLAYING;

        for (int i = 0; i < LANE_COUNT; i++) {
            laneGlowTime[i] = 0;
        }

        music.stop();
        music.load("Assets/FateOfOphelia.wav");
        music.play();
        // importante setar o start AFTER play para sincronizar tempos
        gameStartTime = music.getTimeMillis();
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

    //Desenho dos itens na tela
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //Limpa o desenho antes de partir para o próximo

        Graphics2D g2 = (Graphics2D) g;
        drawLanes(g2);
        drawHitLine(g2);
        drawButtons(g2);
        drawBigHUD(g2);

        for (Note note : notes) {
            if (note.getY() > -50 && note.getY() < getHeight()) {
                drawNote(g2, note);
            }
        }

        long songTime = (music.getTimeMillis() - gameStartTime) - INTRO_DELAY;

        if (feedback != null && feedback.isAlive(songTime)) {
            feedback.draw(
                    g2,
                    getWidth() / 2 - 40,
                    HIT_LINE_Y - 30,
                    songTime
            );
        }

        if (gameOver) {
            drawGameOver(g2);
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

        long songTime = (music.getTimeMillis() - gameStartTime) - INTRO_DELAY;

        for (int lane = 0; lane < LANE_COUNT; lane++){
            int x = LANE_START_X + lane * LANE_WIDTH;
            int y = HIT_LINE_Y;

            Color baseColor = switch (lane){
                case 0 -> Color.GREEN;
                case 1 -> Color.RED;
                case 2 -> Color.YELLOW;
                case 3 -> Color.BLUE;
                default -> Color.WHITE;
            };

            // Glow se ativo
            if (songTime - laneGlowTime[lane] < GLOW_DURATION) {
                g.setColor(new Color(
                        baseColor.getRed(),
                        baseColor.getGreen(),
                        baseColor.getBlue(),
                        120
                ));

                g.fillRoundRect(
                        x + 5,
                        y - 5,
                        LANE_WIDTH - 10,
                        BUTTON_HEIGTH + 10,
                        15,
                        15
                );
            }

            g.setColor(baseColor);
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

    private void drawBigHUD(Graphics2D g) {

        int hudX = LANE_START_X + LANE_COUNT * LANE_WIDTH + 40;
        int hudWidth = getWidth() - hudX - 20;
        int centerX = hudX + hudWidth / 2;

        int centerY = getHeight() / 2;

        // ===== SCORE =====
        String scoreText = String.valueOf(score);

        Font scoreFont = new Font("Arial", Font.BOLD, 64);
        g.setFont(scoreFont);

        FontMetrics fmScore = g.getFontMetrics();
        int scoreWidth = fmScore.stringWidth(scoreText);

        int scoreY = centerY - 40;

        // Glow azul
        g.setColor(new Color(0, 140, 255, 120));
        for (int i = 0; i < 8; i++) {
            g.drawString(scoreText, centerX - scoreWidth / 2 + i, scoreY);
            g.drawString(scoreText, centerX - scoreWidth / 2 - i, scoreY);
        }

        // Texto principal
        g.setColor(Color.WHITE);
        g.drawString(scoreText, centerX - scoreWidth / 2, scoreY);

        // ===== MULTIPLICADOR =====
        if (multiplier > 1) {

            String multText = "x" + multiplier;

            Font multFont = new Font("Arial", Font.BOLD, 96);
            g.setFont(multFont);

            FontMetrics fmMult = g.getFontMetrics();
            int multWidth = fmMult.stringWidth(multText);

            int multY = centerY + 80;

            // Glow azul mais forte
            g.setColor(new Color(0, 160, 255, 180));
            for (int i = 0; i < 10; i++) {
                g.drawString(multText, centerX - multWidth / 2 + i, multY);
                g.drawString(multText, centerX - multWidth / 2 - i, multY);
            }

            g.setColor(Color.WHITE);
            g.drawString(multText, centerX - multWidth / 2, multY);
        }
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(Color.WHITE);
        g.drawString("PARABÉNS!", getWidth() / 2 - 140, 200);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Pontuação: " + score, getWidth() / 2 - 140, 260);
        g.drawString("Maior Streak: x" + maxStreak, getWidth() / 2 - 140, 310);

        g.setFont(new Font("Arial", Font.PLAIN, 22));
        g.drawString("Pressione ENTER para reiniciar", getWidth() / 2 - 180, 380);
    }

    private Rectangle restartButton =
            new Rectangle(0, 0, 220, 50);


    //Teclas configuradas
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

    //Função para o pressionar dos botões (Detecção do HIT)
    @Override
    public void keyPressed(KeyEvent e) {

        if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            restartGame();
            return;
        }

        int lane = keyToLane(e.getKeyCode());
        if (lane == -1) return;

        long songTime = (music.getTimeMillis() - gameStartTime) - INTRO_DELAY;

        Note closestNote = null;
        long smallestDelta = Long.MAX_VALUE;

        for (Note note : notes) {
            if (note.isHit()) continue;
            if (note.getLane() != lane) continue;

            long delta = Math.abs(songTime - note.getHitTime());

            if (delta < smallestDelta) {
                smallestDelta = delta;
                closestNote = note;
            }
        }

        if (closestNote == null) {
            feedback = new HitFeedback("MISS", Color.RED, songTime);
            repaint();
            return;
        }

        if (smallestDelta <= Note.PERFECT_WINDOW) {
            feedback = new HitFeedback("PERFECT", Color.GREEN, songTime);
            laneGlowTime[lane] = songTime;
            registerHit("PERFECT", closestNote);
        }
        else if (smallestDelta <= Note.GOOD_WINDOW) {
            feedback = new HitFeedback("GOOD", Color.YELLOW, songTime);
            laneGlowTime[lane] = songTime;
            registerHit("GOOD", closestNote);
        }
        else {
            feedback = new HitFeedback("MISS", Color.RED, songTime);
            streak = 0;
            multiplier = 1;
        }

        repaint();
    }


    //Registrando o hit e aumentando o multiplicador
    private void registerHit(String result, Note note) {
        note.markHit();

        switch (result) {
            case "PERFECT" -> {
                score += 100 * multiplier;
                streak++;
            }
            case "GOOD" -> {
                score += 50 * multiplier;
                streak++;
            }
            case "MISS" -> {
                streak = 0;
            }
        }

        if (streak > maxStreak) {
            maxStreak = streak;
        }

        multiplier = Math.min(1 + streak / 5, MAX_MULTIPLIER);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameState == GameState.FINISHED &&
                restartButton.contains(e.getPoint())) {
            restartGame();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameOver) return;

        Rectangle button = new Rectangle(260, 420, 280, 60);
        if (button.contains(e.getPoint())) {
            restartGame();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}