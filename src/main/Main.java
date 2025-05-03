package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        JFrame window = new JFrame("Chessboard");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        Board board = new Board();
        window.add(board);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}