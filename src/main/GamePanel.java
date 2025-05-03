package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import piece.*;

public class GamePanel  extends JPanel implements Runnable{
    public static final  int WIDTH = 1100;
    public static final  int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();

    // Pieces
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();

    // Color
    public static boolean currentColor = true;

    public GamePanel(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.BLACK);

        setPiece();
        copyPieces(pieces, simPieces);
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPiece(){
        for(int i = 0; i < 8; i++){
            pieces.add(new Pawn(true, i, 6));
            pieces.add(new Pawn(false, i, 1));
        }

        // White
        pieces.add(new Rook(true, 0, 7));
        pieces.add(new Rook(true, 7, 7));
        pieces.add(new Knight(true, 1, 7));
        pieces.add(new Knight(true, 6, 7));
        pieces.add(new Bishop(true, 2, 7));
        pieces.add(new Bishop(true, 5, 7));
        pieces.add(new Queen(true, 3, 7));
        pieces.add(new King(true, 4, 7));

        // Black
        pieces.add(new Rook(false, 0, 0));
        pieces.add(new Rook(false, 7, 0));
        pieces.add(new Knight(false, 1, 0));
        pieces.add(new Knight(false, 6, 0));
        pieces.add(new Bishop(false, 2, 0));
        pieces.add(new Bishop(false, 5, 0));
        pieces.add(new Queen(false, 3, 0));
        pieces.add(new King(false, 4, 0));
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for(int i = 0; i < source.size(); i++)
            target.add(source.get(i));
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update(){

    }

    // Drawing
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Board
        board.draw(g2);

        // Pieces
        for(Piece p : simPieces){
            p.draw(g2);
        }
    }
}