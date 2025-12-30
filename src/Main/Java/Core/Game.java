package Main.Java.Core;

import javax.swing.JFrame;

public class Game {
    public static void main(String[] args) {
        JFrame window = new JFrame("Guitar Hero Clone");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Fechar o software ao clicar no X
        window.setResizable(false); //Proibir redimensionamento da janela

        GamePanel gamepanel = new GamePanel();
        window.add(gamepanel);

        window.pack(); //Ajusta automaticamente o tamanho da janela
        window.setLocationRelativeTo(null); //Centralizar janela
        window.setVisible(true); //Deixando visivel

        gamepanel.startGame(); //Iniciando o game loop
    }
}
